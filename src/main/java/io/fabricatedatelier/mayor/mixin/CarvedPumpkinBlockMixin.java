package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.StateHelper;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinBlockMixin {

    @Inject(method = "trySpawnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/CarvedPumpkinBlock;spawnEntity(Lnet/minecraft/world/World;Lnet/minecraft/block/pattern/BlockPattern$Result;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void trySpawnEntityMixin(World world, BlockPos pos, CallbackInfo info, BlockPattern.Result result, BlockPattern.Result result2, IronGolemEntity ironGolemEntity) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        VillageData villageData = StateHelper.getClosestVillage(serverWorld, pos);
        if (villageData == null) return;
        villageData.getIronGolems().add(ironGolemEntity.getUuid());
        StateHelper.getMayorVillageState(serverWorld).markDirty();
    }
}
