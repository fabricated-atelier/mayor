package io.fabricatedatelier.mayor.network;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.network.packet.*;
import io.fabricatedatelier.mayor.util.MayorManager;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class CustomC2SNetworking {
    static {
        // ServerPlayNetworking.registerGlobalReceiver(CustomPacket.IDENTIFIER, CustomPacket::handlePacket);
        // ServerPlayNetworking.registerGlobalReceiver(CustomPacket.IDENTIFIER, CustomPacket::handlePacket);
    }

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(StructurePacket.PACKET_ID, StructurePacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(StructureOriginPacket.PACKET_ID, StructureOriginPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(StructureRotatePacket.PACKET_ID, StructureRotatePacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(StructureCenterPacket.PACKET_ID, StructureCenterPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(MayorViewPacket.PACKET_ID, MayorViewPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(StructureRotatePacket.PACKET_ID, (payload, context) -> {
            boolean rotateLeft = payload.rotateLeft();

            context.server().execute(() -> {
                MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
                StructureHelper.updateMajorStructure(context.player(), mayorManager.getStructureId(), StructureHelper.getRotatedStructureRotation(mayorManager.getStructureRotation(), rotateLeft), mayorManager.getStructureCentered());
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(StructureCenterPacket.PACKET_ID, (payload, context) -> {
            boolean center = payload.center();

            context.server().execute(() -> {
                MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
                StructureHelper.updateMajorStructure(context.player(), mayorManager.getStructureId(), mayorManager.getStructureRotation(), center);
            });
        });
    }
}
