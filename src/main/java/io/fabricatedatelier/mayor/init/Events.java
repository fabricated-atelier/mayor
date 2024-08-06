package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.network.packet.StructureOriginPacket;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.Structures;

import java.util.Optional;

public class Events {

    public static void initialize() {

        UseItemCallback.EVENT.register((PlayerEntity player, World world, Hand hand) -> {
            if (player instanceof ServerPlayerEntity serverPlayerEntity) {

                MayorViewPacket viewPacket;
                if (player.getStackInHand(hand).isOf(Items.STICK)) {
                    // TEST
                    Identifier identifier = Identifier.ofVanilla("village/plains/houses/plains_small_house_7");
                    // TEST END
                    StructureHelper.updateMayorStructure(serverPlayerEntity, identifier, BlockRotation.NONE, false);

                    Optional<BlockHitResult> hitResult = Optional.ofNullable(StructureHelper.findCrosshairTarget(serverPlayerEntity));
                    Optional<BlockPos> origin = hitResult.map(BlockHitResult::getBlockPos);

                    new StructureOriginPacket(origin).sendPacket(serverPlayerEntity);
                    viewPacket = new MayorViewPacket(true);
                } else {
                    viewPacket = new MayorViewPacket(false);
                }
                viewPacket.sendPacket(serverPlayerEntity);
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof VillagerEntity || entity instanceof IronGolemEntity) {
                BlockPos villageCenterPos = MayorStateHelper.getVillageCenterPos((ServerWorld) entity.getWorld(), entity.getBlockPos());
                if (villageCenterPos != null) {
                    MayorStateHelper.updateVillageUuids((ServerWorld) entity.getWorld(), villageCenterPos, entity);
                }
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {

                System.out.println("XTES TETST ");
                System.out.println("XTES TETST "); System.out.println("TES TETST ");
                System.out.println("XTES TETST ");

//            for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(TagInit.ARMOR_ITEMS)) {
//                addRecipe(registry, registryEntry.value());
//                RegistryEntryLookup<StructurePool> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
//            }
//            Optional<RegistryEntryLookup<StructurePool>> optional = server.getRegistryManager().createRegistryLookup().getOptional(RegistryKeys.TEMPLATE_POOL);
//            if(optional.isPresent()){
//                optional.get().getOptional(null);
//            }
        });

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
            if (success) {
                System.out.println("TES TETST ");
                System.out.println("TES TETST "); System.out.println("TES TETST ");
                System.out.println("TES TETST ");

//                for (int i = 0; i < server.getPlayerManager().getPlayerList().size(); i++)
//                    PlayerStatsServerPacket.writeS2CListPacket(server.getPlayerManager().getPlayerList().get(i));
//                LOGGER.info("Finished reload on {}", Thread.currentThread());
            } else {
//                LOGGER.error("Failed to reload on {}", Thread.currentThread());
            }
        });

    }

}
