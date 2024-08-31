package io.fabricatedatelier.mayor.init;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.mixin.content.registry.VillagerEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.*;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Entities {

    public static final RegistryKey<PointOfInterestType> BUILDER_POI_KEY = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Mayor.identifierOf("builder"));

    public static final PointOfInterestType BUILDER_POI = PointOfInterestHelper.register(Mayor.identifierOf("builder"), 1, 1, Blocks.CAMERA_DEBUG);

    public static final VillagerProfession BUILDER = register("builder", entry -> entry.value().equals(BUILDER_POI), entry -> entry.value().equals(BUILDER_POI), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_MASON);

//FindPointOfInterestTask
//    RegistryKey
//    public static final VillagerProfession BUILDER = register("builder", entry -> entry.matchesKey(RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE,Identifier.of("builder"))), entry -> entry.matchesKey(RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE,Identifier.of("builder"))), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_MASON);


//    public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> workStation) {
//        var id = new Identifier(RoguesMod.NAMESPACE, name);
//        return Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(RoguesMod.NAMESPACE, name), new VillagerProfession(
//                id.toString(),
//                (entry) -> {
//                    return entry.matchesKey(workStation);
//                },
//                (entry) -> {
//                    return entry.matchesKey(workStation);
//                },
//                ImmutableSet.of(),
//                ImmutableSet.of(),
//                SoundHelper.WORKBENCH)
//        );
//    }


//    VillagerType.PLAINS

    private static VillagerProfession register(String id, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Mayor.identifierOf(id), new VillagerProfession(id, heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound));
    }


//    return PointOfInterestHelper.register(new Identifier(RoguesMod.NAMESPACE, name),
//                1, 10, ImmutableSet.copyOf(block.getStateManager().getStates()));

//    private static RegistryKey<PointOfInterestType> of(String id) {
//        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Mayor.identifierOf(id));
//    }
//
//    private static PointOfInterestType register(
//            Registry<PointOfInterestType> registry, RegistryKey<PointOfInterestType> key, Set<BlockState> states, int ticketCount, int searchDistance
//    ) {
//        PointOfInterestType pointOfInterestType = new PointOfInterestType(states, ticketCount, searchDistance);
//        Registry.register(registry, key, pointOfInterestType);
//        registerStates(registry.entryOf(key), states);
//        return pointOfInterestType;
//    }
//
//    private static void registerStates(RegistryEntry<PointOfInterestType> poiTypeEntry, Set<BlockState> states) {
//        states.forEach(state -> {
//            RegistryEntry<PointOfInterestType> registryEntry2 = (RegistryEntry<PointOfInterestType>)POI_STATES_TO_TYPE.put(state, poiTypeEntry);
//            if (registryEntry2 != null) {
//                throw (IllegalStateException) Util.throwOrPause(new IllegalStateException(String.format(Locale.ROOT, "%s is defined in more than one PoI type", state)));
//            }
//        });
//    }

    public static void initialize() {
        // static initialisation

        Iterator<VillagerProfession> iterator = Registries.VILLAGER_PROFESSION.stream().iterator();
        while (iterator.hasNext()) {
            VillagerProfession villagerProfession = iterator.next();
//            System.out.println(villagerProfession.acquirableWorkstation().test(Registries.POINT_OF_INTEREST_TYPE.getEntry(BUILDER_POI))+ " : "+villagerProfession.id());
        }

        TradeOfferHelper.registerVillagerOffers(BUILDER, 1, factories -> {
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(net.minecraft.item.Items.DIAMOND, 5), new ItemStack(Items.GLOW_ITEM_FRAME), 3, 4, 0.15F)));
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(Items.SADDLE, 5), new ItemStack(Items.DANDELION), 3, 4, 0.15F)));
        });
        TradeOfferHelper.registerVillagerOffers(BUILDER, 2, factories -> {
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(net.minecraft.item.Items.DIAMOND, 5), new ItemStack(Items.GLOW_ITEM_FRAME), 3, 4, 0.15F)));
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(Items.SADDLE, 5), new ItemStack(Items.DANDELION), 3, 4, 0.15F)));
        });
        // Todo: FindPointOfInterestTask
        // Todo: ServerWorld getPointOfInterestStorage()
//        public PointOfInterestStorage getPointOfInterestStorage() {
//            return this.getChunkManager().getPointOfInterestStorage();
//        }

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world instanceof ServerWorld serverWorld) {
                System.out.println(serverWorld.getPointOfInterestStorage()+ " : "+PointOfInterestTypes.isPointOfInterest(player.getSteppingBlockState())+ " : "+player.getSteppingBlockState());

//                Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> set = (Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>>)serverWorld.getPointOfInterestStorage().getSortedTypesAndPositions(
//                                poiPredicate, predicate2, player.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE
//                        )
//                        .limit(5L)
//                        .collect(Collectors.toSet());
            }
            return TypedActionResult.pass(player.getMainHandStack());
        });


//        TradeOfferHelper.

//        VillagerEntity.POINTS_OF_INTEREST.forEach((a,b)->{
//            System.out.println(b);
//        });
//        VillagerEntityAccessor.fabric_setGatherableItems();
//        System.out.println(VillagerEntity.POINTS_OF_INTEREST);

//        Iterator<PointOfInterestType> iterator = Registries.POINT_OF_INTEREST_TYPE.stream().iterator();
//        while (iterator.hasNext()) {
////            System.out.println(iterator.next());
//        }

//        System.out.println(PointOfInterestTypes.isPointOfInterest(Blocks.CAMERA_DEBUG.getDefaultState())+ " : "+PointOfInterestTypes.getTypeForState(Blocks.CAMERA_DEBUG.getDefaultState()));
    }

    private static class SimpleTradeFactory implements TradeOffers.Factory {
        private final TradeOffer offer;

        SimpleTradeFactory(TradeOffer offer) {
            this.offer = offer;
        }

        @Override
        public TradeOffer create(Entity entity, Random random) {
            // ALWAYS supply a copy of the offer.
            return this.offer.copy();
        }
    }

}
