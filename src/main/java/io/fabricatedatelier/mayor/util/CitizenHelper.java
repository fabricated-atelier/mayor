package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.state.VillageData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class CitizenHelper {

    // Could require BlockPos + UUID instead of PlayerEntity
    public static boolean isCitizenOfClosestVillage(ServerWorld serverWorld, PlayerEntity playerEntity) {
        VillageData villageData = StateHelper.getClosestVillage(serverWorld, playerEntity.getBlockPos());
        return villageData != null && villageData.getCitizens().contains(playerEntity.getUuid());
    }

}
