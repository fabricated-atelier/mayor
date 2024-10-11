package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.BallotUrnAccess;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

import java.util.*;

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
        if (mayorManager.getVillageData() != null) {
            mayorManager.setMajorView(this.mayorView);
        }

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
        if (this.mayorView) {
            if (!context.player().isCreativeLevelTwoOp() && CitizenHelper.isCitizenOfClosestVillage(context.player().getServerWorld(), context.player())) {
                return;
            }
            VillageData villageData = StateHelper.getClosestVillage(context.player().getServerWorld(), context.player().getBlockPos());
            if (villageData != null) {
                if ((villageData.getMayorPlayerUuid() != null && villageData.getMayorPlayerUuid().equals(context.player().getUuid())) || context.player().isCreativeLevelTwoOp()) {
                    ((MayorManagerAccess) context.player()).getMayorManager().setVillageData(villageData);
                    if (!MayorManager.mayorStructureMap.isEmpty()) {
                        List<MayorStructuresPacket.MayorStructureData> list = new ArrayList<>();
                        for (var entries : MayorManager.mayorStructureMap.entrySet()) {
                            for (var entry : entries.getValue()) {
                                Identifier structureId = entry.getIdentifier();
                                int level = entry.getLevel();
                                int experience = entry.getExperience();
                                int price = entry.getPrice();
                                String biomeCategory = entry.getBiomeCategory().name();
                                String buildingCategory = entry.getBuildingCategory().name();
                                List<ItemStack> requiredItemStacks = entry.getRequiredItemStacks();
                                Map<BlockPos, NbtCompound> posCompoundMap = StructureHelper.getBlockPosNbtMap(entry.getBlockMap());
                                Vec3i size = entry.getSize();

                                MayorStructuresPacket.MayorStructureData mayorStructureData = new MayorStructuresPacket.MayorStructureData(structureId, level, experience, price, biomeCategory, buildingCategory, requiredItemStacks, posCompoundMap, size);
                                list.add(mayorStructureData);
                            }
                        }
                        new MayorStructuresPacket(new MayorStructuresPacket.MayorStructureDatas(list.size(), list)).sendPacket(context.player());
                    }
                    new VillageDataPacket(villageData.getCenterPos(), villageData.getBiomeCategory().name(), villageData.getLevel(), villageData.getName(), villageData.getAge(), villageData.getFunds(), Optional.ofNullable(villageData.getMayorPlayerUuid()), villageData.getMayorPlayerTime(), Optional.ofNullable(villageData.getBallotUrnPos()), villageData.getStorageOriginBlockPosList(), villageData.getCitizens(), villageData.getVillagers(), villageData.getIronGolems(), villageData.getStructures(), villageData.getConstructions()).sendPacket(context.player());

                    int availableBuilderCount = VillageHelper.getAvailableBuilderCount(context.player().getServerWorld(), villageData.getVillagers());
                    new ExtraVillageInfoPacket(availableBuilderCount).sendPacket(context.player());
                } else {
                    int voteTimeLeft = 0;
                    if (villageData.getBallotUrnPos() != null && context.player().getServerWorld().getBlockEntity(villageData.getBallotUrnPos()) instanceof BallotUrnAccess ballotUrnAccess && ballotUrnAccess.validated()) {
                        voteTimeLeft = (int) (context.player().getServerWorld().getTime() - ballotUrnAccess.getVoteStartTime()) - ballotUrnAccess.getVoteTicks();
                    }
                    String mayorName = null;
                    if (villageData.getMayorPlayerUuid() != null) {
                        if (StringUtil.getOnlinePlayerUuidNames(context.player().getServerWorld()).containsKey(villageData.getMayorPlayerUuid())) {
                            mayorName = StringUtil.getOnlinePlayerUuidNames(context.player().getServerWorld()).get(villageData.getMayorPlayerUuid());
                        } else {
                            Map<UUID, String> offlinePlayers = StringUtil.getOfflinePlayerUuidNames(context.player().getServerWorld());
                            if (offlinePlayers.containsKey(villageData.getMayorPlayerUuid())) {
                                mayorName = offlinePlayers.get(villageData.getMayorPlayerUuid());
                            }
                        }
                    }
                    new VillageViewPacket(villageData.getName(), villageData.getLevel(), Optional.ofNullable(mayorName), Optional.ofNullable(villageData.getBallotUrnPos()), voteTimeLeft).sendPacket(context.player());
                }
            }
        } else {
            context.player().setCameraEntity(null);
        }
        ((MayorManagerAccess) context.player()).getMayorManager().setMajorView(this.mayorView());
        sendPacket(context.player());
    }
}
