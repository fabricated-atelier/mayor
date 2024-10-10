package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.entity.CameraDebugBlockEntity;
import io.fabricatedatelier.mayor.block.entity.DeskBlockEntity;
import io.fabricatedatelier.mayor.block.entity.VillageContainerBlockEntity;
import io.fabricatedatelier.mayor.screen.block.BallotUrnBlockScreenHandler;
import io.fabricatedatelier.mayor.screen.block.DeskBlockScreenHandler;
import io.fabricatedatelier.mayor.network.packet.BallotUrnPacket;
import io.fabricatedatelier.mayor.screen.block.DeskCitizenScreenHandler;
import io.fabricatedatelier.mayor.util.HandledInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public class MayorBlockEntities {
    public static final BlockEntityType<CameraDebugBlockEntity> CAMERA_DEBUG =
            register("camera_debug", CameraDebugBlockEntity::new, MayorBlocks.CAMERA_DEBUG);

    public static final BlockEntityType<VillageContainerBlockEntity> VILLAGE_STORAGE =
            registerWithStorage("lumber_storage", VillageContainerBlockEntity::new,
                    MayorBlocks.LUMBER_STORAGE, MayorBlocks.STONE_STORAGE);

    public static final BlockEntityType<DeskBlockEntity> DESK =
            register("desk", DeskBlockEntity::new, MayorBlocks.DESK);

    public static final ScreenHandlerType<BallotUrnBlockScreenHandler> BALLOT_URN_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(
            BallotUrnBlockScreenHandler::new, BallotUrnPacket.PACKET_CODEC);

    public static final ScreenHandlerType<DeskBlockScreenHandler> DESK_SCREEN_HANDLER = new ScreenHandlerType<>(
            (syncId, playerInventory) -> new DeskBlockScreenHandler(syncId), FeatureFlags.VANILLA_FEATURES);

    public static final ScreenHandlerType<DeskCitizenScreenHandler> DESK_CITIZEN_SCREEN_HANDLER = new ScreenHandlerType<>(
            DeskCitizenScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name, BlockEntityType.BlockEntityFactory<? extends T> entityFactory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Mayor.identifierOf(name),
                BlockEntityType.Builder.<T>create(entityFactory, blocks).build(null));
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends BlockEntity> BlockEntityType<T> registerWithStorage(
            String name, BlockEntityType.BlockEntityFactory<? extends T> entityFactory, Block... blocks) {
        BlockEntityType<T> blockEntityType = register(name, entityFactory, blocks);
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            if (blockEntity instanceof HandledInventory inventory) return inventory.getAsStorage(direction);
            Mayor.LOGGER.error("BlockEntity was missing HandledInventory Interface at registration");
            return null;
        }, blockEntityType);
        return blockEntityType;
    }


    public static void initialize() {
        // static initialisation
        Registry.register(Registries.SCREEN_HANDLER, Mayor.identifierOf("ballot_urn"), BALLOT_URN_SCREEN_HANDLER);
        Registry.register(Registries.SCREEN_HANDLER, Mayor.identifierOf("desk"), DESK_SCREEN_HANDLER);
    }
}
