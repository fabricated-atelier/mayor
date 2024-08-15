package io.fabricatedatelier.mayor.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlock;
import io.fabricatedatelier.mayor.block.entity.AbstractVillageContainerBlockEntity;
import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.screen.widget.ItemScrollableWidget;
import io.fabricatedatelier.mayor.screen.widget.ObjectScrollableWidget;
import io.fabricatedatelier.mayor.state.StructureData;
import io.fabricatedatelier.mayor.util.InventoryUtil;
import io.fabricatedatelier.mayor.util.StringUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.Scaling;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;


@Environment(EnvType.CLIENT)
public class MayorScreen extends Screen {

    public static final Identifier VILLAGE = Identifier.of("mayor", "textures/gui/sprites/hud/mayor_village.png");
    private final MayorManager mayorManager;

    private Map<MayorCategory.BuildingCategory, List<MayorStructure>> availableStructureMap = new HashMap<>();
    private Map<Integer, List<StructureData>> villageStructureMap = new HashMap<>();

    private ObjectScrollableWidget buildingCategoryScrollableWidget;
    private ObjectScrollableWidget buildingScrollableWidget;

    private List<ItemStack> availableStacks = new ArrayList<>();
    private ItemScrollableWidget availableItemScrollableWidget;
    private ItemScrollableWidget requiredItemScrollableWidget;

    private int levelX = 0;
    private int levelY = 0;

    @Nullable
    private MayorCategory.BuildingCategory selectedCategory = null;

    public MayorScreen(MayorManager mayorManager) {
        super(Text.translatable("mayor.screen.title"));
        this.mayorManager = mayorManager;
    }

