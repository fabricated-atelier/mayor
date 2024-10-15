package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.access.MayorVillageStateAccess;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.state.VillageState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StateHelper {

    public static VillageState getMayorVillageState(ServerWorld serverWorld) {
        return ((MayorVillageStateAccess) serverWorld).getMayorVillageState();
    }

    // Used for village create check by desk block
    public static boolean isVillageTooClose(ServerWorld serverWorld, BlockPos blockPos) {
        VillageState villageState = StateHelper.getMayorVillageState(serverWorld);

        int maxDistance = VillageHelper.VILLAGE_LEVEL_RADIUS.values().stream().toList().get(VillageHelper.VILLAGE_LEVEL_RADIUS.size() - 1);

        for (int i = 0; i < villageState.getVillageCenterPoses().size(); i++) {
            if (villageState.getVillageCenterPoses().get(i).isWithinDistance(blockPos, maxDistance)) {
                return true;
            }
        }

        return false;
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
                break;
            }
        }

        return null;
    }

    @Nullable
    public static VillageData getVillage(ServerWorld serverWorld, BlockPos villageCenter) {
        VillageState villageState = StateHelper.getMayorVillageState(serverWorld);

        for (int i = 0; i < villageState.getVillageCenterPoses().size(); i++) {
            if (villageState.getVillageCenterPoses().get(i).equals(villageCenter)) {
                return villageState.getVillageData(villageState.getVillageCenterPoses().get(i));
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

    // Gets ticked once every minute
    public static void tickVillageData(ServerWorld serverWorld, VillageData villageData) {
        if (villageData.getCitizenData().getTaxTime() > 0) {
            if (serverWorld.getTime() - villageData.getCitizenData().getTaxTime() > 0) {
                for (UUID citizen : villageData.getCitizenData().getCitizens()) {
                    if (!villageData.getCitizenData().getTaxPaidCitizens().contains(citizen)) {
                        villageData.getCitizenData().addTaxUnpaidCitizen(citizen);
                    }
                }
                villageData.getCitizenData().setTaxPaidCitizens(new ArrayList<>());

                if (villageData.getCitizenData().getTaxInterval() > 0) {
                    villageData.getCitizenData().setTaxTime(serverWorld.getTime() + villageData.getCitizenData().getTaxInterval());

                    for (UUID uuid : villageData.getCitizenData().getCitizens()) {
                        if (serverWorld.getServer().getPlayerManager().getPlayer(uuid) instanceof ServerPlayerEntity player) {
                            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", villageData.getName())));
                            player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.citizen.taxes")));
                        }
                    }
                }
                StateHelper.getMayorVillageState(serverWorld).markDirty();
            }
        }
    }

}
