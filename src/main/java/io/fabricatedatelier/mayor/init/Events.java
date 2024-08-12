package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.camera.CameraHelper;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.StringUtil;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
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

public class Events {

    public static void initialize() {

//        UseItemCallback.EVENT.register((PlayerEntity player, World world, Hand hand) -> {
//            if (player instanceof ServerPlayerEntity serverPlayerEntity) {
//
//                MayorViewPacket viewPacket;
//                if (player.getStackInHand(hand).isOf(Items.STICK)) {
//                    // TEST
//                    Identifier identifier = Identifier.ofVanilla("village/plains/houses/plains_small_house_7");
//                    // TEST END
//                    StructureHelper.updateMayorStructure(serverPlayerEntity, identifier, BlockRotation.NONE, false);
//
//                    Optional<BlockHitResult> hitResult = Optional.ofNullable(StructureHelper.findCrosshairTarget(serverPlayerEntity));
//                    Optional<BlockPos> origin = hitResult.map(BlockHitResult::getBlockPos);
//
//                    new StructureOriginPacket(origin).sendPacket(serverPlayerEntity);
//                    viewPacket = new MayorViewPacket(true);
//                } else {
//                    viewPacket = new MayorViewPacket(false);
//                }
//                viewPacket.sendPacket(serverPlayerEntity);
//            }
//            return TypedActionResult.pass(ItemStack.EMPTY);
//        });

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
//
//            for (RegistryEntry<Biome> registryEntry : registry.iterateEntries(BiomeTags.VILLAGE_DESERT_HAS_STRUCTURE)) {
//            }
//            for (RegistryEntry<Biome> registryEntry : registry.iterateEntries(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE)) {
//            }
//            for (RegistryEntry<Biome> registryEntry : registry.iterateEntries(BiomeTags.VILLAGE_SAVANNA_HAS_STRUCTURE)) {
//            }
//            for (RegistryEntry<Biome> registryEntry : registry.iterateEntries(BiomeTags.VILLAGE_SNOWY_HAS_STRUCTURE)) {
//            }
//            for (RegistryEntry<Biome> registryEntry : registry.iterateEntries(BiomeTags.VILLAGE_TAIGA_HAS_STRUCTURE)) {
//            }


        });

//        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
//        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!client.isInSingleplayer()) {
                MayorManager.mayorStructureMap.clear();
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.world == null) return;

            CameraHelper camera = CameraHelper.getInstance();
            if (camera.hasTarget() && camera.getTransition().isRunning()) {
                camera.getTransition().renderOverlay(drawContext);
            }
        });
    }
}
