package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class CitizenHelper {

    public static boolean isCitizenOfNearbyVillage(ServerWorld serverWorld, PlayerEntity playerEntity) {
        BlockPos villageCenter = StateHelper.getVillageCenterPos(serverWorld, playerEntity.getBlockPos());
        if (villageCenter != null) {
            BlockPos citizenVillageCenter = ((MayorManagerAccess) playerEntity).getMayorManager().getCitizenManager().getVillagePos();
            return villageCenter.equals(citizenVillageCenter);
        }
        return false;
    }

}
