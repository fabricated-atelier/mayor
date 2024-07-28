package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.network.packet.StructureCenterPacket;
import io.fabricatedatelier.mayor.network.packet.StructureRotatePacket;
import net.minecraft.client.MinecraftClient;

public class KeyHelper {

    public static void rotateKey(MinecraftClient client, boolean rotateLeft) {
        new StructureRotatePacket(rotateLeft).sendPacket();
    }

    public static void centerKey(MinecraftClient client) {
        if (KeyBindings.majorCenterKeyBind.wasPressed()) {
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
        if (KeyBindings.majorUpwardKeyBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView() && mayorManager.getOriginBlockPos() != null) {
                    mayorManager.setOriginBlockPos(mayorManager.getOriginBlockPos().up());
                }
            }
        } else if (KeyBindings.majorDownwardKeyBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView() && mayorManager.getOriginBlockPos() != null) {
                    mayorManager.setOriginBlockPos(mayorManager.getOriginBlockPos().down());
                }
            }
        }
    }

}
