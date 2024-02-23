package com.mlntcandy.netask.cntmoneyminer.network.packets;

import net.minecraft.network.FriendlyByteBuf;

public enum MinerInteractionType {
    CLAIM_REQUEST,
    SYNC_REQUEST,
    AUTO_CLAIM_SET_ON,
    AUTO_CLAIM_SET_OFF,
    ;
    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(this.ordinal());
    }
    public static MinerInteractionType decode(FriendlyByteBuf buf) {
        return values()[buf.readByte()];
    }
}
