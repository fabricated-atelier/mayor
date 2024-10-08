package io.fabricatedatelier.mayor.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.fabricatedatelier.mayor.init.MayorClientEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class AlphaFramebuffer extends SimpleFramebuffer {

    public AlphaFramebuffer(int width, int height, boolean useDepth, boolean getError) {
        super(width, height, useDepth, getError);
    }

    @Override
    public void draw(int width, int height, boolean disableBlend) {
        this.drawInternal(width, height, disableBlend);
    }

    private void drawInternal(int width, int height, boolean disableBlend) {
        RenderSystem.assertOnRenderThread();
        GlStateManager._colorMask(true, true, true, false);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._viewport(0, 0, width, height);
        if (disableBlend) {
            GlStateManager._disableBlend();
        }

        ShaderProgram shaderProgram = Objects.requireNonNull(MayorClientEvents.ALPHA_SHADER, "Alpha shader not loaded");
        shaderProgram.addSampler("DiffuseSampler", this.colorAttachment);
        shaderProgram.bind();
        BufferBuilder bufferBuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.BLIT_SCREEN);
        bufferBuilder.vertex(0.0F, 0.0F, 0.0F);
        bufferBuilder.vertex(1.0F, 0.0F, 0.0F);
        bufferBuilder.vertex(1.0F, 1.0F, 0.0F);
        bufferBuilder.vertex(0.0F, 1.0F, 0.0F);
        BufferRenderer.draw(bufferBuilder.end());
        shaderProgram.unbind();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
    }
}
