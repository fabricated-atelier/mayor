package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.camera.CameraHandler;
import io.fabricatedatelier.mayor.entity.custom.CameraPullEntity;
import io.fabricatedatelier.mayor.init.MayorKeyBind;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.network.packet.AreaPacket;
import io.fabricatedatelier.mayor.network.packet.CameraPullMovementPacket;
import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.screen.MayorScreen;
import io.fabricatedatelier.mayor.screen.MayorVillageScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class KeyHelper {

    public static void rotateKey(MinecraftClient client, boolean rotateLeft) {
        if (client.player != null) {
            MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
            mayorManager.setStructureRotation(StructureHelper.getRotatedStructureRotation(mayorManager.getStructureRotation(), rotateLeft));
        }
    }

    public static void centerKey(MinecraftClient client) {
        if (MayorKeyBind.TARGET_TO_CENTER.get().wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView()) {
                    mayorManager.setStructureCentered(!mayorManager.getStructureCentered());
                }
            }
        }
    }

    public static void heightKey(MinecraftClient client) {
        if (MayorKeyBind.UPWARD.get().wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView() && mayorManager.getStructureOriginBlockPos() != null) {
                    mayorManager.setStructureOriginBlockPos(mayorManager.getStructureOriginBlockPos().up());
                }
            }
        } else if (MayorKeyBind.DOWNWARD.get().wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView() && mayorManager.getStructureOriginBlockPos() != null) {
                    mayorManager.setStructureOriginBlockPos(mayorManager.getStructureOriginBlockPos().down());
                }
            }
        }
    }

    public static void viewKey(MinecraftClient client) {
        if (MayorKeyBind.MAYOR_VIEW.get().wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                new MayorViewPacket(!mayorManager.isInMajorView()).sendClientPacket();
            }
        } else if (MayorKeyBind.MAYOR_VIEW_SELECTION.get().wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();

                if (mayorManager.isInMajorView()) {
                    if (mayorManager.isInAreaMode()) {
                        client.setScreen(new MayorVillageScreen(mayorManager));
                        mayorManager.setAreaModeId(-1);
                        mayorManager.setStructureOriginBlockPos(null);
                    } else if (client.currentScreen instanceof MayorScreen) {
                        client.setScreen(null);
                    } else {
                        client.setScreen(new MayorScreen(mayorManager));
                    }
                }
            }
        }
    }

    public static void useKey(MinecraftClient client) {
        if (client.player == null) return;
        if (!client.options.useKey.wasPressed()) return;
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
        if (mayorManager.isInMajorView() && !(client.currentScreen instanceof MayorScreen)) {
            Optional<BlockHitResult> hitResult = Optional.ofNullable(StructureHelper.findCrosshairTarget(client.player));
            if (mayorManager.getStructureOriginBlockPos() == null) {
                if (hitResult.isPresent()) {
                    Optional<BlockPos> origin = hitResult.map(BlockHitResult::getBlockPos);
                    if (origin.isPresent()) {
                        if (mayorManager.isInAreaMode()) {
                            new AreaPacket(mayorManager.getAreaModeId(), origin.get()).sendPacket();
                        }
                        mayorManager.setStructureOriginBlockPos(origin.get());
                    }
                }
            } else {
                if (mayorManager.isInAreaMode() && hitResult.isPresent()) {
                    Optional<BlockPos> origin = hitResult.map(BlockHitResult::getBlockPos);
                    if (origin.isPresent() && mayorManager.getStructureOriginBlockPos().equals(origin.get())) {
                        mayorManager.setAreaModeId(-1);
                        mayorManager.setMayorStructure(null);
                        client.setScreen(new MayorVillageScreen(mayorManager));
                    }
                }
                mayorManager.setStructureOriginBlockPos(null);
            }
        }

    }

    public static void cameraKeys(MinecraftClient client) {
        if (client == null || client.world == null) return;
        if (CameraHandler.getInstance().getTarget().isEmpty()) return;
        for (var entry : CameraPullEntity.DirectionInput.values()) {
            if (entry.getKeyBind().get().isPressed()) {
                Mayor.LOGGER.info(entry.getDirection().toString());
                new CameraPullMovementPacket(Optional.of(entry)).sendPacket();
            }
        }
    }
}
