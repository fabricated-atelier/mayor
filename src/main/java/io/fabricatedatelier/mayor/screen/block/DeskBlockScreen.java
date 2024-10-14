package io.fabricatedatelier.mayor.screen.block;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.network.packet.DeskScreenPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class DeskBlockScreen extends BookScreen implements ScreenHandlerProvider<DeskBlockScreenHandler> {

    public static final Identifier SWITCH_TEXTURE = Mayor.identifierOf("textures/gui/sprites/widget/switch_button.png");

    private final DeskBlockScreenHandler handler;
    private final ScreenHandlerListener listener = new ScreenHandlerListener() {
        @Override
        public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
            DeskBlockScreen.this.updatePageProvider();
        }

        @Override
        public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
            if (property == 0) {
                DeskBlockScreen.this.updatePage();
            }
        }
    };

    public DeskBlockScreen(DeskBlockScreenHandler handler, PlayerInventory inventory, Text title) {
        this.handler = handler;
    }

    public DeskBlockScreenHandler getScreenHandler() {
        return this.handler;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (this.handler.isValidated()) {
            if (isMouseWithinBounds(this.width / 2 + 72, 3, 18, 10, mouseX, mouseY)) {
                context.drawTexture(SWITCH_TEXTURE, this.width / 2 + 72, 3, 0, 10, 18, 10, 128, 128);
            } else {
                context.drawTexture(SWITCH_TEXTURE, this.width / 2 + 72, 3, 0, 0, 18, 10, 128, 128);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.handler.isValidated() && isMouseWithinBounds(this.width / 2 + 72, 3, 18, 10, mouseX, mouseY)) {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            new DeskScreenPacket(this.handler.isMayor() ? 2 : 1, this.handler.getDeskPos()).sendPacket();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void init() {
        super.init();
        this.handler.addListener(this.listener);
    }

    @Override
    public void close() {
        this.client.player.closeHandledScreen();
        super.close();
    }

    @Override
    public void removed() {
        super.removed();
        this.handler.removeListener(this.listener);
    }

    @Override
    protected void addCloseButton() {
        if (this.client.player.canModifyBlocks()) {
            this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, 196, 98, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("lectern.take_book"), button -> this.sendButtonPressPacket(3)).dimensions(this.width / 2 + 2, 196, 98, 20).build());
        } else {
            super.addCloseButton();
        }
    }

    @Override
    protected void goToPreviousPage() {
        this.sendButtonPressPacket(1);
        super.goToPreviousPage();
    }

    @Override
    protected void goToNextPage() {
        this.sendButtonPressPacket(2);
        super.goToNextPage();
    }

    @Override
    protected boolean jumpToPage(int page) {
        if (page != this.handler.getPage()) {
            this.sendButtonPressPacket(100 + page);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public void updatePageProvider() {
        ItemStack itemStack = this.handler.getBookItem();
        this.setPageProvider(Objects.requireNonNullElse(BookScreen.Contents.create(itemStack), BookScreen.EMPTY_PROVIDER));
    }

    @Override
    protected void closeScreen() {
        this.client.player.closeHandledScreen();
    }

    public void updatePage() {
        this.setPage(this.handler.getPage());
    }

    private void sendButtonPressPacket(int id) {
        this.client.interactionManager.clickButton(this.handler.syncId, id);
    }

    private boolean isMouseWithinBounds(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }
}
