package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class ItemGroups {
    public static final RegistryKey<ItemGroup> MAYOR_ITEMS = registerItemGroup("items", () -> Items.TEST_ITEM);
    public static final RegistryKey<ItemGroup> MAYOR_BLOCKS = registerItemGroup("blocks", Blocks.CAMERA_DEBUG::asItem);


    private static RegistryKey<ItemGroup> registerItemGroup(String name, Supplier<Item> icon) {
        Text displayName = Text.translatable("itemgroup.%s.%s".formatted(Mayor.MODID, name));
        ItemGroup itemGroup = FabricItemGroup.builder().icon(() -> new ItemStack(icon.get())).displayName(displayName).build();
        Registry.register(Registries.ITEM_GROUP, getRegistryKey(name), itemGroup);
        return getRegistryKey(name);
    }

    private static RegistryKey<ItemGroup> getRegistryKey(String name) {
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, Mayor.identifierOf(name));
    }

    public static void initialize() {
        // static initialisation
    }
}
