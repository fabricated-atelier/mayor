package io.fabricatedatelier.mayor.item;

import io.fabricatedatelier.mayor.datagen.TagProvider;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DeconstructionHammerItem extends ToolItem {

    public DeconstructionHammerItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }


    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);
        if (toolComponent == null) {
            return false;
        } else {
            if (!world.isClient() && state.getHardness(world, pos) != 0.0F && toolComponent.damagePerBlock() > 0 && !state.isIn(TagProvider.BlockTags.DECONSTRUCTION_HAMMER_BLOCKS)) {
                stack.damage(toolComponent.damagePerBlock(), miner, EquipmentSlot.MAINHAND);
            }

            return true;
        }
    }
}
