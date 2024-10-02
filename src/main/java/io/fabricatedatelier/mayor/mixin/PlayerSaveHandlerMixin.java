package io.fabricatedatelier.mayor.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PlayerSaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerSaveHandler.class)
public class PlayerSaveHandlerMixin {

    // Store name for offline usage
    @Inject(method = "savePlayerData", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/io/File;toPath()Ljava/nio/file/Path;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void savePlayerDataMixin(PlayerEntity player, CallbackInfo info, NbtCompound nbtCompound) {
        nbtCompound.putString("Name", player.getName().getString());
    }
}
