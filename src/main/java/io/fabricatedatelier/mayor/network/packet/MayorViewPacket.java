package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.camera.CameraHandler;
import io.fabricatedatelier.mayor.camera.target.StaticCameraTarget;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.option.Perspective;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record MayorViewPacket(boolean mayorView) implements CustomPayload {

    public static final CustomPayload.Id<MayorViewPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("mayor_view_packet"));

    public static final PacketCodec<RegistryByteBuf, MayorViewPacket> PACKET_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.BOOL, MayorViewPacket::mayorView,
                    MayorViewPacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }


    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void sendClientPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
        mayorManager.setMajorView(this.mayorView);

//        if (this.mayorView) {
//            mayorManager.setOldPerspective(context.client().options.getPerspective());
//            context.client().options.setPerspective(Perspective.THIRD_PERSON_BACK);
//        } else if (mayorManager.getOldPerspective() != null) {
//            context.client().options.setPerspective(mayorManager.getOldPerspective());
//        }

//        if (mayorManager.getVillageData() != null) {
//            CameraHandler.getInstance().setTarget(this.mayorView ? new StaticCameraTarget(mayorManager.getVillageData().getCenterPos()) : null);
//        }
    }

    public void handleClientPacket(ServerPlayNetworking.Context context) {
        VillageData villageData = MayorStateHelper.getClosestVillage(context.player().getServerWorld(), context.player().getBlockPos());
        if (villageData != null) { // && villageData.getMayorPlayerUuid().equals(context.player().getUuid())
            if (MayorManager.mayorStructureMap.isEmpty()) {
                List<MayorStructuresPacket.MayorStructureData> list = new ArrayList<>();

                for (int i = 0; i < MayorManager.mayorStructureMap.size(); i++) {
                    for (int u = 0; u < MayorManager.mayorStructureMap.get(i).size(); u++) {
                        MayorStructure mayorStructure = MayorManager.mayorStructureMap.get(i).get(u);
                        Identifier structureId = mayorStructure.getIdentifier();
                        int level = mayorStructure.getLevel();
                        String biomeCategory = mayorStructure.getBiomeCategory().name();
                        String buildingCategory = mayorStructure.getBuildingCategory().name();
                        List<ItemStack> requiredItemStacks = mayorStructure.getRequiredItemStacks();
                        Map<BlockPos, NbtCompound> posCompoundMap = StructureHelper.getBlockPosNbtMap(mayorStructure.getBlockMap());
                        Vec3i size = mayorStructure.getSize();

                        MayorStructuresPacket.MayorStructureData mayorStructureData = new MayorStructuresPacket.MayorStructureData(structureId, level, biomeCategory, buildingCategory, requiredItemStacks, posCompoundMap, size);
                        list.add(mayorStructureData);
                    }
                }
                new MayorStructuresPacket(new MayorStructuresPacket.MayorStructureDatas(list.size(), list)).sendPacket(context.player());
            }
            new VillageDataPacket(villageData.getCenterPos(), villageData.getBiomeCategory().name(), villageData.getLevel(), villageData.getName(), villageData.getAge(), Optional.ofNullable(villageData.getMayorPlayerUuid()), villageData.getMayorPlayerTime(), villageData.getStorageOriginBlockPosList(), villageData.getVillagers(), villageData.getIronGolems(), villageData.getStructures()).sendPacket(context.player());

            sendPacket(context.player());
        }
    }
}
