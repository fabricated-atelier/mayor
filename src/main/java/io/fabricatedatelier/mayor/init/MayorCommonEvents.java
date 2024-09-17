package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.data.StructureXpLoader;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.StringUtil;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MayorCommonEvents {

    public static void initialize() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof VillagerEntity || entity instanceof IronGolemEntity) {
                BlockPos villageCenterPos = MayorStateHelper.getVillageCenterPos((ServerWorld) entity.getWorld(), entity.getBlockPos());
                if (villageCenterPos != null) {
                    MayorStateHelper.updateVillageUuids((ServerWorld) entity.getWorld(), villageCenterPos, entity);
                }
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            MayorManager.mayorStructureMap.clear();

            Iterator<Identifier> iterator = server.getStructureTemplateManager().streamTemplates().iterator();
            while (iterator.hasNext()) {
                Identifier identifier = iterator.next();

                if (StringUtil.shouldStoreStructureIdentifier(identifier)) {

                    Identifier mayorStructureIdentifier = StringUtil.getMayorStructureIdentifier(identifier);
                    int level = StringUtil.getStructureLevelByIdentifier(identifier);
                    List<ItemStack> requiredItemStacks = StructureHelper.getStructureItemRequirements(server.getOverworld(), identifier);

                    int experience = 0;
                    if (StructureXpLoader.structureExperienceMap.containsKey(StringUtil.getMayorStructureString(identifier))) {
                        experience = StructureXpLoader.structureExperienceMap.get(StringUtil.getMayorStructureString(identifier));
                    } else {
                        experience = StructureHelper.getStructureExperience(requiredItemStacks);
                        StructureXpLoader.structureExperienceMap.put(StringUtil.getMayorStructureString(identifier), experience);
                    }
                    Map<BlockPos, BlockState> blockMap = StructureHelper.getBlockPosBlockStateMap(server.getOverworld(), identifier, BlockRotation.NONE, false);
                    MayorCategory.BiomeCategory biomeCategory = StructureHelper.getBiomeCategory(identifier);
                    MayorCategory.BuildingCategory buildingCategory = StructureHelper.getBuildingCategory(identifier);
                    Vec3i size = StructureHelper.getStructureSize(server.getOverworld(), identifier);

                    MayorStructure mayorStructure = new MayorStructure(mayorStructureIdentifier, level, experience, biomeCategory, buildingCategory, requiredItemStacks, blockMap, size);
                    if (MayorManager.mayorStructureMap.containsKey(biomeCategory)) {
                        MayorManager.mayorStructureMap.get(biomeCategory).add(mayorStructure);
                    } else {
                        List<MayorStructure> list = new ArrayList<>();
                        list.add(mayorStructure);
                        MayorManager.mayorStructureMap.put(biomeCategory, list);
                    }

                }
            }
        });
    }

}
