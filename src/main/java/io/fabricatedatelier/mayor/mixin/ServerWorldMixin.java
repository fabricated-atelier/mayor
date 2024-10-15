package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.access.MayorVillageStateAccess;
import io.fabricatedatelier.mayor.state.VillageState;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements MayorVillageStateAccess {

    @Unique
    private VillageState villageState;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void initMixin(CallbackInfo info) {
        this.villageState = this.getPersistentStateManager().getOrCreate(VillageState.getPersistentStateType((ServerWorld) (Object) this), "villages");
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        this.villageState.tick();
    }

    @Override
    public VillageState getMayorVillageState() {
        return this.villageState;
    }

    @Shadow
    public PersistentStateManager getPersistentStateManager() {
        return null;
    }

}
