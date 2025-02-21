package io.fabricatedatelier.mayor.screen;

import io.fabricatedatelier.mayor.init.MayorKeyBind;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.network.packet.EntityListC2SPacket;
import io.fabricatedatelier.mayor.network.packet.MayorUpdatePacket;
import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.network.packet.StructureBuildPacket;
import io.fabricatedatelier.mayor.screen.widget.ItemScrollableWidget;
import io.fabricatedatelier.mayor.screen.widget.ObjectScrollableWidget;
import io.fabricatedatelier.mayor.state.StructureData;
import io.fabricatedatelier.mayor.util.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class MayorVillageScreen extends Screen {

    private final MayorManager mayorManager;

    private static final ItemStack EMERALD = new ItemStack(Items.EMERALD);

    private ObjectScrollableWidget villagerScrollableWidget;
    private ObjectScrollableWidget structureScrollableWidget;
    private ItemScrollableWidget upgradeStructureScrollableWidget;
    private StructureButton upgradeStructureButton;
    private ButtonWidget demolishStructureButton;
    private ButtonWidget dismissButton;

    private int levelX = 0;
    private int levelY = 0;
    private int showTextTicks = 0;
    @Nullable
    private Text showText = null;

    private Map<MayorCategory.BuildingCategory, Integer> buildingCategoryExperienceMap = new HashMap<>();

    public MayorVillageScreen(MayorManager mayorManager) {
        super(Text.translatable("mayor.screen.title"));
        this.mayorManager = mayorManager;
    }

    @Override
    protected void init() {
        super.init();
        if (mayorManager.getVillageData() != null) {
            if (!mayorManager.getVillageData().getVillagers().isEmpty()) {
                new EntityListC2SPacket(mayorManager.getVillageData().getVillagers()).sendPacket();
            }
            this.villagerScrollableWidget = this.addDrawableChild(new ObjectScrollableWidget(16, 16, 70, 170, Text.translatable("mayor.screen.villagers", mayorManager.getVillageData().getVillagers().size()), this.textRenderer));
            this.villagerScrollableWidget.setParentScreen(this);

            this.structureScrollableWidget = this.addDrawableChild(new ObjectScrollableWidget(106, 16, 70, 170, Text.translatable("mayor.screen.structures", mayorManager.getVillageData().getStructures().size()), this.textRenderer));
            List<Object> objects = new ArrayList<>();
            List<Text> texts = new ArrayList<>();

            for (int i = 0; i < MayorCategory.BuildingCategory.values().length; i++) {
                this.buildingCategoryExperienceMap.put(MayorCategory.BuildingCategory.values()[i], 0);
            }
            for (StructureData structureData : mayorManager.getVillageData().getStructures().values()) {
                objects.add(structureData);
                texts.add(Text.of(StringUtil.getStructureName(structureData.getIdentifier())));

                MayorCategory.BuildingCategory buildingCategory = StructureHelper.getBuildingCategory(structureData.getIdentifier());
                int experience = this.buildingCategoryExperienceMap.get(buildingCategory);
                experience += structureData.getExperience();
                this.buildingCategoryExperienceMap.put(buildingCategory, experience);
            }
            this.structureScrollableWidget.setObjects(objects, texts);
            this.structureScrollableWidget.setParentScreen(this);

            this.upgradeStructureScrollableWidget = this.addDrawableChild(new ItemScrollableWidget(this.width / 2 - 59, this.height / 2 + 18, 118, 28, Text.translatable("mayor.screen.required_items"), this.textRenderer));
            Text upgrade = Text.translatable("mayor.screen.upgrade");
            this.upgradeStructureButton = this.addDrawableChild(new StructureButton(this.width / 2 - (this.textRenderer.getWidth(upgrade) + 7 + 2), this.height / 2 + 90, this.textRenderer.getWidth(upgrade) + 7, 20, upgrade, button -> {
                button.active = false;
                button.visible = false;
                this.upgradeStructureScrollableWidget.setItemStacks(null);
                this.demolishStructureButton.active = false;
                this.demolishStructureButton.visible = false;
                if (((StructureButton) button).getUpgradeStructure() != null) {
                    new StructureBuildPacket(((StructureButton) button).getUpgradeStructure().getIdentifier(), ((StructureButton) button).getStructureData().getBottomCenterPos(), ((StructureButton) button).getStructureData().getRotation(), true, 1).sendPacket();
                }
            }));
            this.upgradeStructureButton.active = false;
            this.upgradeStructureButton.visible = false;

            Text demolish = Text.translatable("mayor.screen.demolish");
            this.demolishStructureButton = this.addDrawableChild(new StructureButton(this.width / 2 + 2, this.height / 2 + 90, this.textRenderer.getWidth(demolish) + 7, 20, demolish, button -> {
                button.active = false;
                button.visible = false;
                this.upgradeStructureButton.active = false;
                this.upgradeStructureButton.visible = false;
                this.upgradeStructureScrollableWidget.setItemStacks(null);
                if (this.upgradeStructureButton.getStructureData() != null) {
                    new StructureBuildPacket(this.upgradeStructureButton.getStructureData().getIdentifier(), this.upgradeStructureButton.getStructureData().getBottomCenterPos(), this.upgradeStructureButton.getStructureData().getRotation(), true, 2).sendPacket();
                }
            }));
            this.demolishStructureButton.active = false;
            this.demolishStructureButton.visible = false;

            Text dismiss = Text.translatable("mayor.screen.dismiss");
            if (this.mayorManager.getVillageData().getMayorPlayerUuid() != null && this.client != null && this.client.player != null && this.client.player.getUuid().equals(this.mayorManager.getVillageData().getMayorPlayerUuid())) {
                this.dismissButton = this.addDrawableChild(ButtonWidget.builder(dismiss, button -> {
                    if (this.client != null && this.client.player != null) {
                        this.client.setScreen(new PopupScreen.Builder(this, Text.translatable("mayor.screen.dismiss.confirm")).button(ScreenTexts.YES, screen -> {
                            new MayorViewPacket(false).sendClientPacket();
                            new MayorUpdatePacket(this.mayorManager.getVillageData().getCenterPos(), this.mayorManager.getVillageData().getLevel(), this.client.player.getUuid(), true).sendPacket();
                            this.close();
                        }).button(ScreenTexts.CANCEL, PopupScreen::close).build());
                    }
                }).build());

                this.dismissButton.setWidth(this.textRenderer.getWidth(dismiss) + 7);
                this.dismissButton.setX(this.width - 7 - this.dismissButton.getWidth());
                this.dismissButton.setY(this.height - 27);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (mayorManager.getVillageData() != null) {
            int villageMiddleX = this.width / 2;
            int villageY = 10;
            String name = mayorManager.getVillageData().getName();
            int villageLeft = villageMiddleX - this.textRenderer.getWidth(name) / 2;

            context.drawTexture(MayorScreen.VILLAGE, villageLeft - 6, villageY, 0, 0, 10, 23, 128, 128);
            context.drawTexture(MayorScreen.VILLAGE, villageLeft - 6 + 10, villageY, this.textRenderer.getWidth(name) - 9, 23, 10, 0, 5, 23, 128, 128);
            context.drawTexture(MayorScreen.VILLAGE, villageLeft - 6 + 10 + this.textRenderer.getWidth(name) - 9, villageY, 15, 0, 10, 23, 128, 128);
            context.drawText(this.textRenderer, name, villageLeft, villageY + 8, Colors.WHITE, false);

            this.levelX = villageMiddleX + this.textRenderer.getWidth(name) / 2;
            this.levelY = villageY + 9;
            context.drawTexture(MayorScreen.VILLAGE, this.levelX, this.levelY, 0, 23, 19, 19, 128, 128);
            context.drawText(this.textRenderer, Text.of(String.valueOf(mayorManager.getVillageData().getLevel())), this.levelX + 7, this.levelY + 6, Colors.WHITE, false);

            if (isMouseWithinBounds(this.levelX, this.levelY, 19, 19, mouseX, mouseY)) {
                context.drawTexture(MayorScreen.VILLAGE, this.levelX, this.levelY, 19, 23, 19, 19, 128, 128);
            }

            if (this.upgradeStructureScrollableWidget.getItemStacks() != null && !this.upgradeStructureScrollableWidget.getItemStacks().isEmpty() && this.upgradeStructureButton.visible) {
                // Render upgrade button here, done by widget
            } else if (this.showTextTicks > 0 && showText != null) {
                String[] strings = showText.getString().split(" ");
                for (int i = 0; i < strings.length; i++) {
                    context.drawText(this.textRenderer, strings[i], this.width / 2 - this.textRenderer.getWidth(strings[i]) / 2, this.height / 2 - strings.length * 5 + i * 10, Colors.WHITE, false);
                }
            }

            if (this.client != null && mayorManager.getVillageData().getMayorPlayerUuid() != null && this.client.world != null && this.client.player != null && !mayorManager.getVillageData().getMayorPlayerUuid().equals(this.client.player.getUuid()) && this.client.player.isCreativeLevelTwoOp() && this.client.world.getPlayerByUuid(mayorManager.getVillageData().getMayorPlayerUuid()) != null) {
                Text mayor = Text.translatable("mayor.screen.mayor", this.client.world.getPlayerByUuid(mayorManager.getVillageData().getMayorPlayerUuid()).getName());
                context.drawText(this.textRenderer, mayor, this.width - this.textRenderer.getWidth(mayor) - 8, 6, Colors.WHITE, false);
            }
            if (isMouseWithinBounds(1, 1, 11, 13, mouseX, mouseY)) {
                context.drawTexture(MayorScreen.VILLAGE, 1, 1, 67, 0, 11, 13, 128, 128);
                List<Text> buttonTooltip = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    buttonTooltip.add(Text.translatable("mayor.screen.button.tooltip." + i));
                }
                context.drawTooltip(this.textRenderer, buttonTooltip, mouseX, mouseY + 10);
            } else {
                context.drawTexture(MayorScreen.VILLAGE, 1, 1, 56, 0, 11, 13, 128, 128);
            }
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);

        if (this.mayorManager.getVillageData() != null) {
            if (this.mayorManager.getVillageData().getLevel() < VillageHelper.VILLAGE_MAX_LEVEL) {
                int villageBackgroundX = this.width - 170;
                int villageBackgroundY = 16;
                int maxWidth = 160;
                int maxHeight = 20 + MayorCategory.BuildingCategory.values().length * 10;
                // top left
                context.drawTexture(MayorScreen.VILLAGE, villageBackgroundX - 4, villageBackgroundY, 25, 0, 7, 7, 128, 128);
                // top middle
                context.drawTexture(MayorScreen.VILLAGE, villageBackgroundX - 4 + 7, villageBackgroundY, maxWidth - 7, 7, 32, 0, 7, 7, 128, 128);
                // top right
                context.drawTexture(MayorScreen.VILLAGE, villageBackgroundX - 4 + 7 + maxWidth - 7, villageBackgroundY, 39, 0, 7, 7, 128, 128);
                // middle left
                context.drawTexture(MayorScreen.VILLAGE, villageBackgroundX - 4, villageBackgroundY + 7, 7, maxHeight, 25, 7, 7, 7, 128, 128);
                // middle middle
                context.drawTexture(MayorScreen.VILLAGE, villageBackgroundX - 4 + 7, villageBackgroundY + 7, maxWidth - 7, maxHeight, 32, 7, 7, 7, 128, 128);
                // middle right
                context.drawTexture(MayorScreen.VILLAGE, villageBackgroundX - 4 + 7 + maxWidth - 7, villageBackgroundY + 7, 7, maxHeight, 39, 7, 7, 7, 128, 128);
                // bottom left
                context.drawTexture(MayorScreen.VILLAGE, villageBackgroundX - 4, villageBackgroundY + maxHeight + 7, 25, 14, 7, 7, 128, 128);
                // bottom middle
                context.drawTexture(MayorScreen.VILLAGE, villageBackgroundX - 4 + 7, villageBackgroundY + maxHeight + 7, maxWidth - 7, 7, 32, 14, 7, 7, 128, 128);
                // bottom right
                context.drawTexture(MayorScreen.VILLAGE, villageBackgroundX - 4 + 7 + maxWidth - 7, villageBackgroundY + maxHeight + 7, 39, 14, 7, 7, 128, 128);


                context.drawText(this.textRenderer, Text.translatable("mayor.screen.category_values"), villageBackgroundX + 5, villageBackgroundY + 7, Colors.WHITE, true);
                for (int i = 0; i < MayorCategory.BuildingCategory.values().length; i++) {
                    Text category = Text.translatable(MayorCategory.BuildingCategory.values()[i].name());
                    context.drawText(this.textRenderer, category, villageBackgroundX + 10, villageBackgroundY + 20 + i * 10, Colors.WHITE, false);
                    context.drawText(this.textRenderer, ":", villageBackgroundX + 10 + this.textRenderer.getWidth(category), villageBackgroundY + 20 + i * 10, Colors.WHITE, false);

                    int requiredBuildingCategoryExperience = VillageHelper.getVillageLevelBuildingExperienceRequirement(this.mayorManager.getVillageData().getLevel() + 1, MayorCategory.BuildingCategory.values()[i]);
                    Text experience = Text.translatable("mayor.screen.building_value", this.buildingCategoryExperienceMap.get(MayorCategory.BuildingCategory.values()[i]), requiredBuildingCategoryExperience);

                    context.drawText(this.textRenderer, experience, this.width - 20 - this.textRenderer.getWidth(experience), villageBackgroundY + 20 + i * 10, requiredBuildingCategoryExperience <= this.buildingCategoryExperienceMap.get(MayorCategory.BuildingCategory.values()[i]) ? 32319 : Colors.WHITE, false);
                }
            }

            if (this.upgradeStructureButton.visible) {
                RenderUtil.renderCustomBackground(context, this.width / 2 - 56, this.height / 2 + 50, 112, 60);

                Text builder = Text.translatable("mayor.screen.builder", this.mayorManager.getAvailableBuilder());
                context.drawText(this.textRenderer, builder, this.width / 2 - this.textRenderer.getWidth(builder) / 2, this.height / 2 + 60, Colors.GRAY, false);

                int buildingCost = this.upgradeStructureButton.getPrice();
                Text buildingConst = Text.translatable("mayor.screen.building_cost", buildingCost);

                int extraWidth = 8; // cause of emeralds
                context.drawText(this.textRenderer, buildingConst, this.width / 2 - this.textRenderer.getWidth(buildingConst) / 2 - extraWidth, this.height / 2 + 75, Colors.GRAY, false);
                int priceX = this.width / 2 + this.textRenderer.getWidth(buildingConst) / 2 - extraWidth + 4;
                context.drawItem(EMERALD, priceX, this.height / 2 + 70);
                context.drawItemInSlot(this.textRenderer, EMERALD, priceX, this.height / 2 + 70, String.valueOf(buildingCost));
                if (this.client != null && this.client.player != null && InventoryUtil.hasRequiredPrice(this.client.player.getInventory(), buildingCost)) {
                    context.drawTexture(MayorScreen.VILLAGE, priceX + 12, this.height / 2 + 70, 46, 0, 7, 6, 128, 128);
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.showTextTicks > 0) {
            this.showTextTicks--;
            if (this.showTextTicks <= 0) {
                this.showText = null;
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (MayorKeyBind.MAYOR_VIEW_SELECTION.get().matchesKey(keyCode, scanCode) || MayorKeyBind.MAYOR_VIEW.get().matchesKey(keyCode, scanCode) || this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.client.setScreen(new MayorScreen(this.mayorManager));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseWithinBounds(this.levelX, this.levelY, 19, 19, mouseX, mouseY)) {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.client.setScreen(new MayorScreen(this.mayorManager));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public MayorManager getMayorManager() {
        return mayorManager;
    }

    public ObjectScrollableWidget getVillagerScrollableWidget() {
        return this.villagerScrollableWidget;
    }

    public ItemScrollableWidget getUpgradeStructureScrollableWidget() {
        return this.upgradeStructureScrollableWidget;
    }

    public StructureButton getUpgradeButton() {
        return this.upgradeStructureButton;
    }

    public ButtonWidget getDemolishButton() {
        return this.demolishStructureButton;
    }

    public void setShowText(Text text) {
        this.showText = text;
        this.showTextTicks = 80;
    }

    private boolean isMouseWithinBounds(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }

    public static class StructureButton extends ButtonWidget {

        @Nullable
        private MayorStructure upgradeStructure = null;
        @Nullable
        private StructureData structureData = null;
        private int price = 0;

        public StructureButton(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        }

        public void setUpgradeStructure(@Nullable MayorStructure mayorStructure) {
            this.upgradeStructure = mayorStructure;
        }

        @Nullable
        public MayorStructure getUpgradeStructure() {
            return this.upgradeStructure;
        }

        public void setStructureData(@Nullable StructureData structureData) {
            this.structureData = structureData;
        }

        @Nullable
        public StructureData getStructureData() {
            return this.structureData;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getPrice() {
            return price;
        }
    }
}
