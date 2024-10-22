package io.fabricatedatelier.mayor.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureDataLoader implements SimpleSynchronousResourceReloadListener {

    public static Map<String, List<Integer>> structureDataMap = new HashMap<>();
    // List to store replacing structures
    private final List<String> replaceList = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return Mayor.identifierOf("structure_data");
    }

    @Override
    public void reload(ResourceManager manager) {
        manager.findResources("structure_data", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                    if (!replaceList.contains(entry.getKey())) {
                        if (entry.getValue().isJsonObject()) {
                            if (JsonHelper.getBoolean(entry.getValue().getAsJsonObject(), "replace", false)) {
                                replaceList.add(entry.getKey());
                            }
                            String structureIdentifier = entry.getKey();
                            if (entry.getValue().getAsJsonObject().get("id") != null && !entry.getValue().getAsJsonObject().get("id").getAsString().isEmpty()) {
                                structureIdentifier = entry.getValue().getAsJsonObject().get("id").getAsString();
                            }
                            StructureDataLoader.structureDataMap.put(structureIdentifier, List.of(entry.getValue().getAsJsonObject().get("experience").getAsInt(), entry.getValue().getAsJsonObject().get("price").getAsInt()));
                        } else {
                            Mayor.LOGGER.error("Error occurred while loading resource {}. {} is not a valid json object.", id.toString(), entry.getKey());
                        }
                    }
                }
            } catch (Exception e) {
                Mayor.LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });

    }

}
