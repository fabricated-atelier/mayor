package io.fabricatedatelier.mayor.datagen;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.init.MayorBlocks;
import io.fabricatedatelier.mayor.init.MayorItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class TagProvider {
    public static class BlockTags extends FabricTagProvider.BlockTagProvider {
        public static final TagKey<Block> DECONSTRUCTION_HAMMER_BLOCKS =
                TagKey.of(RegistryKeys.BLOCK, Mayor.identifierOf("deconstruction_hammer_blocks"));

        public BlockTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(net.minecraft.registry.tag.BlockTags.AXE_MINEABLE)
                    .add(MayorBlocks.LUMBER_STORAGE, MayorBlocks.STONE_STORAGE).setReplace(false);
            getOrCreateTagBuilder(DECONSTRUCTION_HAMMER_BLOCKS)
                    .add(Blocks.COBBLESTONE, Blocks.BLACKSTONE, Blocks.COBBLED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE_SLAB,
                            Blocks.COBBLED_DEEPSLATE_STAIRS, Blocks.COBBLED_DEEPSLATE_WALL, Blocks.COBBLESTONE_SLAB,
                            Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE_WALL, Blocks.INFESTED_COBBLESTONE,
                            Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_STAIRS,
                            Blocks.MOSSY_COBBLESTONE_WALL, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE_STAIRS,
                            Blocks.BLACKSTONE_WALL, Blocks.GLASS_PANE, Blocks.GLASS)
                    .forceAddTag(net.minecraft.registry.tag.BlockTags.IMPERMEABLE);
        }
    }

    public static class ItemTags extends FabricTagProvider.ItemTagProvider {
        public static final TagKey<Item> LUMBER_STORAGE_STORABLE =
                TagKey.of(RegistryKeys.ITEM, Mayor.identifierOf("lumber_storage_storable"));
        public static final TagKey<Item> STONE_STORAGE_STORABLE =
                TagKey.of(RegistryKeys.ITEM, Mayor.identifierOf("stone_storage_storable"));
        public static final TagKey<Item> CARRIABLE =
                TagKey.of(RegistryKeys.ITEM, Mayor.identifierOf("carriable"));

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

            getOrCreateTagBuilder(CARRIABLE)
                    .forceAddTag(net.minecraft.registry.tag.ItemTags.LOGS)
                    .forceAddTag(net.minecraft.registry.tag.ItemTags.PLANKS)
                    .forceAddTag(net.minecraft.registry.tag.ItemTags.STONE_BRICKS)
                    .forceAddTag(net.minecraft.registry.tag.ItemTags.STONE_CRAFTING_MATERIALS)
                    .addOptionalTag(Identifier.of("c", "logs"))
                    .addOptionalTag(Identifier.of("c", "planks"))
                    .addOptionalTag(Identifier.of("c", "planks_that_burn"));

            getOrCreateTagBuilder(net.minecraft.registry.tag.ItemTags.PICKAXES).add(MayorItems.DECONSTRUCTION_HAMMER);

            getOrCreateTagBuilder(net.minecraft.registry.tag.ItemTags.DECORATED_POT_SHERDS).add(MayorItems.BALLOT_POTTERY_SHERD);
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

    public static void registerAll(FabricDataGenerator.Pack pack) {
        pack.addProvider(BlockTags::new);
        pack.addProvider(ItemTags::new);
        pack.addProvider(EntityTags::new);
        pack.addProvider(FluidTags::new);
    }
}
