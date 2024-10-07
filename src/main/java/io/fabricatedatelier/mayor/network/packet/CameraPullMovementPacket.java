package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.entity.custom.CameraPullEntity;
import io.fabricatedatelier.mayor.init.MayorEntities;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public record CameraPullMovementPacket(Optional<CameraPullEntity.DirectionInput> movement) implements CustomPayload {

    public static final CustomPayload.Id<CameraPullMovementPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("camera_pull_entity_movement"));

    public static final PacketCodec<RegistryByteBuf, CameraPullMovementPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.optional(PacketCodecs.BYTE.xmap(index ->
                    CameraPullEntity.DirectionInput.values()[index], directionInput -> (byte) directionInput.ordinal())),
            CameraPullMovementPacket::movement,
            CameraPullMovementPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }


    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        ServerWorld world = (ServerWorld) context.player().getWorld();
        var entities = world.getEntitiesByType(MayorEntities.CAMERA_PULL, pullEntity -> CameraPullEntity.hasCorrectUUID(pullEntity, context.player()));
        CameraPullEntity pullEntity = entities.getFirst();
        pullEntity.setMovementInput(this.movement.orElse(null));
    }
}
