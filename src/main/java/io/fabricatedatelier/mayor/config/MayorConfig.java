package io.fabricatedatelier.mayor.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.IntField;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

public class MayorConfig {

    public static ConfigClassHandler<MayorConfig> CONFIG = ConfigClassHandler.createBuilder(MayorConfig.class)
            .id(Mayor.identifierOf("mayor"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("mayor.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting).setJson5(true).build()).build();

    @SerialEntry(comment = "0 = disabled")
    @AutoGen(category = "main")
    @IntField
    public int villageCreatePrice = 0;

    @SerialEntry(comment = "Mayor gets removed when too long offline. 0 = disabled")
    @AutoGen(category = "main")
    @IntField
    public int maxTickMayorOffline = 12_096_000;

    @SerialEntry(comment = "Mayor vote can not start before given time")
    @AutoGen(category = "main")
    @IntField
    public int minTickMayorTime = 5_184_000;

    @SerialEntry(comment = "Minimum mayor voting count for an election")
    @AutoGen(category = "main")
    @IntField
    public int minVoteCount = 5;

    @SerialEntry(comment = "Pre generated village structures get experience")
    @AutoGen(category = "main")
    @Boolean
    public boolean generatedStructureXp = false;

    public static void load() {
        CONFIG.load();
    }

    public static void save() {
        CONFIG.save();
    }


    public static Screen configScreen(Screen parent) {
        return CONFIG.generateGui().generateScreen(parent);
    }
}
