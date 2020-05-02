package dev.klepto.bunnytracker;

import dev.klepto.bunnytracker.discord.DiscordBot;
import dev.klepto.bunnytracker.record.RecordsService;
import lombok.val;

import java.time.Duration;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
public class BunnyTracker {

    public static void main(String[] args) throws Exception {
        checkArgument(args.length > 0, "Please specify Discord API token as program argument.");

        val bot = new DiscordBot(args[0]);
        val service = new RecordsService(Duration.ofMinutes(5), bot::announceRecords);
        service.start();
    }

}
