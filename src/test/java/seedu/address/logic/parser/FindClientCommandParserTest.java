package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindClientCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;

public class FindClientCommandParserTest {

    private FindClientCommandParser parser = new FindClientCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindClientCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindClientCommand expectedFindCommand =
                new FindClientCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_prefixNotSupportedByCommand_throwsParseException() {
        assertParseFailure(parser, "Alice n/Bob",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindClientCommand.MESSAGE_USAGE));
    }

}
