package io.fabricatedatelier.mayor.screen.block;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.network.packet.DeskDataPacket;
import io.fabricatedatelier.mayor.network.packet.DeskScreenPacket;
import io.fabricatedatelier.mayor.util.InventoryUtil;
import io.fabricatedatelier.mayor.util.ScreenHelper;
import io.fabricatedatelier.mayor.util.StringUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class DeskCitizenScreen extends Screen {

    public static final Identifier TEXTURE = Mayor.identifierOf("textures/gui/container/citizen_desk.png");

    private int backgroundWidth = 176;
    private int backgroundHeight = 166;
    private int x;
    private int y;

    private BlockPos deskPos;
    private String villageName;
    private int villageLevel;
    private String mayorName;
    private boolean citizen;
    private int taxAmount;
    private long taxTime;
    private int registrationFee;
    private int citizenCount;
    private int villagerCount;
    private int funds;
    private boolean taxPayed;
    private boolean registered;

    private ButtonWidget registrationButton;
    private ButtonWidget payTaxButton;
    private ButtonWidget donationButton;
    private TextFieldWidget donateFieldWidget;

    public DeskCitizenScreen(BlockPos deskPos, String villageName, int villageLevel, String mayorName, boolean citizen, int taxAmount, long taxTime, int registrationFee, int citizenCount, int villagerCount, int funds, boolean taxPayed, boolean registered) {
        super(Text.of(villageName));
        this.deskPos = deskPos;
        this.villageName = villageName;
        this.villageLevel = villageLevel;
        this.mayorName = mayorName;
        this.citizen = citizen;
        this.taxAmount = taxAmount;
        this.taxTime = taxTime;
        this.registrationFee = registrationFee;
        this.citizenCount = citizenCount;
        this.villagerCount = villagerCount;
        this.funds = funds;
        this.taxPayed = taxPayed;
        this.registered = registered;
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        Text donateText = Text.translatable("mayor.screen.desk.donate");
        this.donationButton = this.addDrawableChild(ButtonWidget.builder(donateText, button -> {

            int donationAmount = 0;
            try {
                donationAmount = Integer.parseInt(this.donateFieldWidget.getText());
            } catch (NumberFormatException ignored) {
            }
            if (donationAmount > 0) {
                new DeskDataPacket(this.deskPos, 0, donationAmount).sendPacket();
            }
        }).width(this.textRenderer.getWidth(donateText) + 8).position(this.x + this.backgroundWidth - (this.textRenderer.getWidth(donateText) + 16), this.y + 140).build());
        this.donationButton.setHeight(16);
        this.donationButton.active = false;

        this.donateFieldWidget = new TextFieldWidget(this.textRenderer, this.x + this.backgroundWidth - (this.textRenderer.getWidth(donateText) + 50), this.y + 140, 32, 20, Text.of(""));
        this.donateFieldWidget.setHeight(16);
        this.donateFieldWidget.setMaxLength(4);
        this.donateFieldWidget.setText(Integer.toString(0));
        this.donateFieldWidget.setChangedListener(this::onDonateField);
        this.addSelectableChild(this.donateFieldWidget);

        if (this.citizen) {
            Text payTaxText = Text.translatable("mayor.screen.desk.tax_pay");
            this.payTaxButton = this.addDrawableChild(ButtonWidget.builder(payTaxText, button -> {
                button.active = false;
                // Pay tax
                new DeskDataPacket(this.deskPos, 3, 0).sendPacket();
            }).width(this.textRenderer.getWidth(payTaxText) + 8).position(this.x + this.backgroundWidth - (this.textRenderer.getWidth(payTaxText) + 16), this.y + 58).build());
            this.payTaxButton.setHeight(16);
            this.payTaxButton.active = !this.taxPayed && this.taxAmount > 0 && this.client != null && this.client.player != null && InventoryUtil.hasRequiredPrice(this.client.player.getInventory(), this.taxAmount);
        }

        Text registerText = this.citizen ? Text.translatable("mayor.screen.desk.deregister") : Text.translatable("mayor.screen.desk.register");
        this.registrationButton = this.addDrawableChild(ButtonWidget.builder(registerText, button -> {
            button.active = false;
            if (this.citizen) {
                // Deregister
                new DeskDataPacket(this.deskPos, 2, 0).sendPacket();
                this.registered = false;
                this.close();
            } else {
                // Register
                new DeskDataPacket(this.deskPos, 1, 0).sendPacket();
                this.registered = true;
            }
            // Send register packet to server
            // Maybe add name to already payed tax for first joining village
            // Send info to mayor
        }).width(this.textRenderer.getWidth(registerText) + 8).position(this.x + 8, this.y + 140).build());
        this.registrationButton.setHeight(16);
        if (this.citizen) {
            this.registrationButton.active = !this.registered;
            if (this.mayorName.isEmpty()) {
                this.registrationButton.active = false;
            }
            if (this.registrationButton.active && this.registrationFee > 0 && this.client != null && this.client.player != null && !InventoryUtil.hasRequiredPrice(this.client.player.getInventory(), this.registrationFee)) {
                this.registrationButton.active = false;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.donateFieldWidget.render(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, this.villageName, this.x + 110 - this.textRenderer.getWidth(this.villageName) / 2, this.y + 6, 4210752, false);

        String mayorName = this.mayorName;
        if (this.textRenderer.getWidth(this.mayorName) > 80) {
            mayorName = this.mayorName.substring(0, 14);
            if (isMouseWithinBounds(this.x + 56, this.y + 20, 116, 8, mouseX, mouseY)) {
                context.drawTooltip(this.textRenderer, Text.of(this.mayorName), mouseX, mouseY);
            }
        }
        context.drawText(this.textRenderer, Text.translatable("mayor.screen.mayor", this.mayorName.isEmpty() ? "-" : mayorName), this.x + 56, this.y + 22, 4210752, false);
        context.drawText(this.textRenderer, Text.translatable("mayor.screen.level", this.villageLevel), this.x + 56, this.y + 37, 4210752, false);

        Text taxText = Text.translatable("mayor.screen.desk.tax");
        context.drawText(this.textRenderer, taxText, this.x + 8, this.y + 60, 4210752, false);
        ScreenHelper.drawPrice(context, this.textRenderer, this.x + this.textRenderer.getWidth(taxText) + 8, this.y + 54, this.taxAmount);

        if (this.client != null && this.client.world != null) {
            Text taxTimeText = Text.translatable("mayor.screen.desk.payday", StringUtil.getTimeString((int) (this.taxTime - this.client.world.getTime())));
            context.drawText(this.textRenderer, taxTimeText, this.x + 8, this.y + 75, 4210752, false);
        }
        context.drawText(this.textRenderer, Text.translatable("mayor.screen.desk.citizens", this.citizenCount), this.x + 8, this.y + 90, 4210752, false);
        context.drawText(this.textRenderer, Text.translatable("mayor.screen.desk.villagers", this.villagerCount), this.x + 8, this.y + 105, 4210752, false);

        if (this.citizen) {
            Text fundsText = Text.translatable("mayor.screen.desk.funds");
            context.drawText(this.textRenderer, fundsText, this.x + 8, this.y + 120, 4210752, false);
            ScreenHelper.drawPrice(context, this.textRenderer, this.x + this.textRenderer.getWidth(fundsText) + 8, this.y + 114, this.funds);
        } else {
            Text registrationFeeText = Text.translatable("mayor.screen.desk.fee");
            context.drawText(this.textRenderer, registrationFeeText, this.x + 8, this.y + 120, 4210752, false);
            ScreenHelper.drawPrice(context, this.textRenderer, this.x + this.textRenderer.getWidth(registrationFeeText) + 8, this.y + 114, this.registrationFee);
        }

        if (isMouseWithinBounds(this.x + this.backgroundWidth + 2, this.y, 18, 10, mouseX, mouseY)) {
            context.drawTexture(DeskBlockScreen.SWITCH_TEXTURE, this.x + this.backgroundWidth + 2, this.y, 0, 10, 18, 10, 128, 128);
        } else {
            context.drawTexture(DeskBlockScreen.SWITCH_TEXTURE, this.x + this.backgroundWidth + 2, this.y, 0, 0, 18, 10, 128, 128);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseWithinBounds(this.x + this.backgroundWidth + 2, this.y, 18, 10, mouseX, mouseY)) {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            new DeskScreenPacket(0, this.deskPos).sendPacket();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void onDonateField(String donateAmount) {
        try {
            this.donationButton.active = Integer.parseInt(donateAmount) > 0;
        } catch (NumberFormatException var3) {
            this.donationButton.active = false;
        }
    }

    private boolean isMouseWithinBounds(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }
}
