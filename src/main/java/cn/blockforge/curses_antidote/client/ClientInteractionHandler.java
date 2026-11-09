package cn.blockforge.curses_antidote.client;

import cn.blockforge.curses_antidote.compat.CurseDetector;
import cn.blockforge.curses_antidote.compat.CuriosBridge;
import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import cn.blockforge.curses_antidote.logic.AntidoteLogic;
import cn.blockforge.curses_antidote.network.ModNetwork;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public final class ClientInteractionHandler {
    private ClientInteractionHandler() {
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (handle(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (handle(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        handle(event.getEntity());
    }

    private static boolean handle(Player player) {
        if (!player.isShiftKeyDown()) {
            return false;
        }
        if (Screen.hasControlDown()) {
            return takeOff(player);
        }
        return cycle(player);
    }

    private static boolean cycle(Player player) {
        if (!CursesAntidoteItem.cycleHeldOrEquipped(player)) {
            return false;
        }
        ModNetwork.CHANNEL.sendToServer(new ModNetwork.CycleModeMessage());
        return true;
    }

    private static boolean takeOff(Player player) {
        boolean anyCursed = false;
        for (CuriosBridge.Entry entry : CuriosBridge.entries(player)) {
            if (CurseDetector.isCursed(entry.stack())) {
                anyCursed = true;
                break;
            }
        }
        if (!anyCursed) {
            return false;
        }
        
        AntidoteLogic.takeOff(player, false);
        ModNetwork.CHANNEL.sendToServer(new ModNetwork.TakeOffMessage());
        return true;
    }
}
