package io.fabricatedatelier.mayor.camera.transition;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;

public class FadeTransition {
    private final MinecraftClient client;
    private int tick = 0;
    private final int duration;

    public FadeTransition(@NotNull MinecraftClient client, int duration) {
        this.client = client;
        this.duration = Math.max(1, duration);
    }

    public int getTick() {
        return tick;
    }

    public float getNormalizedTick() {
        return (float) getTick() / duration;
    }

    public void setTick(int tick) {
        this.tick = Math.clamp(tick, 0, duration);
    }

    public void incrementTick() {
        setTick(getTick() + 1);
    }

    public boolean isRunning() {
        return getTick() >= 0 && getTick() < this.duration;
    }

    public void renderOverlay(DrawContext context) {
        int width = client.getWindow().getScaledWidth(), height = client.getWindow().getScaledHeight();
        int alpha = (int) ((1 - getNormalizedTick()) * 255);

        context.fill(0, 0, width, height, ColorHelper.Argb.getArgb(alpha, 0, 0, 0));
    }
}
