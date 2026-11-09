package cn.blockforge.curses_antidote.event;

import cn.blockforge.curses_antidote.compat.CurseDetector;
import cn.blockforge.curses_antidote.compat.CuriosBridge;
import cn.blockforge.curses_antidote.item.AntidoteMode;
import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import cn.blockforge.curses_antidote.logic.AntidoteLogic;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class NumericHandler {
    private static final double CURSE_FACTOR = 1.5D;
    private static final double BLESS_FACTOR = 2.0D;
    private static final float DAMAGE_FACTOR = 1.5F;

    private NumericHandler() {
    }

    public static void onCurioAttributeModifier(CurioAttributeModifierEvent event) {
        if (!(event.getSlotContext().entity() instanceof Player player)) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!CurseDetector.isCursed(stack)) {
            return;
        }
        AntidoteMode mode = CursesAntidoteItem.equippedMode(player);
        if (!AntidoteLogic.hasAntidote(player)) {
            return;
        }
        if (mode == AntidoteMode.NEXT_TIME) {
            
            event.clearModifiers();
            return;
        }
        double curseFactor = CURSE_FACTOR;
        double blessFactor = mode == AntidoteMode.SO_STRONG ? BLESS_FACTOR : 1.0D;
        Multimap<Holder<Attribute>, AttributeModifier> modifiers = event.getModifiers();
        if (modifiers.isEmpty()) {
            return;
        }
        List<Map.Entry<Holder<Attribute>, AttributeModifier>> snapshot = new ArrayList<>(modifiers.entries());
        event.clearModifiers();
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : snapshot) {
            AttributeModifier modifier = entry.getValue();
            double factor = modifier.amount() < 0.0D ? curseFactor : blessFactor;
            if (factor == 1.0D) {
                event.addModifier(entry.getKey(), modifier);
            } else {
                event.addModifier(entry.getKey(), new AttributeModifier(
                        modifier.id(), modifier.amount() * factor, modifier.operation()));
            }
        }
    }

    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        AntidoteMode mode = CursesAntidoteItem.equippedMode(player);
        if (mode != AntidoteMode.SO_STRONG && mode != AntidoteMode.MAN) {
            return;
        }
        for (ItemStack stack : CuriosBridge.equipped(player)) {
            if (CurseDetector.isElCursed(stack)) {
                event.setAmount(event.getAmount() * DAMAGE_FACTOR);
                return;
            }
        }
    }
}
