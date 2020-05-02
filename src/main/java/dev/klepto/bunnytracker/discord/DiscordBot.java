package dev.klepto.bunnytracker.discord;

import dev.klepto.bunnytracker.record.Record;
import dev.klepto.commands.Commands;
import dev.klepto.commands.CommandsBuilder;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.Set;
import java.util.function.Function;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
@Getter
public class DiscordBot {

    private static final long ONWER_ID = 116165551394521095L;

    private final JDA jda;
    private final Commands<Message> commands;
    private final GuildConfigRepository configRepository;
    private final Function<Record, MessageEmbed> embedBuilder;

    public DiscordBot(String discordToken) throws Exception {
        this.commands = CommandsBuilder.forType(Message.class).build();
        this.jda = JDABuilder.createDefault(discordToken).build();
        this.configRepository = new GuildConfigRepository();
        this.embedBuilder = new RecordEmbedBuilder();

        commands.register(new DiscordCommands(this));
        jda.addEventListener((EventListener) this::onEvent);
        jda.awaitReady();
    }

    private void onEvent(GenericEvent genericEvent) {
        if (genericEvent instanceof GuildMessageReceivedEvent) {
            val event = (GuildMessageReceivedEvent) genericEvent;
            val text = event.getMessage().getContentDisplay();
            val botName = jda.getSelfUser().getName();
            if (!text.startsWith("@" + botName) || event.getMember() == null) {
                return;
            }

            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)
                    && event.getAuthor().getIdLong() != ONWER_ID) {
                return;
            }

            val command = text.substring(botName.length() + 2);
            commands.execute(event.getMessage(), command);
        }
    }

    public void announceRecords(Set<Record> records) {
        records.forEach(this::announceRecord);
    }

    private void announceRecord(Record record) {
        val configs = getConfigRepository().getAll();
        if (configs.isEmpty()) {
            return;
        }

        val embed = embedBuilder.apply(record);
        configs.forEach((guildId, guildConfig) -> {
            val guild = jda.getGuildById(guildId);
            if (guild == null) {
                return;
            }

            val channel = guild.getTextChannelById(guildConfig.getChannelId());
            if (channel == null) {
                return;
            }

            channel.sendMessage(embed).queue();
        });
    }

}
