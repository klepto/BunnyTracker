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
import java.util.Set;

import static java.util.function.Predicate.not;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
public class BunnyTracker {

    public static void main(String[] args) throws Exception {
        Preconditions.checkArgument(args.length > 0, "Please specify Discord API token as program argument.");

        val discordToken = args[0];
        new BunnyTracker(discordToken, "general").start();
    }

    private final String discordToken;
    private final String discordChannel;
    private final RecordsService service;
    private JDA discordClient;
    private Set<BTMap> previous = Collections.emptySet();

    public BunnyTracker(String discordToken, String discordChannel) {
        this.discordToken = discordToken;
        this.discordChannel = discordChannel;
        this.service = new RecordsService(Duration.ofMinutes(1), this::compareMap);
    }

    public void start() throws Exception {
        discordClient = JDABuilder.createDefault(discordToken).build();
        discordClient.awaitReady();
        service.start();
    }

    public void compareMap(Set<BTMap> maps) {
        if (discordClient == null) {
            return;
        }

        if (!previous.isEmpty()) {
            maps.stream()
                    .filter(not(previous::contains))
                    .forEach(this::compareMap);
        }

        previous = maps;
    }

    private void compareMap(BTMap current) {
        previous.stream()
                .filter(previous -> previous.getName().equals(current.getName()))
                .findFirst()
                .ifPresent(previous -> comparePlayers(previous, current));
    }

    private void comparePlayers(BTMap previous, BTMap current) {
        for (int i = 0; i < current.getPlayers().length; i++) {
            val newRecord = i >= previous.getPlayers().length
                    || !current.getPlayers()[i].equals(previous.getPlayers()[i]);
            if (newRecord) {
                announce(current, current.getPlayers()[i]);
            }
        }
    }

    private void announce(BTMap map, BTPlayer player) {
        val embed = createEmbed(map, player);

        discordClient.getGuilds().stream()
                .flatMap(guild -> guild.getChannels().stream())
                .filter(channel -> channel.getType() == ChannelType.TEXT)
                .map(MessageChannel.class::cast)
                .filter(channel -> channel.getName().equals(discordChannel))
                .forEach(channel -> channel.sendMessage(embed).queue());
    }

    private MessageEmbed createEmbed(BTMap map, BTPlayer player) {
        val builder = new EmbedBuilder();
        val mapUrl = "https://ut4bt.ga/map/" + map.getName();
        builder.setTitle(map.getName(), mapUrl);
        builder.setAuthor("New personal best by " + player.getName() + "!", mapUrl);
        builder.setDescription(formatPlayers(map, player));
        builder.setColor(Color.GREEN);
        builder.setThumbnail(map.getIconUrl());
        return builder.build();
    }

    private String formatPlayers(BTMap map, BTPlayer highlightPlayer) {
        val builder = new StringBuilder();
        for (int i = 0; i < map.getPlayers().length; i++) {
            val player = map.getPlayers()[i];
            if (player.equals(highlightPlayer)) {
                builder.append("**");
            }
            builder.append(i + 1);
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
        }
        return builder.toString();
    }

}
