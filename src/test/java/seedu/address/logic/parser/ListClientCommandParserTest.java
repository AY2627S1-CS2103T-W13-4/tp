package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListClientCommand;
import seedu.address.model.tag.Tag;

public class ListClientCommandParserTest {
    private final ListClientCommandParser parser = new ListClientCommandParser();

    @Test
    public void parse_noArguments_success() {
        assertParseSuccess(parser, "", new ListClientCommand(Set.of()));
        assertParseSuccess(parser, " \t \n ", new ListClientCommand(Set.of()));
    }

    @Test
    public void parse_tags_success() {
        Set<Tag> tags = Set.of(new Tag("friends"), new Tag("colleagues"));
        ListClientCommand expectedCommand = new ListClientCommand(tags);

        assertParseSuccess(parser, " t/friends t/colleagues ", expectedCommand);
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
