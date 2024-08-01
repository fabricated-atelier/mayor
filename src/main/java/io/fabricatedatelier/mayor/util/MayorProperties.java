package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.block.custom.LumberStorageBlock;
import net.minecraft.state.property.EnumProperty;

public class MayorProperties {
    public static final EnumProperty<LumberStorageBlock.Shape> SHAPE = EnumProperty.of("shape", LumberStorageBlock.Shape.class);
    public static final EnumProperty<LumberStorageBlock.VerticalPosition> POSITION = EnumProperty.of("position", LumberStorageBlock.VerticalPosition.class);
    public static final EnumProperty<LumberStorageBlock.Side> SIDE = EnumProperty.of("side", LumberStorageBlock.Side.class);
}
