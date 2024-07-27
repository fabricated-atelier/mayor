package io.fabricatedatelier.mayor.init;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

@Environment(EnvType.CLIENT)
public class KeyBindings {

    private static final String MAJOR_CATEGORY = "key.categories.major";

    public static final KeyBinding majorRotateLeftKeyBind = new KeyBinding("key.major.structure_rotate_left", GLFW.GLFW_KEY_Q, MAJOR_CATEGORY);
    public static final KeyBinding majorRotateRightKeyBind = new KeyBinding("key.major.structure_rotate_right", GLFW.GLFW_KEY_E, MAJOR_CATEGORY);
    public static final KeyBinding majorCenterKeyBind = new KeyBinding("key.major.structure_center", GLFW.GLFW_KEY_X, MAJOR_CATEGORY);

    public static void initialize() {
        KeyBindingHelper.registerKeyBinding(majorRotateLeftKeyBind);
        KeyBindingHelper.registerKeyBinding(majorRotateRightKeyBind);
        KeyBindingHelper.registerKeyBinding(majorCenterKeyBind);
    }

}
