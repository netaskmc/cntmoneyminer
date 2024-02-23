package com.mlntcandy.netask.cntmoneyminer.api;

import com.google.gson.JsonObject;
import com.mlntcandy.netask.ntservapiclient.APIClient;
import com.mlntcandy.netask.ntservapiclient.APIResponseExecutor;
import com.mlntcandy.netask.ntservapiclient.APIResponseSingular;
import com.mlntcandy.netask.ntservapiclient.IResponseExecutor;
import net.minecraft.server.MinecraftServer;

import java.util.Objects;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Client {
    static Timer timer = new Timer();
    static Boolean isInit = false;

    static class MoneyMinerResponseExecutor implements IResponseExecutor {
        public void execute(APIResponseSingular response, MinecraftServer server) {
            if (Objects.equals(response.action, "minerPrices")) {
                handlePrices(response.payload);
            }
        }

        private void handlePrices(String payload) {
            float price = Float.parseFloat(payload) / 1000f;
            MinerRegistry.setPricePerSuKt(price);
        }
    }

    static void attach() {
        MoneyMinerResponseExecutor executor = new MoneyMinerResponseExecutor();
        APIResponseExecutor.attach("minerPrices", executor);
    }

    public static CompletableFuture<Void> init() {
        isInit = true;
        attach();
        CompletableFuture<Boolean> gotRate = getRate();
        subscribe();
        return gotRate.thenAccept((b) -> {
            // do nothing
        });
    }

    public static CompletableFuture<Boolean> mintMoney(UUID player, float suPerKt) {
        JsonObject payload = new JsonObject();
        payload.addProperty("player", player.toString());
        payload.addProperty("unit", "energy:1k_su_kt");
        payload.addProperty("amount", suPerKt / 1000f);
        return APIClient.requestThenExecute("serverApi/miner/mint", payload);
    }

    public static CompletableFuture<Boolean> getRate() {
        JsonObject payload = new JsonObject();
        payload.addProperty("unit", "energy:1k_su_kt");
        return APIClient.requestThenExecute("serverApi/miner/rate", payload);
    }

    public static void subscribe() {
        // every 5 minutes send getRate request
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                getRate();
            }
        }, 0, 5 * 60 * 1000);
    }

    public static void unsubscribe() {
        timer.cancel();
    }
}
