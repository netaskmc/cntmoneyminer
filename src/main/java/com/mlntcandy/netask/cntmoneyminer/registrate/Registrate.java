package com.mlntcandy.netask.cntmoneyminer.registrate;

import com.mlntcandy.netask.cntmoneyminer.CNTMoneyMiner;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraftforge.eventbus.api.IEventBus;

public class Registrate {
    // Create a Deferred Register to hold Blocks which will all be registered under the "cNTMoneyMiner" namespace
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CNTMoneyMiner.MODID);

    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
    }
    public static void register(IEventBus modEventBus) {
        AllCreativeModeTabs.register(modEventBus);

        AllBlocks.register();
        AllBlockEntityTypes.register();

        REGISTRATE.registerEventListeners(modEventBus);
    }
}
