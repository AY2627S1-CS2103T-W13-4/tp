package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteMeetingCommand;

public class DeleteMeetingCommandParserTest {

    private final DeleteMeetingCommandParser parser = new DeleteMeetingCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        // EP: a positive single index, with or without surrounding whitespace, is valid input.
        assertParseSuccess(parser, "1", new DeleteMeetingCommand(Index.fromOneBased(1)));
        assertParseSuccess(parser, " 1 ", new DeleteMeetingCommand(Index.fromOneBased(1)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // EP: empty, non-positive, non-numeric, and multiple-index inputs are invalid argument partitions.
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMeetingCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "0",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMeetingCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "-1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMeetingCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "1 2",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMeetingCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "abc",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMeetingCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_prefixNotSupportedByCommand_throwsParseException() {
        // EP: deletion accepts a positional index only; a meeting-name prefix is unsupported.
        assertParseFailure(parser, "1 n/Review",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMeetingCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_forceFlag_throwsParseException() {
        // EP: an unsupported force flag must not be silently accepted as a deletion option.
        assertParseFailure(parser, "1 force/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMeetingCommand.MESSAGE_USAGE));
    }
}
