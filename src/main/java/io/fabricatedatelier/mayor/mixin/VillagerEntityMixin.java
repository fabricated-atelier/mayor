package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.access.MayorVillageStateAccess;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/VillagerEntity;", at = @At("RETURN"))
    private void createChildMixin(ServerWorld serverWorld, PassiveEntity passiveEntity, CallbackInfoReturnable<VillagerEntity> info) {
        VillageData villageData = MayorStateHelper.getClosestVillage(serverWorld, this.getBlockPos());
        if (villageData != null) {
            villageData.addVillager(info.getReturnValue().getUuid());
            ((MayorVillageStateAccess) serverWorld).getMayorVillageState().markDirty();
        }
    }

    @Inject(method = "summonGolem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LargeEntitySpawnHelper;trySpawnAt(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;IIILnet/minecraft/entity/LargeEntitySpawnHelper$Requirements;)Ljava/util/Optional;", shift = At.Shift.AFTER))
    private void summonGolemMixin(ServerWorld world, long time, int requiredCount, CallbackInfo info) {
        List<IronGolemEntity> list = world.getEntitiesByClass(IronGolemEntity.class, Box.of(this.getBlockPos().toCenterPos(), 30, 30, 30), EntityPredicates.EXCEPT_SPECTATOR);
        if (!list.isEmpty()) {
            VillageData villageData = MayorStateHelper.getClosestVillage(world, this.getBlockPos());
            if (villageData != null) {
                boolean foundNewIronGolem = false;
                for (IronGolemEntity ironGolemEntity : list) {
                    if (!villageData.getIronGolems().contains(ironGolemEntity.getUuid())) {
                        villageData.getIronGolems().add(ironGolemEntity.getUuid());
                        foundNewIronGolem = true;
                    }
                }
                if (foundNewIronGolem) {
                    ((MayorVillageStateAccess) world).getMayorVillageState().markDirty();
                }
            }
        }
    }
}
