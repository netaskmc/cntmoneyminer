package com.mlntcandy.netask.cntmoneyminer.blocks.miner;

import com.mlntcandy.netask.cntmoneyminer.api.Client;
import com.mlntcandy.netask.cntmoneyminer.blocks.autoMiner.AutoMinerBlock;
import com.mlntcandy.netask.cntmoneyminer.utils.Lang;
import com.mlntcandy.netask.cntmoneyminer.api.MinerRegistry;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MinerBlockEntity extends KineticBlockEntity {
//    public static final int MIN_SU_RPM = 1;
//    public static final int MAX_SU_RPM = 128;

    public static final String OBF_OWNER_NAME = "!obfuscated";

    public static final int AUTO_CLAIM_TIMEOUT = 20 * 60 * 30; // 30 minutes
    public static final int CLAIM_TIMEOUT = 20 * 60 * 5; // 5 minutes

    public UUID registryId = null;

    // is this a miner that can be claimed automatically?
    public boolean isAutoClaimable = false;

    // is it set to claim automatically?
    public boolean autoClaim = false;

//    public int suPerRPM = 1;
    public int ticksUntilClaim = CLAIM_TIMEOUT;
    public int ticksUntilAutoClaim = AUTO_CLAIM_TIMEOUT;
    // energy put in, in SU/Kt (stress unit per kilotick - 50 seconds)
    public float suPerKt = 0;
    public String ownerName = null;
    public UUID ownerUUID = null;
    public float pricePerSuKt = -1f;

    public void setPricePerSuKt(Float price) {
        if (price == null) price = -1f;
        pricePerSuKt = price;
        setChanged();
        syncExplicitly();
    }





//    public void setSuPerRPM(int suPerRPM) {
//        this.suPerRPM = suPerRPM;
//        setChanged();
//        onStressChange();
//         validateKinetics();
//    }
    public boolean hasOwner() {
        boolean has = ownerUUID != null && ownerName != null;
        if (!has) {
            ownerName = null;
            ownerUUID = null;
        }
        return has;
    }
    public LangBuilder getDisplayOwner() {
        return !hasOwner()
                ? Lang.translate("gui.miner.no_owner")
                : Objects.equals(ownerName, OBF_OWNER_NAME)
                    ? Lang.text("Owner").style(ChatFormatting.OBFUSCATED)
                    : Lang.text(ownerName);
    }

    public void setOwner(Player player) {
        if (player == null) {
            ownerName = null;
            ownerUUID = null;
            return;
        }
        ownerName = player.getName().getString();
        ownerUUID = player.getUUID();
    }

    public void setThief(Player player) {
        if (player == null) {
            return;
        }
        ownerName = OBF_OWNER_NAME;
        ownerUUID = player.getUUID();
    }

    public void refreshOwnerName(Player owner) {
        if (Objects.equals(ownerName, OBF_OWNER_NAME)) return;

        if (owner == null) return;
        if (!ownerUUID.equals(owner.getUUID())) throw new IllegalArgumentException("Player passed is not the owner");
        ownerName = owner.getName().getString();
    }
    public void refreshOwnerName() {
        if (ownerUUID == null) {
            ownerName = null;
            return;
        }
        if (level == null) {
            return;
        }
        Player player = level.getPlayerByUUID(ownerUUID);
        refreshOwnerName(player);
    }

    public boolean isOwner(Player player) {
        return player != null && player.getUUID().equals(ownerUUID);
    }

    public float getPower() {
        return Math.abs(calculateStressApplied() * getSpeed());
    }

    public float getMined() {
        return suPerKt;
    }

    public LangBuilder getDisplaySecondsUntilClaim() {
        int seconds = ticksUntilClaim / 20;
        return Lang.number(seconds)
                .translate("gui.miner.sec");
    }

    public LangBuilder getDisplaySecondsUntilAutoClaim() {
        int seconds = ticksUntilAutoClaim / 20;
        return Lang.number(seconds)
                .translate("gui.miner.sec");
    }

    public LangBuilder getDisplayMined() {
        float mined = getMined();
        return Lang.builder().add(
                Lang.Create.number(mined)
                .translate("generic.unit.stress")
        ).text("/kt");
    }

    public LangBuilder getDisplayPower() {
        float power = getPower();
        return Lang.builder().add(
                Lang.Create.number(power)
                .translate("generic.unit.stress")
        );
    }

    public float getMinedInMoney() {
        if (pricePerSuKt < 0f) return -1f;
        return getMined() * pricePerSuKt / 100;
    }

    public LangBuilder getDisplayMinedInMoney() {
        float mined = getMinedInMoney();
        if (mined < 0f) return Lang.translate("gui.miner.no_price");
        return Lang.text("$").add(
                Lang.number(mined)
        );
    }


//
//    public LangBuilder getDisplaySuPerRPM() {
//        return Lang.Create.number(suPerRPM)
//                .translate("generic.unit.stress")
//                .text("/")
//                .translate("generic.unit.rpm");
//    }

    public MinerBlockEntity(BlockEntityType<MinerBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        Block block = state.getBlock();
        if (block instanceof AutoMinerBlock) {
            isAutoClaimable = true;
        }
        if (level == null) return;
        if (!level.isClientSide) {
            setPricePerSuKt(MinerRegistry.getPricePerSuKt());
        }
    }

    @Override
    public void tick() {
        float madeThisTick = getPower() / 1000f;
        suPerKt += madeThisTick;
        if (isAutoClaimable) {
            if (ticksUntilAutoClaim > 0) ticksUntilAutoClaim--;
            if (autoClaim && ticksUntilAutoClaim < 1) claimAutomatically();
        }
        if (ticksUntilClaim > 0) ticksUntilClaim--;
        super.tick();
        setChanged();
    }

    private void claim() {
        if (level == null) return;
        if (level.isClientSide) return;

        float mined = getMined();

        suPerKt = 0;
        ticksUntilClaim = CLAIM_TIMEOUT;
        ticksUntilAutoClaim = AUTO_CLAIM_TIMEOUT;
        setChanged();
        syncExplicitly();

        Client.mintMoney(ownerUUID, mined)
                .thenAccept(success -> {
                    if (!success) {
                        suPerKt = mined;
                        ticksUntilClaim = 0;
                        ticksUntilAutoClaim = 0;
                        setChanged();
                        syncExplicitly();
                    }
                });
    }

    public void claimManually() {
        if (ticksUntilClaim > 0) return;
        claim();
    }

    private void claimAutomatically() {
        if (!isAutoClaimable) return;
        if (ticksUntilAutoClaim > 0) return;
        claim();
    }

    public void syncExplicitly() {
        if (level == null) return;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public boolean canClaimManually() {
        return ticksUntilClaim <= 0;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Lang.translate("gui.miner.goggle_title")
                .forGoggles(tooltip);
        Lang.translate("gui.miner.owner")
                .style(ChatFormatting.GRAY)
                .add(getDisplayOwner().style(ChatFormatting.WHITE))
                .forGoggles(tooltip);

        Lang.translate("gui.miner.mined")
                .style(ChatFormatting.GRAY)
                .add(getDisplayMined().style(ChatFormatting.GREEN))
                .forGoggles(tooltip);

        Lang.text("- ").add(getDisplayMinedInMoney())
                .style(ChatFormatting.YELLOW)
                .forGoggles(tooltip);

        if (canClaimManually()) {
            Lang.translate("gui.miner.can_claim")
                    .style(ChatFormatting.DARK_GREEN)
                    .forGoggles(tooltip);
        } else {
            Lang.translate("gui.miner.can_claim_in")
                    .style(ChatFormatting.GRAY)
                    .space()
                    .add(getDisplaySecondsUntilClaim())
                    .forGoggles(tooltip);
        }

        if (isAutoClaimable) {
            if (autoClaim) {
                Lang.translate("gui.miner.auto_claim_in")
                        .style(ChatFormatting.GOLD)
                        .space()
                        .add(getDisplaySecondsUntilAutoClaim())
                        .forGoggles(tooltip);
            } else {
                Lang.translate("gui.miner.auto_claim_off_verbose")
                        .style(ChatFormatting.GRAY)
                        .forGoggles(tooltip);
            }
        }

        addStressImpactStats(tooltip, calculateStressApplied());

//        Lang.text("(").add(getDisplaySuPerRPM())
//                .text(")")
//                .style(ChatFormatting.GRAY)
//                .forGoggles(tooltip);
        return true;
    }

    @Override
    public void initialize() {
        super.initialize();

        if (level == null) return;
        if (level.isClientSide()) return;
        registryId = MinerRegistry.register(this);
    }

    @Override
    public void destroy() {
        if (level != null && !level.isClientSide() && registryId != null) {
            MinerRegistry.unregister(registryId);
            registryId = null;
        }

        super.destroy();
    }

    private void writeTags(CompoundTag tag) {
        tag.putBoolean("miner.autoClaim", autoClaim);

        tag.putInt("miner.ticksUntilClaim", ticksUntilClaim);
        tag.putInt("miner.ticksUntilAutoClaim", ticksUntilAutoClaim);

        tag.putFloat("miner.price", pricePerSuKt);
        tag.putFloat("miner.suPerKt", suPerKt);
        if (ownerName != null)
            tag.putString("miner.ownerName", ownerName);
        if (ownerUUID != null)
            tag.putUUID("miner.ownerUUID", ownerUUID);
    }
    private void readTags(CompoundTag tag) {
        if (tag.get("miner.ticksUntilClaim") != null)
            ticksUntilClaim = tag.getInt("miner.ticksUntilClaim");
        if (tag.get("miner.ticksUntilAutoClaim") != null)
            ticksUntilAutoClaim = tag.getInt("miner.ticksUntilAutoClaim");


        if (tag.get("miner.price") != null)
            pricePerSuKt = tag.getFloat("miner.price");
        if (tag.get("miner.suPerKt") != null)
            suPerKt = tag.getFloat("miner.suPerKt");
        if (tag.get("miner.ownerName") != null)
            ownerName = tag.getString("miner.ownerName");
        if (tag.get("miner.ownerUUID") != null)
            ownerUUID = tag.getUUID("miner.ownerUUID");
    }
    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        writeTags(tag);

        super.write(tag, clientPacket);
    }

    @Override
    public void writeSafe(CompoundTag tag) {
        writeTags(tag);

        super.writeSafe(tag);
    }

    @Override
    public void saveToItem(@NotNull ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        writeTags(tag);

        itemStack.setTag(tag);

        super.saveToItem(itemStack);
    }

    public void readItemStack(@NotNull ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        if (tag == null) return;
        readTags(tag);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        readTags(tag);

        super.read(tag, clientPacket);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        writeTags(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag) {
        super.handleUpdateTag(tag);
        readTags(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
