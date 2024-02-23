package com.mlntcandy.netask.cntmoneyminer.api;

import com.mlntcandy.netask.cntmoneyminer.blocks.miner.MinerBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class MinerRegistry {
    public static Map<UUID, MinerBlockEntity> miners = new HashMap<>();

    private static float pricePerSuKt = -1f;

    public static void setPricePerSuKt(float price) {
        initOnServer();
        pricePerSuKt = price;
        each(miner -> {
            miner.setPricePerSuKt(pricePerSuKt);
            return null;
        });
    }

    public static float getPricePerSuKt() {
        initOnServer();
        return pricePerSuKt;
    }

//    static {
//        initOnServer();
//    }
    
    public static @NotNull UUID register(MinerBlockEntity miner) {
        initOnServer();
        UUID uuid = UUID.randomUUID();
        miners.put(uuid, miner);
        return uuid;
    }

    public static void unregister(UUID registryId) {
        initOnServer();
        miners.remove(registryId);
    }

    public static void each(Function<MinerBlockEntity, Void> callback) {
        initOnServer();
        miners.forEach((uuid, miner) -> {
            callback.apply(miner);
        });
    }

    public static void initOnServer() {
        if (Client.isInit) return;
        Client.init();
    }
}
