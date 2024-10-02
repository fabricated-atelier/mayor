package io.fabricatedatelier.mayor.access;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.UUID;

public interface BallotUrnAccess extends Inventory {

    void setValidated(boolean validated);

    boolean validated();

    void setMayorPlayerTime(long mayorPlayerTime);

    long getMayorPlayerTime();

    void setVotedPlayerUuids(List<UUID> votedPlayerUuids);

    void addVotedPlayerUuid(UUID votedPlayerUuid);

    List<UUID> getVotedPlayerUuids();

    void setVoteStartTime(long voteStartTime);

    long getVoteStartTime();

    void setVoteTicks(int voteTicks);

    int getVoteTicks();

    void addStack(ItemStack itemStack);

    @Override
    default void onClose(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            ItemStack itemStack = this.getStack(0);
            if (!itemStack.isEmpty()) {
                if (player.isAlive() && !((ServerPlayerEntity) player).isDisconnected()) {
                    player.getInventory().offerOrDrop(itemStack);
                } else {
                    player.dropItem(itemStack, false);
                }
            }
        }
    }
}
