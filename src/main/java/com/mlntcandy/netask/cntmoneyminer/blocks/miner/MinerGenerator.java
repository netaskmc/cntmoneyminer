package com.mlntcandy.netask.cntmoneyminer.blocks.miner;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class MinerGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(BlockState state) {
        return state.getValue(MinerBlock.FACING) == Direction.DOWN ? 180 : 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return state.getValue(MinerBlock.FACING)
                .getAxis()
                .isVertical() ? 0 : horizontalAngle(state.getValue(MinerBlock.FACING));
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                BlockState state) {
        return state.getValue(MinerBlock.FACING)
                .getAxis()
                .isVertical() ? AssetLookup.partialBaseModel(ctx, prov, "vertical")
                : AssetLookup.partialBaseModel(ctx, prov);
    }

}
