package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.Clock;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListMeetingCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

public class ListMeetingCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListMeetingCommand.MESSAGE_USAGE);

    private final ListMeetingCommandParser parser = new ListMeetingCommandParser();

    @Test
    public void parse_noArguments_success() throws Exception {
        assertTrue(parser.parse("") instanceof ListMeetingCommand);
        assertTrue(parser.parse(" \t \n ") instanceof ListMeetingCommand);
    }

    @Test
    public void parse_argumentsOrFlags_throwsParseException() {
        String expectedMessage = MESSAGE_INVALID_FORMAT;
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse(" 1"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse(" --all"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse(" n/John"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse(" /urgent"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse(" urgent t/work"));
    }

    @Test
    public void parse_otherKnownPrefix_failure() {
        String args = " n/Project Review d/08/10/2026 st/1400 et/1500"
                + " t/urgent p/98765432 a/123 Clementi Road";

        assertParseFailure(parser, args, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_forceFlag_failure() {
        assertParseFailure(parser, " force/", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_tags_success() {
        assertParseSuccess(parser, " t/urgent", new ListMeetingCommand(Clock.systemDefaultZone(),
                Set.of(new Tag("urgent"))));
        ListMeetingCommand multipleTags = new ListMeetingCommand(Clock.systemDefaultZone(),
                Set.of(new Tag("urgent"), new Tag("work")));
        assertParseSuccess(parser, " t/urgent t/work", multipleTags);
        assertParseSuccess(parser, "  t/urgent  t/work  ", multipleTags);
        assertParseSuccess(parser, " t/urgent t/work\t ", multipleTags);
        assertParseSuccess(parser, " t/URGENT t/urgent t/work", multipleTags);
    }

    @Test
    public void parse_tagWithoutPrecedingSpace_throwsParseException() {
        for (String arguments : new String[]{"t/urgent", "\tt/urgent", "\nt/urgent"}) {
            assertParseFailure(parser, arguments, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_tagsSeparatedByTabOrNewline_throwsParseException() {
        for (String arguments : new String[]{" t/urgent\tt/work", " t/urgent\nt/work"}) {
            assertParseFailure(parser, arguments, Tag.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_invalidTags_throwsParseException() {
        for (String arguments : new String[]{" t/", " t/  ", " t/urgent!", " t/very urgent",
            " t/ t/work", " t/urgent r/Review", " t/urgent --all"}) {
            assertThrows(ParseException.class, Tag.MESSAGE_CONSTRAINTS, () -> parser.parse(arguments));
        }
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
