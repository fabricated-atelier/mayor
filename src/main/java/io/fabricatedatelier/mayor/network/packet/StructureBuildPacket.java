package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * code: 0 = new build, 1 = upgrade, 2 demolish
 */
public record StructureBuildPacket(Identifier mayorStructureIdentifier, BlockPos originBlockPos, int structureRotation, boolean center, int code) implements CustomPayload {

    public static final CustomPayload.Id<StructureBuildPacket> PACKET_ID = new CustomPayload.Id<>(Mayor.identifierOf("structure_build_packet"));

    public static final PacketCodec<RegistryByteBuf, StructureBuildPacket> PACKET_CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, StructureBuildPacket::mayorStructureIdentifier, BlockPos.PACKET_CODEC, StructureBuildPacket::originBlockPos, PacketCodecs.INTEGER, StructureBuildPacket::structureRotation, PacketCodecs.BOOL, StructureBuildPacket::center, PacketCodecs.INTEGER, StructureBuildPacket::code, StructureBuildPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();

            if (mayorManager.getVillageData() != null) {
                MayorStructure selectedMayorStructure = null;
                for (MayorStructure mayorStructure : MayorManager.mayorStructureMap.get(mayorManager.getVillageData().getBiomeCategory())) {
                    if (mayorStructure.getIdentifier().equals(this.mayorStructureIdentifier())) {
                        selectedMayorStructure = mayorStructure;
                        break;
                    }
                }
                if (selectedMayorStructure != null) {
                    // build or upgrade
                    if (this.code() == 0 || this.code() == 1) {
                        StructureHelper.tryBuildStructure(context.player(), selectedMayorStructure, this.originBlockPos(), StructureHelper.getStructureRotation(this.structureRotation()), this.center(), this.code());
                    } else
                        // demolish
                        if (this.code() == 2) {
                            if (mayorManager.getVillageData().getStructures().containsKey(this.originBlockPos())) {
                                StructureHelper.tryDemolishStructure(context.player(), selectedMayorStructure, this.originBlockPos());
                            }
                        }
                }
            }
        });
    }
}

