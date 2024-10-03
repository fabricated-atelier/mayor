package io.fabricatedatelier.mayor.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.fabricatedatelier.mayor.init.MayorPotPatterns;
import net.minecraft.block.DecoratedPotPattern;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DecoratedPotPatterns.class)
public class DecoratedPotPatternsMixin {
    @ModifyReturnValue(method = "fromSherd", at = @At("RETURN"))
    private static @Nullable RegistryKey<DecoratedPotPattern> appendCustomPatterns(@Nullable RegistryKey<DecoratedPotPattern> original, Item sherd) {
        MayorPotPatterns pattern = MayorPotPatterns.fromItem(sherd);
        if (pattern == null) return original;
        return pattern.getRegistryKey();
    }
}
