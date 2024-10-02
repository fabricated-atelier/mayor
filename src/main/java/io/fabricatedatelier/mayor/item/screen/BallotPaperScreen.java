package io.fabricatedatelier.mayor.item.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.network.packet.BallotPaperC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class BallotPaperScreen extends Screen {

    private static final Identifier TEXTURE = Mayor.identifierOf("textures/gui/container/ballot_paper.png");
    private static final Text title = Text.translatable("mayor.screen.ballot_paper.title");

    private final WidgetButtonPage[] playersButtons = new WidgetButtonPage[7];

    private String votedName;
    private final Map<UUID, String> availablePlayers;

    private int selectedIndex;
    private int indexStartOffset;
    private boolean scrolling;
    private final int backgroundWidth = 122;
    private final int backgroundHeight = 174;
    private int x;
    private int y;

    public BallotPaperScreen(String votedName, Map<UUID, String> availablePlayers) {
        super(NarratorManager.EMPTY);
        this.votedName = votedName;
        this.availablePlayers = availablePlayers;
    }

    @Override
    protected void init() {
        super.init();

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        int k = y + 17;
        for (int l = 0; l < 7; ++l) {
            this.playersButtons[l] = this.addDrawableChild(new WidgetButtonPage(x + 14, k, l, button -> {
                this.selectedIndex = ((WidgetButtonPage) button).getIndex() + this.indexStartOffset;
                this.votedName = this.availablePlayers.values().stream().toList().get(this.selectedIndex);
                new BallotPaperC2SPacket(this.availablePlayers.keySet().stream().toList().get(this.selectedIndex)).sendPacket();
                this.close();
            }));
            if (!this.votedName.isEmpty() && this.availablePlayers.size() > l && this.availablePlayers.values().stream().toList().get(l).equals(this.votedName)) {
                this.playersButtons[l].active = false;
                this.selectedIndex = l;
            }
            k += 20;
        }
        if (!this.votedName.isEmpty() && this.selectedIndex == 0 && this.playersButtons[0].active) {
            for (int i = 0; i < this.availablePlayers.size(); i++) {
                if (this.availablePlayers.values().stream().toList().get(i).equals(this.votedName)) {
                    this.selectedIndex = i;
                }
            }
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);

        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawText(this.textRenderer, title, this.x + this.backgroundWidth / 2 - this.textRenderer.getWidth(this.title) / 2, this.y + 6, 0x7C7160, false);

        if (!this.availablePlayers.isEmpty()) {
            int i = (this.width - this.backgroundWidth) / 2;
            int j = (this.height - this.backgroundHeight) / 2;
            int k = this.y + 16;
            int l = this.x + 14 + 5;
            this.renderScrollbar(context, i, j, this.availablePlayers.values().stream().toList());
            int m = 0;
            for (String player : this.availablePlayers.values().stream().toList()) {
                if (this.canScroll(this.availablePlayers.size()) && (m < this.indexStartOffset || m >= 7 + this.indexStartOffset)) {
                    ++m;
                    continue;
                }

                int n = k + 7;
                context.drawText(this.textRenderer, getPlayerName(player, 78, 9), l, n, 0xE6D6A9, false);
                k += 20;
                ++m;
            }

            for (int u = 0; u < this.playersButtons.length; u++) {
                if (this.playersButtons[u].isHovered()) {
                    this.playersButtons[u].renderTooltip(context, mouseX, mouseY);
                }
                this.playersButtons[u].visible = this.playersButtons[u].getIndex() < this.availablePlayers.size();
                this.playersButtons[u].active = this.selectedIndex - this.indexStartOffset != u;
            }
            RenderSystem.enableDepthTest();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int i = this.availablePlayers.size();
        if (this.canScroll(i)) {
            int j = i - 7;
            this.indexStartOffset = MathHelper.clamp((int) ((double) this.indexStartOffset - verticalAmount), 0, j);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int i = this.availablePlayers.size();
        if (this.scrolling) {
            int j = this.y + 17;
            int k = j + 139;
            int l = i - 7;
            float f = ((float) mouseY - (float) j - 13.5f) / ((float) (k - j) - 27.0f);
            f = f * (float) l + 0.5f;
            this.indexStartOffset = MathHelper.clamp((int) f, 0, l);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        if (this.canScroll(this.availablePlayers.size()) && mouseX > (double) (i + 103) && mouseX < (double) (i + 103 + 6) && mouseY > (double) (j + 17) && mouseY <= (double) (j + 17 + 139 + 1)) {
            this.scrolling = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderScrollbar(DrawContext context, int x, int y, List<String> availablePlayers) {
        int i = availablePlayers.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int m = Math.min(113, this.indexStartOffset * k);
            if (this.indexStartOffset == i - 1) {
                m = 113;
            }
            context.drawTexture(TEXTURE, x + 103, y + 17 + m, 131, 0, 6, 27, 256, 256);
        } else {
            context.drawTexture(TEXTURE, x + 103, y + 17, 137, 0.0f, 6, 27, 256, 256);
        }
    }

    private boolean canScroll(int listSize) {
        return listSize > 7;
    }

    private Text getPlayerName(String playerName, int length, int substringLength) {
        if (this.client != null && this.client.textRenderer.getWidth(playerName) > length && substringLength != 0) {
            playerName = playerName.substring(0, substringLength) + "..";
        }
        return Text.of(playerName);
    }

    private class WidgetButtonPage extends ButtonWidget {

        private static final ButtonTextures TEXTURES = new ButtonTextures(Mayor.identifierOf("widget/button"), Mayor.identifierOf("widget/button_disabled"), Mayor.identifierOf("widget/button_highlighted"));
        private final int index;

        public WidgetButtonPage(int x, int y, int index, ButtonWidget.PressAction onPress) {
            super(x, y, 88, 20, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.index = index;
            this.visible = false;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            context.drawGuiTexture(TEXTURES.get(this.active, this.isSelected()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int i = this.active ? 16777215 : 10526880;
            this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }

        @Override
        public boolean isSelected() {
            return super.isSelected();
        }

        public int getIndex() {
            return this.index;
        }


        public void renderTooltip(DrawContext context, int mouseX, int mouseY) {
            if (this.hovered) {
                Text text = Text.of(BallotPaperScreen.this.availablePlayers.values().stream().toList().get(this.index + BallotPaperScreen.this.indexStartOffset));
                if (client != null && client.textRenderer.getWidth(text) > 78) {
                    context.drawTooltip(textRenderer, text, mouseX, mouseY);
                }
            }
        }
    }

}

