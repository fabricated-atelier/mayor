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
    MAYOR_VIEW("mayor_view", GLFW.GLFW_KEY_Y, MayorKeyBindCategory.MAIN),
    MAYOR_VIEW_SELECTION("mayor_view_selection", GLFW.GLFW_KEY_U, MayorKeyBindCategory.MAIN),
    ROTATE_LEFT("structure_rotate_left", GLFW.GLFW_KEY_Q, MayorKeyBindCategory.MAIN),
    ROTATE_RIGHT("structure_rotate_right", GLFW.GLFW_KEY_E, MayorKeyBindCategory.MAIN),
    TARGET_TO_CENTER("structure_center", GLFW.GLFW_KEY_R, MayorKeyBindCategory.MAIN),
    UPWARD("structure_upward", GLFW.GLFW_KEY_PAGE_UP, MayorKeyBindCategory.MAIN),
    DOWNWARD("structure_downward", GLFW.GLFW_KEY_PAGE_DOWN, MayorKeyBindCategory.MAIN),
    FREE_FLY_CAMERA_MODE("free_fly_camera_mode", GLFW.GLFW_KEY_SPACE, MayorKeyBindCategory.MAIN);

    private final String name;

    private final KeyBinding keyBinding;

    MayorKeyBind(String name, int key, MayorKeyBindCategory category) {
        this.name = name;
        this.keyBinding = register(name, key, category.getTranslation());
    }

    public String getTranslation() {
        return "key.mayor." + this.name;
    }

    public KeyBinding register(String name, int key, String category) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.mayor." + name, key, category));
    }

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            KeyHelper.viewKey(client);
            KeyHelper.centerKey(client);
            KeyHelper.heightKey(client);
        });
        ClientTickEvents.START_CLIENT_TICK.register(KeyHelper::useKey);
    }

    public KeyBinding get() {
        return keyBinding;
    }

    private enum MayorKeyBindCategory {
        MAIN("key.categories.mayor");

        private final String translation;

        MayorKeyBindCategory(String translation) {
            this.translation = translation;
        }

        public String getTranslation() {
            return translation;
        }
    }
}
