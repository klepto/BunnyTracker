package dev.klepto.bunnytracker.record.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import dev.klepto.bunnytracker.record.Record;
import dev.klepto.bunnytracker.record.RecordsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@Slf4j
@RequiredArgsConstructor
public class ApiRecordsProvider implements RecordsProvider {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type COLLECTION_TYPE = new TypeToken<Map<String, ApiRecord[]>>() {
    }.getType();
    private static final Path RECORD_CACHE_PATH = Paths.get("records.json");

    private final RecordsJsonProvider jsonProvider;
    private Map<String, ApiRecord[]> currentRecords = Collections.emptyMap();

    public ApiRecordsProvider() {
        this(new ApiJsonProvider("https://ut4bt.tk/api/home"));
    }

    @Override
    public Set<Record> getNewRecords() {
        try {
            var json = jsonProvider.fetchRecordsJson();
            if (currentRecords.isEmpty()) {
                val cachedJson = readRecordCache();
                if (cachedJson != null) {
                    json = cachedJson;
                }
            }
            val apiRecords = GSON.<Map<String, ApiRecord[]>>fromJson(json, COLLECTION_TYPE);
            val records = parseNewRecords(apiRecords);
            val firstRun = currentRecords.isEmpty();

            this.currentRecords = apiRecords;
            saveRecordCache(json);

            if (firstRun) {
                return getNewRecords();
            }

            return records;
        } catch (Exception cause) {
            log.error("Error occurred during records request!", cause);
        }
        return Collections.emptySet();
    }

    private Set<Record> parseNewRecords(Map<String, ApiRecord[]> newRecords) {
        val records = new HashSet<Record>();
        if (!currentRecords.isEmpty()) {
            newRecords.forEach((key, value) -> {
                val currentRecords = this.currentRecords.getOrDefault(key, new ApiRecord[0]);
                records.addAll(parseMap(key, currentRecords, value));
            });
        }
        return records;
    }

    private Set<Record> parseMap(String mapName, ApiRecord[] currentRecords, ApiRecord[] newRecords) {
        if (Arrays.equals(currentRecords, newRecords)) {
            return Collections.emptySet();
        }

        val previousPlayers = IntStream.range(0, currentRecords.length).mapToObj(index -> {
            val player = currentRecords[index];
            return new Record.Player(player.getPlayerName(), index + 1, -1, player.getTime(), -1, false);
        }).collect(Collectors.toMap(Record.Player::getName, Function.identity()));

        val players = IntStream.range(0, newRecords.length).mapToObj(index -> {
            val player = newRecords[index];
            val previousPlayer = previousPlayers.get(player.getPlayerName());

            val name = player.getPlayerName();
            val rank = index + 1;
            val previousRank = previousPlayer != null ? previousPlayer.getRank() : -1;
            val time = player.getTime();
            val previousTime = previousPlayer != null ? previousPlayer.getTime() : -1;
            val newRecord = previousTime == -1 || time < previousTime;
            return new Record.Player(name, rank, previousRank, time, previousTime, newRecord);
        }).toArray(Record.Player[]::new);

        return Arrays.stream(players)
                .filter(Record.Player::isNewRecord)
                .map(player -> new Record(ApiMapUtils.getMap(mapName), player, players))
                .collect(Collectors.toSet());
    }

    public String readRecordCache() throws IOException {
        if (!Files.exists(RECORD_CACHE_PATH)) {
            return null;
        }

        return Files.readString(RECORD_CACHE_PATH);
    }

    public void saveRecordCache(String json) throws IOException {
        JsonElement element = GSON.fromJson(json, JsonElement.class);
        try (BufferedWriter writer = Files.newBufferedWriter(RECORD_CACHE_PATH, CREATE, TRUNCATE_EXISTING)) {
            GSON.toJson(element, writer);
        }
    }

}
