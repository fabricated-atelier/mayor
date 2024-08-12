package io.fabricatedatelier.mayor.block;

import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

public class Properties {
    public static final EnumProperty<Shape> SHAPE = EnumProperty.of("shape", Shape.class);
    public static final EnumProperty<VerticalPosition> POSITION = EnumProperty.of("position", VerticalPosition.class);
    public static final EnumProperty<Side> SIDE = EnumProperty.of("side", Side.class);


    public enum Shape implements StringIdentifiable {
        ALL_WALLS("all_walls"),
        TWO_WALLS_END("two_walls_end"),
        TWO_WALLS_MID("two_walls_mid"),
        ONE_WALL_END("one_wall_end"),
        ONE_WALL_MID("one_wall_mid");


        private final String name;

        Shape(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

    public enum VerticalPosition implements StringIdentifiable {
        BOTTOM("bottom"), TOP("top");

        private final String name;

        VerticalPosition(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }

    public enum Side implements StringIdentifiable {
        LEFT("left"), RIGHT("right");

        private final String name;

        Side(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
