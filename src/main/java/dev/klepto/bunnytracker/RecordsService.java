package dev.klepto.bunnytracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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
    private final Consumer<Set<BTMap>> listener;
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
            String response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
            Type parseType = new TypeToken<Map<String, BTPlayer[]>>(){}.getType();
            Map<String, BTPlayer[]> recordMap = gson.fromJson(response, parseType);
            Set<BTMap> records = recordMap.entrySet().stream()
                    .map(entry -> new BTMap(entry.getKey(), entry.getValue())).collect(Collectors.toSet());
            listener.accept(records);
        } catch (Exception cause) {
            log.error("Error occurred during records request!", cause);
        }
    }


}
