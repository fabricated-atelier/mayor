package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.network.packet.StructureCenterPacket;
import io.fabricatedatelier.mayor.network.packet.StructureRotatePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class KeyHelper {

    public static void rotateKey(MinecraftClient client, boolean rotateLeft) {
        ClientPlayNetworking.send(new StructureRotatePacket(rotateLeft));
    }

    public static void centerKey(MinecraftClient client){
        if (KeyBindings.majorCenterKeyBind.wasPressed()) {
            if (client.player != null) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (client.player != null && mayorManager.isInMajorView()) {
                    mayorManager.setStructureCentered(!mayorManager.getStructureCentered());
                    ClientPlayNetworking.send(new StructureCenterPacket(mayorManager.getStructureCentered()));
                }
            }
        }
    }

}
