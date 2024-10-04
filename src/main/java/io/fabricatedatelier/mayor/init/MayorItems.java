package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.item.BallotPaperItem;
import io.fabricatedatelier.mayor.item.DeconstructionHammerItem;
import io.fabricatedatelier.mayor.item.StorageBlockItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MayorItems {
    public static final Item LUMBER_STORAGE_BLOCK = register("lumber_storage_block",
            new StorageBlockItem(MayorBlocks.LUMBER_STORAGE, new Item.Settings()), List.of(MayorItemGroups.MAYOR_BLOCKS));

    public static final Item STONE_STORAGE_BLOCK = register("stone_storage_block",
            new StorageBlockItem(MayorBlocks.STONE_STORAGE, new Item.Settings()), List.of(MayorItemGroups.MAYOR_BLOCKS));

    public static final Item CONSTRUCTION_TABLE = register("construction_table", new BlockItem(MayorBlocks.CONSTRUCTION_TABLE, new Item.Settings()), List.of(MayorItemGroups.MAYOR_ITEMS));

    public static final Item DECONSTRUCTION_HAMMER = register("deconstruction_hammer",
            new DeconstructionHammerItem(ToolMaterials.IRON,
                    new Item.Settings().attributeModifiers(PickaxeItem.createAttributeModifiers(ToolMaterials.IRON, 1.0F, -2.8F))
                            .component(DataComponentTypes.TOOL, ToolMaterials.IRON.createComponent(BlockTags.PICKAXE_MINEABLE))), List.of(MayorItemGroups.MAYOR_ITEMS));

    public static final Item BALLOT_POTTERY_SHERD = register("ballot_pottery_sherd", new Item(new Item.Settings()), List.of(MayorItemGroups.MAYOR_ITEMS));

    public static final Item BALLOT_PAPER = register("ballot_paper", new BallotPaperItem(new Item.Settings()), List.of(MayorItemGroups.MAYOR_ITEMS));


    private static <T extends Item> T register(String name, T item, @Nullable List<MayorItemGroups.ItemGroupEntry> itemGroups) {
        Registry.register(Registries.ITEM, Mayor.identifierOf(name), item);
        if (itemGroups != null) {
            for (var entry : itemGroups) {
                entry.addItems(item);
            }
        }
        return item;
    }

    public static void initialize() {
        // static initialisation
    }
}
