package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.state.StructureData;
import io.fabricatedatelier.mayor.state.VillageData;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillageHelper {

    public static int VILLAGE_MAX_LEVEL = 5;

    public static final Map<Integer, Integer> VILLAGE_LEVEL_RADIUS = Map.of(1, 50, 2, 100, 3, 150, 4, 200, 5, 300);

    public static boolean hasTasklessBuildingVillager(VillageData villageData, ServerWorld serverWorld) {
        for (int i = 0; i < villageData.getVillagers().size(); i++) {
            if (serverWorld.getEntity(villageData.getVillagers().get(i)) instanceof VillagerEntity villagerEntity) {
                if (villagerEntity instanceof Builder builder) {
                    if (!builder.hasTargetPosition()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    public static Builder getTasklessBuildingVillagerBuilder(VillageData villageData, ServerWorld serverWorld) {
        for (int i = 0; i < villageData.getVillagers().size(); i++) {
            if (serverWorld.getEntity(villageData.getVillagers().get(i)) instanceof VillagerEntity villagerEntity) {
                if (villagerEntity instanceof Builder builder) {
                    if (!builder.hasTargetPosition()) {
                        return builder;
                    }
                }
            }
        }
        return null;
    }

    public static void updateBuildingVillagerBuilder(ServerWorld serverWorld, Builder builder, boolean freshBuilder) {
        if (builder.getVillageCenterPosition() == null) {
            VillageData villageData = MayorStateHelper.getClosestVillage(serverWorld, builder.getVillagerEntity().getBlockPos());
            if (villageData != null) {
                builder.setVillageCenterPosition(villageData.getCenterPos());
            }
        }

        if (builder.getVillageCenterPosition() != null) {
            MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(serverWorld);

            if (mayorVillageState.getVillageData(builder.getVillageCenterPosition()) != null) {
                VillageData villageData = mayorVillageState.getVillageData(builder.getVillageCenterPosition());
                if (freshBuilder) {
                    for (ConstructionData constructionData : villageData.getConstructions().values()) {
                        if (constructionData.getVillagerUuid() == null) {
                            constructionData.setVillagerUuid(builder.getVillagerEntity().getUuid());
                            builder.setTargetPosition(constructionData.getBottomCenterPos());
                            builder.setVillageCenterPosition(villageData.getCenterPos());
                            break;
                        }
                    }
                } else {
                    if (!builder.getVillagerEntity().isAlive()) {
                        MayorStateHelper.updateVillageUuids(serverWorld, builder.getVillageCenterPosition(), builder.getVillagerEntity());
                    }
                    if (VillageHelper.getTasklessBuildingVillagerBuilder(villageData, serverWorld) instanceof Builder newBuilder) {
                        newBuilder.setTargetPosition(builder.getTargetPosition());
                        newBuilder.setVillageCenterPosition(builder.getVillageCenterPosition());
                        villageData.getConstructions().get(builder.getTargetPosition()).setVillagerUuid(newBuilder.getVillagerEntity().getUuid());
                    } else {
                        villageData.getConstructions().get(builder.getTargetPosition()).setVillagerUuid(null);
                    }
                    builder.setTargetPosition(null);
                }
                mayorVillageState.markDirty();
            }
        }
    }

    public static void tryLevelUpVillage(VillageData villageData, ServerWorld serverWorld) {
        if (villageData.getLevel() < VILLAGE_MAX_LEVEL) {

            Map<MayorCategory.BuildingCategory, Integer> buildingCategoryExperienceMap = new HashMap<>();

            for (int i = 0; i < MayorCategory.BuildingCategory.values().length; i++) {
                buildingCategoryExperienceMap.put(MayorCategory.BuildingCategory.values()[i], 0);
            }
            for (StructureData structureData : villageData.getStructures().values()) {
                MayorCategory.BuildingCategory buildingCategory = StructureHelper.getBuildingCategory(structureData.getIdentifier());
                int experience = buildingCategoryExperienceMap.get(buildingCategory) + structureData.getExperience();
                buildingCategoryExperienceMap.put(buildingCategory, experience);
            }

            for (int i = 0; i < MayorCategory.BuildingCategory.values().length; i++) {
                if (getVillageLevelBuildingExperienceRequirement(villageData.getLevel() + 1, MayorCategory.BuildingCategory.values()[i]) > buildingCategoryExperienceMap.get(MayorCategory.BuildingCategory.values()[i])) {
                    return;
                }
            }
            villageData.setLevel(villageData.getLevel() + 1);
            MayorStateHelper.getMayorVillageState(serverWorld).markDirty();

            List<ServerPlayerEntity> list = serverWorld.getPlayers(player -> player.getBlockPos().isWithinDistance(villageData.getCenterPos(), VILLAGE_LEVEL_RADIUS.get(villageData.getLevel())));
            for (ServerPlayerEntity serverPlayerEntity : list) {
                serverPlayerEntity.sendMessage(Text.translatable("mayor.village.level_up", villageData.getName()), true);
            }
            // Todo: edge case: may sync level to mayor? if mayor is in mayor view
        }
    }

    // Todo: Survival non mayor screen to see infos like village level and building material

    public static int getVillageLevelBuildingExperienceRequirement(int nextVillageLevel, MayorCategory.BuildingCategory buildingCategory) {

        switch (buildingCategory) {
            case HOUSE -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case BARN -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case FOUNTAIN -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case ARMORER -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case BUTCHER -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case CARTOGRAPHER -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case FARMER -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case FISHER -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case FLETCHER -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case LIBRARY -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case MASON -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case SHEPHERD -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case TANNERY -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case TEMPLE -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
            case SMITH -> {
                switch (nextVillageLevel) {
                    case 2:
                        return 10;
                    case 3:
                        return 20;
                    case 4:
                        return 30;
                    case 5:
                        return 40;
                }
            }
        }

        return 0;
    }

    public static boolean canReachSite(PathAwareEntity entity, BlockPos pos) {
        Path path = entity.getNavigation().findPathTo(pos, 256);
        return path != null && path.reachesTarget();
    }

    @Nullable
    public static BlockPos getAirPos(ServerWorld serverWorld, int x, int z, int minY, int maxY) {
        for (int i = minY; i < maxY; i++) {
            BlockPos pos = new BlockPos(x, i, z);
            if (serverWorld.getBlockState(pos).isAir()) {
                return pos;
            }
        }
        return null;
    }

    public static List<BlockPos> getPossibleMiddleTargetBlockPoses(ServerWorld serverWorld, BlockBox blockBox) {
        List<BlockPos> blockPosList = new ArrayList<>();
        int middleX = blockBox.getMinX() + (blockBox.getMaxX() - blockBox.getMinX()) / 2;
        int middleZ = blockBox.getMinZ() + (blockBox.getMaxZ() - blockBox.getMinZ()) / 2;

        BlockPos pos;

        pos = getAirPos(serverWorld, middleX, blockBox.getMinZ(), blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }
        pos = getAirPos(serverWorld, middleX, blockBox.getMaxZ(), blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }
        pos = getAirPos(serverWorld, blockBox.getMinX(), middleZ, blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }
        pos = getAirPos(serverWorld, blockBox.getMaxX(), middleZ, blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }

        return blockPosList;
    }

    @Nullable
    public static BlockPos findClosestTarget(ServerWorld serverWorld, VillagerEntity villagerEntity, ConstructionData constructionData) {
        if (constructionData != null) {
            BlockBox blockBox = constructionData.getStructureData().getBlockBox();

            BlockPos targetPos = null;
            for (BlockPos pos : getPossibleMiddleTargetBlockPoses(serverWorld, blockBox)) {
                if (VillageHelper.canReachSite(villagerEntity, pos)) {
                    if (targetPos != null) {
                        if (pos.getSquaredDistance(villagerEntity.getPos()) < targetPos.getSquaredDistance(villagerEntity.getPos())) {
                            targetPos = pos;
                        }
                    } else {
                        targetPos = pos;
                    }
                }
            }
            return targetPos;
        }
        return null;
    }

}
