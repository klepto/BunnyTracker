package dev.klepto.bunnytracker.discord;

import dev.klepto.bunnytracker.record.Record;
import dev.klepto.bunnytracker.record.api.ApiMapUtils;
import dev.klepto.commands.annotation.Command;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.entities.Message;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@RequiredArgsConstructor
public class DiscordCommands {

    private final DiscordBot bot;

    @Command
    public void setChannel(Message message) {
        val config = bot.getConfigRepository().getById(message.getGuild().getIdLong());
        config.setChannelId(message.getTextChannel().getIdLong());
        bot.getConfigRepository().save();

        val reply = message.getAuthor().getAsMention() + " Default records channel changed to: "
                + message.getTextChannel().getAsMention();
        message.getTextChannel().sendMessage(reply).queue();
    }

    @Command
    public void ping(Message message) {
        message.getTextChannel().sendMessage("pong").queue();
    }

    @Command
    public void testRecord(Message message) {
        val players = new Record.Player[] {
                new Record.Player("Chatouille", 1, 2, 123456, 125456, true),
                new Record.Player("kleps", 2, 1, 123557, 123557, false),
                new Record.Player("Ransom", 3, 3, 234567, 234567, false)
        };
        val record = new Record(ApiMapUtils.getMap("BT-RansomKek"), players[0], players);
        bot.announceRecords(new HashSet<>(Arrays.asList(record)));
    }

}
