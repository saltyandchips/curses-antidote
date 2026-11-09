package cn.blockforge.curses_antidote.compat;

import cn.blockforge.curses_antidote.item.AntidoteMode;
import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import cn.blockforge.curses_antidote.logic.AntidoteLogic;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class CurseRangeGuard {
    private static final String RING_CLASS = "com.aizistral.enigmaticlegacy.items.CursedRing";
    private static final String PARAM_CLASS = "com.aizistral.omniconfig.wrappers.Omniconfig$DoubleParameter";

    private static final List<Field> FIELDS = new ArrayList<>();
    private static final List<Method> GETTERS = new ArrayList<>();
    private static final List<Method> SETTERS = new ArrayList<>();
    private static final Map<UUID, double[]> SAVED = new HashMap<>();
    private static boolean failed;

    private CurseRangeGuard() {
    }

    private static void init() {
        if (!FIELDS.isEmpty() || failed) {
            return;
        }
        try {
            Class<?> ring = Class.forName(RING_CLASS);
            Class<?> param = Class.forName(PARAM_CLASS);
            Method getter = param.getMethod("getValue");
            Method setter = param.getMethod("setValue", double.class);
            for (Field field : ring.getFields()) {
                if (!param.isAssignableFrom(field.getType())) {
                    continue;
                }
                if (!field.getName().toLowerCase(Locale.ROOT).contains("range")) {
                    continue;
                }
                FIELDS.add(field);
                GETTERS.add(getter);
                SETTERS.add(setter);
            }
            if (FIELDS.isEmpty()) {
                failed = true;
            }
        } catch (Throwable t) {
            failed = true;
        }
    }

    public static void enter(Player player) {
        init();
        if (failed || player == null || SAVED.containsKey(player.getUUID())) {
            return;
        }
        if (!AntidoteLogic.hasAntidote(player)
                || CursesAntidoteItem.equippedMode(player) != AntidoteMode.NEXT_TIME) {
            return;
        }
        boolean elCursed = false;
        for (ItemStack stack : CuriosBridge.equipped(player)) {
            if (CurseDetector.isElCursed(stack)) {
                elCursed = true;
                break;
            }
        }
        if (!elCursed) {
            return;
        }
        try {
            double[] saved = new double[FIELDS.size()];
            for (int i = 0; i < FIELDS.size(); i++) {
                Object parameter = FIELDS.get(i).get(null);
                saved[i] = (Double) GETTERS.get(i).invoke(parameter);
                SETTERS.get(i).invoke(parameter, 0.0D);
            }
            SAVED.put(player.getUUID(), saved);
        } catch (Throwable ignored) {
            
        }
    }

    public static void exit(Player player) {
        if (player == null) {
            return;
        }
        double[] saved = SAVED.remove(player.getUUID());
        if (saved == null) {
            return;
        }
        try {
            for (int i = 0; i < FIELDS.size(); i++) {
                SETTERS.get(i).invoke(FIELDS.get(i).get(null), saved[i]);
            }
        } catch (Throwable ignored) {
            
        }
    }
}
