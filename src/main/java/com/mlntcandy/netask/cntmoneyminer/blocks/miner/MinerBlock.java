package com.mlntcandy.netask.cntmoneyminer.blocks.miner;

import com.mlntcandy.netask.cntmoneyminer.api.MinerRegistry;
import com.mlntcandy.netask.cntmoneyminer.registrate.AllBlockEntityTypes;
import com.mlntcandy.netask.cntmoneyminer.registrate.AllBlocks;
import com.mlntcandy.netask.cntmoneyminer.registrate.AllItems;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MinerBlock extends DirectionalKineticBlock implements IBE<MinerBlockEntity> {
    protected static final VoxelShape Y_AXIS_AABB = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(2.0, 2.0, 0.0, 14.0, 14.0, 16.0);
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 2.0, 2.0, 16.0, 14.0, 14.0);

    public MinerBlock(Properties properties) {
        super(properties);
    }

    public @NotNull VoxelShape getShape(BlockState arg, BlockGetter arg2, BlockPos arg3, CollisionContext arg4) {
        return switch ((arg.getValue(FACING)).getAxis()) {
            case X -> X_AXIS_AABB;
            case Z -> Z_AXIS_AABB;
            case Y -> Y_AXIS_AABB;
        };
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.MODEL;
    }

    @Override
    public Direction getPreferredFacing(BlockPlaceContext context) {
        Direction f = super.getPreferredFacing(context);
        if (f == null) return null;
        return f.getOpposite();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }


    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING)
                .getAxis();
    }

    @Override
    public Class<MinerBlockEntity> getBlockEntityClass() {
        return MinerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MinerBlockEntity> getBlockEntityType() {
        return AllBlockEntityTypes.MINER.get();
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        MinerBlockEntity be = getBlockEntity(level, pos);
        if (be == null) return InteractionResult.FAIL;

        // check if not hacking device in hand
        ItemStack stack = player.getItemInHand(hand);
        Item holdingItem = stack.getItem();
        if (holdingItem == AllItems.HACK_DEVICE.asItem()) return InteractionResult.PASS;
        if (holdingItem == com.simibubi.create.AllItems.WRENCH.asItem()) return InteractionResult.PASS;

        if (be.isOwner(player))
            be.refreshOwnerName(player);
        else
            be.refreshOwnerName();

        if (!level.isClientSide) return InteractionResult.SUCCESS;

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft.getInstance().setScreen(new MinerScreen(pos));
        });
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        MinerBlockEntity be = getBlockEntity(level, pos);
        if (be == null) return;
        if (placer instanceof Player) {
            be.readItemStack(stack);
            if (!be.hasOwner())
                be.setOwner((Player) placer);
        }
        be.setPricePerSuKt(MinerRegistry.getPricePerSuKt());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
//        drops.add(AllBlocks.MINER.asStack(1));

        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof MinerBlockEntity miner) {
            drops.forEach(miner::saveToItem);
        }
        return drops;
    }
}
