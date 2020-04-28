package dev.klepto.bunnytracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@Slf4j
@RequiredArgsConstructor
public class RecordsService {

    private static final Gson gson = new GsonBuilder().create();
    private static final HttpClient httpClient = HttpClient.newBuilder().build();
    private static final HttpRequest httpRequest =  HttpRequest.newBuilder()
            .uri(URI.create("https://ut4bt.ga/api/home"))
            .timeout(Duration.ofMinutes(1)).build();

    private final Duration interval;
    private final Consumer<Map<String, BTMap>> listener;
    private ScheduledExecutorService executor;


    public RecordsService start() {
        if (executor != null) {
            executor.shutdownNow();
        }

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::fetchRecords, 0, interval.toMillis(), TimeUnit.MILLISECONDS);
        return this;
    }

    private void fetchRecords() {
        try {
            //val response = new String(Files.readAllBytes(Paths.get("home.json")));
            val response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
            val parseType = new TypeToken<Map<String, Player[]>>(){}.getType();

            Map<String, Player[]> records = gson.fromJson(response, parseType);
            Map<String, BTMap> maps = records.keySet().stream().map(mapName -> {
               val players = records.get(mapName);
               val btPlayers = new HashMap<String, BTPlayer>();
               for (int i = 0; i < players.length; i++) {
                   val player = players[i];
                   btPlayers.put(player.getName(), new BTPlayer(i + 1, player.getName(), player.getTime()));
               }
               return new BTMap(mapName, btPlayers);
            }).collect(Collectors.toMap(BTMap::getName, Function.identity()));

            listener.accept(maps);
        } catch (Exception cause) {
            log.error("Error occurred during records request!", cause);
        }
    }

    @Value
    private static class Player {
        @SerializedName("PlayerName")
        String name;

        @SerializedName("BestTime")
        int time;
    }


}
