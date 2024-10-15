package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.config.MayorConfig;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.InventoryUtil;
import io.fabricatedatelier.mayor.util.ScreenHelper;
import io.fabricatedatelier.mayor.util.StateHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.UUID;

/**
 * code: 0 = villageName, 1 = tax, 2 = tax interval, 3 = registration fee, 4 = accept registration, 5 = deny registration, 6 = debt relief, 7 = kick
 */
public record DeskMayorDataPacket(BlockPos deskPos, int code, int value, String villageName, Optional<UUID> optional) implements CustomPayload {

    public static final CustomPayload.Id<DeskMayorDataPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("desk_mayor_data_packet"));

    public static final PacketCodec<RegistryByteBuf, DeskMayorDataPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBlockPos(value.deskPos);
        buf.writeInt(value.code);
        buf.writeInt(value.value);
        buf.writeString(value.villageName);
        buf.writeOptional(value.optional, (bufx, uuid) -> bufx.writeUuid(uuid));
    }, buf -> new DeskMayorDataPacket(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readString(), buf.readOptional(bufx -> bufx.readUuid())));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        ScreenHelper.updateDeskMayorScreen(context.client(), this.code(), this.value(), this.villageName(), this.optional());
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        VillageData villageData = StateHelper.getClosestVillage(context.player().getServerWorld(), context.player().getBlockPos());
        if (villageData != null && villageData.getMayorPlayerUuid() != null && villageData.getMayorPlayerUuid().equals(context.player().getUuid())) {
            if (villageData.getCitizenData().getDeskPos() == null || !villageData.getCitizenData().getDeskPos().equals(this.deskPos())) {
                return;
            }
            if (this.code() == 0) {
                // set village name
                if (!this.villageName().isEmpty()) {
                    if (InventoryUtil.hasRequiredPrice(context.player().getInventory(), MayorConfig.CONFIG.instance().villageRenameCost)) {
                        villageData.setName(this.villageName());
                        InventoryUtil.consumePrice(context.player().getInventory(), MayorConfig.CONFIG.instance().villageRenameCost);
                    } else {
                        context.player().sendMessage(Text.translatable("mayor.screen.desk.fee_insufficient"), true);
                    }
                }
            } else if (this.code() == 1) {
                // set tax
                villageData.getCitizenData().setTaxAmount(this.value());
            } else if (this.code() == 2) {
                // set tax interval
                int interval = this.value() * 20 * 60 * 60;
                villageData.getCitizenData().setTaxInterval(interval);
                if (this.value() == 0) {
                    villageData.getCitizenData().setTaxTime(0);
                } else {
                    villageData.getCitizenData().setTaxTime(context.player().getServerWorld().getTime() + interval);
                    for (UUID uuid : villageData.getCitizenData().getCitizens()) {
                        if (context.server().getPlayerManager().getPlayer(uuid) instanceof ServerPlayerEntity player) {
                            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", villageData.getName())));
                            player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.citizen.taxes")));
                        }
                    }
                }
            } else if (this.code() == 3) {
                // set registration fee
                villageData.getCitizenData().setRegistrationFee(this.value());
            } else if (this.optional.isPresent()) {
                if (this.code() == 4) {
                    // accept registration
                    villageData.getCitizenData().addCitizen(this.optional.get());
                    // waive the first taxes
                    if (villageData.getCitizenData().getTaxTime() > 0L) {
                        villageData.getCitizenData().addTaxPaidCitizen(this.optional.get());
                    }
                    if (context.server().getPlayerManager().getPlayer(this.optional.get()) instanceof ServerPlayerEntity serverPlayerEntity) {
                        serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", villageData.getName())));
                        serverPlayerEntity.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.citizen.registered", villageData.getName())));
                    }
                } else if (this.code() == 5) {
                    // deny registration
                    villageData.getCitizenData().removeRequestCitizen(this.optional.get());
                    if (context.server().getPlayerManager().getPlayer(this.optional.get()) instanceof ServerPlayerEntity serverPlayerEntity) {
                        serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", villageData.getName())));
                        serverPlayerEntity.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.citizen.declined")));
                    }
                } else if (this.code() == 6) {
                    // relief debt
                    villageData.getCitizenData().removeTaxUnpaidCitizen(this.optional.get());
                    if (context.server().getPlayerManager().getPlayer(this.optional.get()) instanceof ServerPlayerEntity serverPlayerEntity) {
                        serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", villageData.getName())));
                        serverPlayerEntity.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.citizen.debt_reliefed")));
                    }
                } else if (this.code() == 7) {
                    // kick
                    villageData.getCitizenData().removeCitizen(this.optional.get());
                    if (context.server().getPlayerManager().getPlayer(this.optional.get()) instanceof ServerPlayerEntity serverPlayerEntity) {
                        serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", villageData.getName())));
                        serverPlayerEntity.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.citizen.kicked")));
                    }
                }
            }

            StateHelper.getMayorVillageState(context.player().getServerWorld()).markDirty();
            new DeskMayorDataPacket(this.deskPos(), this.code(), this.value(), villageData.getName(), this.optional()).sendPacket(context.player());
        }
    }
}

