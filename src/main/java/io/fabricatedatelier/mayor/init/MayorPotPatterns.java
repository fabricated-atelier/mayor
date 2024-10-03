package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import net.minecraft.block.DecoratedPotPattern;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;

public enum MayorPotPatterns {
    BALLOT("ballot", MayorItems.BALLOT_POTTERY_SHERD);


    private final Item item;
    private final RegistryKey<DecoratedPotPattern> registryKey;

    MayorPotPatterns(String name, Item item) {
        this.registryKey = RegistryKey.of(RegistryKeys.DECORATED_POT_PATTERN, Mayor.identifierOf(name));
        this.item = item;
        Registry.register(Registries.DECORATED_POT_PATTERN, this.registryKey,
                new DecoratedPotPattern(Mayor.identifierOf(name + "_pottery_pattern"))
        );
    }

    @Nullable
    public DecoratedPotPattern getPattern() {
        return Registries.DECORATED_POT_PATTERN.get(this.registryKey);
    }

    public RegistryKey<DecoratedPotPattern> getRegistryKey() {
        return this.registryKey;
    }

    public Item getItem() {
        return this.item;
    }

    @Nullable
    public static MayorPotPatterns fromItem(Item item) {
        for (MayorPotPatterns entry : MayorPotPatterns.values()) {
            if (entry.item.equals(item)) return entry;
        }
        return null;
    }

    @Nullable
    public static DecoratedPotPattern getPatternFromItem(Item item) {
        for (MayorPotPatterns entry : MayorPotPatterns.values()) {
            if (entry.item.equals(item)) return Registries.DECORATED_POT_PATTERN.get(entry.registryKey);
        }
        return null;
    }

    public static void initialize() {
        // static initialisation
    }
}
