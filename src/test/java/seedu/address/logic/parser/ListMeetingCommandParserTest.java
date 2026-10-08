package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListMeetingCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class ListMeetingCommandParserTest {
    private final ListMeetingCommandParser parser = new ListMeetingCommandParser();

    @Test
    public void parse_noArguments_success() throws Exception {
        assertTrue(parser.parse("") instanceof ListMeetingCommand);
        assertTrue(parser.parse(" \t \n ") instanceof ListMeetingCommand);
    }

    @Test
    public void parse_argumentsOrFlags_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListMeetingCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse(" 1"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse(" --all"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse(" n/John"));
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
