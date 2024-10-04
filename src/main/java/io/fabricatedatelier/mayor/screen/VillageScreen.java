package io.fabricatedatelier.mayor.screen;

import io.fabricatedatelier.mayor.init.MayorKeyBindings;
import io.fabricatedatelier.mayor.util.RenderUtil;
import io.fabricatedatelier.mayor.util.StringUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class VillageScreen extends Screen {

    private final int level;
    private final String mayorName;
    @Nullable
    private final BlockPos votePos;
    private int voteTimeLeft;

    private boolean initiated = true;

    public VillageScreen(Text title, int level, String mayorName, @Nullable BlockPos votePos, int voteTimeLeft) {
        super(title);
        this.level = level;
        this.mayorName = mayorName;
        this.votePos = votePos;
        this.voteTimeLeft = voteTimeLeft;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int villageMiddleX = this.width / 2;
        int villageY = 10;
        int villageLeft = villageMiddleX - this.textRenderer.getWidth(title) / 2;

        context.drawTexture(MayorScreen.VILLAGE, villageLeft - 6, villageY, 0, 0, 10, 23, 128, 128);
        context.drawTexture(MayorScreen.VILLAGE, villageLeft - 6 + 10, villageY, this.textRenderer.getWidth(title) - 9, 23, 10, 0, 5, 23, 128, 128);
        context.drawTexture(MayorScreen.VILLAGE, villageLeft - 6 + 10 + this.textRenderer.getWidth(title) - 9, villageY, 15, 0, 10, 23, 128, 128);
        context.drawText(this.textRenderer, this.title, villageLeft, villageY + 8, Colors.WHITE, false);

        int levelX = villageMiddleX + this.textRenderer.getWidth(title) / 2;
        int levelY = villageY + 9;
        context.drawTexture(MayorScreen.VILLAGE, levelX, levelY, 0, 23, 19, 19, 128, 128);
        context.drawText(this.textRenderer, Text.of(String.valueOf(this.level)), levelX + 7, levelY + 6, Colors.WHITE, false);

        context.drawText(this.textRenderer, Text.translatable("mayor.screen.mayor", this.mayorName), this.width / 2 - 150, 30, Colors.GRAY, false);
        context.drawText(this.textRenderer, Text.translatable("mayor.screen.election"), this.width / 2 - 150, 50, Colors.GRAY, false);
        if (this.votePos != null) {
            context.drawText(this.textRenderer, Text.translatable("mayor.screen.vote_time_left", StringUtil.getTimeString(this.voteTimeLeft)), this.width / 2 - 146, 60, Colors.LIGHT_GRAY, false);
            context.drawText(this.textRenderer, Text.translatable("mayor.screen.election.pos", this.votePos.toShortString()), this.width / 2 - 146, 70, Colors.LIGHT_GRAY, false);
        } else {
            context.drawText(this.textRenderer, Text.translatable("mayor.screen.election.none"), this.width / 2 - 146, 60, Colors.LIGHT_GRAY, false);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);

        RenderUtil.renderCustomBackground(context, this.width / 2 - 160, 21, 320, 200);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {

        if (!this.initiated && MayorKeyBindings.mayorView.matchesKey(keyCode, scanCode)) {
            this.close();
        }
        this.initiated = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
}
