package io.fabricatedatelier.mayor.init;

import com.mojang.serialization.Codec;
import io.fabricatedatelier.mayor.Mayor;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Uuids;

import java.util.UUID;
import java.util.function.UnaryOperator;

public class MayorComponents {

    // TODO: CACHE PLAYER NAMES TO UUID

    public static final ComponentType<UUID> VOTE_UUID = register("vote_uuid", builder -> builder.codec(Uuids.CODEC).packetCodec(Uuids.PACKET_CODEC));

//    public static final ComponentType<Boolean> LAVA_LIGHT = register("lava_light", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));
//    public static final ComponentType<GildedActivationComponent> GILDED_DATA = register("gilded_data",
//            builder -> builder.codec(GildedActivationComponent.CODEC).packetCodec(GildedActivationComponent.PACKET_CODEC));

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Mayor.identifierOf(id), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void initialize() {
    }
}
