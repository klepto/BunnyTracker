package dev.klepto.bunnytracker;

import com.google.common.base.Preconditions;
import dev.klepto.bunnytracker.discord.DiscordBot;
import dev.klepto.bunnytracker.record.RecordsService;
import lombok.val;

import java.time.Duration;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
public class BunnyTracker {

    public static void main(String[] args) throws Exception {
        Preconditions.checkArgument(args.length > 0, "Please specify Discord API token as program argument.");

        val discordBot = new DiscordBot(args[0]);
        val recordsService = new RecordsService(Duration.ofMinutes(5), discordBot::announceRecords).start();
    }

}
