package com.mlntcandy.netask.cntmoneyminer.network.packets;

import com.mlntcandy.netask.cntmoneyminer.blocks.miner.MinerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;


public class MinerInteractionC2SPacket {
    public MinerInteractionType type;

    public BlockPos pos;

    public MinerInteractionC2SPacket(MinerInteractionType type, BlockPos pos) {
        this.type = type;
        this.pos = pos;
    }

    public MinerInteractionC2SPacket(FriendlyByteBuf buf) {
        this.type = MinerInteractionType.decode(buf);
        this.pos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        this.type.encode(buf);
        buf.writeBlockPos(this.pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) return;
            BlockEntity be = player.level().getBlockEntity(this.pos);
            if (!(be instanceof MinerBlockEntity miner)) return;

            // check if player is in reasonable range
            if (player.distanceToSqr(this.pos.getX(), this.pos.getY(), this.pos.getZ()) > 16) {
                // send a sync
                miner.setChanged();
                return;
            }

            switch (this.type) {
                case CLAIM_REQUEST:
                    miner.claimManually();
                    break;
                case SYNC_REQUEST:
                    miner.syncExplicitly();
                    break;
                case AUTO_CLAIM_SET_ON:
                    miner.autoClaim = true;
                    miner.ticksUntilAutoClaim = MinerBlockEntity.AUTO_CLAIM_TIMEOUT;
                    miner.setChanged();
                    miner.syncExplicitly();
                    break;
                case AUTO_CLAIM_SET_OFF:
                    miner.autoClaim = false;
                    miner.setChanged();
                    miner.syncExplicitly();
                    break;
            }
        });
        return true;
    }
}
