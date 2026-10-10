package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteMeetingCommand;

public class DeleteMeetingCommandParserTest {

    private final DeleteMeetingCommandParser parser = new DeleteMeetingCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteMeetingCommand(Index.fromOneBased(1)));
        assertParseSuccess(parser, " 1 ", new DeleteMeetingCommand(Index.fromOneBased(1)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "", Messages.MESSAGE_INVALID_MEETING_INDEX);
        assertParseFailure(parser, "0", Messages.MESSAGE_INVALID_MEETING_INDEX);
        assertParseFailure(parser, "-1", Messages.MESSAGE_INVALID_MEETING_INDEX);
        assertParseFailure(parser, "1 2", Messages.MESSAGE_INVALID_MEETING_INDEX);
        assertParseFailure(parser, "abc", Messages.MESSAGE_INVALID_MEETING_INDEX);
    }
}
