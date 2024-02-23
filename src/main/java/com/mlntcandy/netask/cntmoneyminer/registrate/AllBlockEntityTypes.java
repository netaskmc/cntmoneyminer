package com.mlntcandy.netask.cntmoneyminer.registrate;

import com.mlntcandy.netask.cntmoneyminer.blocks.miner.MinerBlockEntity;
import com.mlntcandy.netask.cntmoneyminer.blocks.miner.MinerRenderer;
import com.simibubi.create.content.kinetics.base.HalfShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.mlntcandy.netask.cntmoneyminer.registrate.Registrate.REGISTRATE;

public class AllBlockEntityTypes {
    public static final BlockEntityEntry<MinerBlockEntity> MINER = REGISTRATE
            .blockEntity("miner", MinerBlockEntity::new)
            .instance(() -> HalfShaftInstance::new, false)
            .validBlocks(AllBlocks.MINER, AllBlocks.AUTO_MINER)
            .renderer(() -> MinerRenderer::new)
            .register();

    // Load this class

    public static void register() {}
}
