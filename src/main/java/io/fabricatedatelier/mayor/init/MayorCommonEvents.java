package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.data.StructureDataLoader;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.util.StateHelper;
import io.fabricatedatelier.mayor.util.StringUtil;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
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
        ServerLivingEntityEvents.AFTER_DEATH.register(MayorCommonEvents::handleAfterDeath);
        ServerLifecycleEvents.SERVER_STARTED.register(MayorCommonEvents::handleServerStarted);
    }

    private static void handleAfterDeath(LivingEntity entity, DamageSource damageSource) {
        if (entity instanceof IronGolemEntity) {
            BlockPos villageCenterPos = StateHelper.getVillageCenterPos((ServerWorld) entity.getWorld(), entity.getBlockPos());
            if (villageCenterPos != null) {
                StateHelper.updateVillageUuids((ServerWorld) entity.getWorld(), villageCenterPos, entity);
            }
        }
    }

    private static void handleServerStarted(MinecraftServer server) {
        MayorManager.mayorStructureMap.clear();

        Iterator<Identifier> iterator = server.getStructureTemplateManager().streamTemplates().iterator();
        while (iterator.hasNext()) {
            Identifier identifier = iterator.next();

            if (StringUtil.shouldStoreStructureIdentifier(identifier)) {

                Identifier mayorStructureIdentifier = StringUtil.getMayorStructureIdentifier(identifier);
                int level = StringUtil.getStructureLevelByIdentifier(identifier);
                List<ItemStack> requiredItemStacks = StructureHelper.getStructureItemRequirements(server.getOverworld(), identifier);

                int experience;
                int price;
                if (StructureDataLoader.structureDataMap.containsKey(StringUtil.getMayorStructureString(identifier))) {
                    experience = StructureDataLoader.structureDataMap.get(StringUtil.getMayorStructureString(identifier)).get(0);
                    price = StructureDataLoader.structureDataMap.get(StringUtil.getMayorStructureString(identifier)).get(1);
                } else {
                    experience = StructureHelper.getStructureExperience(requiredItemStacks);
                    price = 8;
                    StructureDataLoader.structureDataMap.put(StringUtil.getMayorStructureString(identifier), List.of(experience, price));
                }
                Map<BlockPos, BlockState> blockMap = StructureHelper.getBlockPosBlockStateMap(server.getOverworld(), identifier, BlockRotation.NONE, false);
                MayorCategory.BiomeCategory biomeCategory = StructureHelper.getBiomeCategory(identifier);
                MayorCategory.BuildingCategory buildingCategory = StructureHelper.getBuildingCategory(identifier);
                Vec3i size = StructureHelper.getStructureSize(server.getOverworld(), identifier);

                MayorStructure mayorStructure = new MayorStructure(mayorStructureIdentifier, level, experience, price, biomeCategory, buildingCategory, requiredItemStacks, blockMap, size);
                if (MayorManager.mayorStructureMap.containsKey(biomeCategory)) {
                    MayorManager.mayorStructureMap.get(biomeCategory).add(mayorStructure);
                } else {
                    List<MayorStructure> list = new ArrayList<>();
                    list.add(mayorStructure);
                    MayorManager.mayorStructureMap.put(biomeCategory, list);
                }

            }
        }
    }
}
