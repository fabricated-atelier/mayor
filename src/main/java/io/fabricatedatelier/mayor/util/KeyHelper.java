package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.network.packet.StructureRotatePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class KeyHelper {

    public static void rotateKey(MinecraftClient client, boolean rotateLeft) {
        ClientPlayNetworking.send(new StructureRotatePacket(rotateLeft));
    }

    public static void centerKey(MinecraftClient client, boolean center){

    }

}
