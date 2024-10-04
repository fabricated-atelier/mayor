package io.fabricatedatelier.mayor.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StringUtil {

    @SuppressWarnings("unused")
    private static List<String> replaceStrings = List.of("desert/", "plains/", "savanna/", "snowy/", "taiga/");
    private static List<String> structureIds = List.of("houses", "town_centers");

    public static Identifier getMayorStructureIdentifier(Identifier structureIdentifier) {
        return Identifier.of(structureIdentifier.getNamespace(), getMayorStructureString(structureIdentifier));
    }

    public static String getMayorStructureString(Identifier structureIdentifier) {
        String[] strings = structureIdentifier.getPath().split("/");
        // structureId = structureId.replaceAll("_[0-9_]+$", "");
        return strings[strings.length - 1];
    }

    public static boolean shouldStoreStructureIdentifier(Identifier structureIdentifier) {
        String structureId = structureIdentifier.getPath();
        if (structureId.contains("/zombie/")) {
            return false;
        }
        for (String id : structureIds) {
            if (structureId.contains("village/") && structureId.contains(id)) {
                return true;
            }
        }
        return false;
    }

    public static int getStructureLevelByIdentifier(Identifier structureIdentifier) {
        String string = structureIdentifier.getPath();
        Pattern pattern = Pattern.compile(".*_\\d+_(\\d+)$");
        Matcher matcher = pattern.matcher(string);

        if (matcher.matches()) {
            String number = matcher.group(1);
            return Integer.parseInt(number);
        }
        return 1;
    }

    public static String getStructureName(Identifier structureIdentifier) {
        Text structure = Text.translatable("building_" + structureIdentifier.getPath());
        String string = structure.getString().replaceAll("[0-9]", "");
        int lastSpaceIndex = string.lastIndexOf(" ");
        if (lastSpaceIndex == string.length() - 1) {
            string = string.substring(0, lastSpaceIndex);
        }
        return string;
    }

    public static String getStructureString(Identifier structureIdentifier) {
        String string = structureIdentifier.getPath().replaceAll("[0-9]", "").replaceAll("_+$", "");
        return string;
    }

    public static int getMaxWidth(TextRenderer textRenderer, Text... texts) {
        int width = 0;
        for (int i = 0; i < texts.length; i++) {
            if (textRenderer.getWidth(texts[i]) > width) {
                width = textRenderer.getWidth(texts[i]);
            }
        }
        return width;
    }

    public static Map<UUID, String> getOnlinePlayerUuidNames(ServerWorld serverWorld) {
        Map<UUID, String> onlinePlayerUuids = new HashMap<>();
        for (ServerPlayerEntity serverPlayerEntity : serverWorld.getServer().getPlayerManager().getPlayerList()) {
            onlinePlayerUuids.put(serverPlayerEntity.getUuid(), serverPlayerEntity.getName().getString());
        }
        return onlinePlayerUuids;
    }

    public static Map<UUID, String> getOfflinePlayerUuidNames(ServerWorld serverWorld) {
        Map<UUID, String> offlinePlayerNames = new HashMap<>();

        List<UUID> onlinePlayerUuids = new ArrayList<>();
        for (ServerPlayerEntity serverPlayerEntity : serverWorld.getServer().getPlayerManager().getPlayerList()) {
            onlinePlayerUuids.add(serverPlayerEntity.getUuid());
        }

        try (Stream<Path> paths = Files.walk(serverWorld.getServer().getSavePath(WorldSavePath.PLAYERDATA))) {
            List<Path> datFiles = paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".dat")).toList();
            datFiles.forEach(path -> {
                if (path.toFile().exists() && path.toFile().isFile()) {
                    try {
                        NbtCompound nbtCompound = NbtIo.readCompressed(path, NbtSizeTracker.ofUnlimitedBytes());
                        if (nbtCompound.contains("UUID") && !onlinePlayerUuids.contains(nbtCompound.getUuid("UUID"))) {
                            if (nbtCompound.contains("Name")) {
                                offlinePlayerNames.put(nbtCompound.getUuid("UUID"), nbtCompound.getString("Name"));
                            }
                        }
                    } catch (IOException ignored) {
                    }
                }
            });

        } catch (IOException ignored) {
        }
        return offlinePlayerNames;
    }

    public static String getTimeString(int ticks) {
        int seconds = ticks / 20;

        if (seconds < 0) {
            seconds = 0;
        }
        String string;
        if (seconds >= 3600) {
            string = String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
        } else {
            string = String.format("%02d:%02d", (seconds % 3600) / 60, (seconds % 60));
        }
        return string;
    }

}
