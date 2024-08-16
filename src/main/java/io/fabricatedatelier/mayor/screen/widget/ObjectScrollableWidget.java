package io.fabricatedatelier.mayor.screen.widget;

import java.util.ArrayList;
import java.util.List;

import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.screen.MayorScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ObjectScrollableWidget extends ScrollableWidget {

    private static final Identifier OBJECTS = Identifier.of("mayor", "textures/gui/sprites/hud/mayor_objects.png");

    private final Text title;
    private final TextRenderer textRenderer;
    private MayorScreen mayorScreen;

    @Nullable
    private List<Object> objects;
    @Nullable
    private List<Text> texts;
    private int rows = 0;
    private int maxRows = 0;
    @Nullable
    private Text selectedText = null;
    @Nullable
    private Object selectedObject = null;

    public ObjectScrollableWidget(int x, int y, int width, int height, Text title, TextRenderer textRenderer) {
        super(x, y, width, height, title);
        this.title = title;
        this.textRenderer = textRenderer;
    }

    public void setMayorScreen(MayorScreen mayorScreen) {
        this.mayorScreen = mayorScreen;
    }

    public void setObjects(@Nullable List<Object> objects, @Nullable List<Text> texts) {
        this.objects = objects;
        this.texts = texts;
        if (objects != null) {
            this.rows = this.objects.size();
            this.maxRows = Math.min(rows, getMaxRows());
            this.height = maxRows * 13;
        }
    }

    @Override
    protected void appendDefaultNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder var1) {
    }

    @Override
    protected int getContentsHeight() {
        return this.rows * 13;
    }

    @Override
    protected boolean overflows() {
        return this.rows > this.maxRows;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return 13;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.texts != null && !this.texts.isEmpty()) {
            int xSpace = this.getX();
            int ySpace = this.getY();

            int row = (int) this.getScrollY() / 13;
            int rowCount = 0;
            for (int i = row; i < (this.maxRows + row); i++) {
                if (i >= this.texts.size()) {
                    break;
                }
                if (this.selectedText != null && this.selectedText.equals(this.texts.get(i))) {
                    // left start
                    context.drawTexture(OBJECTS, xSpace, ySpace + rowCount * 13, 17, 36, 2, 13, 128, 128);
                    // middle
                    context.drawTexture(OBJECTS, xSpace + 2, ySpace + rowCount * 13, this.width, 13, 19, 36, 13, 13, 128, 128);
                    // right end
                    context.drawTexture(OBJECTS, xSpace + this.width + 2, ySpace + rowCount * 13, 32, 36, 2, 13, 128, 128);
                } else if (this.isPointWithinBounds(1, rowCount * 13 + 2, this.width + 4, 11, mouseX, mouseY)) {
                    // left start
                    context.drawTexture(OBJECTS, xSpace, ySpace + rowCount * 13, 34, 36, 2, 13, 128, 128);
                    // middle
                    context.drawTexture(OBJECTS, xSpace + 2, ySpace + rowCount * 13, this.width, 13, 36, 36, 13, 13, 128, 128);
                    // right end
                    context.drawTexture(OBJECTS, xSpace + this.width + 2, ySpace + rowCount * 13, 49, 36, 2, 13, 128, 128);
                } else {
                    // left start
                    context.drawTexture(OBJECTS, xSpace, ySpace + rowCount * 13, 0, 36, 2, 13, 128, 128);
                    // middle
                    context.drawTexture(OBJECTS, xSpace + 2, ySpace + rowCount * 13, this.width, 13, 2, 36, 13, 13, 128, 128);
                    // right end
                    context.drawTexture(OBJECTS, xSpace + this.width + 2, ySpace + rowCount * 13, 15, 36, 2, 13, 128, 128);
                }
                Text text = this.texts.get(i);
                boolean isTextToLong = this.textRenderer.getWidth(text) > (this.width - 8);
                if (isTextToLong) {
                    String string = text.getString().substring(0, (this.width - 16) / 5) + "..";
                    text = Text.of(string);
                }
                context.drawText(this.textRenderer, text, xSpace + this.width / 2 - this.textRenderer.getWidth(text) / 2 + 3, ySpace + 3 + rowCount * 13, Colors.WHITE, false);

                if (isTextToLong && this.isPointWithinBounds(1, rowCount * 13 + 2, this.width + 4, 11, mouseX, mouseY)) {
                    context.drawTooltip(this.textRenderer, this.texts.get(i), mouseX, mouseY);
                }
                rowCount += 1;
            }
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.texts != null) {
            renderTitle(context);
            this.renderContents(context, mouseX, mouseY, delta);
            this.renderOverlay(context, mouseX, mouseY);
        }
    }

    @Override
    protected void renderOverlay(DrawContext context) {
    }

    private void renderTitle(DrawContext context) {
        context.drawText(this.textRenderer, this.title, this.getX() + this.width - this.textRenderer.getWidth(this.title), this.getY() - this.maxRows * 18 - 16, Colors.WHITE, false);
    }

    private void renderOverlay(DrawContext context, int mouseX, int mouseY) {
        if (this.overflows()) {
            int row = (int) this.getScrollY() / 18;
            int arrowX = this.getX() + this.width / 2 - 8;

            // top arrow
            int topArrowY = this.getY() - 15;
            context.drawTexture(OBJECTS, arrowX, topArrowY, 0, 24, 17, 12, 128, 128);
            if (row > 0) {

                if (isPointWithinBounds(arrowX - this.getX(), topArrowY - this.getY(), 17, 12, mouseX, mouseY)) {
                    context.drawTexture(OBJECTS, arrowX, topArrowY, 0, 12, 17, 12, 128, 128);
                } else {
                    context.drawTexture(OBJECTS, arrowX, topArrowY, 0, 0, 17, 12, 128, 128);
                }
            }
            // bottom arrow
            int bottomArrowY = this.getY() + this.maxRows * 13 + 3;
            context.drawTexture(OBJECTS, arrowX, bottomArrowY, 17, 24, 17, 12, 128, 128);

            if ((this.rows - this.maxRows) > row + 1) {
                if (isPointWithinBounds(arrowX - this.getX(), bottomArrowY - this.getY(), 17, 12, mouseX, mouseY)) {
                    context.drawTexture(OBJECTS, arrowX, bottomArrowY, 17, 12, 17, 12, 128, 128);
                } else {
                    context.drawTexture(OBJECTS, arrowX, bottomArrowY, 17, 0, 17, 12, 128, 128);
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
        if (isWithinBounds(mouseX, mouseY)) {
            if (this.texts != null && !this.texts.isEmpty()) {
                if (isPointWithinBounds(this.width / 2 - 8, -15, 17, 12, mouseX, mouseY)) {
                    this.setScrollY(this.getScrollY() - this.getDeltaYPerScroll());
                    return true;
                } else if (isPointWithinBounds(this.width / 2 - 8, this.maxRows * 13 + 3, 17, 12, mouseX, mouseY)) {
                    this.setScrollY(this.getScrollY() + this.getDeltaYPerScroll());
                    return true;
                }

                int row = (int) this.getScrollY() / 13;
                int rowCount = 0;
                for (int i = row; i < (this.maxRows + row); i++) {
                    if (i >= this.texts.size()) {
                        break;
                    }
                    if (isPointWithinBounds(0, rowCount * 13, this.width + 4, 13, mouseX, mouseY)) {
                        this.selectedText = this.texts.get(i);
                        this.selectedObject = this.objects.get(i);
                        clicked();
                        return true;
                    }
                    rowCount += 1;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    @Override
    protected boolean isWithinBounds(double mouseX, double mouseY) {
        return mouseX >= (double) this.getX() && mouseX < this.getX() + this.width + 4 && mouseY < (double) this.getY() + this.maxRows * 13 && mouseY >= (double) (this.getY());
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible && isWithinBounds(mouseX, mouseY);
    }

    private void clicked() {
        if (this.objects != null && !this.objects.isEmpty()) {
            if (this.objects.getFirst() instanceof MayorCategory.BuildingCategory) {
                if (this.selectedObject == null) {
                    this.mayorScreen.setSelectedCategory(null);
                } else {
                    this.mayorScreen.setSelectedCategory((MayorCategory.BuildingCategory) this.selectedObject);
                    List<Object> objects = new ArrayList<>();
                    List<Text> texts = new ArrayList<>();
                    for (MayorStructure entry : this.mayorScreen.getAvailableStructureMap().get((MayorCategory.BuildingCategory) this.selectedObject)) {
                        objects.add(entry);
                        texts.add(Text.translatable("building_" + entry.getIdentifier().getPath()));
                    }
                    this.mayorScreen.getBuildingScrollableWidget().setObjects(objects, texts);
                    this.mayorScreen.getBuildingScrollableWidget().setScrollY(0);


                }
            } else if (this.objects.getFirst() instanceof MayorStructure) {
                this.mayorScreen.getMayorManager().setMayorStructure((MayorStructure) this.selectedObject);
                this.mayorScreen.getRequiredItemScrollableWidget().setItemStacks(((MayorStructure) this.selectedObject).getRequiredItemStacks());
            }
        }
    }

    private boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        int i = this.getX();
        int j = this.getY();
        return (pointX -= (double) i) >= (double) (x - 1) && pointX < (double) (x + width + 1) && (pointY -= (double) j) >= (double) (y - 1) && pointY < (double) (y + height + 1);
    }

    public static int getMaxRows() {
        if (MinecraftClient.getInstance().currentScreen != null) {
            int screenHeight = MinecraftClient.getInstance().currentScreen.height;
            return (screenHeight - 40) / 13;
        }
        return 10;
    }

}
