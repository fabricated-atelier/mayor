package io.fabricatedatelier.mayor.block.custom;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlock;
import io.fabricatedatelier.mayor.block.entity.VillageContainerBlockEntity;
import io.fabricatedatelier.mayor.datagen.TagProvider;
import io.fabricatedatelier.mayor.util.ConnectedBlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LumberStorageBlock extends AbstractVillageContainerBlock {


    public LumberStorageBlock(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(LumberStorageBlock::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VillageContainerBlockEntity(pos, state, TagProvider.ItemTags.LUMBER_STORAGE_STORABLE);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getOutlineShape(state, world, pos, context);
        // return LumberBlockVoxelShapes.get(state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ConnectedBlockUtil.BoundingBox boundingBox = new ConnectedBlockUtil.BoundingBox(world, pos, false);
        Mayor.LOGGER.info("Connected Blocks: {}", ConnectedBlockUtil.connectedBlocksCount(boundingBox));
        Mayor.LOGGER.info("Min: {} | Max: {}", boundingBox.getMinPos(), boundingBox.getMaxPos());
        Mayor.LOGGER.info("Has Holes: {}", boundingBox.hasHoles());
        return super.onUse(state, world, pos, player, hit);
    }
}
