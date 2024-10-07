package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.camera.CameraHandler;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.util.RenderUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.RenderTickCounter;

@Environment(EnvType.CLIENT)
public class MayorClientEvents {
    public static void initialize() {
        ClientPlayConnectionEvents.JOIN.register(MayorClientEvents::handleClientConnection);
        WorldRenderEvents.AFTER_ENTITIES.register(RenderUtil::renderVillageStructure);
        HudRenderCallback.EVENT.register(MayorClientEvents::handleHudRendering);
    }

    private static void handleClientConnection(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (!client.isInSingleplayer()) {
            MayorManager.mayorStructureMap.clear();
        }
    }

    private static void handleHudRendering(DrawContext drawContext, RenderTickCounter tickCounter) {
        RenderUtil.renderMayorHud(drawContext);
        CameraHandler camera = CameraHandler.getInstance();
        if (camera.getTarget().isEmpty()) return;
        if (camera.getStartTransition().isRunning()) {
            camera.getStartTransition().renderOverlay(drawContext);
        }
        if (camera.getEndTransition().isRunning()) {
            camera.getEndTransition().renderOverlay(drawContext);
        }
    }
}
