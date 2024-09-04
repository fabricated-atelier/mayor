package io.fabricatedatelier.mayor.block;

import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MayorProperties {
    public static final EnumProperty<Position> POSITION = EnumProperty.of("position", Position.class);


    public enum Position implements StringIdentifiable {
        NORTH("north", Direction.EAST, Direction.SOUTH, Direction.WEST),
        EAST("east", Direction.NORTH, Direction.SOUTH, Direction.WEST),
        SOUTH("south", Direction.NORTH, Direction.EAST, Direction.WEST),
        WEST("west", Direction.NORTH, Direction.EAST, Direction.SOUTH),
        NORTH_EAST("north_east", Direction.WEST, Direction.SOUTH),
        NORTH_WEST("north_west", Direction.EAST, Direction.SOUTH),
        SOUTH_EAST("south_east", Direction.NORTH, Direction.WEST),
        SOUTH_WEST("south_west", Direction.EAST, Direction.NORTH),
        SINGLE("single"),
        CENTER("center", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

        private final String name;
        private final HashSet<Direction> connectedDirections;

        Position(String name, Direction... involvedPositions) {
            this.name = name;
            this.connectedDirections = new HashSet<>(Arrays.asList(involvedPositions));
        }

        @Override
        public String asString() {
            return name;
        }

        public HashSet<Direction> getConnectedDirections() {
            return connectedDirections;
        }
    }
}
