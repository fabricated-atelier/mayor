package io.fabricatedatelier.mayor.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.entity.villager.access.BuilderInventory;
import io.fabricatedatelier.mayor.entity.villager.task.BuilderTaskListProvider;
import io.fabricatedatelier.mayor.init.MayorVillagerUtilities;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.StateHelper;
import io.fabricatedatelier.mayor.util.VillageHelper;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements Builder {

    @Nullable
    @Unique
    private BlockPos villageCenterPos = null;
    @Unique
    private static final TrackedData<BlockPos> TARGET_POS = DataTracker.registerData(VillagerEntityMixin.class, TrackedDataHandlerRegistry.BLOCK_POS);
    @Unique
    private static final TrackedData<ItemStack> CARRY_ITEM_STACK = DataTracker.registerData(VillagerEntityMixin.class, TrackedDataHandlerRegistry.ITEM_STACK);

    /*
     * 0: Nothing
     * 1: Front Carry Task
     * 2: Breaking Task
     * */
    @Unique
    private static final TrackedData<Integer> TASK_VALUE = DataTracker.registerData(VillagerEntityMixin.class, TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private BuilderInventory builderInventory = new BuilderInventory(2);


    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initDataTrackerMixin(DataTracker.Builder builder, CallbackInfo info) {
        builder.add(TARGET_POS, BlockPos.ORIGIN);
        builder.add(CARRY_ITEM_STACK, ItemStack.EMPTY);
        builder.add(TASK_VALUE, 0);
    }


    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        writeBuilderInventory(nbt, this.getRegistryManager());
        if (this.villageCenterPos != null) {
            nbt.put("VillageCenterPos", NbtHelper.fromBlockPos(this.villageCenterPos));
        }
        nbt.put("TargetPosition", NbtHelper.fromBlockPos(this.dataTracker.get(TARGET_POS)));
        if (!this.dataTracker.get(CARRY_ITEM_STACK).isEmpty()) {
            nbt.put("CarryItemStack", this.dataTracker.get(CARRY_ITEM_STACK).encode(this.getRegistryManager()));
        }
        nbt.putInt("TaskValue", this.dataTracker.get(TASK_VALUE));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.builderInventory = new BuilderInventory(this.getVillagerEntity().getVillagerData().getLevel() + 1);

        readBuilderInventory(nbt, this.getRegistryManager());
        this.villageCenterPos = NbtHelper.toBlockPos(nbt, "VillageCenterPos").orElse(null);
        this.dataTracker.set(TARGET_POS, NbtHelper.toBlockPos(nbt, "TargetPosition").orElse(BlockPos.ORIGIN));

        if (nbt.contains("CarryItemStack", NbtElement.COMPOUND_TYPE)) {
            this.dataTracker.set(CARRY_ITEM_STACK, ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("CarryItemStack")).orElse(ItemStack.EMPTY));
        } else {
            this.dataTracker.set(CARRY_ITEM_STACK, ItemStack.EMPTY);
        }
        this.dataTracker.set(TASK_VALUE, nbt.getInt("TaskValue"));
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeathMixin(DamageSource damageSource, CallbackInfo info) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            if (serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                ItemScatterer.spawn(serverWorld, this.getBlockPos(), this.builderInventory);
            }
            VillageHelper.updateBuildingVillagerBuilder(serverWorld, this, false);
        }
    }

    @Inject(method = "initialize", at = @At("TAIL"))
    private void initializeMixin(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> info) {
        VillageData villageData = StateHelper.getClosestVillage(world.toServerWorld(), this.getBlockPos());
        if (villageData != null) {
            villageData.addVillager(this.getUuid());
            StateHelper.getMayorVillageState(world.toServerWorld()).markDirty();
        }
    }

    @Inject(method = "summonGolem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LargeEntitySpawnHelper;trySpawnAt(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;IIILnet/minecraft/entity/LargeEntitySpawnHelper$Requirements;)Ljava/util/Optional;", shift = At.Shift.AFTER))
    private void summonGolemMixin(ServerWorld world, long time, int requiredCount, CallbackInfo info) {
        List<IronGolemEntity> list = world.getEntitiesByClass(IronGolemEntity.class, Box.of(this.getBlockPos().toCenterPos(), 30, 30, 30), EntityPredicates.EXCEPT_SPECTATOR);
        if (!list.isEmpty()) {
            VillageData villageData = StateHelper.getClosestVillage(world, this.getBlockPos());
            if (villageData != null) {
                boolean foundNewIronGolem = false;
                for (IronGolemEntity ironGolemEntity : list) {
                    if (!villageData.getIronGolems().contains(ironGolemEntity.getUuid())) {
                        villageData.getIronGolems().add(ironGolemEntity.getUuid());
                        foundNewIronGolem = true;
                    }
                }
                if (foundNewIronGolem) {
                    StateHelper.getMayorVillageState(world).markDirty();
                }
            }
        }
    }

    @WrapOperation(method = "initBrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;setTaskList(Lnet/minecraft/entity/ai/brain/Activity;Lcom/google/common/collect/ImmutableList;Ljava/util/Set;)V", ordinal = 0))
    private void initBrainMixin(Brain<VillagerEntity> instance, Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super VillagerEntity>>>
            indexedTasks, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemories, Operation<Void> original) {
        if (this.getVillagerData().getProfession().equals(MayorVillagerUtilities.BUILDER)) {
            instance.setTaskList(Activity.WORK, BuilderTaskListProvider.createBuildingTasks(), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT)));
        } else {
            original.call(instance, activity, indexedTasks, requiredMemories);
        }
    }

    @Inject(method = "setVillagerData", at = @At("HEAD"))
    private void setVillagerDataMixin(VillagerData villagerData, CallbackInfo info) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            VillagerData villagerData3 = this.getVillagerData();
            if (villagerData3.getProfession().equals(VillagerProfession.NONE)) {
                if (villagerData.getProfession().equals(MayorVillagerUtilities.BUILDER)) {
                    // acquire builder job
                    VillageHelper.updateBuildingVillagerBuilder(serverWorld, this, true);
                }
            } else if (villagerData3.getProfession().equals(MayorVillagerUtilities.BUILDER)) {
                if (!villagerData.getProfession().equals(MayorVillagerUtilities.BUILDER)) {
                    // loose builder job
                    VillageHelper.updateBuildingVillagerBuilder(serverWorld, this, false);
                } else {
                    // level up
                    BuilderInventory newBuilderInventory = new BuilderInventory(this.builderInventory.size() + 1);
                    for (int i = 0; i < this.builderInventory.size(); i++) {
                        newBuilderInventory.addStack(this.builderInventory.getStack(i));
                    }
                    this.builderInventory = newBuilderInventory;
                }
            }
        }
    }

    @Inject(method = "sleep", at = @At("TAIL"))
    private void sleepMixin(BlockPos pos, CallbackInfo info) {
        this.brain.forget(MayorVillagerUtilities.BUSY);
        setTaskValue(0);
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;beginTradeWith(Lnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true)
    private void interactMobMixin(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if (this.brain.hasMemoryModule(MayorVillagerUtilities.BUSY)) {
            this.sayNo();
            info.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "reinitializeBrain", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/Brain;stopAllTasks(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)V"))
    private void reinitializeBrainMixin(ServerWorld world, CallbackInfo info) {
        this.brain.forget(MayorVillagerUtilities.BUSY);
        setTaskValue(0);
    }

/*    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;", ordinal = 0))
    private static ImmutableList<MemoryModuleType<?>> initMixin(ImmutableList<MemoryModuleType<?>> original) {
        List<MemoryModuleType<?>> list = new ArrayList<>(original);
        list.add(MayorVillagerUtilities.BUSY);
        return ImmutableList.copyOf(list);
    }*/
    @WrapOperation(method = "createBrainProfile",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ai/brain/Brain;createProfile(Ljava/util/Collection;Ljava/util/Collection;)Lnet/minecraft/entity/ai/brain/Brain$Profile;")
    )
    private <E extends LivingEntity> Brain.Profile<E> addMemoryModules(Collection<? extends MemoryModuleType<?>> memoryModules,
                                                                       Collection<? extends SensorType<? extends Sensor<? super E>>> sensors,
                                                                       Operation<Brain.Profile<E>> original) {
        List<MemoryModuleType<?>> memoryModuleTypes = new ArrayList<>(memoryModules);
        memoryModuleTypes.add(MayorVillagerUtilities.BUSY);
        return original.call(ImmutableList.copyOf(memoryModuleTypes), sensors);
    }


    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    protected abstract void sayNo();

    @Override
    public BlockPos getVillageCenterPosition() {
        return this.villageCenterPos;
    }

    @Override
    public void setVillageCenterPosition(BlockPos villageCenterPosition) {
        this.villageCenterPos = villageCenterPosition;
    }

    @Override
    public boolean hasTargetPosition() {
        return !this.dataTracker.get(TARGET_POS).equals(BlockPos.ORIGIN);
    }

    @Override
    public BlockPos getTargetPosition() {
        return this.dataTracker.get(TARGET_POS);
    }

    @Override
    public void setTargetPosition(@Nullable BlockPos targetPosition) {
        this.dataTracker.set(TARGET_POS, targetPosition != null ? targetPosition : BlockPos.ORIGIN);
    }

    @Override
    public ItemStack getCarryItemStack() {
        return this.dataTracker.get(CARRY_ITEM_STACK);
    }

    @Override
    public void setCarryItemStack(ItemStack itemStack) {
        this.dataTracker.set(CARRY_ITEM_STACK, itemStack);
        if (!itemStack.isEmpty()) {
            setTaskValue(this.getWorld().getRandom().nextInt(2));
        } else {
            setTaskValue(0);
        }
    }

    @Override
    public int getTaskValue() {
        return this.dataTracker.get(TASK_VALUE);
    }

    @Override
    public void setTaskValue(int taskValue) {
        this.dataTracker.set(TASK_VALUE, taskValue);
    }

    @Override
    public BuilderInventory getBuilderInventory() {
        return this.builderInventory;
    }

    @Override
    public VillagerEntity getVillagerEntity() {
        return (VillagerEntity) (Object) this;
    }
}
