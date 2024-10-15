package io.fabricatedatelier.mayor.screen.block;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class DeskMayorScreen extends Screen {

    private static final Identifier TEXTURE = Mayor.identifierOf("textures/gui/container/mayor_desk.png");
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/villager/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla("container/villager/scroller_disabled");

    private int backgroundWidth = 256;
    private int backgroundHeight = 166;
    private int x;
    private int y;

    private final BlockPos deskPos;
    private String villageName;
    private final int villageLevel;
    private final boolean mayor;
    private int taxAmount;
    private int taxInterval;
    private long taxTime;
    private int registrationFee;
    private final int villagerCount;
    private int funds;
    private int foundingCost;
    private Map<UUID, String> registeredCitizens;
    private Map<UUID, String> requestingCitizens;
    private List<UUID> taxPaidCitizens;
    private List<UUID> taxUnpaidCitizens;

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

    private Map<UUID, String> citizenList = new LinkedHashMap<>();
    private int selectedIndex = -1;
    private final WidgetButtonPage[] citizens = new WidgetButtonPage[7];
    private int indexStartOffset;
    private boolean scrolling;

    public DeskMayorScreen(BlockPos deskPos, String villageName, int villageLevel, boolean mayor, int taxAmount, int taxInterval, long taxTime, int registrationFee, int villagerCount, int funds, int foundingCost, Map<UUID, String> registeredCitizens, Map<UUID, String> requestingCitizens, List<UUID> taxPaidCitizens, List<UUID> taxUnpaidCitizens) {
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
        this.taxPaidCitizens = taxPaidCitizens;
        this.taxUnpaidCitizens = taxUnpaidCitizens;
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
                    new DeskMayorDataPacket(this.deskPos, 0, 0, this.villageNameFieldWidget.getText(), Optional.empty()).sendPacket();
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

            this.taxButton = this.addDrawableChild(ButtonWidget.builder(changeText, button -> {
                int taxAmount = 0;
                try {
                    taxAmount = Integer.parseInt(this.taxFieldWidget.getText());
                } catch (NumberFormatException ignored) {
                }
                if (taxAmount > 0) {
                    new DeskMayorDataPacket(this.deskPos, 1, taxAmount, "", Optional.empty()).sendPacket();
                    button.active = false;
                }
            }).width(this.textRenderer.getWidth(changeText) + 8).position(this.x + 87 + 28, this.y + 68).build());
            this.taxButton.setHeight(16);
            this.taxButton.active = false;

            // Tax Interval
            this.taxIntervalFieldWidget = new TextFieldWidget(this.textRenderer, this.x + this.backgroundWidth - (this.textRenderer.getWidth(changeText) + 8 + 8 + 34), this.y + 68, 32, 20, Text.of(""));
            this.taxIntervalFieldWidget.setHeight(16);
            this.taxIntervalFieldWidget.setMaxLength(4);
            this.taxIntervalFieldWidget.setText(Integer.toString(this.taxInterval));
            this.taxIntervalFieldWidget.setChangedListener(this::onTaxIntervalField);
            this.taxIntervalFieldWidget.setTooltip(Tooltip.of(Text.translatable("mayor.screen.desk.tax_interval.tooltip")));
            this.addSelectableChild(this.taxIntervalFieldWidget);

            this.taxIntervalButton = this.addDrawableChild(ButtonWidget.builder(changeText, button -> {
                int taxInterval = 0;
                try {
                    taxInterval = Integer.parseInt(this.taxIntervalFieldWidget.getText());
                } catch (NumberFormatException ignored) {
                }
                if (taxInterval > 0) {
                    new DeskMayorDataPacket(this.deskPos, 2, taxInterval, "", Optional.empty()).sendPacket();
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

            this.registrationFeeButton = this.addDrawableChild(ButtonWidget.builder(changeText, button -> {
                int registrationFee = 0;
                try {
                    registrationFee = Integer.parseInt(this.registrationFeeFieldWidget.getText());
                } catch (NumberFormatException ignored) {
                }
                if (registrationFee > 0) {
                    new DeskMayorDataPacket(this.deskPos, 3, registrationFee, "", Optional.empty()).sendPacket();
                    button.active = false;
                }
            }).width(this.textRenderer.getWidth(changeText) + 8).position(this.x + 87 + 28, this.y + 100).build());
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

            this.citizenList.putAll(this.requestingCitizens);
            this.citizenList.putAll(this.registeredCitizens);

            int k = 0;
            for (int l = 0; l < 7; l++) {
                this.citizens[l] = this.addDrawableChild(new WidgetButtonPage(this.x + 5, this.y + 18 + k, l, button -> {
                    if (button instanceof WidgetButtonPage widgetButtonPage) {
                        this.selectedIndex = widgetButtonPage.getIndex() + this.indexStartOffset;
//                        this.syncRecipeIndex();

                        widgetButtonPage.setFocused(false);
                    }
                }));
                k += 20;
            }
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

            context.drawText(this.textRenderer, Text.translatable("mayor.screen.desk.citizens.title"), this.x + 5, this.y + 6, 4210752, false);

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

            this.renderScrollbar(context, this.x, this.y, this.citizenList.size());
            if (!this.citizenList.isEmpty()) {
                int k = this.y + 16;
                int m = 0;

                for (Map.Entry<UUID, String> entry : this.citizenList.entrySet()) {
                    if (this.canScroll(this.citizenList.size()) && (m < this.indexStartOffset || m >= 7 + this.indexStartOffset)) {
                        ++m;
                        continue;
                    }
                    context.drawText(this.textRenderer, getPlayerName(entry.getValue(), 76, 8), this.x + 9, k + 8, 0xFFFFFF, false);

                    if (this.requestingCitizens.containsKey(entry.getKey())) {
                        context.drawTexture(TEXTURE, this.x + 65, k + 9, 0, 166, 5, 5);
                    } else if (this.taxUnpaidCitizens.contains(entry.getKey())) {
                        context.drawTexture(TEXTURE, this.x + 65, k + 9, 5, 166, 5, 5);
                    } else if (this.taxPaidCitizens.contains(entry.getKey())) {
                        context.drawTexture(TEXTURE, this.x + 65, k + 9, 10, 166, 5, 5);
                    }
                    k += 20;
                    ++m;
                }

                for (int u = 0; u < this.citizens.length; u++) {
                    if (this.citizens[u].isHovered()) {
                        this.citizens[u].renderTooltip(context, mouseX, mouseY);
                    }
                    this.citizens[u].visible = this.citizens[u].getIndex() < this.citizenList.size();
                    this.citizens[u].active = this.selectedIndex - this.indexStartOffset != u;
                }

                if (this.selectedIndex >= 0) {
                    UUID selectedUuid = this.citizenList.keySet().stream().toList().get(this.selectedIndex);
                    if (this.requestingCitizens.containsKey(selectedUuid)) {
                        renderCitizenButton(context, mouseX, mouseY, true, Text.translatable("mayor.screen.desk.accept"), Text.translatable("mayor.screen.desk.reject"));
                    } else if (this.taxUnpaidCitizens.contains(selectedUuid)) {
                        renderCitizenButton(context, mouseX, mouseY, true, Text.translatable("mayor.screen.desk.debt_relief"), Text.translatable("mayor.screen.desk.kick"));
                    } else {
                        renderCitizenButton(context, mouseX, mouseY, false, null, Text.translatable("mayor.screen.desk.kick"));
                    }
                }
            }

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

    private void renderCitizenButton(DrawContext context, int mouseX, int mouseY, boolean greenButton, @Nullable Text tooltipGreen, @Nullable Text tooltipRed) {
        // render red button
        if (isMouseWithinBounds(this.x + 70, this.y + 6, 12, 11, mouseX, mouseY)) {
            context.drawTexture(TEXTURE, this.x + 70, this.y + 6, 36, 171, 12, 11);
            if (greenButton) {
                context.drawTexture(TEXTURE, this.x + 58, this.y + 6, 0, 171, 12, 11);
            }
            if (tooltipRed != null) {
                context.drawTooltip(this.textRenderer, tooltipRed, mouseX, mouseY);
            }
        } else {
            context.drawTexture(TEXTURE, this.x + 70, this.y + 6, 12, 171, 12, 11);
            if (greenButton) {
                // render green button
                if (isMouseWithinBounds(this.x + 58, this.y + 6, 12, 11, mouseX, mouseY)) {
                    context.drawTexture(TEXTURE, this.x + 58, this.y + 6, 24, 171, 12, 11);
                    if (tooltipGreen != null) {
                        context.drawTooltip(this.textRenderer, tooltipGreen, mouseX, mouseY);
                    }
                } else {
                    context.drawTexture(TEXTURE, this.x + 58, this.y + 6, 0, 171, 12, 11);
                }
            }
        }
    }

    private Text getPlayerName(String playerName, int length, int substringLength) {
        if (this.client != null && this.client.textRenderer.getWidth(playerName) > length && substringLength != 0) {
            playerName = playerName.substring(0, substringLength) + "..";
        }
        return Text.of(playerName);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.mayor) {
            if (isMouseWithinBounds(this.x + this.backgroundWidth + 2, this.y, 18, 10, mouseX, mouseY)) {
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                new DeskScreenPacket(0, this.deskPos).sendPacket();
                return true;
            }
            if (this.selectedIndex >= 0) {
                UUID selectedUuid = this.citizenList.keySet().stream().toList().get(this.selectedIndex);
                if (isMouseWithinBounds(this.x + 58, this.y + 6, 12, 11, mouseX, mouseY)) {
                    // green button
                    if (this.requestingCitizens.containsKey(selectedUuid)) {
                        // accept registration
                        new DeskMayorDataPacket(this.deskPos, 4, 0, "", Optional.of(selectedUuid)).sendPacket();
                        this.selectedIndex = -1;
                        this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    } else if (this.taxUnpaidCitizens.contains(selectedUuid)) {
                        // debt relief
                        new DeskMayorDataPacket(this.deskPos, 6, 0, "", Optional.of(selectedUuid)).sendPacket();
                        this.selectedIndex = -1;
                        this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;
                    }
                } else if (isMouseWithinBounds(this.x + 70, this.y + 6, 12, 11, mouseX, mouseY)) {
                    // red button
                    if (this.requestingCitizens.containsKey(selectedUuid)) {
                        // decline registration
                        new DeskMayorDataPacket(this.deskPos, 5, 0, "", Optional.of(selectedUuid)).sendPacket();
                    } else if (this.taxUnpaidCitizens.contains(selectedUuid)) {
                        //kick
                        new DeskMayorDataPacket(this.deskPos, 7, 0, "", Optional.of(selectedUuid)).sendPacket();
                    } else {
                        // kick
                        new DeskMayorDataPacket(this.deskPos, 7, 0, "", Optional.of(selectedUuid)).sendPacket();
                    }
                    this.selectedIndex = -1;
                    this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
            this.scrolling = false;
            if (this.canScroll(this.citizenList.size())
                    && mouseX > (double) (this.x + 76)
                    && mouseX < (double) (this.x + 76 + 6)
                    && mouseY > (double) (this.y + 18)
                    && mouseY <= (double) (this.x + 18 + 139 + 1)) {
                this.scrolling = true;
            }
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

    public void addRegisteredCitizen(UUID citizen) {
        if (this.requestingCitizens.containsKey(citizen)) {
            this.registeredCitizens.put(citizen, this.requestingCitizens.get(citizen));
            this.citizenList.put(citizen, this.requestingCitizens.get(citizen));
            this.taxPaidCitizens.add(citizen);
        }
    }

    public void removeRegisteredCitizen(UUID registeredCitizen) {
        this.registeredCitizens.remove(registeredCitizen);
        this.citizenList.remove(registeredCitizen);
        this.taxUnpaidCitizens.remove(registeredCitizen);
        this.taxPaidCitizens.remove(registeredCitizen);
    }

    public void removeRequestingCitizen(UUID requestingCitizen) {
        this.requestingCitizens.remove(requestingCitizen);
    }

    public void removeTaxUnpaidCitizen(UUID taxUnpaidCitizen) {
        this.taxUnpaidCitizens.remove(taxUnpaidCitizen);
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

    private boolean canScroll(int listSize) {
        return listSize > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int i = this.citizenList.size();
        if (this.canScroll(i)) {
            int j = i - 7;
            this.indexStartOffset = MathHelper.clamp((int) ((double) this.indexStartOffset - verticalAmount), 0, j);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int i = this.citizenList.size();
        if (this.scrolling) {
            int j = this.y + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float) mouseY - (float) j - 13.5F) / ((float) (k - j) - 27.0F);
            f = f * (float) l + 0.5F;
            this.indexStartOffset = MathHelper.clamp((int) f, 0, l);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    private void renderScrollbar(DrawContext context, int x, int y, int playerCount) {
        int i = playerCount + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int m = Math.min(113, this.indexStartOffset * k);
            if (this.indexStartOffset == i - 1) {
                m = 113;
            }
            context.drawGuiTexture(SCROLLER_TEXTURE, x + 76, y + 18 + m, 0, 6, 27);
        } else {
            context.drawGuiTexture(SCROLLER_DISABLED_TEXTURE, x + 76, y + 18, 0, 6, 27);
        }
    }

    private class WidgetButtonPage extends ButtonWidget {

        private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/button"), Identifier.ofVanilla("widget/button_disabled"), Identifier.ofVanilla("widget/button_highlighted"));
        private final int index;

        public WidgetButtonPage(final int x, final int y, final int index, final ButtonWidget.PressAction onPress) {
            super(x, y, 70, 20, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.index = index;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public boolean isSelected() {
            return isHovered();
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            context.drawGuiTexture(TEXTURES.get(this.active, this.isSelected()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int i = this.active ? 16777215 : 10526880;
            this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);

//            if(this.visible && !this.active){
//                context.drawTexture(TEXTURE,  this.getX()+this.getWidth()-12, this.getY(), 0, 171, 12, 11);
//            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        public void renderTooltip(DrawContext context, int mouseX, int mouseY) {
            if (this.hovered) {

                Text text = Text.of(DeskMayorScreen.this.citizenList.values().stream().toList().get(this.index + DeskMayorScreen.this.indexStartOffset));
                if (client != null && client.textRenderer.getWidth(text) > 78) {
                    context.drawTooltip(textRenderer, text, mouseX, mouseY);
                }
            }
        }
    }
}
