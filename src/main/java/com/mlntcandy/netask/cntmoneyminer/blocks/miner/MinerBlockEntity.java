package com.mlntcandy.netask.cntmoneyminer.blocks.miner;

import com.mlntcandy.netask.cntmoneyminer.utils.Lang;
import com.mlntcandy.netask.cntmoneyminer.api.MinerRegistry;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MinerBlockEntity extends KineticBlockEntity {
    public static final int MIN_SU_RPM = 1;
    public static final int MAX_SU_RPM = 128;

    public static final String OBF_OWNER_NAME = "!obfuscated";

    public UUID registryId = null;

    public int suPerRPM = 1;
    // energy put in, in SU/Kt (stress unit per kilotick - 50 seconds)
    public float suPerKt = 0;
    public String ownerName = null;
    public UUID ownerUUID = null;

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
        return Math.abs(suPerRPM * getSpeed());
    }

    public float getMined() {
        return suPerKt;
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

    public LangBuilder getDisplaySuPerRPM() {
        return Lang.Create.number(suPerRPM)
                .translate("generic.unit.stress")
                .text("/")
                .translate("generic.unit.rpm");
    }

    public MinerBlockEntity(BlockEntityType<MinerBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        suPerKt += getPower() / 1000f;
        super.tick();
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

        addStressImpactStats(tooltip, calculateStressApplied());

        Lang.text("(").add(getDisplaySuPerRPM())
                .text(")")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
        return true;
    }

    @Override
    public void initialize() {
        super.initialize();

        registryId = MinerRegistry.register(this);
    }

    @Override
    public void destroy() {
        MinerRegistry.unregister(registryId);
        registryId = null;

        super.destroy();
    }

    private void writeTags(CompoundTag tag) {
        tag.putInt("miner.suPerRPM", suPerRPM);
        tag.putFloat("miner.suPerKt", suPerKt);
        tag.putString("miner.ownerName", ownerName);
        tag.putUUID("miner.ownerUUID", ownerUUID);
    }
    private void readTags(CompoundTag tag) {
        if (tag.get("miner.suPerRPM") != null)
            suPerRPM = tag.getInt("miner.suPerRPM");
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
    public float calculateStressApplied() {
        float sup = super.calculateStressApplied();
        float impact = suPerRPM;
        this.lastStressApplied = impact;
        return impact;
    }
}
