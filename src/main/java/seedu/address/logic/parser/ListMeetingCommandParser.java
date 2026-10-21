package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.time.Clock;

import seedu.address.logic.commands.ListMeetingCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the arguments of the listm command.
 */
public class ListMeetingCommandParser implements Parser<ListMeetingCommand> {

    /**
     * Parses optional repeated {@code t/TAG} arguments, or lists all meetings for blank input.
     *
     * @param args The text following the {@code listm} command word.
     * @return A command that lists meetings matching all supplied tags.
     * @throws ParseException If the arguments contain invalid tags or unsupported syntax.
     * @throws NullPointerException If {@code args} is null.
     */
    @Override
    public ListMeetingCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (args.isBlank()) {
            return new ListMeetingCommand();
        }
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + args.strip().replaceAll("\\s+", " "), PREFIX_TAG);
        if (!arguments.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListMeetingCommand.MESSAGE_USAGE));
        }
        return new ListMeetingCommand(Clock.systemDefaultZone(),
                ParserUtil.parseTags(arguments.getAllValues(PREFIX_TAG)));
    }
}
