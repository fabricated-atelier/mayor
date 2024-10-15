package io.fabricatedatelier.mayor.config;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;

public class MayorConfig {

    public static ConfigClassHandler<MayorConfig> CONFIG = ConfigClassHandler.createBuilder(MayorConfig.class)
            .id(Mayor.identifierOf("mayor"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("mayor.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting).setJson5(true).build()).build();

    @SerialEntry(comment = "0 = disabled")
    @AutoGen(category = "main")
    @IntField
    public int villageFoundingCost = 0;

    @SerialEntry(comment = "Costs to rename a village")
    @AutoGen(category = "main")
    @IntField
    public int villageRenameCost = 12;

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

    @SerialEntry(comment = "Villages choose a random name")
    @AutoGen(category = "main")
    @ListGroup(valueFactory = TestListFactory.class, controllerFactory = TestListFactory.class)
    public List<String> villageNames = Lists.newArrayList(
            "Drakenford", "Wyvernstone", "Eldermoor", "Brightwater",
            "Stormwick", "Ironhold", "Frostfall", "Greystone", "Redbrook",
            "Riverhelm", "Oakshade", "Thornwall", "Emberhollow", "Windmere", "Darkreach", "Silverleaf",
            "Goldspire", "Hollowglen", "Stormglen", "Greenhollow", "Briarcliff", "Grimshade", "Oakendale",
            "Briarstone", "Darkstone", "Hearthwood", "Willowbrook", "Stonehollow", "Ashgrove", "Moondale",
            "Starshade", "Wolfpine", "Redridge", "Windspire", "Fallowgate", "Shadowfen", "Amberfield",
            "Dragonspire", "Highcliff", "Ironridge", "Riverfall", "Emberbrook", "Mistwood", "Greenvale",
            "Hollowfield", "Frostshade", "Darkwood", "Stormbriar", "Westfall", "Grimhold", "Ironvale",
            "Frostgrove", "Moonfall", "Ravenshire", "Redfield", "Blackthorn", "Windford", "Darkpine",
            "Hallowbrook", "Greenwatch", "Silverglen", "Ironhill", "Frostpine", "Mistvale", "Ravenfall",
            "Brighthaven", "Stonebridge", "Briarhill", "Darkwater", "Westbrook", "Frostwick", "Emberhill",
            "Hollowspire", "Ironcliff", "Greenspire", "Hallowshade", "Moonridge", "Shadowbrook",
            "Emberfield", "Darkspire", "Mistmoor", "Ironwood", "Redhill", "Windhaven", "Greenglen",
            "Frostford", "Ravenwood", "Brightshade", "Shadowridge", "Emberstone", "Wolfspire", "Oakspire",
            "Darkridge", "Greengate", "Frostbrook", "Ironspire", "Willowshade", "Briarwood", "Stormhaven",
            "Highridge", "Ravenhill", "Frostwatch", "Mistfall", "Embermoor", "Hollowgrove", "Greendale",
            "Windpine", "Dragonridge", "Redspire", "Greenstone", "Frosthaven", "Blackgrove", "Willowfield",
            "Darkgrove", "Oakstone", "Silverwatch", "Grimridge", "Stormfield", "Emberwatch", "Willowmoor",
            "Ironwatch", "Frostspire", "Greenfield", "Hollowridge", "Oakmoor", "Redgate", "Windridge",
            "Shadowmoor", "Stormcliff", "Highspire", "Froststone", "Darkwatch", "Ravenvale", "Greenspire",
            "Mistfield", "Emberfall", "Hollowhill", "Blackfield", "Windcliff", "Frostgate", "Ironstone",
            "Oakfield", "Silverfall", "Grimvale", "Darkfield", "Windmoor", "Redgrove", "Greenhill",
            "Willowgrove", "Hollowstone", "Mistfield", "Embercliff", "Frosthill", "Blackbrook", "Greycliff",
            "Highgate", "Darkgrove", "Emberfield", "Stormbrook", "Hollowford", "Silvergate",
            "Mistcliff", "Frostbriar", "Ironbrook", "Oakwood", "Windmoor", "Grimwatch", "Dragonbrook",
            "Redvale", "Greenmoor", "Hollowgate", "Emberwood", "Frostvale", "Shadowwood",
            "Willowfield", "Darkstone", "Brightmoor", "Silvermoor", "Oakridge", "Frostgate", "Mistgate",
            "Blackmoor", "Greenbrook", "Windford", "Hollowvale", "Stormstone", "Dragonvale", "Frostmoor",
            "Embervale", "Redmoor", "Silverwood", "Oakwatch"
    );


    public static class TestListFactory implements ListGroup.ValueFactory<String>, ListGroup.ControllerFactory<String> {
        @Override
        public String provideNewValue() {
            return "";
        }

        @Override
        public ControllerBuilder<String> createController(ListGroup annotation, ConfigField<List<String>> field, OptionAccess storage, Option<String> option) {
            return StringControllerBuilder.create(option);
        }
    }

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
