package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.MayorKeyBindings;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.screen.MayorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class KeyHelper {

    public static void rotateKey(MinecraftClient client, boolean rotateLeft) {
//        new StructureRotatePacket(rotateLeft).sendPacket();

        // Test for client only
        if (client.player != null) {
            MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
            mayorManager.setStructureRotation(StructureHelper.getRotatedStructureRotation(mayorManager.getStructureRotation(), rotateLeft));
        }
    }

    public static void centerKey(MinecraftClient client) {
        if (MayorKeyBindings.mayorCenterKeyBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView()) {
                    // Test for client only
                    mayorManager.setStructureCentered(!mayorManager.getStructureCentered());
                    System.out.println("CENTER "+mayorManager.getStructureCentered());
//                    new StructureCenterPacket(mayorManager.getStructureCentered()).sendPacket();
                }
            }
        }
    }

    public static void heightKey(MinecraftClient client) {
        if (MayorKeyBindings.mayorUpwardKeyBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView() && mayorManager.getStructureOriginBlockPos() != null) {
                    mayorManager.setStructureOriginBlockPos(mayorManager.getStructureOriginBlockPos().up());
                }
            }
        } else if (MayorKeyBindings.mayorDownwardKeyBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView() && mayorManager.getStructureOriginBlockPos() != null) {
                    mayorManager.setStructureOriginBlockPos(mayorManager.getStructureOriginBlockPos().down());
                }
            }
        }
    }

    public static void viewKey(MinecraftClient client) {
        if (MayorKeyBindings.mayorViewBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                new MayorViewPacket(!mayorManager.isInMajorView()).sendClientPacket();
            }
        } else if (MayorKeyBindings.mayorViewSelectionBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();

                if (mayorManager.isInMajorView()) {
                    if (client.currentScreen instanceof MayorScreen) {
                        client.setScreen(null);
                    } else {
                        client.setScreen(new MayorScreen(mayorManager));
                    }
                }
            }
        }
    }

    public static void useKey(MinecraftClient client) {
        if (client.options.useKey.wasPressed() && client.player != null) {
            MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
            if (mayorManager.isInMajorView() && !(client.currentScreen instanceof MayorScreen)) {
                if (mayorManager.getStructureOriginBlockPos() == null) {
                    Optional<BlockHitResult> hitResult = Optional.ofNullable(StructureHelper.findCrosshairTarget(client.player));
                    if (hitResult.isPresent()) {
                        Optional<BlockPos> origin = hitResult.map(BlockHitResult::getBlockPos);
                        if (origin.isPresent()) {
                            mayorManager.setStructureOriginBlockPos(origin.get());
                        }
                    }
                } else {
                    mayorManager.setStructureOriginBlockPos(null);
                }
            }
        }
    }
}
