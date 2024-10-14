package io.fabricatedatelier.mayor.screen.block;

import io.fabricatedatelier.mayor.init.MayorBlockEntities;
import io.fabricatedatelier.mayor.network.packet.DeskPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class DeskBlockScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final BlockPos deskPos;
    private final boolean validated;
    private boolean mayor;

    public DeskBlockScreenHandler(int syncId, DeskPacket buf) {
        this(syncId, new SimpleInventory(1), new ArrayPropertyDelegate(1), buf.deskPos(), buf.validated());
        this.mayor = buf.mayor();
    }

    public DeskBlockScreenHandler(int syncId, Inventory inventory, PropertyDelegate propertyDelegate, BlockPos deskPos, boolean validated) {
        super(MayorBlockEntities.DESK_SCREEN_HANDLER, syncId);
        checkSize(inventory, 1);
        checkDataCount(propertyDelegate, 1);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.deskPos = deskPos;
        this.validated = validated;
        this.addSlot(new Slot(inventory, 0, 0, 0) {
            @Override
            public void markDirty() {
                super.markDirty();
                DeskBlockScreenHandler.this.onContentChanged(this.inventory);
            }
        });
        this.addProperties(propertyDelegate);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 100) {
            int i = id - 100;
            this.setProperty(0, i);
            return true;
        } else {
            switch (id) {
                case 1: {
                    int i = this.propertyDelegate.get(0);
                    this.setProperty(0, i - 1);
                    return true;
                }
                case 2: {
                    int i = this.propertyDelegate.get(0);
                    this.setProperty(0, i + 1);
                    return true;
                }
                case 3:
                    if (!player.canModifyBlocks()) {
                        return false;
                    }

                    ItemStack itemStack = this.inventory.removeStack(0);
                    this.inventory.markDirty();
                    // if (itemStack.isOf(Items.WRITABLE_BOOK)) {
                    //     itemStack = itemStack.withItem(Items.WRITTEN_BOOK);
                    // }
                    if (!player.getInventory().insertStack(itemStack)) {
                        player.dropItem(itemStack, false);
                    }

                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        this.sendContentUpdates();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public ItemStack getBookItem() {
        return this.inventory.getStack(0);
    }

    public int getPage() {
        return this.propertyDelegate.get(0);
    }

    public BlockPos getDeskPos() {
        return deskPos;
    }

    public boolean isValidated() {
        return validated;
    }

    public boolean isMayor() {
        return mayor;
    }
}
