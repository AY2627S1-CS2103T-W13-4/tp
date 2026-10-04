package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_validRemark_success() {
        assertParseSuccess(parser, " 1 r/Likes baseball",
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes baseball")));
    }

    @Test
    public void parse_emptyRemark_success() {
        assertParseSuccess(parser, " 1 r/", new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")));
    }

    @Test
    public void parse_missingPrefix_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 1", expected);
    }

    @Test
    public void parse_invalidIndex_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 0 r/Note", expected);
    }

    @Test
    public void parse_duplicateRemark_failure() {
        assertParseFailure(parser, " 1 r/First r/Second",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }
}
