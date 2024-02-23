package com.mlntcandy.netask.cntmoneyminer.network;

import com.mlntcandy.netask.cntmoneyminer.CNTMoneyMiner;
import com.mlntcandy.netask.cntmoneyminer.network.packets.MinerInteractionC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPackets {
    private static SimpleChannel INSTANCE;
    private static int id = 0;

    public static final String NETWORK_VERSION = "1";

    private static int nextID() {
        return id++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(CNTMoneyMiner.MODID, "main_channel"))
                .networkProtocolVersion(() -> NETWORK_VERSION)
                .clientAcceptedVersions(NETWORK_VERSION::equals)
                .serverAcceptedVersions(NETWORK_VERSION::equals)
                .simpleChannel();


        INSTANCE.messageBuilder(MinerInteractionC2SPacket.class, nextID(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(MinerInteractionC2SPacket::new)
                .encoder(MinerInteractionC2SPacket::encode)
                .consumerMainThread(MinerInteractionC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAll(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToAllAround(MSG message, PacketDistributor.TargetPoint point) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> point), message);
    }
}
