package com.serjog.createcolony.network;

import com.serjog.createcolony.ColonyMain;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ColonyPacket(int number, boolean toggle) implements CustomPacketPayload {
    public static final Type<ColonyPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ColonyMain.MODID, "colony_packet"));

    public static final StreamCodec<ByteBuf, ColonyPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ColonyPacket::number,
            ByteBufCodecs.BOOL, ColonyPacket::toggle,
            ColonyPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ColonyPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {

        });
    }
}
