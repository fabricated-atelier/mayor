package io.fabricatedatelier.mayor.init;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.Unit;
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
    public static final RegistryKey<PointOfInterestType> LUMBERJACK_POI_KEY = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Mayor.identifierOf("lumberjack"));
    public static final RegistryKey<PointOfInterestType> MINER_POI_KEY = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Mayor.identifierOf("miner"));

    public static final PointOfInterestType BUILDER_POI = PointOfInterestHelper.register(Mayor.identifierOf("builder"), 1, 1, MayorBlocks.CONSTRUCTION_TABLE);
    public static final PointOfInterestType LUMBERJACK_POI = PointOfInterestHelper.register(Mayor.identifierOf("lumberjack"), 1, 1, MayorBlocks.WOODCUTTER);
    public static final PointOfInterestType MINER_POI = PointOfInterestHelper.register(Mayor.identifierOf("miner"), 1, 1, MayorBlocks.MINER_TABLE);

    public static final VillagerProfession BUILDER = register("builder", entry -> entry.value().equals(BUILDER_POI), entry -> entry.value().equals(BUILDER_POI), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_MASON);
    public static final VillagerProfession LUMBERJACK = register("lumberjack", entry -> entry.value().equals(LUMBERJACK_POI), entry -> entry.value().equals(LUMBERJACK_POI), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_MASON);
    public static final VillagerProfession MINER = register("miner", entry -> entry.value().equals(MINER_POI), entry -> entry.value().equals(MINER_POI), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_MASON);

    private static VillagerProfession register(String id, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Mayor.identifierOf(id), new VillagerProfession(id, heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound));
    }

    // public static final Activity BUILDING =  Registry.register(Registries.ACTIVITY, Mayor.identifierOf("building"), new Activity("building"));

    public static final MemoryModuleType<Unit> BUSY = register("busy", Unit.CODEC);

    private static <U> MemoryModuleType<U> register(String id, Codec<U> codec) {
        return Registry.register(Registries.MEMORY_MODULE_TYPE, Mayor.identifierOf(id), new MemoryModuleType<>(Optional.of(codec)));
    }

    public static void initialize() {
        TradeOfferHelper.registerVillagerOffers(BUILDER, 1, factories -> {
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(net.minecraft.item.Items.DIAMOND, 5), new ItemStack(net.minecraft.item.Items.GLOW_ITEM_FRAME), 3, 4, 0.15F)));
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(net.minecraft.item.Items.SADDLE, 5), new ItemStack(net.minecraft.item.Items.DANDELION), 3, 4, 0.15F)));
        });
        TradeOfferHelper.registerVillagerOffers(BUILDER, 2, factories -> {
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(net.minecraft.item.Items.DIAMOND, 5), new ItemStack(net.minecraft.item.Items.GLOW_ITEM_FRAME), 3, 4, 0.15F)));
            factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(net.minecraft.item.Items.SADDLE, 5), new ItemStack(Items.DANDELION), 3, 4, 0.15F)));
        });
    }

    private record SimpleTradeFactory(TradeOffer offer) implements TradeOffers.Factory {
        @Override
        public TradeOffer create(Entity entity, Random random) {
            return this.offer.copy();
        }
    }

}
