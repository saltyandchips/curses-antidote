package cn.blockforge.curses_antidote.item;

import cn.blockforge.curses_antidote.CursesAntidoteMod;
import cn.blockforge.curses_antidote.compat.CuriosBridge;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public final class CursesAntidoteItem extends Item {
    
    public static final int MODE_COUNT = 3;
    
    public static final String MODE_TAG = "Mode";

    public CursesAntidoteItem(Properties properties) {
        super(properties);
    }

    public static int mode(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(MODE_TAG)) {
            return 0;
        }
        return Mth.clamp(tag.getInt(MODE_TAG), 0, MODE_COUNT - 1);
    }

    public static void setMode(ItemStack stack, int mode) {
        stack.getOrCreateTag().putInt(MODE_TAG, Mth.clamp(mode, 0, MODE_COUNT - 1));
    }

    public static boolean cycleEquipped(Player player) {
        ItemStack stack = CuriosBridge.findEquipped(player, CursesAntidoteMod.CURSES_ANTIDOTE.get());
        if (stack.isEmpty()) {
            return false;
        }
        setMode(stack, (mode(stack) + 1) % MODE_COUNT);
        return true;
    }

    public static boolean cycleHeldOrEquipped(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.is(CursesAntidoteMod.CURSES_ANTIDOTE.get())) {
            setMode(mainHand, (mode(mainHand) + 1) % MODE_COUNT);
            return true;
        }
        ItemStack offHand = player.getOffhandItem();
        if (offHand.is(CursesAntidoteMod.CURSES_ANTIDOTE.get())) {
            setMode(offHand, (mode(offHand) + 1) % MODE_COUNT);
            return true;
        }
        return cycleEquipped(player);
    }

    public static AntidoteMode equippedMode(Player player) {
        ItemStack stack = CuriosBridge.findEquipped(player, CursesAntidoteMod.CURSES_ANTIDOTE.get());
        return AntidoteMode.byId(mode(stack));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(AntidoteMode.byId(mode(stack)).itemNameKey())
                .withStyle(ChatFormatting.BLUE);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        AntidoteMode mode = AntidoteMode.byId(mode(stack));
        tooltip.add(Component.translatable(mode.descKey()));
    }
}
