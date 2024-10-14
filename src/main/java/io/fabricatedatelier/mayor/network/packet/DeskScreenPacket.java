package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.block.custom.DeskBlock;
import io.fabricatedatelier.mayor.block.entity.DeskBlockEntity;
import io.fabricatedatelier.mayor.config.MayorConfig;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.CitizenHelper;
import io.fabricatedatelier.mayor.util.StateHelper;
import io.fabricatedatelier.mayor.util.StringUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// code: 0 = book, 1 = citizen, 2 = mayor
public record DeskScreenPacket(int code, BlockPos deskPos) implements CustomPayload {

    public static final CustomPayload.Id<DeskScreenPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("desk_screen_packet"));

    public static final PacketCodec<RegistryByteBuf, DeskScreenPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.code);
        buf.writeBlockPos(value.deskPos);
    }, buf -> new DeskScreenPacket(buf.readInt(), buf.readBlockPos()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        if (this.code() == 0) {
            if (context.player().getServerWorld().getBlockState(this.deskPos()).get(DeskBlock.HAS_BOOK) && context.player().getServerWorld().getBlockEntity(this.deskPos()) instanceof DeskBlockEntity deskBlockEntity && deskBlockEntity.isValidated()) {
                context.player().openHandledScreen(deskBlockEntity);
            }
        } else {
            VillageData villageData = StateHelper.getClosestVillage(context.player().getServerWorld(), context.player().getBlockPos());
            if (villageData != null && villageData.getCitizenData().getDeskPos() != null && villageData.getCitizenData().getDeskPos().equals(this.deskPos()) && context.player().getServerWorld().getBlockEntity(this.deskPos()) instanceof DeskBlockEntity deskBlockEntity && deskBlockEntity.isValidated()) {
                if (this.code() == 1) {
                    String mayorName = "";
                    if (villageData.getMayorPlayerUuid() != null) {
                        mayorName = StringUtil.getPlayerNameByUuid(context.player().getServerWorld(), villageData.getMayorPlayerUuid());
                    }
                    boolean citizen = CitizenHelper.isCitizenOfClosestVillage(context.player().getServerWorld(), context.player()) || context.player().isCreativeLevelTwoOp();
                    new DeskCitizenScreenPacket(this.deskPos(), villageData.getName(), villageData.getLevel(), mayorName, citizen, villageData.getCitizenData().getTaxAmount(), villageData.getCitizenData().getTaxTime(), villageData.getCitizenData().getRegistrationFee(), villageData.getCitizenData().getCitizens().size(), villageData.getVillagers().size(), villageData.getFunds(), villageData.getCitizenData().getTaxPayedCitizens().contains(context.player().getUuid()), villageData.getCitizenData().getRequestCitizens().contains(context.player().getUuid())).sendPacket(context.player());
                } else if (this.code() == 2) {
                    Map<UUID, String> registeredCitizens = new HashMap<>();
                    if (!villageData.getCitizenData().getCitizens().isEmpty()) {
                        for (UUID uuid : villageData.getCitizenData().getCitizens()) {
                            registeredCitizens.put(uuid, StringUtil.getPlayerNameByUuid(context.player().getServerWorld(), uuid));
                        }
                    }
                    Map<UUID, String> requestingCitizens = new HashMap<>();
                    if (!villageData.getCitizenData().getRequestCitizens().isEmpty()) {
                        for (UUID uuid : villageData.getCitizenData().getRequestCitizens()) {
                            requestingCitizens.put(uuid, StringUtil.getPlayerNameByUuid(context.player().getServerWorld(), uuid));
                        }
                    }
                    Map<UUID, String> taxPayedCitizens = new HashMap<>();
                    if (!villageData.getCitizenData().getTaxPayedCitizens().isEmpty()) {
                        for (UUID uuid : villageData.getCitizenData().getTaxPayedCitizens()) {
                            taxPayedCitizens.put(uuid, StringUtil.getPlayerNameByUuid(context.player().getServerWorld(), uuid));
                        }
                    }
                    new DeskMayorScreenPacket(this.deskPos(), villageData.getName(), villageData.getLevel(), true, villageData.getCitizenData().getTaxAmount(), villageData.getCitizenData().getTaxInterval(), villageData.getCitizenData().getTaxTime(), villageData.getCitizenData().getRegistrationFee(), villageData.getVillagers().size(), villageData.getFunds(), MayorConfig.CONFIG.instance().villageFoundingCost, registeredCitizens, requestingCitizens, taxPayedCitizens).sendPacket(context.player());
                }
            }
        }

    }
}

