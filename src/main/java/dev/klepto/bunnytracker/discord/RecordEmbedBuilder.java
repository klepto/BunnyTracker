package dev.klepto.bunnytracker.discord;

import dev.klepto.bunnytracker.record.Record;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.Duration;
import java.util.function.Function;

import static java.lang.String.format;

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
        // Chatouille voids Pog
        val minutes = time / 60000;
        val seconds = (time / 1000) % 60;
        val millis = time % 1000;
        return minutes > 0
                ? format("%d:%02d.%03d", minutes, seconds, millis)
                : format("%02d.%03d", seconds, millis);
    }

}
