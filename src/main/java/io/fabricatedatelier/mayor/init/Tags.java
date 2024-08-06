package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.pool.StructurePool;

public class Tags {
    public static class Items {
        // public static TagKey<Item> EXAMPLE_ITEMS = createTag("example_items");

        @SuppressWarnings("SameParameterValue")
        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Mayor.identifierOf(name));
        }

        private static void register() {
        }
    }

    public static class Blocks {
        // public static TagKey<Block> EXAMPLE_BLOCKS = createTag("example_blocks");

        @SuppressWarnings("SameParameterValue")
        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Mayor.identifierOf(name));
        }

        private static void register() {
        }
    }

    public static class Entities {
        // public static TagKey<EntityType<?>> EXAMPLE_ENTITIES = createTag("example_entities");

        @SuppressWarnings("SameParameterValue")
        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, Mayor.identifierOf(name));
        }

        private static void register() {
        }
    }

    public static class Fluids {
        // public static TagKey<Fluid> EXAMPLE_FLUIDS = createTag("example_fluids");

        @SuppressWarnings("SameParameterValue")
        private static TagKey<Fluid> createTag(String name) {
            return TagKey.of(RegistryKeys.FLUID, Mayor.identifierOf(name));
        }

        private static void register() {
        }
    }

    public static class StructurePools {
         public static TagKey<StructurePool> VILLAGES = createTag("villages");

        @SuppressWarnings("SameParameterValue")
        private static TagKey<StructurePool> createTag(String name) {
            return TagKey.of(RegistryKeys.TEMPLATE_POOL, Mayor.identifierOf(name));
        }

        private static void register() {
        }
    }


    public static void initialize() {
        Items.register();
        Blocks.register();
        Entities.register();
        Fluids.register();
    }
}
