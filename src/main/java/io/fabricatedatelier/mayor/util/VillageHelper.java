package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.config.MayorConfig;
import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.init.MayorVillagerUtilities;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.VillageState;
import io.fabricatedatelier.mayor.state.StructureData;
import io.fabricatedatelier.mayor.state.VillageData;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            VillageData villageData = StateHelper.getClosestVillage(serverWorld, builder.getVillagerEntity().getBlockPos());
            if (villageData != null) {
                builder.setVillageCenterPosition(villageData.getCenterPos());
            }
        }

        if (builder.getVillageCenterPosition() != null) {
            VillageState villageState = StateHelper.getMayorVillageState(serverWorld);

            if (villageState.getVillageData(builder.getVillageCenterPosition()) != null) {
                VillageData villageData = villageState.getVillageData(builder.getVillageCenterPosition());
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
                        StateHelper.updateVillageUuids(serverWorld, builder.getVillageCenterPosition(), builder.getVillagerEntity());
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
                villageState.markDirty();
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
            StateHelper.getMayorVillageState(serverWorld).markDirty();

            for (UUID uuid : villageData.getCitizenData().getCitizens()) {
                if (serverWorld.getServer().getPlayerManager().getPlayer(uuid) instanceof ServerPlayerEntity serverPlayerEntity) {
                    serverPlayerEntity.sendMessage(Text.translatable("mayor.village.level_up", villageData.getName()), true);
                }
            }
            // Todo: edge case: may sync level to mayor? if mayor is in mayor view
        }
    }

    // Todo: Survival non mayor screen to see infos like village level and building material

    public static void updateOfflineMayor(ServerWorld serverWorld, VillageData villageData) {
        if (villageData.getMayorPlayerUuid() != null && serverWorld.getTime() >= (villageData.getMayorPlayerTime() + MayorConfig.CONFIG.instance().maxTickMayorOffline)) {
            villageData.setMayorPlayerUuid(null);
            villageData.setMayorPlayerTime(0);
        }
    }

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
            case DECORATION -> {
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

    public static int getAvailableBuilderCount(ServerWorld serverWorld, List<UUID> villagers) {
        int availableBuilder = 0;
        for (UUID villager : villagers) {
            if (serverWorld.getEntity(villager) instanceof VillagerEntity villagerEntity && villagerEntity.getVillagerData().getProfession().equals(MayorVillagerUtilities.BUILDER) && villagerEntity instanceof Builder builder && !builder.hasTargetPosition()) {
                availableBuilder++;
            }
        }
        return availableBuilder;
    }

}
