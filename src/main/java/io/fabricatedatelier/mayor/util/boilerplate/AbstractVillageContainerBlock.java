package io.fabricatedatelier.mayor.util.boilerplate;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractVillageContainerBlock extends BlockWithEntity {

    abstract public List<ConnectedSide> connectableSides();

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty IS_ORIGIN = BooleanProperty.of("is_origin");

    protected AbstractVillageContainerBlock(Settings settings) {
        super(settings);
        this.setConnectedSides();
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(IS_ORIGIN, false));
    }

    protected static List<ConnectedSide> getConnectedSides(World world, BlockPos pos) {
        List<ConnectedSide> connectedSides = new ArrayList<>();
        world.getBlockState(pos).getEntries().forEach((key, value) -> {
            if (key.getName().contains("_connected")) {
                ConnectedSide.fromProperty(key).ifPresent(connectedSides::add);
            }
        });
        return connectedSides;
    }

    protected void setConnectedSides(ConnectedSide... connectedSides) {
        for (var possibleSide : connectableSides()) {
            boolean isValid = false;
            for (var connectedSide : connectedSides) {
                if (!possibleSide.equals(connectedSide)) continue;
                this.setDefaultState(this.getDefaultState().with(possibleSide.getProperty(), true));
                isValid = true;
            }
            if (!isValid) {
                this.setDefaultState(this.getDefaultState().with(possibleSide.getProperty(), false));
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        connectableSides().forEach(connectedSide -> builder.add(connectedSide.getProperty()));
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        state.with(IS_ORIGIN, true);    // first placed block is origin
        //TODO: place rest of necessary structure
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    abstract public MapCodec<? extends BlockWithEntity> getCodec();

    @Nullable
    @Override
    abstract public BlockEntity createBlockEntity(BlockPos pos, BlockState state);


    public enum ConnectedSide {
        NORTH("north_connected", Direction.NORTH),
        EAST("east_connected", Direction.EAST),
        SOUTH("south_connected", Direction.SOUTH),
        WEST("west_connected", Direction.WEST),
        UP("above_connected", Direction.UP),
        DOWN("below_connected", Direction.DOWN);

        private final BooleanProperty property;
        private final Direction direction;

        ConnectedSide(String name, Direction direction) {
            this.property = BooleanProperty.of(name);
            this.direction = direction;
        }

        public BooleanProperty getProperty() {
            return property;
        }

        public Direction getDirection() {
            return direction;
        }

        public static Optional<ConnectedSide> fromProperty(Property<?> property) {
            for (ConnectedSide value : ConnectedSide.values()) {
                if (value.getProperty().equals(property)) return Optional.of(value);
            }
            return Optional.empty();
        }

        public static Optional<ConnectedSide> fromDirection(Direction direction) {
            for (ConnectedSide value : ConnectedSide.values()) {
                if (value.getDirection().equals(direction)) return Optional.of(value);
            }
            return Optional.empty();
        }
    }
}
