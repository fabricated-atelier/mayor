package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.MayorKeyBindings;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.screen.MayorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class KeyHelper {

    public static void rotateKey(MinecraftClient client, boolean rotateLeft) {
        if (client.player == null) return;
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();

        mayorManager.setStructureRotation(StructureHelper.getRotatedStructureRotation(mayorManager.getStructureRotation(), rotateLeft));
    }

    public static void centerKey(MinecraftClient client) {
        if (client.player == null) return;
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
        if (!mayorManager.isInMajorView()) return;

        if (MayorKeyBindings.targetToCenter.wasPressed()) {
            mayorManager.setStructureCentered(!mayorManager.getStructureCentered());
        }
    }

    public static void heightKey(MinecraftClient client) {
        if (client.player == null) return;
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
        if (!mayorManager.isInMajorView() || mayorManager.getStructureOriginBlockPos() == null) return;

        if (MayorKeyBindings.upward.wasPressed()) {
            mayorManager.setStructureOriginBlockPos(mayorManager.getStructureOriginBlockPos().up());
            return;
        }
        if (MayorKeyBindings.downward.wasPressed()) {
            mayorManager.setStructureOriginBlockPos(mayorManager.getStructureOriginBlockPos().down());
        }
    }

    public static void viewKey(MinecraftClient client) {
        if (client.player == null) return;
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();

        if (MayorKeyBindings.mayorView.wasPressed()) {
            new MayorViewPacket(!mayorManager.isInMajorView()).sendClientPacket();
            return;
        }
        if (MayorKeyBindings.mayorViewSelection.wasPressed() && mayorManager.isInMajorView()) {
            if (client.currentScreen instanceof MayorScreen) client.setScreen(null);
            else client.setScreen(new MayorScreen(mayorManager));
        }
    }

    public static void useKey(MinecraftClient client) {
        if (!client.options.useKey.wasPressed() || client.player == null) return;
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
        if (!mayorManager.isInMajorView() || client.currentScreen instanceof MayorScreen) return;
        if (mayorManager.getStructureOriginBlockPos() != null) {
            mayorManager.setStructureOriginBlockPos(null);
            return;
        }
        Optional<BlockHitResult> hitResult = Optional.ofNullable(StructureHelper.findCrosshairTarget(client.player));
        if (hitResult.isPresent()) {
            Optional<BlockPos> origin = hitResult.map(BlockHitResult::getBlockPos);
            origin.ifPresent(mayorManager::setStructureOriginBlockPos);
        }
    }
}
