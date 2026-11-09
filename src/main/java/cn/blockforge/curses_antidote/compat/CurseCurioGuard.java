package cn.blockforge.curses_antidote.compat;

import cn.blockforge.curses_antidote.item.AntidoteMode;
import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import cn.blockforge.curses_antidote.logic.AntidoteLogic;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

public final class CurseCurioGuard {
    private static boolean installed;

    private CurseCurioGuard() {
    }

    public static void install() {
        if (installed) {
            return;
        }
        installed = true;
        for (Item item : BuiltInRegistries.ITEM) {
            ICurioItem original = originalOf(item);
            if (original == null || item == null) {
                continue;
            }
            if (!CurseDetector.isCursed(new ItemStack(item))) {
                continue;
            }
            CuriosApi.registerCurio(item, proxy(original));
        }
    }

    private static ICurioItem originalOf(Item item) {
        try {
            Class<?> hooks = Class.forName("top.theillusivec4.curios.mixin.CuriosImplMixinHooks");
            Object result = hooks.getMethod("getCurioFromRegistry", Item.class).invoke(null, item);
            if (result instanceof Optional<?> optional && optional.isPresent()) {
                return (ICurioItem) optional.get();
            }
        } catch (Throwable ignored) {
            
        }
        return item instanceof ICurioItem curioItem ? curioItem : null;
    }

    private static ICurioItem proxy(ICurioItem real) {
        return (ICurioItem) Proxy.newProxyInstance(
                ICurioItem.class.getClassLoader(),
                new Class<?>[]{ICurioItem.class},
                new Handler(real));
    }

    private static final class Handler implements InvocationHandler {
        private final ICurioItem real;

        Handler(ICurioItem real) {
            this.real = real;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            LivingEntity wearer = wearer(args);
            ItemStack stack = stack(args);
            if (wearer != null && stack != null && inert(wearer, stack)) {
                switch (method.getName()) {
                    case "curioTick", "curioAnimate", "onEquip", "onEquipFromUse" -> {
                        return null;
                    }
                    case "getAttributeModifiers" -> {
                        return ImmutableMultimap.of();
                    }
                    case "canUnequip" -> {
                        return true;
                    }
                    case "getFortuneLevel", "getLootingLevel" -> {
                        return 0;
                    }
                    case "makesPiglinsNeutral", "canWalkOnPowderedSnow", "isEnderMask" -> {
                        return false;
                    }
                    default -> {
                    }
                }
            }
            return method.invoke(real, args);
        }
    }

    private static LivingEntity wearer(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof SlotContext context) {
                return context.entity();
            }
            if (arg instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }

    private static ItemStack stack(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof ItemStack itemStack) {
                return itemStack;
            }
        }
        return null;
    }

    private static boolean inert(LivingEntity wearer, ItemStack stack) {
        if (!(wearer instanceof Player player) || stack.isEmpty()) {
            return false;
        }
        return AntidoteLogic.hasAntidote(player)
                && CursesAntidoteItem.equippedMode(player) == AntidoteMode.NEXT_TIME
                && CurseDetector.isCursed(stack);
    }
}
