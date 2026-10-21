package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddMeetingCommand;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.tag.Tag;

public class AddMeetingCommandParserTest {

    private static final String VALID_NAME = "Project Review";
    private static final String VALID_DATE = "08/10/2026";
    private static final String VALID_START_TIME = "1400";
    private static final String VALID_END_TIME = "1500";

    private static final String NAME_DESC = " " + PREFIX_NAME + VALID_NAME;
    private static final String DATE_DESC = " " + PREFIX_DATE + VALID_DATE;
    private static final String START_TIME_DESC = " " + PREFIX_START_TIME + VALID_START_TIME;
    private static final String END_TIME_DESC = " " + PREFIX_END_TIME + VALID_END_TIME;

    private static final Meeting VALID_MEETING = new Meeting(
            new MeetingName(VALID_NAME), new MeetingDate(VALID_DATE),
            new MeetingTime(VALID_START_TIME), new MeetingTime(VALID_END_TIME));

    private final AddMeetingCommandParser parser = new AddMeetingCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        assertParseSuccess(parser, NAME_DESC + DATE_DESC + START_TIME_DESC + END_TIME_DESC,
                new AddMeetingCommand(VALID_MEETING));

        assertParseSuccess(parser, END_TIME_DESC + START_TIME_DESC + DATE_DESC + NAME_DESC,
                new AddMeetingCommand(VALID_MEETING));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, AddMeetingCommand.MESSAGE_USAGE);

        assertParseFailure(parser, DATE_DESC + START_TIME_DESC + END_TIME_DESC, expectedMessage);
        assertParseFailure(parser, NAME_DESC + START_TIME_DESC + END_TIME_DESC, expectedMessage);
        assertParseFailure(parser, NAME_DESC + DATE_DESC + END_TIME_DESC, expectedMessage);
        assertParseFailure(parser, NAME_DESC + DATE_DESC + START_TIME_DESC, expectedMessage);
    }

    @Test
    public void parse_optionalTags_success() {
        String details = NAME_DESC + DATE_DESC + START_TIME_DESC + END_TIME_DESC;
        Meeting singleTag = new Meeting(VALID_MEETING.getName(), VALID_MEETING.getDate(),
                VALID_MEETING.getStartTime(), VALID_MEETING.getEndTime(), Set.of(new Tag("urgent")));
        Meeting multipleTags = new Meeting(VALID_MEETING.getName(), VALID_MEETING.getDate(),
                VALID_MEETING.getStartTime(), VALID_MEETING.getEndTime(),
                Set.of(new Tag("urgent"), new Tag("work")));

        assertParseSuccess(parser, details + " t/urgent", new AddMeetingCommand(singleTag));
        assertParseSuccess(parser, details + " t/urgent t/work", new AddMeetingCommand(multipleTags));
        assertParseSuccess(parser, " t/urgent" + NAME_DESC + DATE_DESC + " t/work"
                + START_TIME_DESC + END_TIME_DESC, new AddMeetingCommand(multipleTags));
        assertParseSuccess(parser, details + " t/urgent t/urgent", new AddMeetingCommand(singleTag));
        assertParseSuccess(parser, details + " t/ urgent  ", new AddMeetingCommand(singleTag));
    }

    @Test
    public void parse_invalidTags_failure() {
        String details = NAME_DESC + DATE_DESC + START_TIME_DESC + END_TIME_DESC;
        assertParseFailure(parser, details + " t/", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, details + " t/   ", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, details + " t/urgent!", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, details + " t/very urgent", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, details + " t/ t/urgent", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedValue_failure() {
        String validMeeting = NAME_DESC + DATE_DESC + START_TIME_DESC + END_TIME_DESC;

        assertParseFailure(parser, NAME_DESC + validMeeting,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, DATE_DESC + validMeeting,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));
        assertParseFailure(parser, START_TIME_DESC + validMeeting,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_START_TIME));
        assertParseFailure(parser, END_TIME_DESC + validMeeting,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_END_TIME));
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser,
                " " + PREFIX_NAME + "Project 2" + DATE_DESC + START_TIME_DESC + END_TIME_DESC,
                MeetingName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser,
                NAME_DESC + " " + PREFIX_DATE + "31/02/2026" + START_TIME_DESC + END_TIME_DESC,
                MeetingDate.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC + DATE_DESC + " " + PREFIX_START_TIME + "2500" + END_TIME_DESC,
                MeetingTime.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC + DATE_DESC + START_TIME_DESC + " " + PREFIX_END_TIME,
                MeetingTime.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, AddMeetingCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "unexpected" + NAME_DESC + DATE_DESC + START_TIME_DESC + END_TIME_DESC,
                expectedMessage);
    }
}
