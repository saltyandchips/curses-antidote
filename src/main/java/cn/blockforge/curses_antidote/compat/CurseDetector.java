package cn.blockforge.curses_antidote.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public final class CurseDetector {
    private static final String INTERFACE_EL = "com.aizistral.enigmaticlegacy.api.items.ICursed";
    private static final String INTERFACE_MS_INIGHTITEM = "com.all.INightItem";
    private static final String INTERFACE_MS_NIGHTMARE_MARK =
            "com.moonstone.moonstonemod.item.nightmare.Nightmare";
    private static final String INTERFACE_MS_NIGHTMARE =
            "com.moonstone.moonstonemod.init.moonstoneitem.i.INightmare";

    private static final String[] CLASS_PREFIXES = {
            "com.xiaoyue.celestial_artifacts.content.curios.curse.",
            "com.ytgld.seeking_immortals.item.nightmare.",
            "com.moonstone.moonstonemod.item.nightmare."
    };

    private static final Set<String> FALLBACK_IDS = Set.of(
            "enigmaticlegacy:cursed_ring",
            "celestial_artifacts:catastrophe_scroll",
            "moonstone:necora",
            "moonstone:nightmare_base");

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
        if (matches(item, INTERFACE_MS_INIGHTITEM)
                || matches(item, INTERFACE_MS_NIGHTMARE_MARK)
                || matches(item, INTERFACE_MS_NIGHTMARE)) {
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
        return stack != null && !stack.isEmpty() && matches(stack.getItem(), INTERFACE_EL);
    }

    private static boolean matches(Object item, String interfaceName) {
        try {
            return Class.forName(interfaceName).isInstance(item);
        } catch (Throwable t) {
            return false;
        }
    }
}
