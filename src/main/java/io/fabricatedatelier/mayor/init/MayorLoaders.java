package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.data.StructureXpLoader;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class MayorLoaders {

    public static void initialize(){
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new StructureXpLoader());
    }
}
