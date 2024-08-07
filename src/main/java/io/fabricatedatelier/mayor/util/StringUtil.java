package io.fabricatedatelier.mayor.util;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    @SuppressWarnings("unused")
    private static List<String> replaceStrings = List.of("desert/", "plains/", "savanna/", "snowy/", "taiga/");
    private static List<String> structureIds = List.of("houses", "town_centers");

    public static Identifier getMayorStructureIdentifier(Identifier structureIdentifier) {
        String[] strings = structureIdentifier.getPath().split("/");
        String structureId = strings[strings.length - 1];
        // structureId = structureId.replaceAll("_[0-9_]+$", "");
        return Identifier.of(structureIdentifier.getNamespace(), structureId);
    }

    public static boolean shouldStoreStructureIdentifier(Identifier structureIdentifier) {
        String structureId = structureIdentifier.getPath();
        for (String id : structureIds) {
            if (structureId.contains("village/") && structureId.contains(id)) {
                return true;
            }
        }
        return false;
    }

    public static int getStructureLevelByIdentifier(Identifier structureIdentifier) {
        String string = structureIdentifier.getPath();
        Pattern pattern = Pattern.compile(".*_\\d{2}_(\\d+)$");
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches()) {
            String number = matcher.group(1);
            return Integer.parseInt(number);
        }
        return 1;
    }
}
