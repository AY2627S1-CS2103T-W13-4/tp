package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;

import java.util.stream.Stream;

import seedu.address.logic.commands.AddMeetingCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;

/**
 * Parses input arguments and creates a new {@code AddMeetingCommand} object.
 */
public class AddMeetingCommandParser implements Parser<AddMeetingCommand> {

    /**
     * Parses the given arguments in the context of the {@code AddMeetingCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public AddMeetingCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_NAME, PREFIX_DATE, PREFIX_START_TIME, PREFIX_END_TIME);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_DATE, PREFIX_START_TIME, PREFIX_END_TIME)
                || !argMultimap.getPreamble().isEmpty()) {
            String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMeetingCommand.MESSAGE_USAGE);
            throw new ParseException(message);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_NAME, PREFIX_DATE, PREFIX_START_TIME, PREFIX_END_TIME);
        MeetingName name = ParserUtil.parseMeetingName(argMultimap.getValue(PREFIX_NAME).get());
        MeetingDate date = ParserUtil.parseMeetingDate(argMultimap.getValue(PREFIX_DATE).get());
        MeetingTime startTime = ParserUtil.parseMeetingTime(argMultimap.getValue(PREFIX_START_TIME).get());
        MeetingTime endTime = ParserUtil.parseMeetingTime(argMultimap.getValue(PREFIX_END_TIME).get());

        Meeting meeting = new Meeting(name, date, startTime, endTime);
        return new AddMeetingCommand(meeting);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
