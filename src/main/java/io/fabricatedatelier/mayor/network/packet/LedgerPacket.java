package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.util.LedgerHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/**
 * code: 1 = donation, 2 = fee, 3 = work, 4 = taxes
 */
public record LedgerPacket(BlockPos blockPos, String textUpdate, String currentPage, int code, int amount) implements CustomPayload {

    public static final CustomPayload.Id<LedgerPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("ledger_packet"));

    public static final PacketCodec<RegistryByteBuf, LedgerPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBlockPos(value.blockPos);
        buf.writeString(value.textUpdate);
        buf.writeString(value.currentPage);
        buf.writeInt(value.code);
        buf.writeInt(value.amount);
    }, buf -> new LedgerPacket(buf.readBlockPos(), buf.readString(), buf.readString(), buf.readInt(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        String prefix = "";
        if (this.code() == 1) {
            prefix = Text.translatable("mayor.ledger.donation").getString();
        } else if (this.code() == 2) {
            prefix = Text.translatable("mayor.ledger.fee").getString();
        } else if (this.code() == 3) {
            prefix = Text.translatable("mayor.ledger.paid_work").getString();
        }else if (this.code() == 4) {
            prefix = Text.translatable("mayor.ledger.tax").getString();
        }
        String newLine = LedgerHelper.formatStringWithSpaces(prefix, this.amount(), context.client().textRenderer);

        int newCode = -1;
        if (!this.currentPage().isEmpty()) {
            if (LedgerHelper.countNewlines(this.currentPage()) >= 14) {
                // new page
                newCode = -2;
            }
        } else {
            newLine = LedgerHelper.formatStringWithSpaces(Text.translatable("mayor.ledger.transaction").getString(), Text.translatable("mayor.ledger.value").getString(), context.client().textRenderer) + "\n" + newLine;
        }
        // code: -1 = normal, -2 new page
        new LedgerPacket(this.blockPos(), newLine, "", newCode, 0).sendPacket();
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        if (this.code() < 0 && !this.textUpdate().isEmpty()) {
            ItemStack itemStack = LedgerHelper.getLedger(context.player().getServerWorld(), this.blockPos());
            if (!itemStack.isEmpty()) {
                LedgerHelper.updateLedger(itemStack, this.textUpdate(), this.code());
            }
        }
    }
}


