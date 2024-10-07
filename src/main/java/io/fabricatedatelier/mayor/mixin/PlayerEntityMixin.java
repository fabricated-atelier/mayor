package io.fabricatedatelier.mayor.mixin;

import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.manager.MayorManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements MayorManagerAccess {

    @Unique
    private final MayorManager mayorManager = new MayorManager((PlayerEntity) (Object) this);

    @Override
    public MayorManager getMayorManager() {
        return this.mayorManager;
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (mayorManager.isInMajorView()) {
            // maybe send packet?
            mayorManager.setMajorView(false);
        }
    }

}
