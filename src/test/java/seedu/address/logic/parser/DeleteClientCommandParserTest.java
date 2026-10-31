package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteClientCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteClientCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteClientCommand, and therefore we test only one of them.
 * The path variation for those two cases occurs inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteClientCommandParserTest {

    private DeleteClientCommandParser parser = new DeleteClientCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteClientCommand() {
        // EP: a positive single index is the valid positional argument partition.
        assertParseSuccess(parser, "1", new DeleteClientCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // EP: non-numeric input is outside the index partition and must be rejected.
        assertParseFailure(parser, "a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteClientCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_prefixNotSupportedByCommand_throwsParseException() {
        // EP: deletion accepts an index only; a name prefix belongs to other commands.
        assertParseFailure(parser, "1 n/Alice",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteClientCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_forceFlag_throwsParseException() {
        // EP: an unsupported force flag must not change deletion semantics.
        assertParseFailure(parser, "1 force/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteClientCommand.MESSAGE_USAGE));
    }
}
