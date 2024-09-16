package io.fabricatedatelier.mayor.datagen;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.datagen.provider.StructurePoolTagProvider;
import io.fabricatedatelier.mayor.init.Blocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class TagProvider {

    public static class BlockTags extends FabricTagProvider.BlockTagProvider {

        public static TagKey<Block> MAYOR_STRUCTURE_EXCLUDED =
                TagKey.of(RegistryKeys.BLOCK, Mayor.identifierOf("mayor_structure_excluded"));

        public static TagKey<Block> MAYOR_STRUCTURE_REPLACEABLE =
                TagKey.of(RegistryKeys.BLOCK, Mayor.identifierOf("mayor_structure_replaceable"));


        public BlockTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(net.minecraft.registry.tag.BlockTags.AXE_MINEABLE)
                    .add(Blocks.LUMBER_STORAGE, Blocks.STONE_STORAGE).setReplace(false);

            getOrCreateTagBuilder(MAYOR_STRUCTURE_EXCLUDED)
                    .add(net.minecraft.block.Blocks.BARRIER)
                    .add(net.minecraft.block.Blocks.BEDROCK)
                    .add(net.minecraft.block.Blocks.COMMAND_BLOCK)
                    .add(net.minecraft.block.Blocks.CHAIN_COMMAND_BLOCK)
                    .add(net.minecraft.block.Blocks.REPEATING_COMMAND_BLOCK)
                    .add(net.minecraft.block.Blocks.STRUCTURE_BLOCK)
                    .add(net.minecraft.block.Blocks.STRUCTURE_VOID)
                    .add(net.minecraft.block.Blocks.END_PORTAL)
                    .add(net.minecraft.block.Blocks.END_GATEWAY)
                    .add(net.minecraft.block.Blocks.MOVING_PISTON);

            getOrCreateTagBuilder(MAYOR_STRUCTURE_REPLACEABLE)
                    .forceAddTag(net.minecraft.registry.tag.BlockTags.DIRT)
                    .forceAddTag(net.minecraft.registry.tag.BlockTags.SNOW)
                    .forceAddTag(net.minecraft.registry.tag.BlockTags.FLOWERS)
                    .add(net.minecraft.block.Blocks.SHORT_GRASS)
                    .add(net.minecraft.block.Blocks.TALL_GRASS)
                    .add(net.minecraft.block.Blocks.FERN)
                    .add(net.minecraft.block.Blocks.LARGE_FERN);
        }
    }

    public static class ItemTags extends FabricTagProvider.ItemTagProvider {

        public static final TagKey<Item> LUMBER_STORAGE_STORABLE =
                TagKey.of(RegistryKeys.ITEM, Mayor.identifierOf("lumber_storage_storable"));

        public static final TagKey<Item> STONE_STORAGE_STORABLE =
                TagKey.of(RegistryKeys.ITEM, Mayor.identifierOf("stone_storage_storable"));

        public static TagKey<Item> MAYOR_STRUCTURE_EXCLUDED =
                TagKey.of(RegistryKeys.ITEM, Mayor.identifierOf("mayor_structure_excluded"));


        public ItemTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
            super(output, completableFuture, null);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(LUMBER_STORAGE_STORABLE)
                    .forceAddTag(net.minecraft.registry.tag.ItemTags.LOGS)
                    .forceAddTag(net.minecraft.registry.tag.ItemTags.PLANKS)
                    .addOptionalTag(Identifier.of("c", "logs"))
                    .addOptionalTag(Identifier.of("c", "planks"))
                    .addOptionalTag(Identifier.of("c", "planks_that_burn"));

            getOrCreateTagBuilder(STONE_STORAGE_STORABLE)
                    .forceAddTag(net.minecraft.registry.tag.ItemTags.STONE_CRAFTING_MATERIALS)
                    .forceAddTag(net.minecraft.registry.tag.ItemTags.SAND)
                    .forceAddTag(net.minecraft.registry.tag.ItemTags.STONE_BRICKS)
                    .addOptionalTag(Identifier.of("c", "ores"))
                    .addOptionalTag(Identifier.of("c", "stone"))
                    .addOptionalTag(Identifier.of("c", "stones"))
                    .addOptionalTag(Identifier.of("c", "cobblestone"))
                    .addOptionalTag(Identifier.of("c", "cobblestones"))
                    .addOptionalTag(Identifier.of("c", "concrete"))
                    .addOptionalTag(Identifier.of("c", "concretes"))
                    .addOptionalTag(Identifier.of("c", "concrete_powder"))
                    .addOptionalTag(Identifier.of("c", "concrete_powders"));

            getOrCreateTagBuilder(MAYOR_STRUCTURE_EXCLUDED)
                    .add(net.minecraft.block.Blocks.BARRIER.asItem())
                    .add(net.minecraft.block.Blocks.BEDROCK.asItem())
                    .add(net.minecraft.block.Blocks.COMMAND_BLOCK.asItem())
                    .add(net.minecraft.block.Blocks.REPEATING_COMMAND_BLOCK.asItem())
                    .add(net.minecraft.block.Blocks.CHAIN_COMMAND_BLOCK.asItem())
                    .add(net.minecraft.block.Blocks.STRUCTURE_BLOCK.asItem())
                    .add(net.minecraft.block.Blocks.JIGSAW.asItem())
                    .add(net.minecraft.block.Blocks.STRUCTURE_VOID.asItem())
                    .add(net.minecraft.block.Blocks.AIR.asItem());
        }
    }

    public static class EntityTags extends FabricTagProvider.EntityTypeTagProvider {

        public EntityTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        }
    }

    public static class FluidTags extends FabricTagProvider.FluidTagProvider {
        public FluidTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

        }
    }

    public static class StructurePoolTags extends StructurePoolTagProvider {

        public static TagKey<StructurePool> VILLAGES =
                TagKey.of(RegistryKeys.TEMPLATE_POOL, Mayor.identifierOf("villages"));

        public StructurePoolTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(VILLAGES)
                    .add(PlainsVillageData.TOWN_CENTERS_KEY)
                    .add(SnowyVillageData.TOWN_CENTERS_KEY)
                    .add(SavannaVillageData.TOWN_CENTERS_KEY)
                    .add(DesertVillageData.TOWN_CENTERS_KEY)
                    .add(TaigaVillageData.TOWN_CENTERS_KEY);
        }
    }

    public static void registerAll(FabricDataGenerator.Pack pack) {
        pack.addProvider(BlockTags::new);
        pack.addProvider(ItemTags::new);
        pack.addProvider(EntityTags::new);
        pack.addProvider(FluidTags::new);
        pack.addProvider(StructurePoolTags::new);
    }
}
