package com.mlntcandy.netask.cntmoneyminer.items.hackDevice;

import com.mlntcandy.netask.cntmoneyminer.CNTMoneyMiner;
import com.mlntcandy.netask.cntmoneyminer.blocks.miner.MinerBlockEntity;
import com.mlntcandy.netask.cntmoneyminer.utils.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HackDeviceItem extends Item {
    public static final int HACK_DURATION = 20 * 20;
    public static final int MAX_USES = 5;

    public HackDeviceItem(Properties properties) {
        super(properties);
    }
    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int i) {
        if (!(livingEntity instanceof Player)) {
            return;
        }
        if (isHacking(stack)) {
            MinerBlockEntity be = hackingWhat(stack, level);
            if (be != null) {
                incrementProgress(stack);
                if (!((Player) livingEntity).isCreative()) {
                    stack.hurtAndBreak(1, (Player) livingEntity, (player) -> {
                        player.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                    });
                }
                BlockPos pos = be.getBlockPos();
                BlockState state = level.getBlockState(pos);
                // draw a line from the miner to the player with particles
                Vec3 playerPos = livingEntity.position();
                Vec3 minerPos = new Vec3(pos.getX(), pos.getY(), pos.getZ());

                drawLine(
                        playerPos.add(0, 1, 0),
                        minerPos.add(0.5, 0.5, 0.5),
                        level,
                        getProgress(stack) / (float) HACK_DURATION
                );
                if (getProgress(stack) >= HACK_DURATION) {
                    be.setThief((Player) livingEntity);
                    // stop hacking
                    setNotHacking(stack);
                    breakIfNotEnough(stack, (Player) livingEntity);
                }
            }
        }
        super.onUseTick(level, livingEntity, stack, i);
    }

    private void drawLine(Vec3 from, Vec3 to, Level level, float frequency) {
        // randomize the particle frequency using the frequency parameter
        Vec3 direction = to.subtract(from);
        Vec3 step = direction.normalize().scale(0.1);
        Vec3 current = from;
        while (current.distanceTo(to) > 0.1) {
            if (level.random.nextFloat() < frequency) {
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, current.x, current.y, current.z, 0, 0, 0);
            }
            current = current.add(step);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        Level level = ctx.getLevel();
        BlockEntity be = level.getBlockEntity(ctx.getClickedPos());
        Player player = ctx.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (breakIfNotEnough(stack, player)) {
            return InteractionResult.FAIL;
        }
        if (be != null) {
            if (be instanceof MinerBlockEntity) {
                setHacking(stack, (MinerBlockEntity) be);
                return ItemUtils.startUsingInstantly(level, player, ctx.getHand()).getResult();
            }
        }
        CNTMoneyMiner.LOGGER.info("useOn");
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (breakIfNotEnough(stack, player)) {
            return InteractionResultHolder.fail(stack);
        }
        if (isHacking(stack)) {
            setNotHacking(stack);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    public boolean breakIfNotEnough(ItemStack stack, Player player) {
        // check durability, if less than HACK_DURATION, break
        if (stack.getDamageValue() >= stack.getMaxDamage() - HACK_DURATION) {
            stack.hurtAndBreak(HACK_DURATION, player, (p) -> {
                p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        setNotHacking(stack);
        return super.useOnRelease(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return HACK_DURATION + 1;
    }

    public static boolean isHacking(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("Hacking");
    }

    public static void setNotHacking(ItemStack stack) {
        stack.getOrCreateTag().putBoolean("Hacking", false);
    }

    public static void setHacking(ItemStack stack, MinerBlockEntity be) {
        stack.getOrCreateTag().putBoolean("Hacking", true);
        stack.getOrCreateTag().putInt("Progress", 0);
        BlockPos pos = be.getBlockPos();
        stack.getOrCreateTag().putInt("HackingX", pos.getX());
        stack.getOrCreateTag().putInt("HackingY", pos.getY());
        stack.getOrCreateTag().putInt("HackingZ", pos.getZ());
    }

    public static void incrementProgress(ItemStack stack) {
        stack.getOrCreateTag().putInt("Progress", stack.getOrCreateTag().getInt("Progress") + 1);
    }

    public static int getProgress(ItemStack stack) {
        return stack.getOrCreateTag().getInt("Progress");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        int durability = stack.getMaxDamage() - stack.getDamageValue();
        int uses = durability / HACK_DURATION;
        components.add(
                Lang.translate("item.hack_device.uses_tooltip", uses)
                .style(ChatFormatting.GRAY)
                .component()
        );
        super.appendHoverText(stack, level, components, tooltipFlag);
    }

    public static MinerBlockEntity hackingWhat(ItemStack stack, Level level) {
        if (isHacking(stack)) {
            int x = stack.getOrCreateTag().getInt("HackingX");
            int y = stack.getOrCreateTag().getInt("HackingY");
            int z = stack.getOrCreateTag().getInt("HackingZ");
            BlockEntity be = level.getBlockEntity(new BlockPos(x, y, z));
            if (be instanceof MinerBlockEntity) {
                return (MinerBlockEntity) be;
            }
        }
        return null;
    }
}