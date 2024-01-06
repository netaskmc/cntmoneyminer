package com.mlntcandy.netask.cntmoneyminer.api;

import com.mlntcandy.netask.cntmoneyminer.blocks.miner.MinerBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class MinerRegistry {
    public static Map<UUID, MinerBlockEntity> miners = new HashMap<>();

    public static @NotNull UUID register(MinerBlockEntity miner) {
        UUID uuid = UUID.randomUUID();
        miners.put(uuid, miner);
        return uuid;
    }

    public static void unregister(UUID registryId) {
        miners.remove(registryId);
    }

    public static void each(Function<MinerBlockEntity, Void> callback) {
        miners.forEach((uuid, miner) -> {
            callback.apply(miner);
        });
    }
}