    @Override
    protected void init() {
        this.availableStructureMap.clear();
        if (MayorManager.mayorStructureMap.containsKey(mayorManager.getBiomeCategory())) {
            for (int i = 0; i < MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).size(); i++) {
                MayorCategory.BuildingCategory buildingCategory = MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).get(i).getBuildingCategory();
                if (availableStructureMap.containsKey(buildingCategory)) {
                    availableStructureMap.get(buildingCategory).add(MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).get(i));
                } else {
                    List<MayorStructure> list = new ArrayList<>();
                    list.add(MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).get(i));
                    availableStructureMap.put(buildingCategory, list);
                }
            }
        }

        this.buildingCategoryScrollableWidget = this.addDrawableChild(new ObjectScrollableWidget(16, 16, 56, 170, Text.translatable("building.category"), this.textRenderer, this));
        List<Object> objects = new ArrayList<>();
        List<Text> texts = new ArrayList<>();
        for (int i = 0; i < MayorCategory.BuildingCategory.values().length; i++) {
            objects.add(MayorCategory.BuildingCategory.values()[i]);
            texts.add(Text.translatable(MayorCategory.BuildingCategory.values()[i].name()));
        }
        this.buildingCategoryScrollableWidget.setObjects(objects, texts);

        this.buildingScrollableWidget = this.addDrawableChild(new ObjectScrollableWidget(80, 16, 70, 170, Text.translatable("building.buildings"), this.textRenderer, this));

        this.availableStacks.clear();
        if (this.mayorManager.getVillageData() != null && this.client != null && this.client.world != null) {
            for (int i = 0; i < this.mayorManager.getVillageData().getStorageOriginBlockPosList().size(); i++) {
                if (this.client.world.getBlockState(this.mayorManager.getVillageData().getStorageOriginBlockPosList().get(i)).getBlock() instanceof AbstractVillageContainerBlock && this.client.world.getBlockEntity(this.mayorManager.getVillageData().getStorageOriginBlockPosList().get(i)) instanceof AbstractVillageContainerBlockEntity abstractVillageContainerBlockEntity) {

//                    for (int u = 0; u < this.availableStacks.size(); u++) {
//                        if (this.availableStacks.get(u).isOf() && this.availableStacks.get(u).getCount() < this.availableStacks.get(u).getMaxCount()) {
//
//                        }
//                    }
//                    this.availableStacks.add();
                }
            }
            // TEST
//            for (int i = 0; i < 60; i++) {
//                Item item = Registries.ITEM.get(this.client.world.getRandom().nextInt(300));
//                this.availableStacks.add(new ItemStack(item).copyWithCount(item.getMaxCount()));
//            }
            // TEST END

            this.availableItemScrollableWidget = this.addDrawableChild(new ItemScrollableWidget(this.width - 10 - 118, this.height - 11, 118, 28, Text.translatable("mayor.screen.available_items"), this.textRenderer));
            if (this.availableStacks.isEmpty()) {
                this.availableStacks.add(ItemStack.EMPTY);
            }
            this.availableItemScrollableWidget.setItemStacks(this.availableStacks);
        }
        int availableItemRows = Math.min(this.availableStacks.size() / 6 + 1, ItemScrollableWidget.getMaxRows());

        this.requiredItemScrollableWidget = this.addDrawableChild(new ItemScrollableWidget(this.width - 10 - 118, this.height - 9 - availableItemRows * 18 - 25, 118, 28, Text.translatable("mayor.screen.required_items"), this.textRenderer));

        // Send a packet to the server to sync again to the client with a second method to call before
        if (this.mayorManager.getVillageData() != null) {
            for (Map.Entry<BlockPos, StructureData> entry : mayorManager.getVillageData().getStructures().entrySet()) {
                if (!this.villageStructureMap.containsKey(entry.getValue().getLevel())) {
                    List<StructureData> list = new ArrayList<>();
                    list.add(entry.getValue());
                    this.villageStructureMap.put(entry.getValue().getLevel(), list);
                } else {
                    this.villageStructureMap.get(entry.getValue().getLevel()).add(entry.getValue());
                }
            }
        }

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // VillageData
        if (mayorManager.getVillageData() != null) {
            int villageMiddleX = this.width - 10 - 59;
            int villageY = 5;

            Text villagers = Text.translatable("mayor.screen.villagers", mayorManager.getVillageData().getVillagers().size());
            Text ironGolems = Text.translatable("mayor.screen.iron_golems", mayorManager.getVillageData().getIronGolems().size());
            Text storages = Text.translatable("mayor.screen.storages", mayorManager.getVillageData().getStorageOriginBlockPosList().size());
            Text structures = Text.translatable("mayor.screen.structures", mayorManager.getVillageData().getStructures().size());

            int maxWidth = StringUtil.getMaxWidth(this.textRenderer, villagers, ironGolems, storages, structures);
            int villageBackgroundX = villageMiddleX - maxWidth / 2;
            int villageBackgroundY = villageY + 16;

            // top left
            context.drawTexture(VILLAGE, villageBackgroundX - 4, villageBackgroundY, 25, 0, 7, 7, 128, 128);
            // top middle
            context.drawTexture(VILLAGE, villageBackgroundX - 4 + 7, villageBackgroundY, maxWidth - 7, 7, 32, 0, 7, 7, 128, 128);
            // top right
            context.drawTexture(VILLAGE, villageBackgroundX - 4 + 7 + maxWidth - 7, villageBackgroundY, 39, 0, 7, 7, 128, 128);
            // middle left
            context.drawTexture(VILLAGE, villageBackgroundX - 4, villageBackgroundY + 7, 7, 40, 25, 7, 7, 7, 128, 128);
            // middle middle
            context.drawTexture(VILLAGE, villageBackgroundX - 4 + 7, villageBackgroundY + 7, maxWidth - 7, 40, 32, 7, 7, 7, 128, 128);
            // middle right
            context.drawTexture(VILLAGE, villageBackgroundX - 4 + 7 + maxWidth - 7, villageBackgroundY + 7, 7, 40, 39, 7, 7, 7, 128, 128);
            // bottom left
            context.drawTexture(VILLAGE, villageBackgroundX - 4, villageBackgroundY + 47, 25, 14, 7, 7, 128, 128);
            // bottom middle
            context.drawTexture(VILLAGE, villageBackgroundX - 4 + 7, villageBackgroundY + 47, maxWidth - 7, 7, 32, 14, 7, 7, 128, 128);
            // bottom right
            context.drawTexture(VILLAGE, villageBackgroundX - 4 + 7 + maxWidth - 7, villageBackgroundY + 47, 39, 14, 7, 7, 128, 128);

            String name = mayorManager.getVillageData().getName();
            int villageLeft = villageMiddleX - this.textRenderer.getWidth(name) / 2;

            context.drawTexture(VILLAGE, villageLeft - 6, villageY, 0, 0, 10, 23, 128, 128);
            context.drawTexture(VILLAGE, villageLeft - 6 + 10, villageY, this.textRenderer.getWidth(name) - 9, 23, 10, 0, 5, 23, 128, 128);
            context.drawTexture(VILLAGE, villageLeft - 6 + 10 + this.textRenderer.getWidth(name) - 9, villageY, 15, 0, 10, 23, 128, 128);
            context.drawText(this.textRenderer, name, villageLeft, villageY + 8, Colors.WHITE, false);

            // Text level = Text.translatable("mayor.screen.level", mayorManager.getVillageData().getLevel());
            boolean longVillageName = this.textRenderer.getWidth(name) > 50;
            this.levelX = villageBackgroundX + (longVillageName ? -13 : maxWidth - 7);
            this.levelY = villageY + 6 + (longVillageName ? 57 : 0);
            context.drawTexture(VILLAGE, this.levelX, this.levelY, 0, 23, 19, 19, 128, 128);
            context.drawText(this.textRenderer, Text.of(String.valueOf(mayorManager.getVillageData().getLevel())), this.levelX + 7, this.levelY + 6, Colors.WHITE, false);


            context.drawText(this.textRenderer, villagers, villageMiddleX - this.textRenderer.getWidth(villagers) / 2, villageY + 25, Colors.GRAY, false);
            context.drawText(this.textRenderer, ironGolems, villageMiddleX - this.textRenderer.getWidth(ironGolems) / 2, villageY + 35, Colors.GRAY, false);
            context.drawText(this.textRenderer, storages, villageMiddleX - this.textRenderer.getWidth(storages) / 2, villageY + 45, Colors.GRAY, false);
            context.drawText(this.textRenderer, structures, villageMiddleX - this.textRenderer.getWidth(structures) / 2, villageY + 55, Colors.GRAY, false);

            if (isMouseWithinBounds(villageMiddleX - this.textRenderer.getWidth(structures) / 2, villageY + 55, this.textRenderer.getWidth(structures), 8, mouseX, mouseY)) {
                List<Text> structuresTooltip = new ArrayList<>();
                for (Map.Entry<Integer, List<StructureData>> entry : this.villageStructureMap.entrySet()) {
                    structuresTooltip.add(Text.translatable("mayor.screen.structures_level", entry.getKey()));
                    Map<String, Integer> structureTooltip = new HashMap<>();
                    for (int i = 0; i < entry.getValue().size(); i++) {
                        String structureName = StringUtil.getStructureName(entry.getValue().get(i).getIdentifier());
                        if (!structureTooltip.containsKey(structureName)) {
                            structureTooltip.put(structureName, 1);
                        } else {
                            structureTooltip.put(structureName, structureTooltip.get(structureName) + 1);
                        }
                    }
                    for (Map.Entry<String, Integer> structureNames : structureTooltip.entrySet()) {
                        structuresTooltip.add(Text.of(" - " + structureNames.getKey() + structureNames.getValue() + "x"));
                    }

                }
                if (!structuresTooltip.isEmpty()) {
                    context.drawTooltip(this.textRenderer, structuresTooltip, mouseX, mouseY);
                }
            } else if (isMouseWithinBounds(levelX, levelY, 19, 19, mouseX, mouseY)) {
                context.drawTexture(VILLAGE, levelX, levelY, 19, 23, 19, 19, 128, 128);
            }

        }
        // Structure requirements
        if (this.mayorManager.getMayorStructure() != null) {
            if (this.requiredItemScrollableWidget.visible && this.requiredItemScrollableWidget.getItemStacks() != null) {
                List<ItemStack> missingItems = InventoryUtil.getMissingItems(this.availableStacks, this.mayorManager.getMayorStructure().getRequiredItemStacks());
                if (!missingItems.isEmpty()) {
                    context.drawTexture(ItemScrollableWidget.SLOTS, this.requiredItemScrollableWidget.getX() + this.requiredItemScrollableWidget.getWidth() + 2, this.requiredItemScrollableWidget.getY() - this.requiredItemScrollableWidget.getHeight() - 18, 27, 28, 3, 11, 128, 128);
                    if (isMouseWithinBounds(this.requiredItemScrollableWidget.getX() + this.requiredItemScrollableWidget.getWidth() + 2, this.requiredItemScrollableWidget.getY() - this.requiredItemScrollableWidget.getHeight() - 18, 3, 11, mouseX, mouseY)) {
                        List<Text> missingTooltip = new ArrayList<>();
                        for (int i = 0; i < missingItems.size(); i++) {
                            missingTooltip.add(Text.of(missingItems.get(i).getName().getString() + " " + missingItems.get(i).getCount() + "x"));
                        }
                        context.drawTooltip(this.textRenderer, missingTooltip, mouseX, mouseY);
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseWithinBounds(this.levelX, this.levelY, 19, 19, mouseX, mouseY)) {
            this.client.setScreen(new MayorVillageScreen(this.mayorManager));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyBindings.mayorViewSelectionBind.matchesKey(keyCode, scanCode) || KeyBindings.mayorViewBind.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    public boolean shouldPause() {
        return false;
    }

    public MayorManager getMayorManager() {
        return mayorManager;
    }

    public void setSelectedCategory(@Nullable MayorCategory.BuildingCategory buildingCategory) {
        this.selectedCategory = buildingCategory;
    }

    @Nullable
    public MayorCategory.BuildingCategory getSelectedCategory() {
        return this.selectedCategory;
    }

    public Map<MayorCategory.BuildingCategory, List<MayorStructure>> getAvailableStructureMap() {
        return this.availableStructureMap;
    }

    public ItemScrollableWidget getRequiredItemScrollableWidget() {
        return this.requiredItemScrollableWidget;
    }

    public ObjectScrollableWidget getBuildingCategoryScrollableWidget() {
        return this.buildingCategoryScrollableWidget;
    }

    public ObjectScrollableWidget getBuildingScrollableWidget() {
        return this.buildingScrollableWidget;
    }


    private boolean isMouseWithinBounds(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }
}
