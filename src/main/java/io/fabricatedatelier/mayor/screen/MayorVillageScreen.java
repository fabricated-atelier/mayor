package io.fabricatedatelier.mayor.screen;

import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.network.packet.EntityListC2SPacket;
import io.fabricatedatelier.mayor.screen.widget.ObjectScrollableWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

@Environment(EnvType.CLIENT)
public class MayorVillageScreen extends Screen {

    private final MayorManager mayorManager;

    private ObjectScrollableWidget villagerScrollableWidget;

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
        }
    }

    public ObjectScrollableWidget getVillagerScrollableWidget() {
        return villagerScrollableWidget;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        Text wip = Text.of("WIP");
        context.drawText(this.textRenderer, wip, this.width / 2 - this.textRenderer.getWidth(wip), this.height / 2 / 2, Colors.GRAY, false);

        if (mayorManager.getVillageData() != null) {
            int villageMiddleX = this.width / 2;
            int villageY = 10;
            String name = mayorManager.getVillageData().getName();
            int villageLeft = villageMiddleX - this.textRenderer.getWidth(name) / 2;

            context.drawTexture(MayorScreen.VILLAGE, villageLeft - 6, villageY, 0, 0, 10, 23, 128, 128);
            context.drawTexture(MayorScreen.VILLAGE, villageLeft - 6 + 10, villageY, this.textRenderer.getWidth(name) - 9, 23, 10, 0, 5, 23, 128, 128);
            context.drawTexture(MayorScreen.VILLAGE, villageLeft - 6 + 10 + this.textRenderer.getWidth(name) - 9, villageY, 15, 0, 10, 23, 128, 128);
            context.drawText(this.textRenderer, name, villageLeft, villageY + 8, Colors.WHITE, false);

            // CHeck what needs to a higher level village
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
    public boolean shouldPause() {
        return false;
    }


    private boolean isMouseWithinBounds(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }
}
