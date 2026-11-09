package cn.blockforge.curses_antidote.event;

import cn.blockforge.curses_antidote.compat.CurseRangeGuard;
import cn.blockforge.curses_antidote.compat.CuriosJudgment;
import cn.blockforge.curses_antidote.compat.EnigmaticTime;
import cn.blockforge.curses_antidote.item.AntidoteMode;
import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import cn.blockforge.curses_antidote.logic.AntidoteLogic;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class ServerTickHandler {
    private ServerTickHandler() {
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.level().isClientSide()) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            CurseRangeGuard.enter(player);
            return;
        }
        CurseRangeGuard.exit(player);
        if (AntidoteLogic.hasAntidote(player)
                && CursesAntidoteItem.equippedMode(player) == AntidoteMode.NEXT_TIME) {
            cleanupDirectModifiers(player);
        }
        if (player.tickCount % 20 != 0) {
            return;
        }
        if (player.tickCount % 200 == 0) {
            
            CuriosJudgment.install();
        }
        if (AntidoteLogic.hasAntidote(player)) {
            EnigmaticTime.forceFull(player);
        }
    }

    private static void cleanupDirectModifiers(Player player) {
        for (Attribute attribute : ForgeRegistries.ATTRIBUTES.getValues()) {
            AttributeInstance instance = player.getAttributes().getInstance(attribute);
            if (instance == null) {
                continue;
            }
            List<AttributeModifier> removals = null;
            for (AttributeModifier modifier : instance.getModifiers()) {
                String name = modifier.getName();
                if ("ara".equals(name) || "catastrophe_scroll".equals(name)) {
                    if (removals == null) {
                        removals = new ArrayList<>();
                    }
                    removals.add(modifier);
                }
            }
            if (removals != null) {
                for (AttributeModifier modifier : removals) {
                    instance.removeModifier(modifier);
                }
            }
        }
    }
}
