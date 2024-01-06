package com.mlntcandy.netask.cntmoneyminer.blocks.miner;

import com.mlntcandy.netask.cntmoneyminer.utils.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class MinerBlockItem extends BlockItem {
    public MinerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        CompoundTag tag = itemStack.getTag();

        if (tag == null) {
            super.appendHoverText(itemStack, level, components, tooltipFlag);
            return;
        }

        @Nullable Integer suPerRPM = null;
        @Nullable Float suPerKt = null;
        String ownerName = null;
        UUID ownerUUID = null;

        if (tag.get("miner.suPerRPM") != null)
            suPerRPM = tag.getInt("miner.suPerRPM");
        if (tag.get("miner.suPerKt") != null)
            suPerKt = tag.getFloat("miner.suPerKt");
        if (tag.get("miner.ownerName") != null)
            ownerName = tag.getString("miner.ownerName");
        if (tag.get("miner.ownerUUID") != null)
            ownerUUID = tag.getUUID("miner.ownerUUID");

        if (suPerRPM != null)
            components.add(Lang.Create.number(suPerRPM)
                    .translate("generic.unit.stress")
                    .text("/")
                    .translate("generic.unit.rpm")
                    .style(ChatFormatting.DARK_GRAY)
                    .component()
            );
        if (suPerKt != null)
            components.add(
                    Lang.translate("gui.miner.mined")
                            .style(ChatFormatting.GRAY)
                            .add(Lang.Create.number(suPerKt)
                                    .translate("generic.unit.stress")
                                    .text("/")
                                    .translate("generic.unit.kt")
                                    .style(ChatFormatting.GREEN)
                            ).component()
            );
        if (ownerName != null && ownerUUID != null)
            components.add(Lang.translate("gui.miner.owner")
                    .style(ChatFormatting.GRAY)
                    .add(Lang.text(ownerName).style(ChatFormatting.WHITE)).component()
            );

        super.appendHoverText(itemStack, level, components, tooltipFlag);
    }
}
