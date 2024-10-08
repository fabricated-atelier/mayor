package io.fabricatedatelier.mayor.init;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.camera.CameraHandler;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.render.AlphaFramebuffer;
import io.fabricatedatelier.mayor.util.RenderUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.Window;

import java.util.Objects;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.RenderTickCounter;

@Environment(EnvType.CLIENT)
public class MayorClientEvents {

    public static ShaderProgram ALPHA_SHADER;

    public static final VertexConsumerProvider.Immediate IMMEDIATE = VertexConsumerProvider.immediate(new BufferAllocator(4096));

    public static final Supplier<Framebuffer> ALPHA_FRAMEBUFFER = Suppliers.memoize(() -> {
        Window window = MinecraftClient.getInstance().getWindow();
        AlphaFramebuffer alphaFramebuffer = new AlphaFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
        alphaFramebuffer.setClearColor(0f, 0f, 0f, 0f);
        return alphaFramebuffer;
    });

    public static void initialize() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!client.isInSingleplayer()) {
                MayorManager.mayorStructureMap.clear();
            }
        });
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            RenderUtil.renderMayorHud(drawContext);
            CameraHandler camera = CameraHandler.getInstance();
            if (camera.getTarget().isEmpty()) return;
            if (camera.getStartTransition().isRunning()) {
                camera.getStartTransition().renderOverlay(drawContext);
            }
            if (camera.getEndTransition().isRunning()) {
                camera.getEndTransition().renderOverlay(drawContext);
            }
        });
        CoreShaderRegistrationCallback.EVENT.register((context) -> {
            context.register(Mayor.identifierOf("blit_alpha"), VertexFormats.BLIT_SCREEN, shaderProgram -> {
                Objects.requireNonNull(shaderProgram.getUniform("Alpha")).set(0.5f);
                ALPHA_SHADER = shaderProgram;
            });
        });
        WorldRenderEvents.AFTER_ENTITIES.register(RenderUtil::renderVillageStructure);
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
