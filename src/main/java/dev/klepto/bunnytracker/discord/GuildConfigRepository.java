package dev.klepto.bunnytracker.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
public class GuildConfigRepository {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type COLLECTION_TYPE = new TypeToken<Map<Long, GuildConfig>>(){}.getType();
    private static final Path DEFAULT_PATH = Paths.get("guilds.json");

    private final Path configPath;
    private final Map<Long, GuildConfig> configs;

    public GuildConfigRepository() throws IOException {
        this(DEFAULT_PATH);
    }

    public GuildConfigRepository(Path configPath) throws IOException {
        this.configPath = configPath;
        this.configs = deserialize();
    }

    public GuildConfig getById(long guildId) {
        configs.computeIfAbsent(guildId, key -> new GuildConfig());
        return configs.get(guildId);
    }

    public Map<Long, GuildConfig> getAll() {
        return new HashMap<>(configs);
    }

    private Map<Long, GuildConfig> deserialize() throws IOException {
        if (!Files.exists(configPath)) {
            return new HashMap<>();
        }

        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            return GSON.fromJson(reader, COLLECTION_TYPE);
        }
    }

    private void serialize() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(configs, writer);
        }
    }

    @SneakyThrows
    public void save() {
        serialize();
    }

}
