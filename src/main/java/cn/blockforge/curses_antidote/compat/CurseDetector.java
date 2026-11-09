package cn.blockforge.curses_antidote.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;
import java.util.Set;

public final class CurseDetector {
    private static final String ELPLUS_HANDLER = "auviotre.enigmatic.legacy.handlers.EnigmaticHandler";
    private static final String INTERFACE_MS_DNA = "com.ytgld.moonstone.item.IDNASequence";
    private static final String INTERFACE_MS_NIGHTMARE =
            "com.moonstone.moonstonemod.init.moonstoneitem.i.INightmare";

    private static final String[] CLASS_PREFIXES = {
            "com.xiaoyue.celestial_artifacts.content.curios.curse.",
            "com.ytgld.moonstone.item.si.nightmare.",
            "com.ytgld.moonstone.item.ms.necora."
    };

    private static final Set<String> FALLBACK_IDS = Set.of(
            "enigmaticlegacyplus:cursed_ring",
            "celestial_artifacts:catastrophe_scroll",
            "moonstone:necora",
            "moonstone:nightmare_base");

    private static Method elCurseCheck;
    private static boolean elCurseCheckFailed;

    private CurseDetector() {
    }

    public static boolean isCursed(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (isElCursed(stack)) {
            return true;
        }
        Object item = stack.getItem();
        if (matches(item, INTERFACE_MS_DNA) || matches(item, INTERFACE_MS_NIGHTMARE)) {
            return true;
        }
        String className = item.getClass().getName();
        for (String prefix : CLASS_PREFIXES) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && FALLBACK_IDS.contains(id.toString());
    }

    public static boolean isElCursed(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (elCurseCheck == null && !elCurseCheckFailed) {
            try {
                elCurseCheck = Class.forName(ELPLUS_HANDLER).getMethod("isCursedItem", ItemStack.class);
            } catch (Throwable t) {
                elCurseCheckFailed = true;
            }
        }
        if (elCurseCheck == null) {
            return false;
        }
        try {
            return (Boolean) elCurseCheck.invoke(null, stack);
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean matches(Object item, String interfaceName) {
        try {
            return Class.forName(interfaceName).isInstance(item);
        } catch (Throwable t) {
            return false;
        }
    }
}
