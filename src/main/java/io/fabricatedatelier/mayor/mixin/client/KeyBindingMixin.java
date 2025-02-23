package io.fabricatedatelier.mayor.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.camera.CameraHandler;
import io.fabricatedatelier.mayor.entity.custom.CameraPullEntity;
import io.fabricatedatelier.mayor.init.MayorKeyBind;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressedMixin(InputUtil.Key key, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();

        if (key.getCode() == InputUtil.GLFW_KEY_Q || key.getCode() == InputUtil.GLFW_KEY_E) {
            if (mayorManager.isInMajorView()) {
                if (key.getCode() == InputUtil.GLFW_KEY_Q && MayorKeyBind.ROTATE_LEFT.get().isDefault()) {
                    info.cancel();
                }
                if (key.getCode() == InputUtil.GLFW_KEY_E && MayorKeyBind.ROTATE_RIGHT.get().isDefault()) {
                    info.cancel();
                }
            }
            return;
        }
        if (key.getCode() == InputUtil.GLFW_KEY_RIGHT || key.getCode() == InputUtil.GLFW_KEY_LEFT ||
                key.getCode() == InputUtil.GLFW_KEY_DOWN || key.getCode() == InputUtil.GLFW_KEY_UP) {
            if (!mayorManager.isInMajorView() || mayorManager.getStructureOriginBlockPos() == null) return;
            mayorManager.setStructureOriginBlockPos(StructureHelper.moveOrigin(mayorManager.getStructureOriginBlockPos(),
                    key.getCode() - 262, client.player.getHorizontalFacing()));

            // ClientPlayNetworking.send(new StructureOriginPacket(Optional.of(StructureHelper.moveOrigin(mayorManager.getOriginBlockPos(), key.getCode() - 263))));
            // Maybe sync origin to server - nope or maybe
            return;
        }
        if (CameraHandler.getInstance().getTarget().isPresent()) {
            for (var entry : CameraPullEntity.DirectionInput.values()) {
                MayorKeyBind mayorKeyBind = entry.getKeyBind();
                if (mayorKeyBind.get().isDefault() && mayorKeyBind.getKey() == key.getCode()) {
                    info.cancel();
                }
            }
        }
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void setKeyPressedMixin(InputUtil.Key key, boolean pressed, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();

        if (mayorManager.isInMajorView()) {
            if (key.getCode() == InputUtil.GLFW_KEY_Q && MayorKeyBind.ROTATE_LEFT.get().isDefault()) {
                MayorKeyBind.ROTATE_LEFT.get().setPressed(pressed);
                info.cancel();
            } else if (key.getCode() == InputUtil.GLFW_KEY_E && MayorKeyBind.ROTATE_RIGHT.get().isDefault()) {
                MayorKeyBind.ROTATE_RIGHT.get().setPressed(pressed);
                info.cancel();
            }
            return;
        }
        // works as intended
        if (CameraHandler.getInstance().getTarget().isPresent()) {
            for (var entry : CameraPullEntity.DirectionInput.values()) {
                MayorKeyBind mayorKeyBind = entry.getKeyBind();
                if (mayorKeyBind.getKey() == key.getCode() && mayorKeyBind.get().isDefault()) {
                    info.cancel();
                }
            }
        }
    }






/*    @WrapOperation(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1)
    )
    private <K, V> V preventKeybindOnInstantiation(Map<K, V> instance, K k, V v, Operation<V> original) {
        InputUtil.Key key = (InputUtil.Key) k;
        KeyBinding keyBinding = (KeyBinding) v;

        for (var entry : MayorKeyBind.values()) {
            if (entry.getKey() == key.getCode()) return v;
        }

        return original.call(instance, k, v);
    }

    @WrapOperation(method = "updateKeysByCode", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static <K, V> V preventKeybindOnUpdate(Map<K, V> instance, K k, V v, Operation<V> original) {
        InputUtil.Key key = (InputUtil.Key) k;
        KeyBinding keyBinding = (KeyBinding) v;

        for (var entry : MayorKeyBind.values()) {
            if (entry.getKey() == key.getCode()) return v;
        }

        return original.call(instance, k, v);
    }*/
}
