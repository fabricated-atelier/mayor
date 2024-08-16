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

public class StructureXpLoader implements SimpleSynchronousResourceReloadListener {

    public static Map<String, Integer> structureExperienceMap = new HashMap<>();
    // List to store replacing structures
    private final List<String> replaceList = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return Identifier.of("mayor", "structure_experience");
    }

    @Override
    public void reload(ResourceManager manager) {
        manager.findResources("structure_experience", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
                    if (!replaceList.contains(entry.getKey())) {
                        if (entry.getValue().isJsonObject()) {
                            if (JsonHelper.getBoolean(entry.getValue().getAsJsonObject(), "replace", false)) {
                                replaceList.add(entry.getKey());
                            }
                            StructureXpLoader.structureExperienceMap.put(entry.getKey(), entry.getValue().getAsJsonObject().get("experience").getAsInt());
                        } else {
                            StructureXpLoader.structureExperienceMap.put(entry.getKey(), entry.getValue().getAsInt());
                        }
                    }
                }
            } catch (Exception e) {
                Mayor.LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });

    }

}
