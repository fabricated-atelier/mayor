package io.fabricatedatelier.mayor.screen;

import io.fabricatedatelier.mayor.data.StructureXpLoader;
import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.network.packet.EntityListC2SPacket;
import io.fabricatedatelier.mayor.screen.widget.ObjectScrollableWidget;
import io.fabricatedatelier.mayor.state.StructureData;
import io.fabricatedatelier.mayor.util.StringUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class MayorVillageScreen extends Screen {

    private final MayorManager mayorManager;

    private ObjectScrollableWidget villagerScrollableWidget;
    private ObjectScrollableWidget structureScrollableWidget;
    private int levelX = 0;
    private int levelY = 0;

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

            this.structureScrollableWidget = this.addDrawableChild(new ObjectScrollableWidget(96, 16, 70, 170, Text.translatable("mayor.screen.structures", mayorManager.getVillageData().getStructures().size()), this.textRenderer));
            List<Object> objects = new ArrayList<>();
            List<Text> texts = new ArrayList<>();
            for (StructureData structureData : mayorManager.getVillageData().getStructures().values()) {
                objects.add(structureData);
                texts.add(Text.of(StringUtil.getStructureName(structureData.getIdentifier())));

//                System.out.println(StructureXpLoader.structureExperienceMap.get(structureData.getIdentifier().getPath())+ " : "+structureData.getIdentifier());
            }
            this.structureScrollableWidget.setObjects(objects, texts);
        }
    }

    public ObjectScrollableWidget getVillagerScrollableWidget() {
        return villagerScrollableWidget;
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
            // Todo check what needs to a higher level village
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


    private boolean isMouseWithinBounds(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }
}
