package io.fabricatedatelier.mayor.block.custom;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.block.entity.CameraDebugBlockEntity;
import io.fabricatedatelier.mayor.camera.mode.OrbitMode;
import io.fabricatedatelier.mayor.init.MayorBlockEntities;
import io.fabricatedatelier.mayor.camera.CameraHandler;
import io.fabricatedatelier.mayor.camera.util.CameraTarget;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CameraDebugBlock extends BlockWithEntity {
    public CameraDebugBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        player.sendMessage(Text.literal("Used Block"), true);
        if (world instanceof ClientWorld clientWorld && clientWorld.getBlockEntity(pos) instanceof CameraTarget target) {
            if (CameraHandler.getInstance().getTarget().isEmpty()) {
                CameraHandler.getInstance().setTarget(target).setMode(new OrbitMode());
            } else {
                CameraHandler.getInstance().setTarget(null).setMode(new OrbitMode());
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient()) CameraHandler.getInstance().end();
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(CameraDebugBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CameraDebugBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, MayorBlockEntities.CAMERA_DEBUG, CameraDebugBlockEntity::tick);
    }
}
