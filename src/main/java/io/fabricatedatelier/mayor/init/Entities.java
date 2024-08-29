package io.fabricatedatelier.mayor.init;

import com.google.common.collect.ImmutableSet;
import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

public class Entities {

    public static final RegistryKey<PointOfInterestType> BUILDER_POI_KEY = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Mayor.identifierOf("builder"));

    public static final PointOfInterestType BUILDER_POI = PointOfInterestHelper.register(Mayor.identifierOf("builder"), 1, 1, Blocks.CAMERA_DEBUG);

    public static final VillagerProfession BUILDER = register("builder", entry -> entry.matchesKey(BUILDER_POI_KEY), entry -> entry.matchesKey(BUILDER_POI_KEY), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_MASON);

    private static VillagerProfession register(String id, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Mayor.identifierOf(id), new VillagerProfession(id, heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound));
    }


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
    }

}
