
package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindMeetingCommand;

public class FindMeetingCommandParserTest {

    private final FindMeetingCommandParser parser = new FindMeetingCommandParser();

    @Test
    public void parse_validArgs_returnsFindMeetingCommand() {
        assertParseSuccess(parser, "project",
                new FindMeetingCommand(List.of("project")));

        assertParseSuccess(parser, "project review",
                new FindMeetingCommand(List.of("project", "review")));
    }

    @Test
    public void parse_surroundingWhitespace_returnsFindMeetingCommand() {
        assertParseSuccess(parser, "  project review  ",
                new FindMeetingCommand(List.of("project", "review")));
    }

    @Test
    public void parse_multipleSpacesBetweenKeywords_returnsFindMeetingCommand() {
        assertParseSuccess(parser, "project   review",
                new FindMeetingCommand(List.of("project", "review")));
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindMeetingCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_whitespaceOnlyArgs_throwsParseException() {
        assertParseFailure(parser, "   ",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindMeetingCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_forceFlag_throwsParseException() {
        assertParseFailure(parser, "Project force/",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindMeetingCommand.MESSAGE_USAGE));
    }
}
