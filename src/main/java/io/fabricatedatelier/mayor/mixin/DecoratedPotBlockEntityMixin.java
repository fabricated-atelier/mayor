package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.access.BallotUrnAccess;
import io.fabricatedatelier.mayor.screen.block.BallotUrnBlockScreenHandler;
import io.fabricatedatelier.mayor.network.packet.BallotUrnPacket;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(DecoratedPotBlockEntity.class)
public abstract class DecoratedPotBlockEntityMixin extends BlockEntity implements BallotUrnAccess, ExtendedScreenHandlerFactory<BallotUrnPacket> {

    // When there are citizens, take citizen count as inventory size
    @Unique
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    @Unique
    private boolean validated;
    @Unique
    private long mayorPlayerTime;
    @Unique
    private List<UUID> votedPlayerUuids = new ArrayList<>();
    @Unique
    private long voteStartTime = 0;
    @Unique
    private int voteTicks = 0;

    public DecoratedPotBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    protected void writeNbtMixin(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo info) {
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        nbt.putBoolean("Validated", this.validated);
        nbt.putInt("VotedPlayerCount", this.votedPlayerUuids.size());
        for (int i = 0; i < this.votedPlayerUuids.size(); i++) {
            nbt.putUuid("VotedPlayerUuid" + i, this.votedPlayerUuids.get(i));
        }
        nbt.putLong("VoteStartTime", this.voteStartTime);
        nbt.putInt("VoteTicks", this.voteTicks);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    protected void readNbtMixin(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo info) {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        this.validated = nbt.getBoolean("Validated");
        this.votedPlayerUuids.clear();
        for (int i = 0; i < nbt.getInt("VotedPlayerCount"); i++) {
            this.votedPlayerUuids.add(nbt.getUuid("VotedPlayerUuid" + i));
        }
        this.voteStartTime = nbt.getLong("VoteStartTime");
        this.voteTicks = nbt.getInt("VoteTicks");
    }

    @Override
    public BallotUrnPacket getScreenOpeningData(ServerPlayerEntity player) {
        String villageName = "Village";
        VillageData villageData = MayorStateHelper.getClosestVillage((ServerWorld) world, pos);
        if (villageData != null) {
            villageName = villageData.getName();
        }
        return new BallotUrnPacket(this.pos, villageName, this.validated, this.mayorPlayerTime, this.votedPlayerUuids, this.voteStartTime, this.voteTicks);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.inventory, slot, amount);
        this.markDirty();
        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    // Check if player is citizen
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("mayor.container.ballot_urn");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BallotUrnBlockScreenHandler(syncId, playerInventory, this, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    public boolean validated() {
        return validated;
    }

    @Override
    public void setMayorPlayerTime(long mayorPlayerTime) {
        this.mayorPlayerTime = mayorPlayerTime;
    }

    @Override
    public long getMayorPlayerTime() {
        return mayorPlayerTime;
    }

    @Override
    public void setVotedPlayerUuids(List<UUID> votedPlayerUuids) {
        this.votedPlayerUuids = votedPlayerUuids;
    }

    @Override
    public void addVotedPlayerUuid(UUID votedPlayerUuid) {
        if (!this.votedPlayerUuids.contains(votedPlayerUuid)) {
            this.votedPlayerUuids.add(votedPlayerUuid);
        }
    }

    @Override
    public List<UUID> getVotedPlayerUuids() {
        return votedPlayerUuids;
    }

    @Override
    public void setVoteStartTime(long voteStartTime) {
        this.voteStartTime = voteStartTime;
    }

    @Override
    public long getVoteStartTime() {
        return voteStartTime;
    }

    @Override
    public void setVoteTicks(int voteTicks) {
        this.voteTicks = voteTicks;
    }

    @Override
    public int getVoteTicks() {
        return voteTicks;
    }

    @Override
    public void addStack(ItemStack itemStack) {
        for (int i = 1; i < this.inventory.size(); i++) {
            if (this.inventory.get(i).isEmpty()) {
                this.inventory.set(i, itemStack);
            }
        }
    }
}
