package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.network.packet.StructureCenterPacket;
import io.fabricatedatelier.mayor.network.packet.StructureRotatePacket;
import io.fabricatedatelier.mayor.screen.MayorScreen;
import net.minecraft.client.MinecraftClient;

public class KeyHelper {

    public static void rotateKey(MinecraftClient client, boolean rotateLeft) {
        new StructureRotatePacket(rotateLeft).sendPacket();
    }

    public static void centerKey(MinecraftClient client) {
        if (KeyBindings.mayorCenterKeyBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView()) {
                    mayorManager.setStructureCentered(!mayorManager.getStructureCentered());
                    new StructureCenterPacket(mayorManager.getStructureCentered()).sendPacket();
                }
            }
        }
    }

    public static void heightKey(MinecraftClient client) {
        if (KeyBindings.mayorUpwardKeyBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView() && mayorManager.getOriginBlockPos() != null) {
                    mayorManager.setOriginBlockPos(mayorManager.getOriginBlockPos().up());
                }
            }
        } else if (KeyBindings.mayorDownwardKeyBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView() && mayorManager.getOriginBlockPos() != null) {
                    mayorManager.setOriginBlockPos(mayorManager.getOriginBlockPos().down());
                }
            }
        }
    }

    public static void viewKey(MinecraftClient client) {
        if (KeyBindings.mayorViewBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                new MayorViewPacket(!mayorManager.isInMajorView()).sendClientPacket();
            }
        } else if (KeyBindings.mayorViewSelectionBind.wasPressed()) {
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
}
