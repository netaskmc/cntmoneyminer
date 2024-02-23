package com.mlntcandy.netask.cntmoneyminer.registrate;

import com.mlntcandy.netask.cntmoneyminer.blocks.autoMiner.AutoMinerBlock;
import com.mlntcandy.netask.cntmoneyminer.blocks.miner.MinerBlockItem;
import com.mlntcandy.netask.cntmoneyminer.blocks.miner.MinerBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import org.checkerframework.checker.units.qual.A;

import java.util.Objects;

import static com.mlntcandy.netask.cntmoneyminer.registrate.Registrate.REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class AllBlocks {
    public static final BlockEntry<MinerBlock> MINER = REGISTRATE
            .block("miner", MinerBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(pickaxeOnly())
            .transform(BlockStressDefaults.setImpact(4.0))
            .item(MinerBlockItem::new)
            .tab(Objects.requireNonNull(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey()))
            .build()
            .register();

    public static final BlockEntry<AutoMinerBlock> AUTO_MINER = REGISTRATE
            .block("auto_miner", AutoMinerBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(pickaxeOnly())
            .transform(BlockStressDefaults.setImpact(4.0))
            .item(MinerBlockItem::new)
            .tab(Objects.requireNonNull(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey()))
            .build()
            .register();

    // Load this class

    public static void register() {}
}
