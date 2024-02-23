package com.mlntcandy.netask.cntmoneyminer.utils;

import com.mlntcandy.netask.cntmoneyminer.CNTMoneyMiner;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.LangBuilder;
import com.simibubi.create.foundation.utility.LangNumberFormat;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

public class Lang extends com.simibubi.create.foundation.utility.Lang {

    public static class Create extends com.simibubi.create.foundation.utility.Lang {}
    public static MutableComponent translateDirect(String key, Object... args) {
        return Components.translatable(CNTMoneyMiner.MODID + "." + key, resolveBuilders(args));
    }

    public static LangBuilder builder() {
        return builder(CNTMoneyMiner.MODID);
    }

    public static LangBuilder builder(String namespace) {
        return new LangBuilder(namespace);
    }

    public static LangBuilder blockName(BlockState state) {
        return builder().add(state.getBlock()
                .getName());
    }

    public static LangBuilder itemName(ItemStack stack) {
        return builder().add(stack.getHoverName()
                .copy());
    }

    public static LangBuilder fluidName(FluidStack stack) {
        return builder().add(stack.getDisplayName()
                .copy());
    }

    public static LangBuilder number(double d) {
        return builder().text(LangNumberFormat.format(d));
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

    public static LangBuilder text(String text) {
        return builder().text(text);
    }
}
