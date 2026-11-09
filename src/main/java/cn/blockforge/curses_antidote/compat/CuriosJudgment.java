package cn.blockforge.curses_antidote.compat;

import cn.blockforge.curses_antidote.item.AntidoteMode;
import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import cn.blockforge.curses_antidote.logic.AntidoteLogic;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.util.ICuriosHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CuriosJudgment {
    private static final String[] CURSE_PACKAGES = {
            "com.aizistral.enigmaticlegacy",
            "auviotre.enigmatic.legacy",
            "com.xiaoyue.celestial_artifacts",
            "com.moonstone",
            "com.ytgld.moonstone"
    };
    private static final StackWalker WALKER =
            StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private static ICuriosHelper proxy;

    private CuriosJudgment() {
    }

    public static synchronized void install() {
        try {
            ICuriosHelper current = CuriosApi.getCuriosHelper();
            if (current == null || current == proxy) {
                return;
            }
            proxy = (ICuriosHelper) Proxy.newProxyInstance(
                    ICuriosHelper.class.getClassLoader(),
                    new Class<?>[]{ICuriosHelper.class},
                    new Handler(current));
            CuriosApi.setCuriosHelper(proxy);
        } catch (Throwable ignored) {
            
        }
    }

    private static final class Handler implements InvocationHandler {
        private final ICuriosHelper real;

        Handler(ICuriosHelper real) {
            this.real = real;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(real, args);
            if (!callerIsCurseMod()) {
                return result;
            }
            LivingEntity wearer = wearer(args);
            if (wearer == null) {
                return result;
            }
            return switch (method.getName()) {
                case "findEquippedCurio" -> filterTriple(result, wearer);
                case "findFirstCurio", "findCurio" -> filterOptional(result, wearer);
                case "findCurios" -> filterList(result, wearer);
                default -> result;
            };
        }
    }

    private static LivingEntity wearer(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Object filterTriple(Object result, LivingEntity wearer) {
        Optional<ImmutableTriple<String, Integer, ItemStack>> optional =
                (Optional<ImmutableTriple<String, Integer, ItemStack>>) result;
        if (optional != null && optional.isPresent() && isInert(optional.get().getRight(), wearer)) {
            return Optional.empty();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Object filterOptional(Object result, LivingEntity wearer) {
        Optional<SlotResult> optional = (Optional<SlotResult>) result;
        if (optional != null && optional.isPresent() && isInert(optional.get().stack(), wearer)) {
            return Optional.empty();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Object filterList(Object result, LivingEntity wearer) {
        List<SlotResult> list = (List<SlotResult>) result;
        if (list == null || list.isEmpty()) {
            return result;
        }
        List<SlotResult> kept = new ArrayList<>(list.size());
        for (SlotResult slotResult : list) {
            if (!isInert(slotResult.stack(), wearer)) {
                kept.add(slotResult);
            }
        }
        return kept;
    }

    public static boolean isInert(ItemStack stack, LivingEntity wearer) {
        if (stack == null || stack.isEmpty() || !(wearer instanceof Player player)) {
            return false;
        }
        if (!AntidoteLogic.hasAntidote(player)
                || CursesAntidoteItem.equippedMode(player) != AntidoteMode.NEXT_TIME) {
            return false;
        }
        return CurseDetector.isCursed(stack);
    }

    private static boolean callerIsCurseMod() {
        return WALKER.walk(frames -> frames
                .map(StackWalker.StackFrame::getClassName)
                .filter(CuriosJudgment::isAppClass)
                .findFirst()
                .map(CuriosJudgment::isCurseClass)
                .orElse(false));
    }

    private static boolean isAppClass(String name) {
        return !name.startsWith("cn.blockforge.curses_antidote")
                && !name.startsWith("top.theillusivec4.curios")
                && !name.startsWith("java.")
                && !name.startsWith("jdk.")
                && !name.startsWith("sun.")
                && !name.startsWith("com.sun.proxy")
                && !name.startsWith("net.minecraft.")
                && !name.startsWith("net.minecraftforge.");
    }

    private static boolean isCurseClass(String name) {
        for (String prefix : CURSE_PACKAGES) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
