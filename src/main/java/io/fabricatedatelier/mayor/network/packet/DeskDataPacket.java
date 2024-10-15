package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.InventoryUtil;
import io.fabricatedatelier.mayor.util.LedgerHelper;
import io.fabricatedatelier.mayor.util.StateHelper;
import io.fabricatedatelier.mayor.util.StringUtil;
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

import java.util.UUID;

/**
 * code: 0 = none, 1 = register, 2 = deregister, 3 = pay taxes
 */
public record DeskDataPacket(BlockPos deskPos, int code, int donationAmount) implements CustomPayload {

    public static final CustomPayload.Id<DeskDataPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("desk_data_packet"));

    public static final PacketCodec<RegistryByteBuf, DeskDataPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBlockPos(value.deskPos);
        buf.writeInt(value.code);
        buf.writeInt(value.donationAmount);
    }, buf -> new DeskDataPacket(buf.readBlockPos(), buf.readInt(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        VillageData villageData = StateHelper.getClosestVillage(context.player().getServerWorld(), context.player().getBlockPos());
        if (villageData != null) {
            if (this.code() == 1) {
                // register
                if (villageData.getCitizenData().getRegistrationFee() > 0 && !InventoryUtil.hasRequiredPrice(context.player().getInventory(), villageData.getCitizenData().getRegistrationFee())) {
                    context.player().sendMessage(Text.translatable("mayor.screen.desk.registration_fee_insufficient"), true);
                    return;
                }
                villageData.getCitizenData().addRequestCitizen(context.player().getUuid());
                if (villageData.getMayorPlayerUuid() != null && context.server().getPlayerManager().getPlayer(villageData.getMayorPlayerUuid()) instanceof ServerPlayerEntity mayor) {
                    mayor.sendMessage(Text.translatable("mayor.village.citizen.registering", context.player().getName()));
                }
                if (villageData.getCitizenData().getRegistrationFee() > 0) {
                    InventoryUtil.consumePrice(context.player().getInventory(), villageData.getCitizenData().getRegistrationFee());
                    villageData.setFunds(villageData.getFunds() + villageData.getCitizenData().getRegistrationFee());

                    LedgerHelper.updateLedger(context.player(), this.deskPos(), 2, villageData.getCitizenData().getRegistrationFee());
                }
            } else if (this.code() == 2) {
                // deregister
                if (villageData.getMayorPlayerUuid() != null) {
                    if (villageData.getMayorPlayerUuid().equals(context.player().getUuid())) {
                        for (UUID uuid : villageData.getCitizenData().getCitizens()) {
                            if (context.server().getPlayerManager().getPlayer(uuid) instanceof ServerPlayerEntity player) {
                                player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", villageData.getName())));
                                player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.mayor_dismissed", StringUtil.getPlayerNameByUuid(context.player().getServerWorld(), villageData.getMayorPlayerUuid()))));
                            }
                        }
                        villageData.setMayorPlayerUuid(null);
                    } else if (context.server().getPlayerManager().getPlayer(villageData.getMayorPlayerUuid()) instanceof ServerPlayerEntity mayor) {
                        mayor.sendMessage(Text.translatable("mayor.village.citizen.deregistering", context.player().getName()));
                    }
                }
                villageData.getCitizenData().removeCitizen(context.player().getUuid());
            } else if (this.code() == 3) {
                // pay taxes
                if (villageData.getCitizenData().getTaxAmount() > 0) {
                    if (!villageData.getCitizenData().getTaxPaidCitizens().contains(context.player().getUuid())) {
                        if (InventoryUtil.hasRequiredPrice(context.player().getInventory(), villageData.getCitizenData().getTaxAmount())) {
                            villageData.setFunds(villageData.getFunds() + villageData.getCitizenData().getTaxAmount());
                            InventoryUtil.consumePrice(context.player().getInventory(), villageData.getCitizenData().getTaxAmount());
                            villageData.getCitizenData().addTaxPaidCitizen(context.player().getUuid());

                            LedgerHelper.updateLedger(context.player(), this.deskPos(), 4, villageData.getCitizenData().getTaxAmount());
                        } else {
                            context.player().sendMessage(Text.translatable("mayor.screen.desk.taxes_insufficient"));
                        }
                    } else {
                        context.player().sendMessage(Text.translatable("mayor.screen.desk.taxes_already_paid"));
                    }
                } else {
                    context.player().sendMessage(Text.translatable("mayor.screen.desk.non_taxes"));
                }
            } else if (this.donationAmount() > 0) {
                if (InventoryUtil.hasRequiredPrice(context.player().getInventory(), this.donationAmount())) {
                    villageData.setFunds(villageData.getFunds() + this.donationAmount());
                    InventoryUtil.consumePrice(context.player().getInventory(), this.donationAmount());

                    LedgerHelper.updateLedger(context.player(), this.deskPos(), 1, this.donationAmount());
                } else {
                    context.player().sendMessage(Text.translatable("mayor.screen.desk.donation_insufficient"), true);
                }
            }
            StateHelper.getMayorVillageState(context.player().getServerWorld()).markDirty();
        }
    }
}

