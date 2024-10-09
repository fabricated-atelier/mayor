package io.fabricatedatelier.mayor.screen.block;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.config.MayorConfig;
import io.fabricatedatelier.mayor.network.packet.ElectionPacket;
import io.fabricatedatelier.mayor.util.StringUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BallotUrnBlockScreen extends HandledScreen<BallotUrnBlockScreenHandler> {

    private static Identifier TEXTURE = Mayor.identifierOf("textures/gui/container/ballot_urn.png");

    private SliderWidget sliderWidget;
    private ButtonWidget buttonWidget;

    private final PlayerEntity playerEntity;

    public BallotUrnBlockScreen(BallotUrnBlockScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerEntity = inventory.player;
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width / 2 - this.backgroundWidth / 2);
        this.y = (this.height / 2 - this.backgroundHeight / 2);

        sliderWidget = this.addDrawableChild(new SliderWidget(this.x + 8, this.y + 64, 100, 16, Text.translatable("mayor.screen.vote_time"), 0.0) {
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Text.translatable("mayor.screen.vote_time", BallotUrnBlockScreen.this.handler.getBallotUrn().getVoteTicks() / 20 / 60));
            }

            @Override
            protected void applyValue() {
                BallotUrnBlockScreen.this.handler.getBallotUrn().setVoteTicks((int) (this.value * 1_728_000D));
            }
        });
        buttonWidget = this.addDrawableChild(ButtonWidget.builder(Text.translatable("mayor.screen.start_vote"), button -> {
            button.active = false;
            sliderWidget.active = false;
            this.handler.getBallotUrn().setValidated(true);
            new ElectionPacket(this.handler.getDecoratedPotBlockEntity().getPos(), BallotUrnBlockScreen.this.handler.getBallotUrn().getVoteTicks()).sendPacket();
        }).build());
        buttonWidget.active = false;
        buttonWidget.setWidth(40);
        buttonWidget.setHeight(16);
        buttonWidget.setX(this.x + 128);
        buttonWidget.setY(this.y + 64);

        if (this.handler.getBallotUrn().getVoteStartTime() > 0 || this.handler.getBallotUrn().validated() || (this.handler.getBallotUrn().getMayorPlayerTime() > 0 && this.client != null && this.client.world != null && (int) (this.client.world.getTime() - this.handler.getBallotUrn().getMayorPlayerTime()) < MayorConfig.CONFIG.instance().minTickMayorTime)) {
            sliderWidget.active = false;
        }
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();

        buttonWidget.active = sliderWidget.active && this.handler.getBallotUrn().getVoteTicks() > 0;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Title
        context.drawText(this.textRenderer, this.title, this.x + this.backgroundWidth / 2 - this.textRenderer.getWidth(this.title) / 2, this.y + 8, 0x404040, false);

        context.drawText(this.textRenderer, Text.translatable("mayor.village.name", this.handler.getVillageName()), this.x + 8, this.y + 23, 0x404040, false);

        context.drawText(this.textRenderer, Text.translatable("mayor.screen.vote_count", this.handler.getBallotUrn().getVotedPlayerUuids().size()), this.x + 8, this.y + 38, 0x404040, false);

        if (this.client != null && this.client.world != null) {
            int ticks = this.handler.getBallotUrn().getVoteTicks() - (int) (this.client.world.getTime() - this.handler.getBallotUrn().getVoteStartTime());
            if (this.handler.getBallotUrn().getVoteStartTime() <= 0) {
                ticks = 0;
            }
            context.drawText(this.textRenderer, Text.translatable("mayor.screen.vote_time_left", StringUtil.getTimeString(ticks)), this.x + 8, this.y + 53, 0x404040, false);
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

//    private class Test extends  SliderWidget{
//
//        public Test(int x, int y, int width, int height, Text text, double value) {
//            super(x, y, width, height, text, value);
//        }
//
//        @Override
//        protected void updateMessage() {
//            super.updateMessage();
//        }
//
//        @Override
//        protected void applyValue() {
//
//        }
//    }

//    @Override
//    public void onSlotUpdate(ScreenHandler var1, int var2, ItemStack var3) {
//    }
//
//    @Override
//    public void onPropertyUpdate(ScreenHandler var1, int var2, int var3) {
//    }


}

