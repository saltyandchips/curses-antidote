package cn.blockforge.curses_antidote;

import cn.blockforge.curses_antidote.compat.CurseCurioGuard;
import cn.blockforge.curses_antidote.compat.CuriosJudgment;
import cn.blockforge.curses_antidote.event.NumericHandler;
import cn.blockforge.curses_antidote.event.ServerTickHandler;
import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import cn.blockforge.curses_antidote.network.ModNetwork;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CursesAntidoteMod.MOD_ID)
public final class CursesAntidoteMod {
    public static final String MOD_ID = "curses_antidote";

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> CURSES_ANTIDOTE = ITEMS.register(
            "curses_antidote", () -> new CursesAntidoteItem(new Item.Properties().stacksTo(1)));

    public CursesAntidoteMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        modBus.addListener(this::addCreativeTabEntries);

        ModNetwork.register();
        modBus.addListener(this::onCommonSetup);
        MinecraftForge.EVENT_BUS.addListener(NumericHandler::onCurioAttributeModifier);
        MinecraftForge.EVENT_BUS.addListener(NumericHandler::onLivingHurt);
        MinecraftForge.EVENT_BUS.addListener(ServerTickHandler::onPlayerTick);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CuriosJudgment.install();
            CurseCurioGuard.install();
        });
    }

    private void addCreativeTabEntries(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(CURSES_ANTIDOTE.get());
        }
    }
}
