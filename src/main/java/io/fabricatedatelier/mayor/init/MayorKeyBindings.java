package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.util.KeyHelper;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

@Environment(EnvType.CLIENT)
public class MayorKeyBindings {

    private static final String MAYOR_CATEGORY = "key.categories.mayor";

    public static final KeyBinding mayorViewBind = new KeyBinding("key.mayor.mayor_view", GLFW.GLFW_KEY_Y, MAYOR_CATEGORY);
    public static final KeyBinding mayorViewSelectionBind = new KeyBinding("key.mayor.mayor_view_selection", GLFW.GLFW_KEY_U, MAYOR_CATEGORY);

    public static final KeyBinding mayorRotateLeftKeyBind = new KeyBinding("key.mayor.structure_rotate_left", GLFW.GLFW_KEY_Q, MAYOR_CATEGORY);
    public static final KeyBinding mayorRotateRightKeyBind = new KeyBinding("key.mayor.structure_rotate_right", GLFW.GLFW_KEY_E, MAYOR_CATEGORY);
    public static final KeyBinding mayorCenterKeyBind = new KeyBinding("key.mayor.structure_center", GLFW.GLFW_KEY_R, MAYOR_CATEGORY);
    public static final KeyBinding mayorUpwardKeyBind = new KeyBinding("key.mayor.structure_upward", GLFW.GLFW_KEY_PAGE_UP, MAYOR_CATEGORY);
    public static final KeyBinding mayorDownwardKeyBind = new KeyBinding("key.mayor.structure_downward", GLFW.GLFW_KEY_PAGE_DOWN, MAYOR_CATEGORY);

    public static void initialize() {
        KeyBindingHelper.registerKeyBinding(mayorViewBind);
        KeyBindingHelper.registerKeyBinding(mayorViewSelectionBind);
        KeyBindingHelper.registerKeyBinding(mayorRotateLeftKeyBind);
        KeyBindingHelper.registerKeyBinding(mayorRotateRightKeyBind);
        KeyBindingHelper.registerKeyBinding(mayorCenterKeyBind);
        KeyBindingHelper.registerKeyBinding(mayorUpwardKeyBind);
        KeyBindingHelper.registerKeyBinding(mayorDownwardKeyBind);

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            KeyHelper.viewKey(client);
            KeyHelper.centerKey(client);
            KeyHelper.heightKey(client);
        });
        ClientTickEvents.START_CLIENT_TICK.register(KeyHelper::useKey);
    }

}
