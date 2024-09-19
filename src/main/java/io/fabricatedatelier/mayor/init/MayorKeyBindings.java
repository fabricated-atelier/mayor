package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.util.KeyHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class MayorKeyBindings {

    private static final String MAYOR_CATEGORY = "key.categories.mayor";

    public static final KeyBinding mayorView = new KeyBinding("key.mayor.mayor_view", GLFW.GLFW_KEY_Y, MAYOR_CATEGORY);
    public static final KeyBinding mayorViewSelection = new KeyBinding("key.mayor.mayor_view_selection", GLFW.GLFW_KEY_U, MAYOR_CATEGORY);

    public static final KeyBinding rotateLeft = new KeyBinding("key.mayor.structure_rotate_left", GLFW.GLFW_KEY_Q, MAYOR_CATEGORY);
    public static final KeyBinding rotateRight = new KeyBinding("key.mayor.structure_rotate_right", GLFW.GLFW_KEY_E, MAYOR_CATEGORY);
    public static final KeyBinding targetToCenter = new KeyBinding("key.mayor.structure_center", GLFW.GLFW_KEY_R, MAYOR_CATEGORY);
    public static final KeyBinding upward = new KeyBinding("key.mayor.structure_upward", GLFW.GLFW_KEY_PAGE_UP, MAYOR_CATEGORY);
    public static final KeyBinding downward = new KeyBinding("key.mayor.structure_downward", GLFW.GLFW_KEY_PAGE_DOWN, MAYOR_CATEGORY);

    public static void initialize() {
        KeyBindingHelper.registerKeyBinding(mayorView);
        KeyBindingHelper.registerKeyBinding(mayorViewSelection);
        KeyBindingHelper.registerKeyBinding(rotateLeft);
        KeyBindingHelper.registerKeyBinding(rotateRight);
        KeyBindingHelper.registerKeyBinding(targetToCenter);
        KeyBindingHelper.registerKeyBinding(upward);
        KeyBindingHelper.registerKeyBinding(downward);

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            KeyHelper.viewKey(client);
            KeyHelper.centerKey(client);
            KeyHelper.heightKey(client);
        });
        ClientTickEvents.START_CLIENT_TICK.register(KeyHelper::useKey);
    }

}
