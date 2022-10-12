import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;

public class CommandHandler extends CommandExampleBase {
    private final Twirk twirk;


    public CommandHandler(Twirk twirk) {
        super(CommandType.PREFIX_COMMAND);
        this.twirk = twirk;
    }

    @Override
    protected String getCommandWords() {
        return "!паста";
    }

    @Override
    protected USER_TYPE getMinUserPrevilidge() {
        return USER_TYPE.DEFAULT;
    }

    @Override
    protected void performCommand(String s, TwitchUser twitchUser, TwitchMessage twitchMessage) {
        twirk.channelMessage(twitchUser.getDisplayName() + " " + Start.generateMsg());
    }

    @Override
    public void onPrivMsg(TwitchUser sender, TwitchMessage message) {
        Duration delay = null;
        String condition = null;
        if (Start.isGlued != null) {
            String[] ar = Start.isGlued.split("_");
            if (ar.length == 2) {
                condition = ar[0];
                delay = Duration.parse("PT" + ar[1]);
            }
        }
        if (Start.isAutoResponse || StringUtils.containsAnyIgnoreCase(message.getContent(), getCommandWords())) {
            twirk.channelMessage(sender.getDisplayName() + " " + Start.generateMsg());
        } else if ("true".equalsIgnoreCase(condition) && delay != null) {
            try {
                Thread.sleep(delay.toMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            twirk.channelMessage(Start.target + " " + Start.generateMsg());
        }
    }
}
