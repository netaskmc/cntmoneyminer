package com.mlntcandy.netask.cntmoneyminer.registrate;

import com.mlntcandy.netask.cntmoneyminer.blocks.autoMiner.AutoMinerBlock;
import com.mlntcandy.netask.cntmoneyminer.blocks.miner.MinerBlock;
import com.mlntcandy.netask.cntmoneyminer.blocks.miner.MinerBlockItem;
import com.mlntcandy.netask.cntmoneyminer.items.hackDevice.HackDeviceItem;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;

import java.util.Objects;

import static com.mlntcandy.netask.cntmoneyminer.registrate.Registrate.REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class AllItems {
    public static final ItemEntry<HackDeviceItem> HACK_DEVICE = REGISTRATE
            .item("hack_device", HackDeviceItem::new)
            .properties(p -> p.stacksTo(1)
                    .durability(HackDeviceItem.MAX_USES * HackDeviceItem.HACK_DURATION)
            )
//            .tab(Objects.requireNonNull(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey()))
            .register();

    // Load this class

    public static void register() {}
}
