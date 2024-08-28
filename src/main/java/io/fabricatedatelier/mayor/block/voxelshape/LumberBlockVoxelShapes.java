package io.fabricatedatelier.mayor.block.voxelshape;

import io.fabricatedatelier.mayor.block.Properties;
import io.fabricatedatelier.mayor.block.custom.LumberStorageBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class LumberBlockVoxelShapes {

    public static VoxelShape get(BlockState state) {
        var shape = state.get(Properties.SHAPE);
        boolean needsGroundPlate = state.get(Properties.POSITION).equals(Properties.VerticalPosition.BOTTOM);
        VoxelShape shape_north = Block.createCuboidShape(0, 0, 0, 16, 16, 2.5);
        VoxelShape shape_east = Block.createCuboidShape(13.5, 0, 0, 16, 16, 16);
        VoxelShape shape_south = Block.createCuboidShape(0, 0, 13.5, 16, 16, 16);
        VoxelShape shape_west = Block.createCuboidShape(0, 0, 0, 2.5, 16, 16);

        VoxelShape ground_1x1 = Block.createCuboidShape(1, 0, 1, 15, 2, 15);
        VoxelShape ground_ns = Block.createCuboidShape(0, 0, 0, 15, 2, 16);
        VoxelShape ground_ew = Block.createCuboidShape(0, 0, 0, 16, 2, 15);

        return switch (shape) {
            case ALL_WALLS -> {
                VoxelShape voxelShape = switch (state.get(LumberStorageBlock.FACING)) {
                    case NORTH, SOUTH -> VoxelShapes.union(shape_east, shape_west);
                    case EAST, WEST -> VoxelShapes.union(shape_north, shape_south);
                    default -> VoxelShapes.empty();
                };
                yield needsGroundPlate ? VoxelShapes.union(ground_1x1, voxelShape) : voxelShape;
            }
            case TWO_WALLS_END, TWO_WALLS_MID -> switch (state.get(LumberStorageBlock.FACING)) {
                case NORTH, SOUTH -> {
                    VoxelShape voxelShape = VoxelShapes.union(shape_east, shape_west);
                    yield needsGroundPlate ? VoxelShapes.union(voxelShape, ground_ns) : voxelShape;
                }
                case WEST, EAST -> {
                    VoxelShape voxelShape = VoxelShapes.union(shape_north, shape_south);
                    yield needsGroundPlate ? VoxelShapes.union(voxelShape, ground_ew) : voxelShape;
                }
                default -> VoxelShapes.empty();
            };
            case ONE_WALL_END -> switch (state.get(Properties.SIDE)) {
                case LEFT -> switch (state.get(LumberStorageBlock.FACING)) {
                    case NORTH -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0.75, 0.125, 1, 0.875),
                                VoxelShapes.cuboid(0.09375, 0.25, 0, 0.15625, 0.4375, 0.9375)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.0625, 0, 0, 1, 0.125, 0.9375),
                                VoxelShapes.cuboid(0.09375, 0.625, 0, 0.15625, 0.8125, 0.9375),
                                VoxelShapes.cuboid(0, 0, 0.75, 0.125, 1, 0.875)
                        );
                    };
                    case SOUTH -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.875, 0, 0.125, 1, 1, 0.25),
                                VoxelShapes.cuboid(0.84375, 0.25, 0.0625, 0.90625, 0.4375, 1)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0.0625, 0.9375, 0.125, 1),
                                VoxelShapes.cuboid(0.84375, 0.625, 0.0625, 0.90625, 0.8125, 1),
                                VoxelShapes.cuboid(0.875, 0, 0.125, 1, 1, 0.25)
                        );
                    };
                    case WEST -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.75, 0, 0.875, 0.875, 1, 1),
                                VoxelShapes.cuboid(0, 0.25, 0.84375, 0.9375, 0.4375, 0.90625)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0, 0.9375, 0.125, 0.9375),
                                VoxelShapes.cuboid(0, 0.625, 0.84375, 0.9375, 0.8125, 0.90625),
                                VoxelShapes.cuboid(0.75, 0, 0.875, 0.875, 1, 1)
                        );
                    };
                    case EAST -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.125, 0, 0, 0.25, 1, 0.125),
                                VoxelShapes.cuboid(0.0625, 0.25, 0.09375, 1, 0.4375, 0.15625)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.0625, 0, 0.0625, 1, 0.125, 1),
                                VoxelShapes.cuboid(0.0625, 0.625, 0.09375, 1, 0.8125, 0.15625),
                                VoxelShapes.cuboid(0.125, 0, 0, 0.25, 1, 0.125)
                        );
                    };
                    default -> VoxelShapes.empty();
                };
                case RIGHT -> switch (state.get(LumberStorageBlock.FACING)) {
                    case NORTH -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.84375, 0.25, 0, 0.90625, 0.4375, 0.9375),
                                VoxelShapes.cuboid(0.875, 0, 0.75, 1, 1, 0.875)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0, 0.9375, 0.125, 0.9375),
                                VoxelShapes.cuboid(0.875, 0, 0.75, 1, 1, 0.875),
                                VoxelShapes.cuboid(0.84375, 0.625, 0, 0.90625, 0.8125, 0.9375)
                        );
                    };
                    case SOUTH -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.09375, 0.25, 0.0625, 0.15625, 0.4375, 1),
                                VoxelShapes.cuboid(0, 0, 0.125, 0.125, 1, 0.25)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.0625, 0, 0.0625, 1, 0.125, 1),
                                VoxelShapes.cuboid(0, 0, 0.125, 0.125, 1, 0.25),
                                VoxelShapes.cuboid(0.09375, 0.625, 0.0625, 0.15625, 0.8125, 1)
                        );
                    };
                    case WEST -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0.25, 0.09375, 0.9375, 0.4375, 0.15625),
                                VoxelShapes.cuboid(0.75, 0, 0, 0.875, 1, 0.125)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0.0625, 0.9375, 0.125, 1),
                                VoxelShapes.cuboid(0.75, 0, 0, 0.875, 1, 0.125),
                                VoxelShapes.cuboid(0, 0.625, 0.09375, 0.9375, 0.8125, 0.15625)
                        );
                    };
                    case EAST -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.0625, 0.25, 0.84375, 1, 0.4375, 0.90625),
                                VoxelShapes.cuboid(0.125, 0, 0.875, 0.25, 1, 1)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.0625, 0, 0, 1, 0.125, 0.9375),
                                VoxelShapes.cuboid(0.125, 0, 0.875, 0.25, 1, 1),
                                VoxelShapes.cuboid(0.0625, 0.625, 0.84375, 1, 0.8125, 0.90625)
                        );
                    };
                    default -> VoxelShapes.empty();
                };
            };
            case ONE_WALL_MID -> switch (state.get(Properties.SIDE)) {
                case LEFT -> switch (state.get(LumberStorageBlock.FACING)) {
                    case NORTH -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0.4375, 0.125, 1, 0.5625),
                                VoxelShapes.cuboid(0.09375, 0.25, 0, 0.15625, 0.4375, 1)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.0625, 0, 0, 1, 0.125, 1),
                                VoxelShapes.cuboid(0, 0, 0.4375, 0.125, 1, 0.5625),
                                VoxelShapes.cuboid(0.09375, 0.625, 0, 0.15625, 0.8125, 1)
                        );
                    };
                    case SOUTH -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.875, 0, 0.4375, 1, 1, 0.5625),
                                VoxelShapes.cuboid(0.84375, 0.25, 0, 0.90625, 0.4375, 1)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0, 0.9375, 0.125, 1),
                                VoxelShapes.cuboid(0.875, 0, 0.4375, 1, 1, 0.5625),
                                VoxelShapes.cuboid(0.84375, 0.625, 0, 0.90625, 0.8125, 1)
                        );
                    };
                    case WEST -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.4375, 0, 0.875, 0.5625, 1, 1),
                                VoxelShapes.cuboid(0, 0.25, 0.84375, 1, 0.4375, 0.90625)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 0.9375),
                                VoxelShapes.cuboid(0.4375, 0, 0.875, 0.5625, 1, 1),
                                VoxelShapes.cuboid(0, 0.625, 0.84375, 1, 0.8125, 0.90625)
                        );
                    };
                    case EAST -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.4375, 0, 0, 0.5625, 1, 0.125),
                                VoxelShapes.cuboid(0, 0.25, 0.09375, 1, 0.4375, 0.15625)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0.0625, 1, 0.125, 1),
                                VoxelShapes.cuboid(0.4375, 0, 0, 0.5625, 1, 0.125),
                                VoxelShapes.cuboid(0, 0.625, 0.09375, 1, 0.8125, 0.15625)
                        );
                    };
                    default -> VoxelShapes.empty();
                };
                case RIGHT -> switch (state.get(LumberStorageBlock.FACING)) {
                    case NORTH -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.875, 0, 0.4375, 1, 1, 0.5625),
                                VoxelShapes.cuboid(0.84375, 0.25, 0, 0.90625, 0.4375, 1)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0, 0.9375, 0.125, 1),
                                VoxelShapes.cuboid(0.875, 0, 0.4375, 1, 1, 0.5625),
                                VoxelShapes.cuboid(0.84375, 0.625, 0, 0.90625, 0.8125, 1)
                        );
                    };
                    case SOUTH -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0.4375, 0.125, 1, 0.5625),
                                VoxelShapes.cuboid(0.09375, 0.25, 0, 0.15625, 0.4375, 1)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.0625, 0, 0, 1, 0.125, 1),
                                VoxelShapes.cuboid(0, 0, 0.4375, 0.125, 1, 0.5625),
                                VoxelShapes.cuboid(0.09375, 0.625, 0, 0.15625, 0.8125, 1)
                        );
                    };
                    case WEST -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.4375, 0, 0, 0.5625, 1, 0.125),
                                VoxelShapes.cuboid(0, 0.25, 0.09375, 1, 0.4375, 0.15625)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0.0625, 1, 0.125, 1),
                                VoxelShapes.cuboid(0.4375, 0, 0, 0.5625, 1, 0.125),
                                VoxelShapes.cuboid(0, 0.625, 0.09375, 1, 0.8125, 0.15625)
                        );
                    };
                    case EAST -> switch (state.get(Properties.POSITION)) {
                        case TOP -> VoxelShapes.union(
                                VoxelShapes.cuboid(0.4375, 0, 0.875, 0.5625, 1, 1),
                                VoxelShapes.cuboid(0, 0.25, 0.84375, 1, 0.4375, 0.90625)
                        );
                        case BOTTOM -> VoxelShapes.union(
                                VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 0.9375),
                                VoxelShapes.cuboid(0.4375, 0, 0.875, 0.5625, 1, 1),
                                VoxelShapes.cuboid(0, 0.625, 0.84375, 1, 0.8125, 0.90625)
                        );
                    };
                    default -> VoxelShapes.empty();
                };
            };
        };
    }
}
