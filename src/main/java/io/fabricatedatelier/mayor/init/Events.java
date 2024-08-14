package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.network.packet.StructureOriginPacket;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.StringUtil;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.*;

public class Events {

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
                    Map<BlockPos, BlockState> blockMap = StructureHelper.getBlockPosBlockStateMap(server.getOverworld(), identifier, BlockRotation.NONE, false);
                    MayorCategory.BiomeCategory biomeCategory = StructureHelper.getBiomeCategory(identifier);
                    MayorCategory.BuildingCategory buildingCategory = StructureHelper.getBuildingCategory(identifier);
                    Vec3i size = StructureHelper.getStructureSize(server.getOverworld(), identifier);

                    MayorStructure mayorStructure = new MayorStructure(mayorStructureIdentifier, level, biomeCategory, buildingCategory, requiredItemStacks, blockMap, size);

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

        // ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
        // });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!client.isInSingleplayer()) {
                MayorManager.mayorStructureMap.clear();
            }
        });

    }

}
