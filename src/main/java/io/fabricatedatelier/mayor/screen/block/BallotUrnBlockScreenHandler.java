package io.fabricatedatelier.mayor.screen.block;

import io.fabricatedatelier.mayor.access.BallotUrnAccess;
import io.fabricatedatelier.mayor.init.MayorBlockEntities;
import io.fabricatedatelier.mayor.init.MayorItems;
import io.fabricatedatelier.mayor.network.packet.BallotUrnPacket;
import io.fabricatedatelier.mayor.util.BallotUrnHelper;
import io.fabricatedatelier.mayor.util.CitizenHelper;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

public class BallotUrnBlockScreenHandler extends ScreenHandler {

    private final ScreenHandlerContext context;
    private final Inventory inventory;
    @Nullable
    private DecoratedPotBlockEntity decoratedPotBlockEntity = null;
    private PlayerEntity playerEntity;
    private String villageName;

    public BallotUrnBlockScreenHandler(int syncId, PlayerInventory playerInventory, BallotUrnPacket buf) {
        this(syncId, playerInventory, new SimpleInventory(1), ScreenHandlerContext.EMPTY);
        this.decoratedPotBlockEntity = (DecoratedPotBlockEntity) playerInventory.player.getWorld().getBlockEntity(buf.blockPos());
        this.playerEntity = playerInventory.player;
        this.villageName = buf.villageName();
        if (this.decoratedPotBlockEntity instanceof BallotUrnAccess ballotUrnAccess) {
            ballotUrnAccess.setMayorPlayerTime(buf.mayorPlayerTime());
            ballotUrnAccess.setValidated(buf.validated());
            ballotUrnAccess.setVotedPlayerUuids(buf.votedPlayerUuids());
            ballotUrnAccess.setVoteStartTime(buf.voteStartTime());
            ballotUrnAccess.setVoteTicks(buf.voteTicks());
        }
    }

    public BallotUrnBlockScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, ScreenHandlerContext context) {
        super(MayorBlockEntities.BALLOT_URN_SCREEN_HANDLER, syncId);
        this.context = context;
        this.inventory = inventory;
        this.context.run((world, pos) -> this.decoratedPotBlockEntity = (DecoratedPotBlockEntity) world.getBlockEntity(pos));
        this.playerEntity = playerInventory.player;
        int i;
        this.addSlot(new Slot(this.inventory, 0, 116, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (!stack.isOf(MayorItems.BALLOT_PAPER)) {
                    return false;
                }
                return !playerEntity.getWorld().isClient() && getBallotUrn().validated() && CitizenHelper.isCitizenOfClosestVillage((ServerWorld) playerEntity.getWorld(), playerEntity);
            }

            @Override
            public void setStackNoCallbacks(ItemStack stack) {
                super.setStackNoCallbacks(stack);
                if (!playerEntity.getWorld().isClient()) {
                    if (BallotUrnHelper.voteMayor((ServerWorld) playerEntity.getWorld(), decoratedPotBlockEntity.getPos(), playerEntity, stack)) {
                        playerEntity.getWorld().playSound(null, decoratedPotBlockEntity.getPos().getX(), decoratedPotBlockEntity.getPos().getY(), decoratedPotBlockEntity.getPos().getZ(), SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS, 1.0F, 0.7F + 0.5F * playerEntity.getWorld().getRandom().nextFloat(), playerEntity.getRandom().nextLong());
                        ((ServerWorld) playerEntity.getWorld()).spawnParticles(ParticleTypes.DUST_PLUME, (double) decoratedPotBlockEntity.getPos().getX() + 0.5, (double) decoratedPotBlockEntity.getPos().getY() + 1.2, (double) decoratedPotBlockEntity.getPos().getZ() + 0.5, 7, 0.0, 0.0, 0.0, 0.0);

                    }
                }
            }

        });

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 0) {
                if (!this.insertItem(itemStack2, 1, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 1 && index < 36) {
                if (itemStack.isOf(MayorItems.BALLOT_PAPER) && !this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public DecoratedPotBlockEntity getDecoratedPotBlockEntity() {
        return this.decoratedPotBlockEntity;
    }

    public BallotUrnAccess getBallotUrn() {
        return (BallotUrnAccess) this.decoratedPotBlockEntity;
    }

    public String getVillageName() {
        return this.villageName;
    }
}
