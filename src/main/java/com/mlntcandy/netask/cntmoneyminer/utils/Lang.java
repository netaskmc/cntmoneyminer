package com.mlntcandy.netask.cntmoneyminer.utils;

import com.mlntcandy.netask.cntmoneyminer.CNTMoneyMiner;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.network.chat.MutableComponent;

public class Lang extends com.simibubi.create.foundation.utility.Lang {

    public static class Create extends com.simibubi.create.foundation.utility.Lang {}
    public static MutableComponent translateDirect(String key, Object... args) {
        return Components.translatable(CNTMoneyMiner.MODID + "." + key, resolveBuilders(args));
    }

    public static LangBuilder builder() {
        return builder(CNTMoneyMiner.MODID);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }
}
