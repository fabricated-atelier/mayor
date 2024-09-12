package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.manager.MayorManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Environment(EnvType.CLIENT)
public class ClientEvents {

    public static void initialize() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!client.isInSingleplayer()) {
                MayorManager.mayorStructureMap.clear();
            }
        });
    }

}
