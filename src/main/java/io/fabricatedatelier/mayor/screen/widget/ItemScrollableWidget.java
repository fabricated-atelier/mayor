package io.fabricatedatelier.mayor.screen.widget;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ItemScrollableWidget extends ScrollableWidget {

    public static final Identifier SLOTS = Identifier.of("mayor", "textures/gui/sprites/hud/mayor_slots.png");

    private final Text title;
    private final TextRenderer textRenderer;

    @Nullable
    private List<ItemStack> itemStacks;
    private int rows = 0;
    private int maxRows = 0;

    public ItemScrollableWidget(int x, int y, int width, int height, Text title, TextRenderer textRenderer) {
        super(x, y, width, height, title);
        this.title = title;
        this.textRenderer = textRenderer;
    }

    public void setItemStacks(@Nullable List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
        if (itemStacks != null) {
            this.rows = this.itemStacks.size() / 6 + (this.itemStacks.size() % 6 == 0 ? 0 : 1);
            this.maxRows = Math.min(rows, getMaxRows());
            this.height = maxRows * 18;
        }
    }

    @Nullable
    public  List<ItemStack> getItemStacks(){
        return this.itemStacks;
    }

    @Override
    protected void appendDefaultNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder var1) {
    }

    @Override
    protected int getContentsHeight() {
        return this.rows * 18;
    }

    @Override
    protected boolean overflows() {
        return this.rows > this.maxRows;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 18;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.itemStacks.isEmpty()) {
            int ySpace = this.getY() + 1;
            int xSpace = this.getX() + 6;

            int row = (int) this.getScrollY() / 18;
            for (int u = row * 6; u < this.itemStacks.size() && u < ((this.maxRows * 6) + row * 6); u++) {
                context.drawItemWithoutEntity(this.itemStacks.get(u), xSpace, ySpace - this.maxRows * 18);
                context.drawItemInSlot(this.textRenderer, this.itemStacks.get(u), xSpace, ySpace - this.maxRows * 18);

                if (this.isPointWithinBounds(xSpace - this.getX(), ySpace - this.maxRows * 18 - this.getY(), 16, 16, mouseX, mouseY)) {
                    context.drawTooltip(this.textRenderer, this.itemStacks.get(u).getName(), mouseX, mouseY);
                }
                xSpace += 18;
                if ((u + 1) % 6 == 0) {
                    xSpace = this.getX() + 6;
                    ySpace += 18;
                }
            }
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.itemStacks != null) {
            renderBackground(context);
            renderTitle(context);
            this.renderContents(context, mouseX, mouseY, delta);
            this.renderOverlay(context, mouseX, mouseY);
        }
    }

    @Override
    protected void renderOverlay(DrawContext context) {
    }

    private void renderTitle(DrawContext context) {
        context.drawText(this.textRenderer, this.title, this.getX() + this.width - this.textRenderer.getWidth(this.title), this.getY() - this.maxRows * 18 - 15, Colors.WHITE, false);
    }

    private void renderOverlay(DrawContext context, int mouseX, int mouseY) {
        if (this.overflows()) {
            context.drawTexture(SLOTS, this.getX() + 118, this.getY() - this.maxRows * 18 - 5, 0, 28, 9, 18, 128, 128);

            int row = (int) this.getScrollY() / 18;
            // down
            if ((this.rows - this.maxRows) > row) {
                if (isPointWithinBounds(118, -this.maxRows * 18 + 4, 9, 9, mouseX, mouseY)) {
                    context.drawTexture(SLOTS, this.getX() + 118, this.getY() - this.maxRows * 18 + 4, 18, 37, 9, 9, 128, 128);
                } else {
                    context.drawTexture(SLOTS, this.getX() + 118, this.getY() - this.maxRows * 18 + 4, 9, 37, 9, 9, 128, 128);
                }
            }
            if (row > 0) {
                // up
                if (isPointWithinBounds(118, -this.maxRows * 18 - 5, 9, 9, mouseX, mouseY)) {
                    context.drawTexture(SLOTS, this.getX() + 118, this.getY() - this.maxRows * 18 - 5, 18, 28, 9, 9, 128, 128);
                } else {
                    context.drawTexture(SLOTS, this.getX() + 118, this.getY() - this.maxRows * 18 - 5, 9, 28, 9, 9, 128, 128);
                }
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.visible) {
            return false;
        }
        this.setScrollY(this.getScrollY() - verticalAmount * this.getDeltaYPerScroll());
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible) {
            return false;
        }
        if (isPointWithinBounds(118, -this.maxRows * 18 + 4, 9, 9, mouseX, mouseY)) {
            this.setScrollY(this.getScrollY() + this.getDeltaYPerScroll());
            return true;
        } else if (isPointWithinBounds(118, -this.maxRows * 18 - 5, 9, 9, mouseX, mouseY)) {
            this.setScrollY(this.getScrollY() - this.getDeltaYPerScroll());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    protected boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= (double) this.getX() && mouseX < this.getX() + this.width && mouseY >= (double) this.getY() - this.maxRows * 18 - 5 && mouseY < (double) (this.getY());
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && isWithinBounds(mouseX, mouseY);
    }

    private void renderBackground(DrawContext context) {
        // top + first row
        context.drawTexture(SLOTS, this.getX(), this.getY() - this.maxRows * 18 - 5, 0, 0, 118, 23, 128, 128);
        for (int i = 1; i < this.maxRows; i++) {
            context.drawTexture(SLOTS, this.getX(), this.getY() - i * 18, 0, 5, 118, 18, 128, 128);
        }
        // bottom
        context.drawTexture(SLOTS, this.getX(), this.getY(), 0, 23, 118, 5, 128, 128);
    }

    private boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        int i = this.getX();
        int j = this.getY();
        return (pointX -= (double) i) >= (double) (x - 1) && pointX < (double) (x + width + 1) && (pointY -= (double) j) >= (double) (y - 1) && pointY < (double) (y + height + 1);
    }

    public static int getMaxRows() {
        if (MinecraftClient.getInstance().currentScreen != null) {
            int screenHeight = MinecraftClient.getInstance().currentScreen.height;
            if (screenHeight < 256) {
                return 3;
            } else if (screenHeight > 400) {
                return 5;
            }
        }
        return 4;
    }

}
