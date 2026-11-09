package cn.blockforge.curses_antidote.network;

import cn.blockforge.curses_antidote.CursesAntidoteMod;
import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CycleModePayload() implements CustomPacketPayload {
    public static final Type<CycleModePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CursesAntidoteMod.MOD_ID, "cycle_mode"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CycleModePayload> STREAM_CODEC =
            StreamCodec.unit(new CycleModePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CycleModePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                CursesAntidoteItem.cycleHeldOrEquipped(player);
            }
        });
    }
}
