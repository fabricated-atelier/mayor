package io.fabricatedatelier.mayor.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.entity.villager.task.BuilderTaskListProvider;
import io.fabricatedatelier.mayor.init.VillagerUtilities;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements Builder {


    @Nullable
    @Unique
    private BlockPos villageCenterPos = null;
    @Nullable
    @Unique
    private BlockPos targetPosition = null;
    @Unique
    private final SimpleInventory builderInventory = new SimpleInventory(27);


    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initialize", at = @At("TAIL"))
    private void initializeMixin(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> info) {
        VillageData villageData = MayorStateHelper.getClosestVillage(world.toServerWorld(), this.getBlockPos());
        if (villageData != null) {
            villageData.addVillager(this.getUuid());
            MayorStateHelper.getMayorVillageState(world.toServerWorld()).markDirty();
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
                    MayorStateHelper.getMayorVillageState(world).markDirty();
                }
            }
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        writeBuilderInventory(nbt, this.getRegistryManager());
        if (this.villageCenterPos != null) {
            nbt.put("VillageCenterPos", NbtHelper.fromBlockPos(this.villageCenterPos));
        }
        if (this.targetPosition != null) {
            nbt.put("TargetPosition", NbtHelper.fromBlockPos(this.targetPosition));
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        readBuilderInventory(nbt, this.getRegistryManager());
        this.villageCenterPos = NbtHelper.toBlockPos(nbt, "VillageCenterPos").orElse(null);
        this.targetPosition = NbtHelper.toBlockPos(nbt, "TargetPosition").orElse(null);
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeathMixin(DamageSource damageSource, CallbackInfo info) {
        if (!this.getWorld().isClient()) {
            if (this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                ItemScatterer.spawn(this.getWorld(), this.getBlockPos(), this.builderInventory);
            }
            // Todo: Find new villager to build structure if this villager hat a task to build
        }
    }

    @Inject(method = "initBrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;setTaskList(Lnet/minecraft/entity/ai/brain/Activity;Lcom/google/common/collect/ImmutableList;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void initBrainMixin(Brain<VillagerEntity> brain, CallbackInfo info, VillagerProfession villagerProfession) {
//        brain.setTaskList(Activity.WORK, BuilderTaskListProvider.createBuildingTasks(villagerProfession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT)));
    }


    @WrapOperation(method = "initBrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;setTaskList(Lnet/minecraft/entity/ai/brain/Activity;Lcom/google/common/collect/ImmutableList;)V", ordinal = 1))
    private void initBrainMixin(Brain<VillagerEntity> instance, Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super VillagerEntity>>> indexedTasks, Operation<Void> original) {
        if (this.getVillagerData().getProfession().equals(VillagerUtilities.BUILDER)) {
            instance.setTaskList(Activity.WORK, BuilderTaskListProvider.createBuildingTasks(null, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT)));
        } else {
            original.call(instance, activity, indexedTasks);
        }
    }

    @Shadow
    public abstract VillagerData getVillagerData();

    @Override
    public BlockPos getVillageCenterPosition() {
        return this.villageCenterPos;
    }

    @Override
    public void setVillageCenterPosition(BlockPos villageCenterPosition) {
        this.villageCenterPos = villageCenterPosition;
    }

    @Override
    public BlockPos getTargetPosition() {
        return this.targetPosition;
    }

    @Override
    public void setTargetPosition(BlockPos targetPosition) {
        this.targetPosition = targetPosition;
    }

    @Override
    public SimpleInventory getBuilderInventory() {
        return this.builderInventory;
    }
}
