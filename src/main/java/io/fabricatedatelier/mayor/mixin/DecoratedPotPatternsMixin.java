package io.fabricatedatelier.mayor.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.fabricatedatelier.mayor.init.MayorItems;
import net.minecraft.block.DecoratedPotPattern;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(DecoratedPotPatterns.class)
public class DecoratedPotPatternsMixin {

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Map;ofEntries([Ljava/util/Map$Entry;)Ljava/util/Map;"))
    private static Map<Item, RegistryKey<DecoratedPotPattern>> initMixin(Map<Item, RegistryKey<DecoratedPotPattern>> original) {
        Map<Item, RegistryKey<DecoratedPotPattern>> map = new LinkedHashMap<>(original);
        map.put(MayorItems.BALLOT_POTTERY_SHERD, MayorItems.BALLOT);
        return map;
    }

//    @Inject(method = "fromSherd",at = @At("RETURN"),cancellable = true)
//    private static void fromSherdMixin(Item sherd, CallbackInfoReturnable<RegistryKey<DecoratedPotPattern>> info) {
//        if(info.getReturnValue() == null && sherd.equals(MayorItems.BALLOT_POTTERY_SHERD)){
//            info.setReturnValue(MayorItems.BALLOT);
//        }
//    }


    @Inject(method = "registerAndGetDefault", at = @At("HEAD"))
    private static void registerAndGetDefaultMixin(Registry<DecoratedPotPattern> registry, CallbackInfoReturnable<DecoratedPotPattern> info) {
        Registry.register(registry, MayorItems.BALLOT, new DecoratedPotPattern(Identifier.ofVanilla("ballot_pottery_pattern")));
    }

}
