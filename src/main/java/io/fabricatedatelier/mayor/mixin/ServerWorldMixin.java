package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.access.MayorVillageStateAccess;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements MayorVillageStateAccess {

    @Unique
    private MayorVillageState mayorVillageState;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void initMixin(CallbackInfo info) {
        this.mayorVillageState = this.getPersistentStateManager().getOrCreate(MayorVillageState.getPersistentStateType((ServerWorld) (Object) this), "villages");
    }

    @Override
    public MayorVillageState getMayorVillageState() {
        return this.mayorVillageState;
    }

    @Shadow
    public PersistentStateManager getPersistentStateManager() {
        return null;
    }

}
