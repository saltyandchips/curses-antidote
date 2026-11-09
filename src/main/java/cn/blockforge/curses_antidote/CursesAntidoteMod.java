package cn.blockforge.curses_antidote;

import cn.blockforge.curses_antidote.compat.CurseCurioGuard;
import cn.blockforge.curses_antidote.compat.CuriosJudgment;
import cn.blockforge.curses_antidote.event.NumericHandler;
import cn.blockforge.curses_antidote.event.ServerTickHandler;
import cn.blockforge.curses_antidote.network.CycleModePayload;
import cn.blockforge.curses_antidote.network.TakeOffPayload;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(CursesAntidoteMod.MOD_ID)
public final class CursesAntidoteMod {
    public static final String MOD_ID = "curses_antidote";

    public CursesAntidoteMod(IEventBus modBus) {
        ModItems.ITEMS.register(modBus);
        ModItems.COMPONENTS.register(modBus);
        modBus.addListener(this::addCreativeTabEntries);
        modBus.addListener(this::registerPayloads);
        modBus.addListener(this::onCommonSetup);

        NeoForge.EVENT_BUS.addListener(NumericHandler::onCurioAttributeModifier);
        NeoForge.EVENT_BUS.addListener(NumericHandler::onLivingIncomingDamage);
        NeoForge.EVENT_BUS.addListener(ServerTickHandler::onEntityTickPre);
        NeoForge.EVENT_BUS.addListener(ServerTickHandler::onEntityTick);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CuriosJudgment.install();
            CurseCurioGuard.install();
        });
    }

    private void addCreativeTabEntries(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.CURSES_ANTIDOTE.get());
        }
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(CycleModePayload.TYPE, CycleModePayload.STREAM_CODEC, CycleModePayload::handle);
        registrar.playToServer(TakeOffPayload.TYPE, TakeOffPayload.STREAM_CODEC, TakeOffPayload::handle);
    }
}
