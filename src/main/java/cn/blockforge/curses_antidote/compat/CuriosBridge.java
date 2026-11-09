package cn.blockforge.curses_antidote.compat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CuriosBridge {
    private static final String API_CLASS = "top.theillusivec4.curios.api.CuriosApi";
    private static final String HANDLER_CLASS = "top.theillusivec4.curios.api.type.capability.ICuriosItemHandler";
    private static final String STACKS_HANDLER_CLASS = "top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler";

    private static Method getInventory;
    private static Method getCurios;
    private static Method getStacks;
    private static Method setEquippedCurio;
    private static boolean failed;

    private CuriosBridge() {
    }

    private static void init() {
        if (getInventory != null || failed) {
            return;
        }
        try {
            Class<?> api = Class.forName(API_CLASS);
            Class<?> handler = Class.forName(HANDLER_CLASS);
            Class<?> stacksHandler = Class.forName(STACKS_HANDLER_CLASS);
            getInventory = api.getMethod("getCuriosInventory", LivingEntity.class);
            getCurios = handler.getMethod("getCurios");
            setEquippedCurio = handler.getMethod("setEquippedCurio", String.class, int.class, ItemStack.class);
            getStacks = stacksHandler.getMethod("getStacks");
        } catch (Throwable t) {
            failed = true;
        }
    }

    public record Entry(String slotType, int index, ItemStack stack) {
    }

    public static List<Entry> entries(LivingEntity entity) {
        List<Entry> out = new ArrayList<>();
        init();
        if (failed || entity == null) {
            return out;
        }
        try {
            Object handler = unwrap(getInventory.invoke(null, entity));
            if (handler == null) {
                return out;
            }
            Map<?, ?> curios = (Map<?, ?>) getCurios.invoke(handler);
            for (Map.Entry<?, ?> slotEntry : curios.entrySet()) {
                String slotType = String.valueOf(slotEntry.getKey());
                Object stacks = getStacks.invoke(slotEntry.getValue());
                if (stacks instanceof IItemHandlerModifiable inv) {
                    for (int i = 0; i < inv.getSlots(); i++) {
                        ItemStack stack = inv.getStackInSlot(i);
                        if (!stack.isEmpty()) {
                            out.add(new Entry(slotType, i, stack));
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
            
        }
        return out;
    }

    public static List<ItemStack> equipped(LivingEntity entity) {
        List<ItemStack> out = new ArrayList<>();
        for (Entry entry : entries(entity)) {
            out.add(entry.stack());
        }
        return out;
    }

    public static ItemStack findEquipped(LivingEntity entity, Item item) {
        for (Entry entry : entries(entity)) {
            if (entry.stack().is(item)) {
                return entry.stack();
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean forceSet(LivingEntity entity, String slotType, int index, ItemStack stack) {
        init();
        if (failed || entity == null) {
            return false;
        }
        try {
            Object handler = unwrap(getInventory.invoke(null, entity));
            if (handler == null) {
                return false;
            }
            setEquippedCurio.invoke(handler, slotType, index, stack);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static Object unwrap(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.util.Optional<?> optional) {
            return optional.orElse(null);
        }
        try {
            return value.getClass().getMethod("orElse", Object.class).invoke(value, (Object) null);
        } catch (Throwable t) {
            return null;
        }
    }
}
