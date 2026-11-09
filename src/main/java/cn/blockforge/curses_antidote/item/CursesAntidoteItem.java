package cn.blockforge.curses_antidote.item;

import cn.blockforge.curses_antidote.ModItems;
import cn.blockforge.curses_antidote.compat.CuriosBridge;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class CursesAntidoteItem extends Item {
    
    public static final int MODE_COUNT = 3;

    public CursesAntidoteItem(Properties properties) {
        super(properties);
    }

    public static int mode(ItemStack stack) {
        Integer value = stack.get(ModItems.MODE.get());
        return value == null ? 0 : Mth.clamp(value, 0, MODE_COUNT - 1);
    }

    public static void setMode(ItemStack stack, int mode) {
        stack.set(ModItems.MODE.get(), Mth.clamp(mode, 0, MODE_COUNT - 1));
    }

    public static boolean cycleEquipped(Player player) {
        ItemStack stack = CuriosBridge.findEquipped(player, ModItems.CURSES_ANTIDOTE.get());
        if (stack.isEmpty()) {
            return false;
        }
        setMode(stack, (mode(stack) + 1) % MODE_COUNT);
        return true;
    }

    public static boolean cycleHeldOrEquipped(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.is(ModItems.CURSES_ANTIDOTE.get())) {
            setMode(mainHand, (mode(mainHand) + 1) % MODE_COUNT);
            return true;
        }
        ItemStack offHand = player.getOffhandItem();
        if (offHand.is(ModItems.CURSES_ANTIDOTE.get())) {
            setMode(offHand, (mode(offHand) + 1) % MODE_COUNT);
            return true;
        }
        return cycleEquipped(player);
    }

    public static AntidoteMode equippedMode(Player player) {
        ItemStack stack = CuriosBridge.findEquipped(player, ModItems.CURSES_ANTIDOTE.get());
        return AntidoteMode.byId(mode(stack));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(AntidoteMode.byId(mode(stack)).itemNameKey())
                .withStyle(ChatFormatting.BLUE);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        AntidoteMode mode = AntidoteMode.byId(mode(stack));
        tooltip.add(Component.translatable(mode.descKey()));
    }
}
