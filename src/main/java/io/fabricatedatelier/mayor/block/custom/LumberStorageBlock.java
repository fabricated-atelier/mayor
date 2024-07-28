package io.fabricatedatelier.mayor.block.custom;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.util.boilerplate.AbstractVillageContainerBlock;
import io.fabricatedatelier.mayor.block.entity.LumberStorageBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LumberStorageBlock extends AbstractVillageContainerBlock {
    @Override
    public List<ConnectedSide> connectableSides() {
        return List.of(ConnectedSide.NORTH, ConnectedSide.EAST, ConnectedSide.SOUTH, ConnectedSide.WEST,
                ConnectedSide.UP, ConnectedSide.DOWN);
    }

    public LumberStorageBlock(Settings settings) {
        super(settings);
    }



    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(LumberStorageBlock::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (!state.get(IS_ORIGIN)) return null;
        return new LumberStorageBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        ConnectedSide.fromDirection(ctx.getHorizontalPlayerFacing()).ifPresent(this::setConnectedSides);
        return super.getPlacementState(ctx);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!(placer instanceof ServerPlayerEntity player)) return;

    }

    //TODO: outline shape
}
