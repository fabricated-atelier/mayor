package io.fabricatedatelier.mayor.block.custom;

import com.mojang.serialization.MapCodec;
import io.fabricatedatelier.mayor.util.boilerplate.AbstractVillageContainerBlock;
import io.fabricatedatelier.mayor.block.entity.StoneStorageBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StoneStorageBlock extends AbstractVillageContainerBlock {
    @Override
    public List<ConnectedSide> connectableSides() {
        return List.of();
    }

    public StoneStorageBlock(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(StoneStorageBlock::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StoneStorageBlockEntity(pos, state);
    }
}
