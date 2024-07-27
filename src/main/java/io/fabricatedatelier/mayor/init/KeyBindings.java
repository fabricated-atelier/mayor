package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.network.packet.StructureCenterPacket;
import io.fabricatedatelier.mayor.util.MayorManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeyBindings {

    private static final String MAJOR_CATEGORY = "key.categories.major";

    public static final KeyBinding majorRotateLeftKeyBind = new KeyBinding("key.major.structure_rotate_left", GLFW.GLFW_KEY_Q, MAJOR_CATEGORY);
    public static final KeyBinding majorRotateRightKeyBind = new KeyBinding("key.major.structure_rotate_right", GLFW.GLFW_KEY_E, MAJOR_CATEGORY);
    public static final KeyBinding majorCenterKeyBind = new KeyBinding("key.major.structure_center", GLFW.GLFW_KEY_R, MAJOR_CATEGORY);

    public static void initialize() {
        KeyBindingHelper.registerKeyBinding(majorRotateLeftKeyBind);
        KeyBindingHelper.registerKeyBinding(majorRotateRightKeyBind);
        KeyBindingHelper.registerKeyBinding(majorCenterKeyBind);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (majorCenterKeyBind.wasPressed()) {
                MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
                if (mayorManager.isInMajorView()) {
                    new StructureCenterPacket(!mayorManager.getStructureCentered()).sendPacket();
                }
            }
        });
    }

}
