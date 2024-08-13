package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.util.RenderUtil;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

@Environment(EnvType.CLIENT)
public class Renderer {

    public static void initialize() {
        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            RenderUtil.renderVillageStructure(context);
        });
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            RenderUtil.renderMayorHud(drawContext);
            CameraHandler camera = CameraHandler.getInstance();
            if (camera.hasTarget() && camera.getTransition().isRunning()) {
                camera.getTransition().renderOverlay(drawContext);
            }
        });
    }

}
