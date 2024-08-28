package io.fabricatedatelier.mayor.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ConnectedBlockUtil {
    public static int connectedBlocksCount(BoundingBox boundingBox) {
        return boundingBox.getConnectedPosList().size();
    }

    public static int connectedBlocksCount(World world, BlockPos pos, boolean includeVertical) {
        BoundingBox boundingBox = new BoundingBox(world, pos, includeVertical);
        return connectedBlocksCount(boundingBox);
    }

    /**
     * Generates the BoundingBox of BlockPos. It collects neighbor Blocks which are
     * the same as the starting pos Block iteratively (DFS).
     *
     * @implNote No limit has been set yet, besides avoiding air blocks. Make sure that
     * the connected shape is not excessively big. Iterating over near infinite blocks could potentially hurt the performance.
     */
    public static class BoundingBox {
        private final World world;
        private final BlockState originalState;
        private final boolean includeVerticalNeighbors;

        private BlockPos minPos, maxPos;
        final HashSet<BlockPos> connectedPosList = new HashSet<>();

        public BoundingBox(World world, BlockPos startPos, boolean includeVertical) {
            this.world = world;
            this.minPos = startPos;
            this.maxPos = startPos;
            this.originalState = world.getBlockState(startPos);
            this.includeVerticalNeighbors = includeVertical;

            this.update(startPos);
        }

        public BlockPos getMinPos() {
            return minPos;
        }

        public void setMinPos(BlockPos minPos) {
            this.minPos = minPos;
        }

        public BlockPos getMaxPos() {
            return maxPos;
        }

        public void setMaxPos(BlockPos maxPos) {
            this.maxPos = maxPos;
        }

        public HashSet<BlockPos> getConnectedPosList() {
            return connectedPosList;
        }

        public void update(BlockPos currentPos) {
            if (world.getBlockState(currentPos).isAir()) return;
            if (this.connectedPosList.contains(currentPos)) return;
            this.connectedPosList.add(currentPos);

            boolean exceedsMinBoundary = currentPos.getX() < minPos.getX() || currentPos.getZ() < minPos.getZ();
            boolean exceedsMaxBoundary = currentPos.getX() > maxPos.getX() || currentPos.getZ() > maxPos.getZ();
            if (includeVerticalNeighbors) {
                exceedsMinBoundary = exceedsMinBoundary || currentPos.getY() < minPos.getY();
                exceedsMaxBoundary = exceedsMaxBoundary || currentPos.getY() > maxPos.getY();
            }
            if (exceedsMinBoundary) {
                setMinPos(new BlockPos(
                        Math.min(currentPos.getX(), minPos.getX()),
                        includeVerticalNeighbors ? Math.min(currentPos.getY(), minPos.getY()) : currentPos.getY(),
                        Math.min(currentPos.getZ(), minPos.getZ()))
                );
            }
            if (exceedsMaxBoundary) {
                setMaxPos(new BlockPos(
                        Math.max(currentPos.getX(), maxPos.getX()),
                        includeVerticalNeighbors ? Math.max(currentPos.getY(), maxPos.getY()) : currentPos.getY(),
                        Math.max(currentPos.getZ(), maxPos.getZ()))
                );
            }

            List<Direction> directions = new ArrayList<>();
            if (includeVerticalNeighbors) directions.addAll(List.of(Direction.values()));
            else Direction.Type.HORIZONTAL.iterator().forEachRemaining(directions::add);

            for (Direction direction : directions) {
                BlockPos newPos = currentPos.offset(direction);
                BlockState newState = world.getBlockState(newPos);
                if (!newState.getBlock().equals(this.originalState.getBlock())) continue;
                update(newPos);
            }
        }

        public int getXLength() {
            return this.getMaxPos().getX() - this.getMinPos().getX();
        }

        public int getZLength() {
            return this.getMaxPos().getZ() - this.getMinPos().getZ();
        }

        public boolean hasHoles() {
            for (BlockPos blockPos : BlockPos.iterate(getMinPos(), getMaxPos())) {
                if (!world.getBlockState(blockPos).getBlock().equals(originalState.getBlock())) {
                    return true;
                }
            }
            return false;
        }
    }


}
