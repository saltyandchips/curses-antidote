package cn.blockforge.curses_antidote.network;

import cn.blockforge.curses_antidote.CursesAntidoteMod;
import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import cn.blockforge.curses_antidote.logic.AntidoteLogic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CursesAntidoteMod.MOD_ID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private ModNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(0, CycleModeMessage.class, CycleModeMessage::encode, CycleModeMessage::decode,
                (message, contextSupplier) -> {
                    NetworkEvent.Context context = contextSupplier.get();
                    ServerPlayer player = context.getSender();
                    context.enqueueWork(() -> {
                        if (player != null) {
                            CursesAntidoteItem.cycleHeldOrEquipped(player);
                        }
                    });
                    context.setPacketHandled(true);
                });
        CHANNEL.registerMessage(1, TakeOffMessage.class, TakeOffMessage::encode, TakeOffMessage::decode,
                (message, contextSupplier) -> {
                    NetworkEvent.Context context = contextSupplier.get();
                    ServerPlayer player = context.getSender();
                    context.enqueueWork(() -> {
                        if (player != null) {
                            AntidoteLogic.takeOff(player, true);
                        }
                    });
                    context.setPacketHandled(true);
                });
    }

    public record CycleModeMessage() {
        public static void encode(CycleModeMessage message, FriendlyByteBuf buffer) {
        }

        public static CycleModeMessage decode(FriendlyByteBuf buffer) {
            return new CycleModeMessage();
        }
    }

    public record TakeOffMessage() {
        public static void encode(TakeOffMessage message, FriendlyByteBuf buffer) {
        }

        public static TakeOffMessage decode(FriendlyByteBuf buffer) {
            return new TakeOffMessage();
        }
    }
}
