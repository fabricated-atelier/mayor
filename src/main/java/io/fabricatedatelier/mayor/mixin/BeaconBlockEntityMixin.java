package io.fabricatedatelier.mayor.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.StateHelper;
import io.fabricatedatelier.mayor.util.VillageHelper;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {

    @Inject(method = "applyPlayerEffects", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getNonSpectatingEntities(Ljava/lang/Class;Lnet/minecraft/util/math/Box;)Ljava/util/List;", shift = At.Shift.AFTER))
    private static void applyPlayerEffectsMixin(World world, BlockPos pos, int beaconLevel, @Nullable RegistryEntry<StatusEffect> primaryEffect, @Nullable RegistryEntry<StatusEffect> secondaryEffect, CallbackInfo info, @Local() LocalRef<List<PlayerEntity>> list) {
        if (!world.isClient()) {
            VillageData villageData = StateHelper.getClosestVillage((ServerWorld) world, pos);
            if (villageData != null) {
                double expand;
                if (beaconLevel < 4) {
                    expand = VillageHelper.VILLAGE_LEVEL_RADIUS.get(beaconLevel);
                } else {
                    expand = VillageHelper.VILLAGE_LEVEL_RADIUS.get(VillageHelper.VILLAGE_LEVEL_RADIUS.size() - 1);
                }

                Box box = new Box(villageData.getCenterPos()).expand(expand).stretch(0.0, world.getHeight(), 0.0);

                List<PlayerEntity> playerList = world.getNonSpectatingEntities(PlayerEntity.class, box);
                List<PlayerEntity> newPlayerList = new ArrayList<>();
                for (PlayerEntity playerEntity : playerList) {
                    if (villageData.getCitizens().contains(playerEntity.getUuid()) && villageData.getCenterPos().isWithinDistance(playerEntity.getBlockPos(), expand)) {
                        newPlayerList.add(playerEntity);
                    }
                }
                list.set(newPlayerList);
            }
        }
    }
}
