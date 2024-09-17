package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.camera.CameraHandler;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.util.RenderUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

@Environment(EnvType.CLIENT)
public class ClientEvents {

    public static void initialize() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!client.isInSingleplayer()) {
                MayorManager.mayorStructureMap.clear();
            }
        });
        WorldRenderEvents.AFTER_ENTITIES.register(RenderUtil::renderVillageStructure);
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            RenderUtil.renderMayorHud(drawContext);
            CameraHandler camera = CameraHandler.getInstance();
            if (!camera.hasTarget()) return;
            if (camera.getStartTransition().isRunning()) {
                camera.getStartTransition().renderOverlay(drawContext);
            }
            if (camera.getEndTransition().isRunning()) {
                camera.getEndTransition().renderOverlay(drawContext);
            }
        });
    }

}
