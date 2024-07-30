package io.fabricatedatelier.mayor.item;

import io.fabricatedatelier.mayor.block.custom.StoneStorageBlock;
import io.fabricatedatelier.mayor.util.boilerplate.AbstractVillageContainerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class StoneStorageBlockItem extends BlockItem {
    public StoneStorageBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!(this.getBlock() instanceof StoneStorageBlock stoneStorageBlock)) return ActionResult.FAIL;
        BlockPos targetPos = context.getBlockPos();
        BlockPos placePos = targetPos.offset(context.getSide());


        List<Direction> directions = stoneStorageBlock.connectableSides().stream().map(AbstractVillageContainerBlock.ConnectedSide::getDirection).toList();
        for (Direction direction : directions) {
            BlockState state = context.getWorld().getBlockState(placePos.offset(direction));
            if (state.getBlock().equals(this.getBlock())) return ActionResult.FAIL;
        }
        return super.useOnBlock(context);
    }
}
