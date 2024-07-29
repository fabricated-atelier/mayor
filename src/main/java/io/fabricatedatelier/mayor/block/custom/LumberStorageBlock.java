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
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

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
        World world = ctx.getWorld();
        BlockPos centerPos = ctx.getBlockPos();
        BlockState centerState = super.getPlacementState(ctx);

        for (Direction direction : Properties.HORIZONTAL_FACING.stream().map(Property.Value::value).toList()) {
            BlockPos offsetPos = centerPos.offset(direction);
            if (!world.getBlockState(offsetPos).getBlock().equals(this)) continue;
            Optional<ConnectedSide> connectedSide = ConnectedSide.fromDirection(direction);
            if (connectedSide.isEmpty()) continue;
            centerState = stateWithConnectedSide(connectedSide.get(), true, centerState);

        }

        return centerState;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        for (Direction direction : Properties.HORIZONTAL_FACING.stream().map(Property.Value::value).toList()) {
            BlockPos offsetPos = pos.offset(direction);
            BlockState offsetState = world.getBlockState(offsetPos);
            if (!offsetState.getBlock().equals(this)) continue;

            Optional<ConnectedSide> connectedOppositeSide = ConnectedSide.fromDirection(direction.getOpposite());
            if (connectedOppositeSide.isEmpty()) continue;
            world.setBlockState(offsetPos, stateWithConnectedSide(connectedOppositeSide.get(), true, offsetState));

        }

    }

    //TODO: outline shape
}
