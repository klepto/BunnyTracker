package dev.klepto.bunnytracker.discord;

import dev.klepto.commands.annotation.Command;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.entities.Message;

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

        val reply = message.getAuthor().getAsMention() + "Default records channel changed to: "
                + message.getTextChannel().getAsMention();
        message.getTextChannel().sendMessage(reply).queue();
    }

    @Command
    public void ping(Message message) {
        message.getTextChannel().sendMessage("pong").queue();
    }

}
