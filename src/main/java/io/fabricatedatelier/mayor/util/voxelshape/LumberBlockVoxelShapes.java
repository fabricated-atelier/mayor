package io.fabricatedatelier.mayor.util.voxelshape;

import io.fabricatedatelier.mayor.block.custom.LumberStorageBlock;
import io.fabricatedatelier.mayor.util.MayorProperties;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class LumberBlockVoxelShapes {

    public static VoxelShape get(BlockState state) {
        var shape = state.get(MayorProperties.SHAPE);
        return switch (shape) {
            case ALL_WALLS -> {
                if (state.get(LumberStorageBlock.FACING).equals(Direction.NORTH) || state.get(LumberStorageBlock.FACING).equals(Direction.SOUTH)) {
                    yield VoxelShapes.union(
                            VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 0.125, 0.9375),
                            VoxelShapes.cuboid(0, 0, 0.125, 0.125, 0.5, 0.25),
                            VoxelShapes.cuboid(0.875, 0, 0.75, 1, 0.5, 0.875),
                            VoxelShapes.cuboid(0.875, 0, 0.125, 1, 0.5, 0.25),
                            VoxelShapes.cuboid(0, 0, 0.75, 0.125, 0.5, 0.875)
                    );
                } else {
                    yield VoxelShapes.union(
                            VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 0.125, 0.9375),
                            VoxelShapes.cuboid(0.125, 0, 0.875, 0.25, 0.5, 1),
                            VoxelShapes.cuboid(0.75, 0, 0, 0.875, 0.5, 0.125),
                            VoxelShapes.cuboid(0.125, 0, 0, 0.25, 0.5, 0.125),
                            VoxelShapes.cuboid(0.75, 0, 0.875, 0.875, 0.5, 1)
                    );
                }
            }
            case TWO_WALLS_END -> switch (state.get(LumberStorageBlock.FACING)) {
                case NORTH -> VoxelShapes.union(
                        VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 0.125, 1),
                        VoxelShapes.cuboid(0.84375, 0.625, 0.0625, 0.90625, 0.8125, 1),
                        VoxelShapes.cuboid(0.09375, 0.625, 0.0625, 0.15625, 0.8125, 1),
                        VoxelShapes.cuboid(0.875, 0, 0.125, 1, 1, 0.25),
                        VoxelShapes.cuboid(0, 0, 0.125, 0.125, 1, 0.25)
                );
                case SOUTH -> VoxelShapes.union(
                        VoxelShapes.cuboid(0.0625, 0, 0, 0.9375, 0.125, 0.9375),
                        VoxelShapes.cuboid(0.09375, 0.625, 0, 0.15625, 0.8125, 0.9375),
                        VoxelShapes.cuboid(0.84375, 0.625, 0, 0.90625, 0.8125, 0.9375),
                        VoxelShapes.cuboid(0, 0, 0.75, 0.125, 1, 0.875),
                        VoxelShapes.cuboid(0.875, 0, 0.75, 1, 1, 0.875)
                );
                case WEST -> VoxelShapes.union(
                        VoxelShapes.cuboid(0.0625, 0, 0.0625, 1, 0.125, 0.9375),
                        VoxelShapes.cuboid(0.0625, 0.625, 0.09375, 1, 0.8125, 0.15625),
                        VoxelShapes.cuboid(0.0625, 0.625, 0.84375, 1, 0.8125, 0.90625),
                        VoxelShapes.cuboid(0.125, 0, 0, 0.25, 1, 0.125),
                        VoxelShapes.cuboid(0.125, 0, 0.875, 0.25, 1, 1)
                );
                case EAST -> VoxelShapes.union(
                        VoxelShapes.cuboid(0, 0, 0.0625, 0.9375, 0.125, 0.9375),
                        VoxelShapes.cuboid(0, 0.625, 0.84375, 0.9375, 0.8125, 0.90625),
                        VoxelShapes.cuboid(0, 0.625, 0.09375, 0.9375, 0.8125, 0.15625),
                        VoxelShapes.cuboid(0.75, 0, 0.875, 0.875, 1, 1),
                        VoxelShapes.cuboid(0.75, 0, 0, 0.875, 1, 0.125)
                );
                default -> VoxelShapes.empty();
            };
            case TWO_WALLS_MID -> switch (state.get(LumberStorageBlock.FACING)) {
                case NORTH, SOUTH -> VoxelShapes.union(
                        VoxelShapes.cuboid(0.0625, 0, 0, 0.9375, 0.125, 1),
                        VoxelShapes.cuboid(0.09375, 0.625, 0, 0.15625, 0.8125, 1),
                        VoxelShapes.cuboid(0.84375, 0.625, 0, 0.90625, 0.8125, 1),
                        VoxelShapes.cuboid(0, 0, 0.4375, 0.125, 1, 0.5625),
                        VoxelShapes.cuboid(0.875, 0, 0.4375, 1, 1, 0.5625)
                );
                case EAST, WEST -> VoxelShapes.union(
                        VoxelShapes.cuboid(0, 0.625, 0.84375, 1, 0.8125, 0.90625),
                        VoxelShapes.cuboid(0, 0.625, 0.09375, 1, 0.8125, 0.15625),
                        VoxelShapes.cuboid(0.4375, 0, 0.875, 0.5625, 1, 1),
                        VoxelShapes.cuboid(0.4375, 0, 0, 0.5625, 1, 0.125),
                        VoxelShapes.cuboid(0, 0, 0.0625, 1, 0.125, 0.9375)
                );
                default -> VoxelShapes.empty();
            };
            case ONE_WALL_END -> switch (state.get(MayorProperties.SIDE)) {
                case LEFT -> switch (state.get(LumberStorageBlock.FACING)) {
                    case NORTH -> switch (state.get(MayorProperties.POSITION)) {
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
                    case SOUTH -> switch (state.get(MayorProperties.POSITION)) {
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
                    case WEST -> switch (state.get(MayorProperties.POSITION)) {
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
                    case EAST -> switch (state.get(MayorProperties.POSITION)) {
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
                    case NORTH -> switch (state.get(MayorProperties.POSITION)) {
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
                    case SOUTH -> switch (state.get(MayorProperties.POSITION)) {
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
                    case WEST -> switch (state.get(MayorProperties.POSITION)) {
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
                    case EAST -> switch (state.get(MayorProperties.POSITION)) {
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
            case ONE_WALL_MID -> switch (state.get(MayorProperties.SIDE)) {
                case LEFT -> switch (state.get(LumberStorageBlock.FACING)) {
                    case NORTH -> switch (state.get(MayorProperties.POSITION)) {
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
                    case SOUTH -> switch (state.get(MayorProperties.POSITION)) {
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
                    case WEST -> switch (state.get(MayorProperties.POSITION)) {
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
                    case EAST -> switch (state.get(MayorProperties.POSITION)) {
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
                    case NORTH -> switch (state.get(MayorProperties.POSITION)) {
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
                    case SOUTH -> switch (state.get(MayorProperties.POSITION)) {
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
                    case WEST -> switch (state.get(MayorProperties.POSITION)) {
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
                    case EAST -> switch (state.get(MayorProperties.POSITION)) {
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
