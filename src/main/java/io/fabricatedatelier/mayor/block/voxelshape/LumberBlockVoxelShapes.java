package io.fabricatedatelier.mayor.block.voxelshape;

import io.fabricatedatelier.mayor.block.custom.LumberStorageBlock;
import io.fabricatedatelier.mayor.block.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class LumberBlockVoxelShapes {
    private static final VoxelShape ground_1x1 = Block.createCuboidShape(1, 0, 1, 15, 2, 15);

    private static final VoxelShape ground_n = Block.createCuboidShape(1, 0, 0, 15, 2, 15);
    private static final VoxelShape ground_s = Block.createCuboidShape(1, 0, 1, 15, 2, 16);
    private static final VoxelShape ground_e = Block.createCuboidShape(1, 0, 1, 16, 2, 15);
    private static final VoxelShape ground_w = Block.createCuboidShape(0, 0, 1, 15, 2, 15);
    // private static final VoxelShape ground_ns = Block.createCuboidShape(1, 0, 0, 15, 2, 15);
    // private static final VoxelShape ground_ew = Block.createCuboidShape(1, 0, 1, 16, 2, 15);


    private static final VoxelShape wall_end_west = Block.createCuboidShape(1.5, 0, 0, 2.5, 13, 15);
    private static final VoxelShape wall_end_east = Block.createCuboidShape(13.5, 0, 0, 14.5, 13, 15);
    private static final VoxelShape wall_end_south = Block.createCuboidShape(0, 10, 13.5, 15, 13, 14.5);
    private static final VoxelShape wall_end_north = Block.createCuboidShape(12, 0, 14, 14, 16, 16);


    public static VoxelShape get(BlockState state) {
        var shape = state.get(Properties.SHAPE);
        boolean needsGroundPlate = state.get(Properties.POSITION).equals(Properties.VerticalPosition.BOTTOM);

        return switch (shape) {
            case ALL_WALLS -> {
                if (state.get(LumberStorageBlock.FACING).equals(Direction.NORTH) || state.get(LumberStorageBlock.FACING).equals(Direction.SOUTH)) {
                    yield VoxelShapes.union(
                            ground_1x1,
                            VoxelShapes.cuboid(0, 0, 0.125, 0.125, 0.5, 0.25),
                            VoxelShapes.cuboid(0.875, 0, 0.75, 1, 0.5, 0.875),
                            VoxelShapes.cuboid(0.875, 0, 0.125, 1, 0.5, 0.25),
                            VoxelShapes.cuboid(0, 0, 0.75, 0.125, 0.5, 0.875)
                    );
                } else {
                    yield VoxelShapes.union(
                            ground_1x1,
                            VoxelShapes.cuboid(0.125, 0, 0.875, 0.25, 0.5, 1),
                            VoxelShapes.cuboid(0.75, 0, 0, 0.875, 0.5, 0.125),
                            VoxelShapes.cuboid(0.125, 0, 0, 0.25, 0.5, 0.125),
                            VoxelShapes.cuboid(0.75, 0, 0.875, 0.875, 0.5, 1)
                    );
                }
            }
            case TWO_WALLS_END -> switch (state.get(LumberStorageBlock.FACING)) {
                case NORTH -> {
                    VoxelShape voxelShape = VoxelShapes.union(wall_end_west, wall_end_east);
                    yield needsGroundPlate ? VoxelShapes.union(voxelShape, ground_n) : voxelShape;
                }
                case SOUTH -> {
                    VoxelShape voxelShape = VoxelShapes.union(
                            VoxelShapes.cuboid(0.84375, 0.625, 0.0625, 0.90625, 0.8125, 1),
                            VoxelShapes.cuboid(0.09375, 0.625, 0.0625, 0.15625, 0.8125, 1),
                            VoxelShapes.cuboid(0.875, 0, 0.125, 1, 1, 0.25),
                            VoxelShapes.cuboid(0, 0, 0.125, 0.125, 1, 0.25)
                    );
                    yield needsGroundPlate ? VoxelShapes.union(voxelShape, ground_s) : voxelShape;
                }
                case WEST -> {
                    VoxelShape voxelShape = VoxelShapes.union(wall_end_south, wall_end_north);
                    yield needsGroundPlate ? VoxelShapes.union(voxelShape, ground_w) : voxelShape;
                }
                case EAST -> {
                    VoxelShape voxelShape = VoxelShapes.union(
                            VoxelShapes.cuboid(0.0625, 0.625, 0.09375, 1, 0.8125, 0.15625),
                            VoxelShapes.cuboid(0.0625, 0.625, 0.84375, 1, 0.8125, 0.90625),
                            VoxelShapes.cuboid(0.125, 0, 0, 0.25, 1, 0.125),
                            VoxelShapes.cuboid(0.125, 0, 0.875, 0.25, 1, 1)
                    );
                    yield needsGroundPlate ? VoxelShapes.union(voxelShape, ground_e) : voxelShape;
                }
                default -> VoxelShapes.empty();
            };
            case TWO_WALLS_MID -> switch (state.get(LumberStorageBlock.FACING)) {
                case NORTH, SOUTH -> {
                    VoxelShape voxelShape = VoxelShapes.union(
                            VoxelShapes.cuboid(0.09375, 0.625, 0, 0.15625, 0.8125, 1),
                            VoxelShapes.cuboid(0.84375, 0.625, 0, 0.90625, 0.8125, 1),
                            VoxelShapes.cuboid(0, 0, 0.4375, 0.125, 1, 0.5625),
                            VoxelShapes.cuboid(0.875, 0, 0.4375, 1, 1, 0.5625));

                    yield needsGroundPlate ? VoxelShapes.union(voxelShape, Block.createCuboidShape(1, 0, 0, 15, 2, 15)) : voxelShape;
                }
                case EAST, WEST -> VoxelShapes.union(
                        // Block.createCuboidShape(1, 0, 0, 15, 2, 15),

                        VoxelShapes.cuboid(0, 0.625, 0.84375, 1, 0.8125, 0.90625),
                        VoxelShapes.cuboid(0, 0.625, 0.09375, 1, 0.8125, 0.15625),
                        VoxelShapes.cuboid(0.4375, 0, 0.875, 0.5625, 1, 1),
                        VoxelShapes.cuboid(0.4375, 0, 0, 0.5625, 1, 0.125)
                        // VoxelShapes.cuboid(0, 0, 0.0625, 1, 0.125, 0.9375)
                );
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
