package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.*;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("Convert2MethodRef")  // method references instead of lambdas cause issues when lazy initialisation
public class ItemGroups {
    public static final ItemGroupEntry MAYOR_ITEMS = new ItemGroupEntry("items", () -> Items.STICK);
    public static final ItemGroupEntry MAYOR_BLOCKS = new ItemGroupEntry("blocks", () -> Blocks.CAMERA_DEBUG.asItem());

    public static void initialize() {
        ItemGroupEntry.ALL_GROUPS.forEach(entry -> entry.register());
    }

    public record ItemGroupEntry(String name, Supplier<ItemConvertible> icon, List<ItemConvertible> items) {
        public static List<ItemGroupEntry> ALL_GROUPS = new ArrayList<>();

        public ItemGroupEntry(String name, Supplier<ItemConvertible> icon) {
            this(name, icon, new ArrayList<>());
            ALL_GROUPS.add(this);
        }

        public void addItems(ItemConvertible... items) {
            this.items.addAll(List.of(items));
        }

        public RegistryKey<ItemGroup> register() {
            Text displayName = Text.translatable("itemgroup.%s.%s".formatted(Mayor.MODID, this.name));
            ItemGroup itemGroup = FabricItemGroup.builder()
                    .icon(() -> new ItemStack(this.icon.get()))
                    .displayName(displayName)
                    .entries((displayContext, entries) -> entries.addAll(items.stream().map(item -> new ItemStack(item)).toList()))
                    .build();
            Registry.register(Registries.ITEM_GROUP, getRegistryKey(), itemGroup);
            return getRegistryKey();
        }

        private RegistryKey<ItemGroup> getRegistryKey() {
            return RegistryKey.of(RegistryKeys.ITEM_GROUP, Mayor.identifierOf(this.name));
        }
    }
}
