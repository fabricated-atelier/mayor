package io.fabricatedatelier.mayor.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.manager.MayorManager;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements MayorManagerAccess {

    private final MayorManager mayorManager = new MayorManager((PlayerEntity) (Object) this);

    @Override
    public MayorManager getMayorManager() {
        return this.mayorManager;
    }

}
