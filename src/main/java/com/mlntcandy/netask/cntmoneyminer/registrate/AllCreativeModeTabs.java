package com.mlntcandy.netask.cntmoneyminer.registrate;

import com.mlntcandy.netask.cntmoneyminer.CNTMoneyMiner;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AllCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CNTMoneyMiner.MODID);

    public static final RegistryObject<CreativeModeTab> BASE_CREATIVE_TAB = REGISTER
            .register("base", () -> CreativeModeTab.builder()
                    .title(Components.translatable("itemGroup.cntmoneyminer.base"))
                    .withTabsBefore(com.simibubi.create.AllCreativeModeTabs.BASE_CREATIVE_TAB.getId())
                    .icon(AllBlocks.MINER::asStack)
                    .build()
            );

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}
