package io.fabricatedatelier.mayor.camera.transition;

import io.fabricatedatelier.mayor.util.TransitionState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;

public class FadeTransition {
    private final MinecraftClient client;
    private final TransitionState type;
    private int tick = -1;
    private final int duration;

    public FadeTransition(@NotNull MinecraftClient client, int duration, TransitionState type) {
        this.client = client;
        this.duration = Math.max(1, duration);
        this.type = type;
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

    public void startTicking() {
        setTick(0);
    }

    public void tick() {
        if (getTick() < 0) return;
        setTick(getTick() + 1);
    }

    public boolean isRunning() {
        return getTick() >= 0 && getTick() < this.duration;
    }

    public boolean isFinished() {
        return getTick() >= duration;
    }

    public void renderOverlay(DrawContext context) {
        int width = client.getWindow().getScaledWidth(), height = client.getWindow().getScaledHeight();
        int alpha = switch (type) {
            case STARTING -> (int) ((1 - getNormalizedTick()) * 255);
            case IDLE -> 0;
            case ENDING -> (int) (getNormalizedTick() * 255);
        };

        context.fill(0, 0, width, height, ColorHelper.Argb.getArgb(alpha, 0, 0, 0));
    }
}
