package io.fabricatedatelier.mayor.block.entity;

import io.fabricatedatelier.mayor.block.custom.DeskBlock;
import io.fabricatedatelier.mayor.init.MayorBlockEntities;
import io.fabricatedatelier.mayor.network.packet.BallotUrnPacket;
import io.fabricatedatelier.mayor.network.packet.DeskPacket;
import io.fabricatedatelier.mayor.screen.block.DeskBlockScreenHandler;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.StateHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class DeskBlockEntity extends BlockEntity implements Clearable, NamedScreenHandlerFactory, ExtendedScreenHandlerFactory<DeskPacket> {

    private final Inventory inventory = new Inventory() {
        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return DeskBlockEntity.this.book.isEmpty();
        }

        @Override
        public ItemStack getStack(int slot) {
            return slot == 0 ? DeskBlockEntity.this.book : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            if (slot == 0) {
                ItemStack itemStack = DeskBlockEntity.this.book.split(amount);
                if (DeskBlockEntity.this.book.isEmpty()) {
                    DeskBlockEntity.this.onBookRemoved();
                }

                return itemStack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public ItemStack removeStack(int slot) {
            if (slot == 0) {
                ItemStack itemStack = DeskBlockEntity.this.book;
                DeskBlockEntity.this.book = ItemStack.EMPTY;
                DeskBlockEntity.this.onBookRemoved();
                return itemStack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
        }

        @Override
        public int getMaxCountPerStack() {
            return 1;
        }

        @Override
        public void markDirty() {
            DeskBlockEntity.this.markDirty();
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return Inventory.canPlayerUse(DeskBlockEntity.this, player) && DeskBlockEntity.this.hasBook();
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public void clear() {
        }
    };
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return index == 0 ? DeskBlockEntity.this.currentPage : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                DeskBlockEntity.this.setCurrentPage(value);
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };
    private ItemStack book = ItemStack.EMPTY;
    private int currentPage;
    private int pageCount;

    private boolean validated = false;

    public DeskBlockEntity(BlockPos pos, BlockState state) {
        super(MayorBlockEntities.DESK, pos, state);
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    @Override
    protected void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("Book", NbtElement.COMPOUND_TYPE)) {
            this.book = this.resolveBook(ItemStack.fromNbt(registryLookup, nbt.getCompound("Book")).orElse(ItemStack.EMPTY), null);
        } else {
            this.book = ItemStack.EMPTY;
        }

        this.pageCount = getPageCount(this.book);
        this.currentPage = MathHelper.clamp(nbt.getInt("Page"), 0, this.pageCount - 1);
        this.validated = nbt.getBoolean("Validated");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.getBook().isEmpty()) {
            nbt.put("Book", this.getBook().encode(registryLookup));
            nbt.putInt("Page", this.currentPage);
        }
        nbt.putBoolean("Validated", this.validated);
    }

    @Override
    public void clear() {
        this.setBook(ItemStack.EMPTY);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.hasBook()) {
            return new DeskBlockScreenHandler(syncId, this.inventory, this.propertyDelegate, this.getPos(), this.validated);
        }
        return null;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("mayor.container.desk");
    }

    public ItemStack getBook() {
        return this.book;
    }

    private boolean hasBook() {
        return this.book.isOf(Items.WRITABLE_BOOK) || this.book.isOf(Items.WRITTEN_BOOK);
    }

    public void setBook(ItemStack book) {
        this.setBook(book, null);
    }

    private void onBookRemoved() {
        this.currentPage = 0;
        this.pageCount = 0;
        BlockState blockState = this.getCachedState().with(DeskBlock.HAS_BOOK, false);
        world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(null, blockState));
    }

    private void setBook(ItemStack book, @Nullable PlayerEntity player) {
        this.book = this.resolveBook(book, player);
        this.currentPage = 0;
        this.pageCount = getPageCount(this.book);
        this.markDirty();
    }

    private void setCurrentPage(int currentPage) {
        int i = MathHelper.clamp(currentPage, 0, this.pageCount - 1);
        if (i != this.currentPage) {
            this.currentPage = i;
            this.markDirty();
        }
    }

    private ItemStack resolveBook(ItemStack book, @Nullable PlayerEntity player) {
        if (this.world instanceof ServerWorld && book.isOf(Items.WRITTEN_BOOK)) {
            WrittenBookItem.resolve(book, this.getCommandSource(player), player);
        }

        return book;
    }

    private ServerCommandSource getCommandSource(@Nullable PlayerEntity player) {
        String string;
        Text text;
        if (player == null) {
            string = "Desk";
            text = Text.literal("Desk");
        } else {
            string = player.getName().getString();
            text = player.getDisplayName();
        }

        Vec3d vec3d = Vec3d.ofCenter(this.pos);
        return new ServerCommandSource(CommandOutput.DUMMY, vec3d, Vec2f.ZERO, (ServerWorld) this.world, 2, string, text, this.world.getServer(), player);
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public boolean isValidated() {
        return validated;
    }

    private static int getPageCount(ItemStack stack) {
        WrittenBookContentComponent writtenBookContentComponent = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        if (writtenBookContentComponent != null) {
            return writtenBookContentComponent.pages().size();
        } else {
            WritableBookContentComponent writableBookContentComponent = stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
            return writableBookContentComponent != null ? writableBookContentComponent.pages().size() : 0;
        }
    }

    @Override
    public DeskPacket getScreenOpeningData(ServerPlayerEntity player) {
        boolean mayor = false;
        if (this.validated) {
            VillageData villageData = StateHelper.getClosestVillage(player.getServerWorld(), this.getPos());
            if (villageData != null && villageData.getMayorPlayerUuid() != null && villageData.getMayorPlayerUuid().equals(player.getUuid())) {
                mayor = true;
            }
        }
        return new DeskPacket(this.getPos(), this.validated, mayor);
    }
}

