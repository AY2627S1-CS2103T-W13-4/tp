package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ListMeetingCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the arguments of the listm command.
 */
public class ListMeetingCommandParser implements Parser<ListMeetingCommand> {

    /**
     * Parses the text after the command word and returns a command to list all meetings.
     * Accepts empty or whitespace-only input. Rejects parameters and flags.
     *
     * @param args The text following the {@code listm} command word.
     * @return A command that lists all meetings.
     * @throws ParseException If {@code args} contains any non-whitespace characters.
     * @throws NullPointerException If {@code args} is null.
     */
    @Override
    public ListMeetingCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (!args.isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListMeetingCommand.MESSAGE_USAGE));
        }
        return new ListMeetingCommand();
    }
}
