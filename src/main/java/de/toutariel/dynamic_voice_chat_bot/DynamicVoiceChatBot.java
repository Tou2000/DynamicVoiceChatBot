package de.toutariel.dynamic_voice_chat_bot;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DynamicVoiceChatBot {

    static final Level LOGLEVEL = Level.ALL;

    static DynamicVoiceChatBot dynamicVoiceChatBot;

    public final ShardManager MAN;
    public final Path PATH_TO_TOKEN = Path.of("token.txt");
    public final Logger LOGGER;

    public DynamicVoiceChatBot() throws LoginException, IOException {

        LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        LOGGER.setLevel(LOGLEVEL);

        String token = Files.readString(PATH_TO_TOKEN, StandardCharsets.UTF_8);
        final DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching("Is managing voice Chats"));
        builder.addEventListeners(new Listener());
        MAN = builder.build();
        LOGGER.info("bot startup complete");

    }




    public static void main(String[] args)  {
        try {
            dynamicVoiceChatBot = new DynamicVoiceChatBot();
        } catch (LoginException e) {
            System.err.println("Error while connecting to discord api");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println("Error while reading token file");
            throw new RuntimeException(e);
        }
    }
}
