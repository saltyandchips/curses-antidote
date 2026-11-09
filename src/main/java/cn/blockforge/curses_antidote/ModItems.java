package cn.blockforge.curses_antidote;

import cn.blockforge.curses_antidote.item.CursesAntidoteItem;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CursesAntidoteMod.MOD_ID);

    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(CursesAntidoteMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MODE =
            COMPONENTS.registerComponentType("mode", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredItem<CursesAntidoteItem> CURSES_ANTIDOTE =
            ITEMS.registerItem("curses_antidote", CursesAntidoteItem::new,
                    new Item.Properties().stacksTo(1));

    private ModItems() {
    }
}
