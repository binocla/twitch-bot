import com.gikk.twirk.Twirk;
import com.gikk.twirk.commands.CommandExampleBase;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;

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
        twirk.channelMessage(sender.getDisplayName() + " " + Start.generateMsg());
    }
}
