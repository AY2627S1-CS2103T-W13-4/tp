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
    public void parse_allPrefixesWithTags_success() {
        Set<Tag> tags = Set.of(new Tag("friends"), new Tag("colleagues"));
        ListClientCommand expectedCommand = new ListClientCommand(tags);

        String args = " n/John Doe p/98765432 e/john@example.com"
                + " t/friends a/123 Clementi Road d/08/10/2026"
                + " st/1400 et/1500 t/colleagues";

        assertParseSuccess(parser, args, expectedCommand);
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
