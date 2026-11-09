package cn.blockforge.curses_antidote.compat;

import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public final class EnigmaticTime {
    private static final String ATTACHMENTS_CLASS = "auviotre.enigmatic.legacy.registries.EnigmaticAttachments";
    private static final String DATA_CLASS = "auviotre.enigmatic.legacy.contents.attachement.EnigmaticData";
    private static final String HOLDER_CLASS = "net.neoforged.neoforge.attachment.AttachmentHolder";
    private static final String TYPE_CLASS = "net.neoforged.neoforge.attachment.AttachmentType";

    private static Object dataSupplier;
    private static Method getData;
    private static Method getWith;
    private static Method setWith;
    private static Method setWithout;
    private static boolean failed;

    private EnigmaticTime() {
    }

    private static void init() {
        if (dataSupplier != null || failed) {
            return;
        }
        try {
            Class<?> attachments = Class.forName(ATTACHMENTS_CLASS);
            Field field = attachments.getField("ENIGMATIC_DATA");
            dataSupplier = field.get(null);
            Class<?> holder = Class.forName(HOLDER_CLASS);
            getData = holder.getMethod("getData", Class.forName(TYPE_CLASS));
            Class<?> data = Class.forName(DATA_CLASS);
            getWith = data.getMethod("getTimeWithCurses");
            setWith = data.getMethod("setTimeWithCurses", long.class);
            setWithout = data.getMethod("setTimeWithoutCurses", long.class);
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
            Object type = ((Supplier<?>) dataSupplier).get();
            Object data = getData.invoke(player, type);
            if (data == null) {
                return;
            }
            long with = (Long) getWith.invoke(data);
            if (with <= 0L) {
                setWith.invoke(data, 1000L);
            }
            setWithout.invoke(data, 0L);
        } catch (Throwable ignored) {
            
        }
    }
}
