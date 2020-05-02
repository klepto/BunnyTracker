package dev.klepto.bunnytracker.discord;

import dev.klepto.bunnytracker.record.Record;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.Duration;
import java.util.function.Function;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
public class RecordEmbedBuilder implements Function<Record, MessageEmbed> {

    @Override
    public MessageEmbed apply(Record record) {
        val builder = new EmbedBuilder();
        val player = record.getPlayer();
        val map = record.getMap();

        val message = player.isNewRecord()
                ? "New #" + player.getRank() + " by " + player.getName() + "!"
                : "New personal best by " + player.getName() + "!";

        builder.setTitle(map.getName(), map.getRecordsUrl());
        builder.setAuthor(message, "http://github.com/klepto/BunnyTracker");
        builder.setDescription(formatPlayers(record));
        builder.setColor(Color.GREEN);
        builder.setThumbnail(map.getImageUrl());
        return builder.build();
    }

    private String formatPlayers(Record record) {
        val builder = new StringBuilder();

        for (val player : record.getPlayers()) {
            if (player.isNewRecord()) {
                builder.append("**");
            }
            builder.append(player.getRank());
            builder.append(". ");
            builder.append(player.getName());
            builder.append(" - ");
            builder.append(formatTime(player.getTime()));
            builder.append(" ");

            if (player.isNewRecord()) {
                builder.append("**");
            }

            if (player.getPreviousRank() != -1 && player.getRank() != player.getPreviousRank()) {
                val difference = player.getPreviousRank() - player.getRank();
                builder.append("(");
                if (difference > 0) builder.append("+");
                builder.append(difference);
                builder.append(") ");
            }

            builder.append("\n");
        }
        return builder.toString();
    }

    private String formatTime(int time) {
        val builder = new StringBuilder();
        val duration = Duration.ofMillis(time);
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
        return builder.toString();
    }

}
