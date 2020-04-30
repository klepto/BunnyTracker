package dev.klepto.bunnytracker;

import com.google.common.base.Preconditions;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
public class BunnyTracker {

    public static void main(String[] args) throws Exception {
        Preconditions.checkArgument(args.length > 0, "Please specify Discord API token as program argument.");

        val discordToken = args[0];
        new BunnyTracker(discordToken, "records").start();
    }

    private final String discordToken;
    private final String discordChannel;
    private final RecordsService service;
    private JDA discordClient;
    private Map<String, BTMap> previous = Collections.emptyMap();

    public BunnyTracker(String discordToken, String discordChannel) {
        this.discordToken = discordToken;
        this.discordChannel = discordChannel;
        this.service = new RecordsService(Duration.ofMinutes(5), this::compareMaps);
    }

    public void start() throws Exception {
        discordClient = JDABuilder.createDefault(discordToken).build();
        discordClient.awaitReady();
        service.start();
    }

    public void compareMaps(Map<String, BTMap> maps) {
        if (!previous.isEmpty()) {
            maps.values().forEach(this::compareMap);
        }

        previous = maps;
    }

    private void compareMap(BTMap map) {
        if (discordClient == null) {
            return;
        }

        val previousMap = previous.get(map.getName());
        if (previousMap == null) {
            return;
        }

        if (previousMap.equals(map)) {
            return;
        }

        comparePlayers(previousMap, map);
    }

    private void comparePlayers(BTMap previous, BTMap current) {
        current.getPlayers().values().forEach(player -> {
            val previousPlayer = previous.getPlayers().get(player.getName());
            var newPosition = previousPlayer == null || previousPlayer.getRank() > player.getRank();
            if (previousPlayer != null && player.getTime() >= previousPlayer.getTime()) {
                return;
            }

            announce(current, player, newPosition);
        });
    }

    private void announce(BTMap map, BTPlayer player, boolean newPosition) {
        val embed = createEmbed(map, player, newPosition);

        discordClient.getGuilds().stream()
                .flatMap(guild -> guild.getChannels().stream())
                .filter(channel -> channel.getType() == ChannelType.TEXT)
                .map(MessageChannel.class::cast)
                .filter(channel -> channel.getName().equals(discordChannel))
                .forEach(channel -> channel.sendMessage(embed).queue());
    }

    private MessageEmbed createEmbed(BTMap map, BTPlayer player, boolean newPosition) {
        val builder = new EmbedBuilder();
        val mapUrl = "https://ut4bt.ga/map/" + map.getName();
        val message = newPosition
                ? "New #" + player.getRank() + " by " + player.getName() + "!"
                : "New personal best by " + player.getName() + "!";
        builder.setTitle(map.getName(), mapUrl);
        builder.setAuthor(message, mapUrl);
        builder.setDescription(formatPlayers(map, player));
        builder.setColor(Color.GREEN);
        builder.setThumbnail(map.getIconUrl());
        return builder.build();
    }

    private String formatPlayers(BTMap map, BTPlayer highlightPlayer) {
        val builder = new StringBuilder();
        map.getPlayers().values().stream()
                .sorted(Comparator.comparingInt(BTPlayer::getRank))
                .forEach(player -> {
            if (player.equals(highlightPlayer)) {
                builder.append("**");
            }
            builder.append(player.getRank());
            builder.append(". ");
            builder.append(player.getName());
            builder.append(" - ");

            val duration = Duration.ofMillis(player.getTime());
            val minutes = duration.toMinutesPart();
            val seconds = duration.toSecondsPart();
            val millis = duration.toMillisPart();
            if (minutes > 0) {
                builder.append(duration.toMinutesPart());
                builder.append(":");
            }
            if (seconds < 10) {
                builder.append("0");
            }
            builder.append(seconds);
            builder.append(".");
            if (millis < 10) {
                builder.append("00");
            } else if (millis < 100) {
                builder.append("0");
            }
            builder.append(millis);
            if (player.equals(highlightPlayer)) {
                builder.append("**");
            }
            builder.append("\n");
        });
        return builder.toString();
    }

}
