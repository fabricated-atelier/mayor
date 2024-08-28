package io.fabricatedatelier.mayor.screen;

import io.fabricatedatelier.mayor.data.StructureXpLoader;
import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.network.packet.EntityListC2SPacket;
import io.fabricatedatelier.mayor.screen.widget.ItemScrollableWidget;
import io.fabricatedatelier.mayor.screen.widget.ObjectScrollableWidget;
import io.fabricatedatelier.mayor.state.StructureData;
import io.fabricatedatelier.mayor.util.StringUtil;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class MayorVillageScreen extends Screen {

    private final MayorManager mayorManager;

    private ObjectScrollableWidget villagerScrollableWidget;
    private ObjectScrollableWidget structureScrollableWidget;
    private ItemScrollableWidget upgradeStructureScrollableWidget;
    private ButtonWidget upgradeButton;

    private int levelX = 0;
    private int levelY = 0;
    private int upgradeStructureNotAvailableTicks = 0;

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
            this.upgradeButton = this.addDrawableChild(ButtonWidget.builder(upgrade, button -> {
                button.active = false;
                button.visible = false;
                this.upgradeStructureScrollableWidget.setItemStacks(null);
                // Send packet to try to upgrade here
            }).build());
            this.upgradeButton.active = false;
            this.upgradeButton.visible = false;
            this.upgradeButton.setWidth(this.textRenderer.getWidth(upgrade) + 7);
            this.upgradeButton.setX(this.width / 2 - this.upgradeButton.getWidth() / 2);
            this.upgradeButton.setY(this.height / 2 + 38);
        }
    }

    public ObjectScrollableWidget getVillagerScrollableWidget() {
        return this.villagerScrollableWidget;
    }

    public ItemScrollableWidget getUpgradeStructureScrollableWidget() {
        return this.upgradeStructureScrollableWidget;
    }

    public ButtonWidget getUpgradeButton() {
        return this.upgradeButton;
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

            if (this.upgradeStructureScrollableWidget.getItemStacks() != null && !this.upgradeStructureScrollableWidget.getItemStacks().isEmpty()) {
                // Render upgrade button here, done by widget
            } else if (this.upgradeStructureNotAvailableTicks > 0) {
                Text text = Text.translatable("mayor.screen.no_structure_upgrade_available");
                String[] strings = text.getString().split(" ");
                for (int i = 0; i < strings.length; i++) {
                    context.drawText(this.textRenderer, strings[i], this.width / 2 - this.textRenderer.getWidth(strings[i]) / 2, this.height / 2 - strings.length * 5 + i * 10, Colors.WHITE, false);
                }
            }

            if (this.client != null && mayorManager.getVillageData().getMayorPlayerUuid() != null && this.client.world != null && this.client.player != null && !mayorManager.getVillageData().getMayorPlayerUuid().equals(this.client.player.getUuid()) && this.client.player.isCreativeLevelTwoOp()) {
                Text mayor = Text.translatable("mayor.screen.mayor", this.client.world.getPlayerByUuid(mayorManager.getVillageData().getMayorPlayerUuid()).getName());
                context.drawText(this.textRenderer, mayor, this.width - this.textRenderer.getWidth(mayor), 10, Colors.WHITE, false);
            }
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);

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

            // Todo: Adjust the 10000 to the correct value maybe in a final list hardcoded?
            Text experience = Text.translatable("mayor.screen.building_value", this.buildingCategoryExperienceMap.get(MayorCategory.BuildingCategory.values()[i]), 10000);
            context.drawText(this.textRenderer, experience, this.width - 20 - this.textRenderer.getWidth(experience), villageBackgroundY + 20 + i * 10, Colors.WHITE, false);


        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.upgradeStructureNotAvailableTicks > 0) {
            this.upgradeStructureNotAvailableTicks--;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyBindings.mayorViewSelectionBind.matchesKey(keyCode, scanCode) || KeyBindings.mayorViewBind.matchesKey(keyCode, scanCode) || this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
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

    public void setUpgradeStructureNotAvailableTicks(int ticks) {
        this.upgradeStructureNotAvailableTicks = ticks;
    }


    private boolean isMouseWithinBounds(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }
}
