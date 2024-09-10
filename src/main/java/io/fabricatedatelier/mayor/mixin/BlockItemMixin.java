package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.block.custom.LumberStorageBlock;
import io.fabricatedatelier.mayor.block.custom.StoneStorageBlock;
import io.fabricatedatelier.mayor.datagen.TagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Shadow
    public abstract Block getBlock();

    // Note: This is probably a scuffed implementation, so change it if there is something better!
    // The issue was that BlockItems which were placed in storage blocks with right click sometimes
    // flashed up for a split second as if they were placed in the world before being inserted in the inventory
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void avoidPlacementIfUsedOnStorage(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        BlockState targetBlockState = context.getWorld().getBlockState(context.getBlockPos());
        ItemStack blockItemStack = new ItemStack((BlockItem) (Object) this);

        if (blockItemStack.isIn(TagProvider.ItemTags.LUMBER_STORAGE_STORABLE)) {
            if (targetBlockState.getBlock() instanceof LumberStorageBlock) {
                cir.setReturnValue(ActionResult.PASS);
                return;
            }
        }
        if (blockItemStack.isIn(TagProvider.ItemTags.STONE_STORAGE_STORABLE)) {
            if (targetBlockState.getBlock() instanceof StoneStorageBlock) {
                cir.setReturnValue(ActionResult.PASS);
                return;
            }
        }
    }
}
