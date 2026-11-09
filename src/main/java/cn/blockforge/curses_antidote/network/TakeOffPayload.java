package cn.blockforge.curses_antidote.network;

import cn.blockforge.curses_antidote.CursesAntidoteMod;
import cn.blockforge.curses_antidote.logic.AntidoteLogic;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TakeOffPayload() implements CustomPacketPayload {
    public static final Type<TakeOffPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CursesAntidoteMod.MOD_ID, "take_off"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TakeOffPayload> STREAM_CODEC =
            StreamCodec.unit(new TakeOffPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TakeOffPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                AntidoteLogic.takeOff(player, true);
            }
        });
    }
}
