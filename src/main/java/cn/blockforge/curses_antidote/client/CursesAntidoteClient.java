package cn.blockforge.curses_antidote.client;

import cn.blockforge.curses_antidote.ModItems;
import cn.blockforge.curses_antidote.CursesAntidoteMod;
import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(modid = CursesAntidoteMod.MOD_ID, value = Dist.CLIENT)
public final class CursesAntidoteClient {
    private CursesAntidoteClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    ModItems.CURSES_ANTIDOTE.get(),
                    ResourceLocation.fromNamespaceAndPath(CursesAntidoteMod.MOD_ID, "mode"),
                    (stack, level, entity, seed) -> CursesAntidoteItem.mode(stack) / 2.0F);
            NeoForge.EVENT_BUS.addListener(ClientInteractionHandler::onRightClickBlock);
            NeoForge.EVENT_BUS.addListener(ClientInteractionHandler::onRightClickItem);
            NeoForge.EVENT_BUS.addListener(ClientInteractionHandler::onRightClickEmpty);
        });
    }
}
