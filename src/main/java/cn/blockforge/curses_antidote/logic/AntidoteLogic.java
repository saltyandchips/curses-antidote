package cn.blockforge.curses_antidote.logic;

import cn.blockforge.curses_antidote.CursesAntidoteMod;
import cn.blockforge.curses_antidote.compat.CuriosBridge;
import cn.blockforge.curses_antidote.compat.CurseDetector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class AntidoteLogic {
    private AntidoteLogic() {
    }

    public static boolean hasAntidote(Player player) {
        return !CuriosBridge.findEquipped(player, CursesAntidoteMod.CURSES_ANTIDOTE.get()).isEmpty();
    }

    public static boolean takeOff(Player player, boolean giveBack) {
        boolean changed = false;
        for (CuriosBridge.Entry entry : CuriosBridge.entries(player)) {
            if (!CurseDetector.isCursed(entry.stack())) {
                continue;
            }
            ItemStack stack = entry.stack().copy();
            if (CuriosBridge.forceSet(player, entry.slotType(), entry.index(), ItemStack.EMPTY)) {
                if (giveBack && !player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                changed = true;
            }
        }
        return changed;
    }
}
