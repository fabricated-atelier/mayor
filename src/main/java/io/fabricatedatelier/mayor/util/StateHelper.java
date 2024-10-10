package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.access.MayorVillageStateAccess;
import io.fabricatedatelier.mayor.state.VillageState;
import io.fabricatedatelier.mayor.state.VillageData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StateHelper {

    public static VillageState getMayorVillageState(ServerWorld serverWorld) {
        return ((MayorVillageStateAccess) serverWorld).getMayorVillageState();
    }

    public static boolean isInVillageRange(ServerWorld serverWorld, BlockPos blockPos) {
        VillageState villageState = StateHelper.getMayorVillageState(serverWorld);

        int maxDistance = VillageHelper.VILLAGE_LEVEL_RADIUS.values().stream().toList().get(VillageHelper.VILLAGE_LEVEL_RADIUS.size() - 1);

        for (int i = 0; i < villageState.getVillageCenterPoses().size(); i++) {
            if (villageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, maxDistance)) {
                VillageData villageData = villageState.getVillageData(villageState.getVillageCenterPoses().get(i));
                if (villageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, VillageHelper.VILLAGE_LEVEL_RADIUS.get(villageData.getLevel()))) {
                    return true;
                }
            }
        }

        return false;
    }

    // Similar to isInVillageRange
    @Nullable
    public static BlockPos getVillageCenterPos(ServerWorld serverWorld, BlockPos blockPos) {
        VillageState villageState = StateHelper.getMayorVillageState(serverWorld);

        int maxDistance = VillageHelper.VILLAGE_LEVEL_RADIUS.values().stream().toList().get(VillageHelper.VILLAGE_LEVEL_RADIUS.size() - 1);

        for (int i = 0; i < villageState.getVillageCenterPoses().size(); i++) {
            if (villageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, maxDistance)) {
                VillageData villageData = villageState.getVillageData(villageState.getVillageCenterPoses().get(i));
                if (villageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, VillageHelper.VILLAGE_LEVEL_RADIUS.get(villageData.getLevel()))) {
                    return villageState.getVillageCenterPoses().get(i);
                }
            }
        }

        return null;
    }

    @Nullable
    public static VillageData getClosestVillage(ServerWorld serverWorld, BlockPos blockPos) {
        VillageState villageState = StateHelper.getMayorVillageState(serverWorld);

        int maxDistance = VillageHelper.VILLAGE_LEVEL_RADIUS.values().stream().toList().get(VillageHelper.VILLAGE_LEVEL_RADIUS.size() - 1);

        for (int i = 0; i < villageState.getVillageCenterPoses().size(); i++) {
            if (villageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, maxDistance)) {
                VillageData villageData = villageState.getVillageData(villageState.getVillageCenterPoses().get(i));
                if (villageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, VillageHelper.VILLAGE_LEVEL_RADIUS.get(villageData.getLevel()))) {
                    return villageData;
                }
            }
        }

        return null;
    }

    public static List<VillageData> getVillages(ServerWorld serverWorld) {
        List<VillageData> list = new ArrayList<>();
        VillageState villageState = StateHelper.getMayorVillageState(serverWorld);
        for (int i = 0; i < villageState.getVillageCenterPoses().size(); i++) {
            if (villageState.getVillageData(villageState.getVillageCenterPoses().get(i)) != null) {
                list.add(villageState.getVillageData(villageState.getVillageCenterPoses().get(i)));
            }
        }

        return list;
    }

    public static void updateVillageUuids(ServerWorld serverWorld, BlockPos centerPos, LivingEntity livingEntity) {
        VillageData villageData = StateHelper.getMayorVillageState(serverWorld).getVillageData(centerPos);
        if (livingEntity instanceof VillagerEntity) {
            if (villageData.getVillagers().contains(livingEntity.getUuid())) {
                villageData.getVillagers().remove(livingEntity.getUuid());
            } else {
                villageData.getVillagers().add(livingEntity.getUuid());
            }
            StateHelper.getMayorVillageState(serverWorld).markDirty();
        } else if (livingEntity instanceof IronGolemEntity) {
            if (villageData.getIronGolems().contains(livingEntity.getUuid())) {
                villageData.getIronGolems().remove(livingEntity.getUuid());
            } else {
                villageData.getIronGolems().add(livingEntity.getUuid());
            }
            StateHelper.getMayorVillageState(serverWorld).markDirty();
        }
    }

    public static boolean isCitizenOfNearbyVillage(ServerWorld serverWorld, PlayerEntity playerEntity) {
        BlockPos villageCenter = getVillageCenterPos(serverWorld, playerEntity.getBlockPos());
        if (villageCenter != null) {
            BlockPos citizenVillageCenter = ((MayorManagerAccess) playerEntity).getMayorManager().getCitizenManager().getVillagePos();
            return villageCenter.equals(citizenVillageCenter);
        }
        return false;
    }

}
