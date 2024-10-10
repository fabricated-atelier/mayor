package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.access.BallotUrnAccess;
import io.fabricatedatelier.mayor.init.MayorItems;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.BallotUrnHelper;
import io.fabricatedatelier.mayor.util.StateHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(DecoratedPotBlock.class)
public abstract class DecoratedPotBlockMixin extends BlockWithEntity {

    public DecoratedPotBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onUseWithItem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/entity/DecoratedPotBlockEntity;getStack()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    private void onUseWithItemMixin(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player,
                                    Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> info) {
        // already on server world cause of injection point
        if (!stack.isOf(MayorItems.BALLOT_PAPER)) return;
        if (!(world.getBlockEntity(pos) instanceof DecoratedPotBlockEntity decoratedPotBlockEntity)) return;
        if (isInvalidBallotPot(decoratedPotBlockEntity)) return;
        if (!(decoratedPotBlockEntity instanceof BallotUrnAccess ballotUrnAccess)) return;
        if (ballotUrnAccess.validated() && !ballotUrnAccess.getVotedPlayerUuids().contains(player.getUuid())) {
            ballotUrnAccess.addStack(stack);
            ballotUrnAccess.addVotedPlayerUuid(player.getUuid());
            world.playSound(null, pos, SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS,
                    1.0F, 0.7F + 0.5F * world.getRandom().nextFloat());
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.DUST_PLUME,
                        (double) pos.getX() + 0.5,
                        (double) pos.getY() + 1.2,
                        (double) pos.getZ() + 0.5,
                        7, 0.0, 0.0, 0.0, 0.0);
            }
            decoratedPotBlockEntity.markDirty();
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            info.setReturnValue(ItemActionResult.SUCCESS);
        }

    }

    // Open vote screen
    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;emitGameEvent(Lnet/minecraft/entity/Entity;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/util/math/BlockPos;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onUseMixin(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit,
                            CallbackInfoReturnable<ActionResult> info, DecoratedPotBlockEntity decoratedPotBlockEntity) {
        if (world.isClient()) return;
        if (isInvalidBallotPot(decoratedPotBlockEntity)) return;
        VillageData villageData = StateHelper.getClosestVillage((ServerWorld) world, pos);
        if (villageData == null) return;
        if (villageData.getBallotUrnPos() == null || villageData.getBallotUrnPos().equals(pos)) {
            if (decoratedPotBlockEntity instanceof BallotUrnAccess ballotUrnAccess) {
                ballotUrnAccess.setMayorPlayerTime(villageData.getMayorPlayerTime());
            }
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            info.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(method = "onStateReplaced", at = @At("TAIL"))
    protected void onStateReplacedMixin(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo info) {
        if (!world.isClient() && !state.equals(newState)) {
            BallotUrnHelper.updateBallotUrn((ServerWorld) world, pos);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.isClient()) return;
        if (!(world.getBlockEntity(pos) instanceof DecoratedPotBlockEntity decoratedPotBlockEntity)) return;
        if (decoratedPotBlockEntity instanceof BallotUrnAccess ballotUrnAccess) {
            ballotUrnAccess.setValidated(false);
            decoratedPotBlockEntity.markDirty();
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, BlockEntityType.DECORATED_POT, world.isClient() ? BallotUrnHelper::clientTick : BallotUrnHelper::serverTick);
    }

    @Unique
    private static boolean isInvalidBallotPot(DecoratedPotBlockEntity blockEntity) {
        List<Item> equippedSherds = blockEntity.getSherds().stream();
        return !equippedSherds.stream().allMatch(item -> item.equals(MayorItems.BALLOT_POTTERY_SHERD));
    }
}
