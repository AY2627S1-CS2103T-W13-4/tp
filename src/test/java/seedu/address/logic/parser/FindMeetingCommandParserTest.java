package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindMeetingCommand;

public class FindMeetingCommandParserTest {

    private final FindMeetingCommandParser parser = new FindMeetingCommandParser();

    @Test
    public void parse_validArgs_returnsFindCommand() {
        assertParseSuccess(parser, "Project Review", new FindMeetingCommand(List.of("Project", "Review")));
    }

    @Test
    public void parse_prefixNotSupportedByCommand_throwsParseException() {
        assertParseFailure(parser, "Project n/Review",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindMeetingCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_forceFlag_throwsParseException() {
        assertParseFailure(parser, "Project force/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindMeetingCommand.MESSAGE_USAGE));
    }
}
