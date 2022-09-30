import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine;

@TopCommand
@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {Start.class, CommandLine.HelpCommand.class}, version = "0.0.1", description = "@|fg(red) Use --help for each command for more details.|@  " +
        "Example: @|fg(green) start --help|@")
public class EntryCommand { }
