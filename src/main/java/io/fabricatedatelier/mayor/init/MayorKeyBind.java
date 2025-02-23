package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.util.KeyHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public enum MayorKeyBind {
    MAYOR_VIEW("mayor_view", GLFW.GLFW_KEY_Y,MayorKeyBindCategory.MAIN),
    MAYOR_VIEW_SELECTION("mayor_view_selection", GLFW.GLFW_KEY_U, MayorKeyBindCategory.MAIN),
    ROTATE_LEFT("rotate_structure_left", GLFW.GLFW_KEY_Q, MayorKeyBindCategory.MAIN),
    ROTATE_RIGHT("rotate_structure_right", GLFW.GLFW_KEY_E, MayorKeyBindCategory.MAIN),
    TARGET_TO_CENTER("center_structure", GLFW.GLFW_KEY_R, MayorKeyBindCategory.MAIN),
    UPWARD("upward_structure", GLFW.GLFW_KEY_PAGE_UP, MayorKeyBindCategory.MAIN),
    DOWNWARD("downward_structure", GLFW.GLFW_KEY_PAGE_DOWN, MayorKeyBindCategory.MAIN),

    FREE_FLY_CAMERA_MODE("free_fly_camera_mode", GLFW.GLFW_KEY_SPACE, MayorKeyBindCategory.MAIN),
    FREE_FLY_LEFT("fly_left", GLFW.GLFW_KEY_A, MayorKeyBindCategory.CAMERA),
    FREE_FLY_RIGHT("fly_right", GLFW.GLFW_KEY_D, MayorKeyBindCategory.CAMERA),
    FREE_FLY_FORWARD("fly_forward", GLFW.GLFW_KEY_W, MayorKeyBindCategory.CAMERA),
    FREE_FLY_BACKWARD("fly_backward", GLFW.GLFW_KEY_S, MayorKeyBindCategory.CAMERA),
    FREE_FLY_UP("fly_up", GLFW.GLFW_KEY_SPACE, MayorKeyBindCategory.CAMERA),
    FREE_FLY_DOWN("fly_down", GLFW.GLFW_KEY_LEFT_CONTROL, MayorKeyBindCategory.CAMERA);


    private final String name;
    private final int key;
    private final KeyBinding keyBinding;

    MayorKeyBind(String name, int key, MayorKeyBindCategory category) {
        this.name = name;
        this.key = key;
        this.keyBinding = register(name, key, category.getTranslation());
    }

    public String getTranslation() {
        return "key.mayor." + this.name;
    }

    public int getKey() {
        return key;
    }

    public KeyBinding get() {
        return keyBinding;
    }

    public KeyBinding register(String name, int key, String category) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.mayor." + name, key, category));
    }

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            KeyHelper.viewKey(client);
            KeyHelper.centerKey(client);
            KeyHelper.heightKey(client);
            // KeyHelper.cameraKeys(client);
        });
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            KeyHelper.useKey(client);
            KeyHelper.cameraKeys(client);
        });
    }

    public enum MayorKeyBindCategory {
        MAIN("mayor"),
        CAMERA("mayor_camera");

        private final String translation;

        MayorKeyBindCategory(String name) {
            this.translation = "key.categories." + name;
        }

        public String getTranslation() {
            return translation;
        }
    }
}
