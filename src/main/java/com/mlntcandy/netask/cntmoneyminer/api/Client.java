package com.mlntcandy.netask.cntmoneyminer.api;

import com.google.gson.JsonObject;
import com.mlntcandy.netask.ntservapiclient.APIClient;
import com.mlntcandy.netask.ntservapiclient.APIResponseExecutor;
import com.mlntcandy.netask.ntservapiclient.APIResponseSingular;
import com.mlntcandy.netask.ntservapiclient.IResponseExecutor;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Client {
    static class MoneyMinerResponseExecutor implements IResponseExecutor {
        public void execute(APIResponseSingular response, MinecraftServer server) {
            if (Objects.equals(response.action, "minerPrices")) {
                handlePrices(response.payload);
            }
        }

        private void handlePrices(String payload) {

        }
    }

    static {
        APIResponseExecutor.attach("minerPrices", new MoneyMinerResponseExecutor());
    }

    public static CompletableFuture<Boolean> mintMoney(UUID player, int amount) {
        JsonObject payload = new JsonObject();
        payload.addProperty("player", player.toString());
        payload.addProperty("amount", amount);
        return APIClient.requestThenExecute("serverApi/miner/mint", payload);
    }

    public static CompletableFuture<Boolean> getRate() {
        return APIClient.requestThenExecute("serverApi/miner/rate", new JsonObject());
    }
}
