package io.fabricatedatelier.mayor.screen.block;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.config.MayorConfig;
import io.fabricatedatelier.mayor.network.packet.DeskDataPacket;
import io.fabricatedatelier.mayor.network.packet.DeskMayorDataPacket;
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
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class DeskMayorScreen extends Screen {

    private static final Identifier TEXTURE = Mayor.identifierOf("textures/gui/container/mayor_desk.png");

    private int backgroundWidth = 256;
    private int backgroundHeight = 166;
    private int x;
    private int y;

    private BlockPos deskPos;
    private String villageName;
    private int villageLevel;
    private boolean mayor;
    private int taxAmount;
    private int taxInterval;
    private long taxTime;
    private int registrationFee;
    private int villagerCount;
    private int funds;
    private int foundingCost;
    private Map<UUID, String> registeredCitizens;
    private Map<UUID, String> requestingCitizens;
    private Map<UUID, String> taxPayedCitizens;

    private ButtonWidget donationButton;
    private TextFieldWidget donateFieldWidget;

    private ButtonWidget taxButton;
    private TextFieldWidget taxFieldWidget;

    private ButtonWidget taxIntervalButton;
    private TextFieldWidget taxIntervalFieldWidget;

    private ButtonWidget registrationFeeButton;
    private TextFieldWidget registrationFeeFieldWidget;

    private ButtonWidget villageNameButton;
    private TextFieldWidget villageNameFieldWidget;

    private ButtonWidget foundVillageButton;
    private ButtonWidget cancelButton;

    public DeskMayorScreen(BlockPos deskPos, String villageName, int villageLevel, boolean mayor, int taxAmount, int taxInterval, long taxTime, int registrationFee, int villagerCount, int funds, int foundingCost, Map<UUID, String> registeredCitizens, Map<UUID, String> requestingCitizens, Map<UUID, String> taxPayedCitizens) {
        super(Text.of(villageName));
        this.deskPos = deskPos;
        this.villageName = villageName;
        this.villageLevel = villageLevel;
        this.mayor = mayor;
        this.taxAmount = taxAmount;
        this.taxTime = taxTime;
        this.taxInterval = taxInterval;
        this.registrationFee = registrationFee;
        this.villagerCount = villagerCount;
        this.funds = funds;
        this.foundingCost = foundingCost;
        this.registeredCitizens = registeredCitizens;
        this.requestingCitizens = requestingCitizens;
        this.taxPayedCitizens = taxPayedCitizens;
    }

    @Override
    protected void init() {
        super.init();
        if (!this.mayor) {
            this.backgroundWidth = 176;
        }
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        if (this.mayor) {
            Text changeText = Text.translatable("mayor.screen.desk.change");

            // Village Name
            this.villageNameFieldWidget = new TextFieldWidget(this.textRenderer, this.x + this.backgroundWidth / 2 + 10, this.y + 6, 110, 20, Text.of(""));
            this.villageNameFieldWidget.setText(this.villageName);
            this.villageNameFieldWidget.setChangedListener(this::onVillageNameField);
            this.addSelectableChild(this.villageNameFieldWidget);

            this.villageNameButton = this.addDrawableChild(ButtonWidget.builder(changeText, button -> {
                if (!this.villageNameFieldWidget.getText().isEmpty()) {
                    new DeskMayorDataPacket(this.deskPos, 0, 0, this.villageNameFieldWidget.getText()).sendPacket();
                    button.active = false;
                }
            }).width(this.textRenderer.getWidth(changeText) + 8).position(this.x + this.backgroundWidth - (this.textRenderer.getWidth(changeText) + 16), this.y + 30).build());
            this.villageNameButton.setHeight(16);
            this.villageNameButton.active = false;

            // Tax Amount
            this.taxFieldWidget = new TextFieldWidget(this.textRenderer, this.x + 87, this.y + 68, 26, 20, Text.of(""));
            this.taxFieldWidget.setHeight(16);
            this.taxFieldWidget.setMaxLength(3);
            this.taxFieldWidget.setText(Integer.toString(this.taxAmount));
            this.taxFieldWidget.setChangedListener(this::onTaxField);
            this.addSelectableChild(this.taxFieldWidget);

            Text taxButtonText = Text.translatable("mayor.screen.desk.change");
            this.taxButton = this.addDrawableChild(ButtonWidget.builder(taxButtonText, button -> {
                int taxAmount = 0;
                try {
                    taxAmount = Integer.parseInt(this.taxFieldWidget.getText());
                } catch (NumberFormatException ignored) {
                }
                if (taxAmount > 0) {
                    new DeskMayorDataPacket(this.deskPos, 1, taxAmount, "").sendPacket();
                    button.active = false;
                }
            }).width(this.textRenderer.getWidth(taxButtonText) + 8).position(this.x + 87 + 28, this.y + 68).build());
            this.taxButton.setHeight(16);
            this.taxButton.active = false;

            // Tax Interval
            this.taxIntervalFieldWidget = new TextFieldWidget(this.textRenderer, this.x + this.backgroundWidth - (this.textRenderer.getWidth(changeText) + 8 + 8 + 34), this.y + 68, 32, 20, Text.of(""));
            this.taxIntervalFieldWidget.setHeight(16);
            this.taxIntervalFieldWidget.setMaxLength(4);
            this.taxIntervalFieldWidget.setText(Integer.toString(this.taxInterval));
            this.taxIntervalFieldWidget.setChangedListener(this::onTaxIntervalField);
            this.addSelectableChild(this.taxIntervalFieldWidget);

            this.taxIntervalButton = this.addDrawableChild(ButtonWidget.builder(changeText, button -> {
                int taxInterval = 0;
                try {
                    taxInterval = Integer.parseInt(this.taxIntervalFieldWidget.getText());
                } catch (NumberFormatException ignored) {
                }
                if (taxInterval > 0) {
                    new DeskMayorDataPacket(this.deskPos, 2, taxInterval, "").sendPacket();
                    button.active = false;
                }
            }).width(this.textRenderer.getWidth(changeText) + 8).position(this.x + this.backgroundWidth - (this.textRenderer.getWidth(changeText) + 8 + 8), this.y + 68).build());
            this.taxIntervalButton.setHeight(16);
            this.taxIntervalButton.active = false;

            // Registration Fee
            this.registrationFeeFieldWidget = new TextFieldWidget(this.textRenderer, this.x + 87, this.y + 100, 26, 20, Text.of(""));
            this.registrationFeeFieldWidget.setHeight(16);
            this.registrationFeeFieldWidget.setMaxLength(3);
            this.registrationFeeFieldWidget.setText(Integer.toString(this.registrationFee));
            this.registrationFeeFieldWidget.setChangedListener(this::onRegistrationFeeField);
            this.addSelectableChild(this.registrationFeeFieldWidget);

            Text registrationFeeText = Text.translatable("mayor.screen.desk.change");
            this.registrationFeeButton = this.addDrawableChild(ButtonWidget.builder(registrationFeeText, button -> {
                int registrationFee = 0;
                try {
                    registrationFee = Integer.parseInt(this.registrationFeeFieldWidget.getText());
                } catch (NumberFormatException ignored) {
                }
                if (registrationFee > 0) {
                    new DeskMayorDataPacket(this.deskPos, 3, registrationFee, "").sendPacket();
                    button.active = false;
                }
            }).width(this.textRenderer.getWidth(registrationFeeText) + 8).position(this.x + 87 + 28, this.y + 100).build());
            this.registrationFeeButton.setHeight(16);
            this.registrationFeeButton.active = false;

            // Donation
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
        } else {
            // Found a village
            Text foundVillageText = ScreenTexts.PROCEED;
            this.foundVillageButton = this.addDrawableChild(ButtonWidget.builder(foundVillageText, button -> {
                this.foundVillageButton.active = false;
                this.close();
            }).width(this.textRenderer.getWidth(foundVillageText) + 8).position(this.x + this.backgroundWidth / 2 - (this.textRenderer.getWidth(foundVillageText) + 16 + 8), this.y + 140).build());
            this.foundVillageButton.setHeight(16);
            this.foundVillageButton.active = this.client != null && this.client.player != null && InventoryUtil.hasRequiredPrice(this.client.player.getInventory(), this.foundingCost);
            Text cancelText = ScreenTexts.CANCEL;
            this.cancelButton = this.addDrawableChild(ButtonWidget.builder(cancelText, button -> {
                this.cancelButton.active = false;
                this.close();
            }).width(this.textRenderer.getWidth(cancelText) + 8).position(this.x + this.backgroundWidth / 2 + 16, this.y + 140).build());
            this.cancelButton.setHeight(16);
            this.cancelButton.active = true;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (this.mayor) {
            this.villageNameFieldWidget.render(context, mouseX, mouseY, delta);
            this.taxFieldWidget.render(context, mouseX, mouseY, delta);
            this.taxIntervalFieldWidget.render(context, mouseX, mouseY, delta);
            this.registrationFeeFieldWidget.render(context, mouseX, mouseY, delta);
            this.donateFieldWidget.render(context, mouseX, mouseY, delta);

            Text feeText = Text.translatable("mayor.screen.desk.fee");
            context.drawText(this.textRenderer, feeText, this.x + this.backgroundWidth / 2 + 10, this.y + 34, 4210752, false);
            ScreenHelper.drawPrice(context, this.textRenderer, this.x + this.backgroundWidth / 2 + 10 + this.textRenderer.getWidth(feeText), this.y + 28, this.foundingCost);

            context.drawText(this.textRenderer, Text.translatable("mayor.screen.level", this.villageLevel), this.x + this.backgroundWidth / 2 + 10, this.y + 45, 4210752, false);

            context.drawText(this.textRenderer, Text.translatable("mayor.screen.desk.tax_amount", this.taxAmount), this.x + 87, this.y + 58, 4210752, false);

            Text taxIntervalText = Text.translatable("mayor.screen.desk.tax_interval", this.taxTime);
            context.drawText(this.textRenderer, taxIntervalText, this.x + this.backgroundWidth - (this.textRenderer.getWidth(taxIntervalText) + 8), this.y + 58, 4210752, false);

            if (this.client != null && this.client.world != null) {
                Text taxTimeText = Text.translatable("mayor.screen.desk.payday", StringUtil.getTimeString((int) (this.taxTime - this.client.world.getTime())));
                context.drawText(this.textRenderer, taxTimeText, this.x + backgroundWidth - (this.textRenderer.getWidth(taxTimeText) + 8), this.y + 126, 4210752, false);
            }

            context.drawText(this.textRenderer, Text.translatable("mayor.screen.desk.registration_fee", this.registrationFee), this.x + 87, this.y + 90, 4210752, false);

            context.drawText(this.textRenderer, Text.translatable("mayor.screen.desk.villagers", this.villagerCount), this.x + 87, this.y + 126, 4210752, false);

            Text fundsText = Text.translatable("mayor.screen.desk.funds");
            context.drawText(this.textRenderer, fundsText, this.x + 87, this.y + 144, 4210752, false);
            ScreenHelper.drawPrice(context, this.textRenderer, this.x + 87 + this.textRenderer.getWidth(fundsText), this.y + 138, this.funds);

            if (isMouseWithinBounds(this.x + this.backgroundWidth + 2, this.y, 18, 10, mouseX, mouseY)) {
                context.drawTexture(DeskBlockScreen.SWITCH_TEXTURE, this.x + this.backgroundWidth + 2, this.y, 0, 10, 18, 10, 128, 128);
            } else {
                context.drawTexture(DeskBlockScreen.SWITCH_TEXTURE, this.x + this.backgroundWidth + 2, this.y, 0, 0, 18, 10, 128, 128);
            }
        } else {
            Text title = Text.translatable("mayor.screen.desk.found_village");
            context.drawText(this.textRenderer, title, this.x + 75 - this.textRenderer.getWidth(this.villageName) / 2, this.y + 6, 4210752, false);

            Text foundingCostText = Text.translatable("mayor.screen.desk.founding_costs");
            context.drawText(this.textRenderer, foundingCostText, this.x + 60, this.y + 26, 4210752, false);
            ScreenHelper.drawPrice(context, this.textRenderer, this.x + 60 + this.textRenderer.getWidth(foundingCostText), this.y + 20, this.foundingCost);

            for (int i = 0; i < 4; i++) {
                context.drawText(this.textRenderer, Text.translatable("mayor.screen.desk.village_creation." + i), this.x + 8, this.y + 65 + i * 13, 4210752, false);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.mayor && isMouseWithinBounds(this.x + this.backgroundWidth + 2, this.y, 18, 10, mouseX, mouseY)) {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            new DeskScreenPacket(0, this.deskPos).sendPacket();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        if (this.mayor) {
            context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        } else {
            context.drawTexture(DeskCitizenScreen.TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        }

    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public void setTaxAmount(int taxAmount) {
        this.taxAmount = taxAmount;
    }

    public void setTaxInterval(int taxInterval) {
        this.taxInterval = taxInterval;
    }

    public void setRegistrationFee(int registrationFee) {
        this.registrationFee = registrationFee;
    }

    private void onVillageNameField(String villageName) {
        if (this.client != null && this.client.player != null && !InventoryUtil.hasRequiredPrice(this.client.player.getInventory(), MayorConfig.CONFIG.instance().villageRenameCost)) {
            this.villageNameButton.active = false;
        } else {
            this.villageNameButton.active = !villageName.equals(this.villageName);
        }
    }

    private void onTaxField(String taxAmount) {
        try {
            int newTaxAmount = Integer.parseInt(taxAmount);
            this.taxButton.active = newTaxAmount >= 0 && this.taxAmount != newTaxAmount;
        } catch (NumberFormatException var3) {
            this.taxButton.active = false;
        }
    }

    private void onTaxIntervalField(String taxInterval) {
        try {
            int newTaxInterval = Integer.parseInt(taxInterval);
            this.taxIntervalButton.active = newTaxInterval >= 0 && this.taxInterval != newTaxInterval;
            if (this.taxIntervalButton.active && newTaxInterval > 0) {
                if (this.taxAmount > 0) {
                    this.taxIntervalButton.active = true;
                } else {
                    this.taxIntervalButton.active = false;
                }
            }
        } catch (NumberFormatException var3) {
            this.taxIntervalButton.active = false;
        }
    }

    private void onRegistrationFeeField(String registrationFee) {
        try {
            int newRegistrationFee = Integer.parseInt(registrationFee);
            this.registrationFeeButton.active = newRegistrationFee >= 0 && this.registrationFee != newRegistrationFee;
        } catch (NumberFormatException var3) {
            this.registrationFeeButton.active = false;
        }
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
