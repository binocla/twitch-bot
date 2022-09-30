import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Quarkus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;


@CommandLine.Command(name = "start", mixinStandardHelpOptions = true, description = "Triggers SPAMMING of jokes", version = "0.0.1", showDefaultValues = true)
public class Start implements Runnable {
    @CommandLine.Option(names = {"-p", "--pass"}, description = "Providing password from Twitch. Required. " +
            "@|fg(green) Example: start -p=oauth:5scxxxxxxxxxx" +
            "            You can receive token here: https://twitchapps.com/tmi/|@", required = true)
    String pass;

    @CommandLine.Option(names = {"-u", "--username"}, description = "Providing username from Twitch. Required. " +
            "@|fg(green) Example: start -u=USER_NAME|@")
    String username;

    @CommandLine.Option(names = {"-c", "--channel"}, description = "Providing Twitch channel to join. Required. " +
            "@|fg(green) Example: start -c=ckorovoda|@")
    String channel;

    @CommandLine.Option(names = {"-t", "--target"}, description = "Providing target to be SPAMMED. Optional. " +
            "@|fg(green) Example: start -t=binoclagonnalit|@")
    String target;

    @CommandLine.Option(names = {"-pa", "--path"}, description = "Path for providing text file. Optional. " +
            "@|fg(green) Example: start -pa='C:\\Users\\liwgfr\\IdeaProjects\\twitch_bot\\src\\main\\resources\\ForMessages.txt'@", defaultValue = "src/main/resources/ForMessages.txt")
    static String file;

    @ConfigProperty(name = "verbose.mode")
    boolean isVerbose;



    @Override
    public void run() {

        Twirk twirk = new TwirkBuilder(channel, username, pass)
                .setVerboseMode(isVerbose)
                .build();

        twirk.addIrcListener(getOnDisconnectListener(twirk));
        twirk.addIrcListener(new CommandHandler(twirk));

        Log.info("Connecting to the Twitch!");
        try {
            twirk.connect();
        } catch (IOException | InterruptedException e) {
            Log.error("Can't connect to the Twitch with specified credentials!");
            throw new RuntimeException(e);
        }
        String msg = generateMsg();
        String twitchUser = "@";
        if (StringUtils.isNotBlank(target)) {
            twitchUser += target;
            twirk.channelMessage(twitchUser + " " + msg);
        } else {
            Log.info("Online users are: " + twirk.getUsersOnline());
            if (!twirk.getUsersOnline().isEmpty()) {
                while (true) {
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    twitchUser += twirk.getUsersOnline().toArray()[twirk.getUsersOnline().size() - 1];
                    twirk.channelMessage(twitchUser + " " + msg);
                    twitchUser = "";
                }
            } else {
                twirk.channelMessage(msg);
            }
        }



        Quarkus.waitForExit();
    }

    private static TwirkListener getOnDisconnectListener(final Twirk twirk) {

        return new TwirkListener() {
            @Override
            public void onDisconnect() {
                try {
                    if (!twirk.connect()) {
                        twirk.close();
                    }
                } catch (IOException | InterruptedException e) {
                    twirk.close();
                    Log.error("Something went wrong: " + e.getMessage());
                }
            }
        };
    }

    public static String generateMsg() {
        List<String> messages;
        try {
            messages = FileUtils.readLines(Path.of(file).toFile(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Log.error("Can't read file with messages");
            throw new RuntimeException(e);
        }
        return messages.get(RandomUtils.nextInt(0, messages.size()));
    }
}
