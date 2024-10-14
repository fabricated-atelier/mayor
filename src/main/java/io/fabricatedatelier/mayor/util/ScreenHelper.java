package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.screen.VillageScreen;
import io.fabricatedatelier.mayor.screen.block.DeskCitizenScreen;
import io.fabricatedatelier.mayor.screen.block.DeskMayorScreen;
import io.fabricatedatelier.mayor.screen.item.BallotPaperScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class ScreenHelper {

    private static final ItemStack EMERALD = new ItemStack(Items.EMERALD);

    public static void openBallotPaperScreen(MinecraftClient client, String votedName, Map<UUID, String> availablePlayers) {
        client.setScreen(new BallotPaperScreen(votedName, availablePlayers));
    }

    public static void openVillageScreen(MinecraftClient client, String villageName, int villageLevel, String mayorName, @Nullable BlockPos votePos, int voteTimeLeft) {
        client.setScreen(new VillageScreen(Text.of(villageName), villageLevel, mayorName, votePos, voteTimeLeft));
    }

    public static void openDeskCitizenScreen(MinecraftClient client, BlockPos deskPos, String villageName, int villageLevel, String mayorName, boolean citizen, int taxAmount, long taxTime, int registrationFee, int citizenCount, int villagerCount, int funds, boolean taxPayed, boolean registered) {
        client.setScreen(new DeskCitizenScreen(deskPos, villageName, villageLevel, mayorName, citizen, taxAmount, taxTime, registrationFee, citizenCount, villagerCount, funds, taxPayed, registered));
    }

    public static void openDeskMayorScreen(MinecraftClient client, BlockPos deskPos, String villageName, int villageLevel, boolean mayor, int taxAmount, int taxInterval, long taxTime, int registrationFee, int villagerCount, int funds, int foundingCost, Map<UUID, String> registeredCitizens, Map<UUID, String> requestingCitizens, Map<UUID, String> taxPayedCitizens) {
        client.setScreen(new DeskMayorScreen(deskPos, villageName, villageLevel, mayor, taxAmount, taxInterval, taxTime, registrationFee, villagerCount, funds, foundingCost, registeredCitizens, requestingCitizens, taxPayedCitizens));
    }

    /**
     * code: 0 = villageName, 1 = tax, 2 = tax interval, 3 = registration fee
     */
    public static void updateDeskMayorScreen(MinecraftClient client, int code, int value, String villageName) {
        if (client.currentScreen instanceof DeskMayorScreen deskMayorScreen) {
            if (code == 0) {
                deskMayorScreen.setVillageName(villageName);
            } else if (code == 1) {
                deskMayorScreen.setTaxAmount(value);
            } else if (code == 2) {
                deskMayorScreen.setTaxInterval(value);
            } else if (code == 3) {
                deskMayorScreen.setRegistrationFee(value);
            }
        }
    }

    public static void drawPrice(DrawContext context, TextRenderer textRenderer, int x, int y, int price) {
//        if (StructureHelper.isNumismaticLoaded) {
//
//        } else
        context.drawItem(EMERALD, x, y);
        context.drawItemInSlot(textRenderer, EMERALD, x, y, String.valueOf(price));
    }

}
