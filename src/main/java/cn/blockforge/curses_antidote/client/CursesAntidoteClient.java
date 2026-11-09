package cn.blockforge.curses_antidote.client;

import cn.blockforge.curses_antidote.CursesAntidoteMod;
import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CursesAntidoteMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CursesAntidoteClient {
    private CursesAntidoteClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    CursesAntidoteMod.CURSES_ANTIDOTE.get(),
                    new ResourceLocation(CursesAntidoteMod.MOD_ID, "mode"),
                    (stack, level, entity, seed) -> CursesAntidoteItem.mode(stack) / 2.0F);
            MinecraftForge.EVENT_BUS.addListener(ClientInteractionHandler::onRightClickBlock);
            MinecraftForge.EVENT_BUS.addListener(ClientInteractionHandler::onRightClickItem);
            MinecraftForge.EVENT_BUS.addListener(ClientInteractionHandler::onRightClickEmpty);
        });
    }
}
