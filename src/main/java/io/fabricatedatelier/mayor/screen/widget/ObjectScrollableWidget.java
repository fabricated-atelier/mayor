package io.fabricatedatelier.mayor.screen.widget;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.network.packet.EntityViewPacket;
import io.fabricatedatelier.mayor.screen.MayorScreen;
import io.fabricatedatelier.mayor.screen.MayorVillageScreen;
import io.fabricatedatelier.mayor.state.StructureData;
import io.fabricatedatelier.mayor.util.InventoryUtil;
import io.fabricatedatelier.mayor.util.RenderUtil;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ObjectScrollableWidget extends ScrollableWidget {

    private static final Identifier OBJECTS = Mayor.identifierOf("textures/gui/sprites/hud/mayor_objects.png");

    private final Text title;
    private final TextRenderer textRenderer;
    private Screen parentScreen;

    @Nullable
    private List<Object> objects;
    @Nullable
    private List<Text> texts;
    private int rows = 0;
    private int maxRows = 0;

    private int selectedIndex = -1;

    public ObjectScrollableWidget(int x, int y, int width, int height, Text title, TextRenderer textRenderer) {
        super(x, y, width, height, title);
        this.title = title;
        this.textRenderer = textRenderer;
    }

    public void setParentScreen(Screen parentScreen) {
        this.parentScreen = parentScreen;
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
        if (this.texts != null && !this.texts.isEmpty() && this.objects != null) {
            int xSpace = this.getX();
            int ySpace = this.getY();

            int row = (int) this.getScrollY() / 13;
            int rowCount = 0;
            for (int i = row; i < (this.maxRows + row); i++) {
                if (i >= this.texts.size() || i >= this.objects.size()) {
                    break;
                }
                if (this.selectedIndex == i) {
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

                if (this.isPointWithinBounds(1, rowCount * 13 + 2, this.width + 4, 11, mouseX, mouseY)) {
                    if (this.objects.size() > i && this.objects.get(i) instanceof VillagerEntity villagerEntity) {
                        List<Text> villagerTooltip = new ArrayList<>();
                        villagerTooltip.add(villagerEntity.getName());
                        if (villagerEntity.hasCustomName()) {
                            String profession = villagerEntity.getVillagerData().getProfession().id();
                            villagerTooltip.add(Text.translatable("mayor.screen.villager_profession", (profession.substring(0, 1).toUpperCase() + profession.substring(1))));
                        }
                        villagerTooltip.add(Text.translatable("mayor.screen.level", villagerEntity.getVillagerData().getLevel()));
                        context.drawTooltip(this.textRenderer, villagerTooltip, mouseX, mouseY);
                    } else if (isTextToLong) {
                        context.drawTooltip(this.textRenderer, this.texts.get(i), mouseX, mouseY);
                    }
                }
                rowCount += 1;
            }
            if (this.selectedIndex >= 0) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (this.objects.size() > this.selectedIndex && this.objects.get(this.selectedIndex) instanceof StructureData structureData) {
                    if (client.worldRenderer.isRenderingReady(structureData.getBottomCenterPos())) {
                        BlockBox box = structureData.getBlockBox();
                        RenderUtil.renderParticlePole(client, box.getMinX(), box.getMinY(), box.getMinZ(), 1);
                        RenderUtil.renderParticlePole(client, box.getMinX(), box.getMinY(), box.getMaxZ() + 1, 2);
                        RenderUtil.renderParticlePole(client, box.getMaxX() + 1, box.getMinY(), box.getMaxZ() + 1, 3);
                        RenderUtil.renderParticlePole(client, box.getMaxX() + 1, box.getMinY(), box.getMinZ(), 4);
                    }
                }
            }
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.texts != null && !this.texts.isEmpty()) {
            // renderTitle(context);
            this.renderContents(context, mouseX, mouseY, delta);
            this.renderOverlay(context, mouseX, mouseY);
        }
    }

    @Override
    protected void renderOverlay(DrawContext context) {
    }

    private void renderTitle(DrawContext context) {
        context.drawText(this.textRenderer, this.title, this.getX() + this.width - this.textRenderer.getWidth(this.title), this.getY(), Colors.WHITE, false);
    }

    private void renderOverlay(DrawContext context, int mouseX, int mouseY) {
        if (this.overflows()) {
            int row = (int) this.getScrollY() / 13;
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

            if ((this.rows - this.maxRows) > row) {
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
        if (isWithinExtraBounds(mouseX, mouseY, 0, 15)) {
            if (this.texts != null && !this.texts.isEmpty() && this.objects != null) {
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
                        this.selectedIndex = i;
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

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    private boolean isWithinExtraBounds(double mouseX, double mouseY, int extraX, int extraY) {
        return mouseX >= (double) this.getX() - extraX && mouseX < this.getX() + this.width + 4 + extraX && mouseY < (double) this.getY() + this.maxRows * 13 + extraY && mouseY >= (double) (this.getY() - extraY);
    }

    private void clicked() {
        if (this.objects != null && !this.objects.isEmpty() && this.objects.size() > this.selectedIndex) {
            if (this.parentScreen instanceof MayorScreen mayorScreen) {
                if (this.objects.getFirst() instanceof MayorCategory.BuildingCategory) {
                    if (this.selectedIndex < 0) {
                        mayorScreen.setSelectedCategory(null);
                    } else {
                        mayorScreen.setSelectedCategory((MayorCategory.BuildingCategory) this.objects.get(this.selectedIndex));
                        List<Object> objects = new ArrayList<>();
                        List<Text> texts = new ArrayList<>();
                        if (mayorScreen.getAvailableStructureMap().containsKey((MayorCategory.BuildingCategory) this.objects.get(this.selectedIndex))) {
                            for (MayorStructure entry : mayorScreen.getAvailableStructureMap().get((MayorCategory.BuildingCategory) this.objects.get(this.selectedIndex))) {
                                objects.add(entry);
                                texts.add(Text.translatable("building_" + entry.getIdentifier().getPath()));
                            }
                            mayorScreen.getBuildingScrollableWidget().setObjects(objects, texts);
                            mayorScreen.getBuildingScrollableWidget().setScrollY(0);
                            mayorScreen.getBuildingScrollableWidget().setSelectedIndex(-1);

                            mayorScreen.getMayorManager().setMayorStructure(null);
                            mayorScreen.getRequiredItemScrollableWidget().setItemStacks(null);

                            mayorScreen.getBuildButton().active = false;
                            mayorScreen.getBuildButton().visible = false;
                        }
                    }
                } else if (this.objects.get(this.selectedIndex) instanceof MayorStructure mayorStructure) {
                    mayorScreen.getMayorManager().setMayorStructure(mayorStructure);
                    mayorScreen.getRequiredItemScrollableWidget().setItemStacks(((MayorStructure) this.objects.get(this.selectedIndex)).getRequiredItemStacks());
                    if ((MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.isCreativeLevelTwoOp()) || (InventoryUtil.getMissingItems(mayorScreen.getAvailableStacks(), mayorStructure.getRequiredItemStacks()).isEmpty() && InventoryUtil.hasRequiredPrice(MinecraftClient.getInstance().player.getInventory(), mayorStructure.getPrice()))) {
                        if (mayorScreen.getMayorManager().getAvailableBuilder() > 0) {
                            mayorScreen.getBuildButton().active = true;
                        }
                        mayorScreen.getBuildButton().visible = true;
                    } else {
                        mayorScreen.getBuildButton().active = false;
                        mayorScreen.getBuildButton().visible = false;
                    }
                }
            } else if (this.parentScreen instanceof MayorVillageScreen mayorVillageScreen) {
                if (this.objects.get(this.selectedIndex) instanceof VillagerEntity villagerEntity) {
                    new EntityViewPacket(villagerEntity.getId()).sendPacket();
                } else if (this.objects.get(this.selectedIndex) instanceof StructureData structureData) {
                    MayorStructure mayorUpgradeStructure = StructureHelper.getUpgradeStructure(structureData.getIdentifier(), mayorVillageScreen.getMayorManager().getBiomeCategory());

                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null) {
                        boolean creativeLevelTwoOp = client.player.isCreativeLevelTwoOp();

                        if (mayorUpgradeStructure != null) {
                            List<ItemStack> requiredItemStacks = InventoryUtil.getMissingItems(StructureHelper.getStructureItems(MinecraftClient.getInstance().world, structureData.getBlockBox()), mayorUpgradeStructure.getRequiredItemStacks());
                            mayorVillageScreen.getUpgradeStructureScrollableWidget().setItemStacks(requiredItemStacks);
                            if ((creativeLevelTwoOp || (requiredItemStacks.isEmpty() && InventoryUtil.hasRequiredPrice(MinecraftClient.getInstance().player.getInventory(), mayorUpgradeStructure.getPrice()))) && mayorVillageScreen.getMayorManager().getAvailableBuilder() > 0) {
                                mayorVillageScreen.getUpgradeButton().setUpgradeStructure(mayorUpgradeStructure);
                                mayorVillageScreen.getUpgradeButton().setStructureData(structureData);
                                // Todo: Set price has an issue, probably be better to have a demolish price on the structure data so no set price is needed
                                mayorVillageScreen.getUpgradeButton().setPrice(mayorUpgradeStructure.getPrice());
                                mayorVillageScreen.getUpgradeButton().active = true;
                            } else {
                                mayorVillageScreen.getUpgradeButton().active = false;
                            }
                        }
                        MayorStructure mayorStructure = StructureHelper.getMayorStructureById(structureData.getIdentifier(), mayorVillageScreen.getMayorManager().getBiomeCategory());
                        if (mayorStructure != null) {
                            if ((creativeLevelTwoOp || InventoryUtil.hasRequiredPrice(MinecraftClient.getInstance().player.getInventory(), mayorStructure.getPrice())) && mayorVillageScreen.getMayorManager().getAvailableBuilder() > 0) {
                                mayorVillageScreen.getUpgradeButton().setStructureData(structureData);
                                mayorVillageScreen.getUpgradeButton().setPrice(mayorStructure.getPrice());
                                mayorVillageScreen.getDemolishButton().active = true;
                            } else {
                                mayorVillageScreen.getDemolishButton().active = false;
                            }
                        }

                        if (mayorUpgradeStructure == null && mayorStructure == null) {
                            mayorVillageScreen.getUpgradeButton().setUpgradeStructure(null);
                            mayorVillageScreen.getUpgradeButton().setStructureData(null);
                            mayorVillageScreen.getUpgradeButton().setPrice(0);
                            mayorVillageScreen.getUpgradeStructureScrollableWidget().setItemStacks(null);
                            mayorVillageScreen.getUpgradeButton().visible = false;
                            mayorVillageScreen.getDemolishButton().visible = false;
                            this.selectedIndex = -1;
                        } else {
                            mayorVillageScreen.getUpgradeButton().visible = true;
                            mayorVillageScreen.getDemolishButton().visible = true;
                        }
                    }
//                    if (mayorUpgradeStructure != null) {
//
//                        mayorVillageScreen.getUpgradeStructureScrollableWidget().setItemStacks(requiredItemStacks);
//                        if ((MinecraftClient.getInstance().player != null && (MinecraftClient.getInstance().player.isCreativeLevelTwoOp()) || (requiredItemStacks.isEmpty() && InventoryUtil.hasRequiredPrice(MinecraftClient.getInstance().player.getInventory(), mayorUpgradeStructure.getPrice())))) {
//                            if (mayorVillageScreen.getMayorManager().getAvailableBuilder() > 0) {
//                                mayorVillageScreen.getUpgradeButton().setStructureData(structureData);
//                                mayorVillageScreen.getUpgradeButton().active = true;
//                                mayorVillageScreen.getDemolishButton().active = true;
//                            } else if (MinecraftClient.getInstance().player.isCreativeLevelTwoOp()) {
//                                mayorVillageScreen.getDemolishButton().active = true;
//                            }
//                            mayorVillageScreen.getUpgradeButton().setUpgradeStructure(mayorUpgradeStructure);
//                            mayorVillageScreen.getUpgradeButton().visible = true;
//                            mayorVillageScreen.getDemolishButton().visible = true;
////                            System.out.println("TESTXX");
//                        } else {
////                            System.out.println("::A");
//                            mayorVillageScreen.getUpgradeButton().setUpgradeStructure(null);
//                            mayorVillageScreen.getUpgradeButton().setStructureData(null);
//                            mayorVillageScreen.getUpgradeButton().active = false;
//                            mayorVillageScreen.getUpgradeButton().visible = false;
//                            mayorVillageScreen.getDemolishButton().active = false;
//                            mayorVillageScreen.getDemolishButton().visible = false;
//                        }
//                    } else {
//                        this.selectedIndex = -1;
//                        mayorVillageScreen.setUpgradeStructureNotAvailableTicks(60);
//                        mayorVillageScreen.getUpgradeStructureScrollableWidget().setItemStacks(null);
//                    }
                }
            }
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
