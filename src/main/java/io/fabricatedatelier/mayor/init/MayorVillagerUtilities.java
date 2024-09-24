package io.fabricatedatelier.mayor.init;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class MayorVillagerUtilities {

    public static final RegistryKey<PointOfInterestType> BUILDER_POI_KEY = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Mayor.identifierOf("builder"));

    public static final PointOfInterestType BUILDER_POI = PointOfInterestHelper.register(Mayor.identifierOf("builder"), 1, 1, MayorBlocks.CONSTRUCTION_TABLE);

    public static final VillagerProfession BUILDER = register("builder", entry -> entry.value().equals(BUILDER_POI), entry -> entry.value().equals(BUILDER_POI), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_MASON);

    private static VillagerProfession register(String id, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Mayor.identifierOf(id), new VillagerProfession(id, heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound));
    }

//    public static final Activity BUILDING =  Registry.register(Registries.ACTIVITY, Mayor.identifierOf("building"), new Activity("building"));

    // Can't use this cause MEMORY_MODULES in VillagerEntity class is final and immutable except with non compat mixin
//    public static final MemoryModuleType<Boolean> SHOULD_DUMP = register("should_dump", Codec.BOOL);

    private static <U> MemoryModuleType<U> register(String id, Codec<U> codec) {
        return Registry.register(Registries.MEMORY_MODULE_TYPE, Mayor.identifierOf(id), new MemoryModuleType<>(Optional.of(codec)));
    }

//    VillagerClothingFeatureRenderer
//    public static final RegistryKey<PointOfInterestType> BUILDER_POI_KEY = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE,  Identifier.ofVanilla("builder"));
//
//    public static final PointOfInterestType BUILDER_POI = PointOfInterestHelper.register( Identifier.ofVanilla("builder"), 1, 1, Blocks.CAMERA_DEBUG);
//
//    public static final VillagerProfession BUILDER = register("builder", entry -> entry.value().equals(BUILDER_POI), entry -> entry.value().equals(BUILDER_POI), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_MASON);
//
//    private static VillagerProfession register(String id, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
//        return Registry.register(Registries.VILLAGER_PROFESSION, Identifier.ofVanilla(id), new VillagerProfession(id, heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound));
//    }


    public static void initialize() {
        // static initialisation

//        Iterator<VillagerProfession> iterator = Registries.VILLAGER_PROFESSION.stream().iterator();
//        while (iterator.hasNext()) {
//            VillagerProfession villagerProfession = iterator.next();
////            System.out.println(villagerProfession.acquirableWorkstation().test(Registries.POINT_OF_INTEREST_TYPE.getEntry(BUILDER_POI))+ " : "+villagerProfession.id());
//        }

        TradeOfferHelper.registerVillagerOffers(BUILDER, 1, factories -> {
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(net.minecraft.item.Items.DIAMOND, 5), new ItemStack(net.minecraft.item.Items.GLOW_ITEM_FRAME), 3, 4, 0.15F)));
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(net.minecraft.item.Items.SADDLE, 5), new ItemStack(net.minecraft.item.Items.DANDELION), 3, 4, 0.15F)));
        });
        TradeOfferHelper.registerVillagerOffers(BUILDER, 2, factories -> {
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(net.minecraft.item.Items.DIAMOND, 5), new ItemStack(net.minecraft.item.Items.GLOW_ITEM_FRAME), 3, 4, 0.15F)));
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(net.minecraft.item.Items.SADDLE, 5), new ItemStack(Items.DANDELION), 3, 4, 0.15F)));
        });
//        public PointOfInterestStorage getPointOfInterestStorage() {
//            return this.getChunkManager().getPointOfInterestStorage();
//        }

//        FindPointOfInterestTask
//        System.out.println(Registries.VILLAGER_PROFESSION.getId(BUILDER));

//        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
//            if(entity instanceof  VillagerEntity villagerEntity){
//                System.out.println(villagerEntity.getVillagerData().getProfession());
//                if(!world.isClient()){
//                    villagerEntity.setVillagerData(new VillagerData(villagerEntity.getVillagerData().getType(),BUILDER,2));
//
//                    System.out.println(villagerEntity.getVillagerData().getProfession());
//                }
//            }
//            return ActionResult.PASS;
//        });
//
//        UseItemCallback.EVENT.register((player, world, hand) -> {
//            if (world instanceof ServerWorld serverWorld) {
//                System.out.println(serverWorld.getPointOfInterestStorage() + " : " + PointOfInterestTypes.isPointOfInterest(player.getSteppingBlockState()) + " : " + player.getSteppingBlockState());
//
////                Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> set = (Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>>)serverWorld.getPointOfInterestStorage().getSortedTypesAndPositions(
////                                poiPredicate, predicate2, player.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE
////                        )
////                        .limit(5L)
////                        .collect(Collectors.toSet());
//            }
//            return TypedActionResult.pass(player.getMainHandStack());
//        });


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
