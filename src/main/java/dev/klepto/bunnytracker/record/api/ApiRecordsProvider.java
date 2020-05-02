package dev.klepto.bunnytracker.record.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.klepto.bunnytracker.record.Record;
import dev.klepto.bunnytracker.record.RecordsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@Slf4j
@RequiredArgsConstructor
public class ApiRecordsProvider implements RecordsProvider {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Type COLLECTION_TYPE = new TypeToken<Map<String, ApiRecord[]>>(){}.getType();

    private final RecordsJsonProvider jsonProvider;
    private Map<String, ApiRecord[]> currentRecords = Collections.emptyMap();

    public ApiRecordsProvider() {
        this(new ApiJsonProvider("https://ut4bt.ga/api/home"));
    }

    @Override
    public Set<Record> getNewRecords() {
        try {
            val json = jsonProvider.fetchRecordsJson();
            val apiRecords = GSON.<Map<String, ApiRecord[]>>fromJson(json, COLLECTION_TYPE);
            return parseNewRecords(apiRecords);
        } catch (Exception cause) {
            log.error("Error occurred during records request!", cause);
        }
        return Collections.emptySet();
    }

    private Set<Record> parseNewRecords(Map<String, ApiRecord[]> newRecords) {
        val records = new HashSet<Record>();
        currentRecords.keySet().stream().filter(newRecords::containsKey).forEach(key -> {
            records.addAll(parseMap(key, currentRecords.get(key), newRecords.get(key)));
        });
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

}
