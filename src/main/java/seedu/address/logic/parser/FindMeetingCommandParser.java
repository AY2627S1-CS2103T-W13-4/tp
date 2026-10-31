package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import seedu.address.logic.commands.FindMeetingCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.meeting.MeetingName;

/**
 * Parses input arguments and creates a new FindMeetingCommand object.
 */
public class FindMeetingCommandParser implements Parser<FindMeetingCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * FindMeetingCommand and returns a FindMeetingCommand object for execution.
     *
     * @param args The user input arguments.
     * @return A FindMeetingCommand object.
     * @throws ParseException If the user input does not conform to the expected format.
     */
    @Override
    public FindMeetingCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, CliSyntax.getAllPrefixes());
        if (trimmedArgs.isEmpty() || !argMultimap.containsOnlyPrefixes()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindMeetingCommand.MESSAGE_USAGE));
        }

        String[] keywords = trimmedArgs.split("\\s+");

        for (String keyword : keywords) {
            if (!MeetingName.isValidKeyword(keyword)) {
                throw new ParseException(MeetingName.MESSAGE_CONSTRAINTS);
            }
        }

        return new FindMeetingCommand(Arrays.asList(keywords));
    }
}
