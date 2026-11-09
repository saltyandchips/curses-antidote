package cn.blockforge.curses_antidote.compat;

import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;

public final class EnigmaticTime {
    private static final String COUNTER_CLASS =
            "com.aizistral.enigmaticlegacy.api.capabilities.IPlaytimeCounter";

    private static Method getCounter;
    private static Method getWith;
    private static Method setWith;
    private static Method setWithout;
    private static boolean failed;

    private EnigmaticTime() {
    }

    private static void init() {
        if (getCounter != null || failed) {
            return;
        }
        try {
            Class<?> counter = Class.forName(COUNTER_CLASS);
            getCounter = counter.getMethod("get", Player.class);
            getWith = counter.getMethod("getTimeWithCurses");
            setWith = counter.getMethod("setTimeWithCurses", long.class);
            setWithout = counter.getMethod("setTimeWithoutCurses", long.class);
        } catch (Throwable t) {
            failed = true;
        }
    }

    public static void forceFull(Player player) {
        init();
        if (failed || player == null) {
            return;
        }
        try {
            Object counter = getCounter.invoke(null, player);
            if (counter == null) {
                return;
            }
            long with = (Long) getWith.invoke(counter);
            if (with <= 0L) {
                setWith.invoke(counter, 1000L);
            }
            setWithout.invoke(counter, 0L);
        } catch (Throwable ignored) {
            
        }
    }
}
