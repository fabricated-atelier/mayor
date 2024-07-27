package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.network.packet.StructureRotatePacket;
import net.minecraft.client.MinecraftClient;

public class KeyHelper {

    public static void rotateKey(MinecraftClient client, boolean rotateLeft) {
        new StructureRotatePacket(rotateLeft).sendPacket();
    }

    public static void centerKey(MinecraftClient client, boolean center) {

    }

}
