package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.screen.item.BallotPaperScreen;
import io.fabricatedatelier.mayor.screen.VillageScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class ScreenHelper {

    public static void openBallotPaperScreen(MinecraftClient client, String votedName, Map<UUID, String> availablePlayers) {
        client.setScreen(new BallotPaperScreen(votedName, availablePlayers));
    }

    public static void openVillageScreen(MinecraftClient client, String villageName, int villageLevel, String mayorName, @Nullable BlockPos votePos, int voteTimeLeft) {
        client.setScreen(new VillageScreen(Text.of(villageName), villageLevel, mayorName, votePos, voteTimeLeft));
    }

}
