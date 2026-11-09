package cn.blockforge.curses_antidote.event;

import cn.blockforge.curses_antidote.compat.CurseRangeGuard;
import cn.blockforge.curses_antidote.compat.CuriosJudgment;
import cn.blockforge.curses_antidote.compat.EnigmaticTime;
import cn.blockforge.curses_antidote.logic.AntidoteLogic;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public final class ServerTickHandler {
    private ServerTickHandler() {
    }

    public static void onEntityTickPre(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            CurseRangeGuard.enter(player);
        }
    }

    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }
        CurseRangeGuard.exit(player);
        if (player.tickCount % 20 != 0) {
            return;
        }
        if (player.tickCount % 200 == 0) {
            
            CuriosJudgment.install();
        }
        if (AntidoteLogic.hasAntidote(player)) {
            EnigmaticTime.forceFull(player);
        }
    }
}
