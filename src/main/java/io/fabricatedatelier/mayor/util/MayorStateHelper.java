package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.access.MayorVillageStateAccess;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.state.VillageData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MayorStateHelper {

    public static boolean isInVillageRange(ServerWorld serverWorld, BlockPos blockPos) {
        MayorVillageState mayorVillageState = ((MayorVillageStateAccess) serverWorld).getMayorVillageState();

        int maxDistance = VillageHelper.VILLAGE_LEVEL_RADIUS.values().stream().toList().get(VillageHelper.VILLAGE_LEVEL_RADIUS.size() - 1);

        for (int i = 0; i < mayorVillageState.getVillageCenterPoses().size(); i++) {
            if (mayorVillageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, maxDistance)) {
                VillageData villageData = mayorVillageState.getVillageData(mayorVillageState.getVillageCenterPoses().get(i));
                if (mayorVillageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, VillageHelper.VILLAGE_LEVEL_RADIUS.get(villageData.getLevel()))) {
                    return true;
                }
            }
        }

        return false;
    }

    // Similar to isInVillageRange
    @Nullable
    public static BlockPos getVillageCenterPos(ServerWorld serverWorld, BlockPos blockPos) {
        MayorVillageState mayorVillageState = ((MayorVillageStateAccess) serverWorld).getMayorVillageState();

        int maxDistance = VillageHelper.VILLAGE_LEVEL_RADIUS.values().stream().toList().get(VillageHelper.VILLAGE_LEVEL_RADIUS.size() - 1);

        for (int i = 0; i < mayorVillageState.getVillageCenterPoses().size(); i++) {
            if (mayorVillageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, maxDistance)) {
                VillageData villageData = mayorVillageState.getVillageData(mayorVillageState.getVillageCenterPoses().get(i));
                if (mayorVillageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, VillageHelper.VILLAGE_LEVEL_RADIUS.get(villageData.getLevel()))) {
                    return mayorVillageState.getVillageCenterPoses().get(i);
                }
            }
        }

        return null;
    }

    @Nullable
    public static VillageData getClosestVillage(ServerWorld serverWorld, BlockPos blockPos) {
        MayorVillageState mayorVillageState = ((MayorVillageStateAccess) serverWorld).getMayorVillageState();

        int maxDistance = VillageHelper.VILLAGE_LEVEL_RADIUS.values().stream().toList().get(VillageHelper.VILLAGE_LEVEL_RADIUS.size() - 1);

        for (int i = 0; i < mayorVillageState.getVillageCenterPoses().size(); i++) {
            if (mayorVillageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, maxDistance)) {
                VillageData villageData = mayorVillageState.getVillageData(mayorVillageState.getVillageCenterPoses().get(i));
                if (mayorVillageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, VillageHelper.VILLAGE_LEVEL_RADIUS.get(villageData.getLevel()))) {
                    return villageData;
                }
            }
        }

        return null;
    }

    public static List<VillageData> getVillages(ServerWorld serverWorld) {
        List<VillageData> list = new ArrayList<>();
        MayorVillageState mayorVillageState = ((MayorVillageStateAccess) serverWorld).getMayorVillageState();
        for (int i = 0; i < mayorVillageState.getVillageCenterPoses().size(); i++) {
            if (mayorVillageState.getVillageData(mayorVillageState.getVillageCenterPoses().get(i)) != null) {
                list.add(mayorVillageState.getVillageData(mayorVillageState.getVillageCenterPoses().get(i)));
            }
        }

        return list;
    }

    public static void updateVillageUuids(ServerWorld serverWorld, BlockPos centerPos, LivingEntity livingEntity) {
        VillageData villageData = ((MayorVillageStateAccess) serverWorld).getMayorVillageState().getVillageData(centerPos);
        if (livingEntity instanceof VillagerEntity) {
            if (villageData.getVillagers().contains(livingEntity.getUuid())) {
                villageData.getVillagers().remove(livingEntity.getUuid());
            } else {
                villageData.getVillagers().add(livingEntity.getUuid());
            }
            ((MayorVillageStateAccess) serverWorld).getMayorVillageState().markDirty();
        } else if (livingEntity instanceof IronGolemEntity) {
            if (villageData.getIronGolems().contains(livingEntity.getUuid())) {
                villageData.getIronGolems().remove(livingEntity.getUuid());
            } else {
                villageData.getIronGolems().add(livingEntity.getUuid());
            }
            ((MayorVillageStateAccess) serverWorld).getMayorVillageState().markDirty();
        }
    }

}
