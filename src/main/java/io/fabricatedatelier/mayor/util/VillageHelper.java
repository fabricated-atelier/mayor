package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.manager.MayorCategory;
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

    @Nullable
    public static VillagerEntity getTasklessBuildingVillager(VillageData villageData, ServerWorld serverWorld) {
        for (int i = 0; i < villageData.getVillagers().size(); i++) {
            if (serverWorld.getEntity(villageData.getVillagers().get(i)) instanceof VillagerEntity villagerEntity) {
                if (villagerEntity instanceof Builder builder) {
                    if (!builder.hasTargetPosition()) {
                        return villagerEntity;
                    }
                }
            }
        }
        return null;
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

}
