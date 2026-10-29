package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_MEETING_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddClientCommand;
import seedu.address.logic.commands.AddMeetingCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteClientCommand;
import seedu.address.logic.commands.DeleteMeetingCommand;
import seedu.address.logic.commands.EditClientCommand;
import seedu.address.logic.commands.EditClientCommand.EditPersonDescriptor;
import seedu.address.logic.commands.EditMeetingCommand;
import seedu.address.logic.commands.EditMeetingCommand.EditMeetingDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindClientCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListClientCommand;
import seedu.address.logic.commands.ListMeetingCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditMeetingDescriptorBuilder;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_addClient() throws Exception {
        Person person = new PersonBuilder().build();
        AddClientCommand command = (AddClientCommand) parser.parseCommand(PersonUtil.getAddClientCommand(person));
        assertEquals(new AddClientCommand(person), command);
    }

    @Test
    public void parseCommand_addMeeting() throws Exception {
        Meeting meeting = new Meeting(
                new MeetingName("Project Review"), new MeetingDate("08/10/2026"),
                new MeetingTime("1400"), new MeetingTime("1500"));
        AddMeetingCommand command = (AddMeetingCommand) parser.parseCommand(
                AddMeetingCommand.COMMAND_WORD + " n/Project Review d/08/10/2026 st/1400 et/1500");
        assertEquals(new AddMeetingCommand(meeting), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_deleteClient() throws Exception {
        DeleteClientCommand command = (DeleteClientCommand) parser.parseCommand(
                DeleteClientCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteClientCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_deleteMeeting() throws Exception {
        Index index = Index.fromOneBased(1);
        DeleteMeetingCommand command = (DeleteMeetingCommand) parser.parseCommand(
                DeleteMeetingCommand.COMMAND_WORD + " " + index.getOneBased());
        assertEquals(new DeleteMeetingCommand(index), command);
        assertEquals(new DeleteMeetingCommand(index), parser.parseCommand(" \tdeletem \t1 \t"));
    }

    @Test
    public void parseCommand_deleteMeetingInvalidArguments_throwsParseException() {
        assertThrows(ParseException.class,
                MESSAGE_INVALID_MEETING_INDEX, () -> parser.parseCommand("deletem"));
        assertThrows(ParseException.class,
                MESSAGE_INVALID_MEETING_INDEX, () -> parser.parseCommand("deletem 1 2"));
    }

    @Test
    public void parseCommand_deleteMeetingWrongCase_throwsParseException() {
        assertThrows(ParseException.class,
                MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("DeleteM 1"));
    }

    @Test
    public void parseCommand_editClient() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditClientCommand command = (EditClientCommand) parser.parseCommand("editc "
                + INDEX_FIRST_PERSON.getOneBased() + " "
                + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditClientCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_oldEditCommand_throwsParseException() {
        String oldCommand = "edit 1 n/Alice";

        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(oldCommand));
    }

    @Test
    public void parseCommand_editMeeting() throws Exception {
        EditMeetingDescriptor descriptor = new EditMeetingDescriptorBuilder()
                .withDate("10/10/2026")
                .withStartTime("1500")
                .withTags("urgent")
                .build();
        EditMeetingCommand command = (EditMeetingCommand) parser.parseCommand(
                "editm 1 d/10/10/2026 st/1500 t/urgent");

        assertEquals(new EditMeetingCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindClientCommand command = (FindClientCommand) parser.parseCommand(
                FindClientCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindClientCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_listClient() throws Exception {
        assertTrue(parser.parseCommand(ListClientCommand.COMMAND_WORD) instanceof ListClientCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_listMeeting_success() throws Exception {
        // EP: a bare listm command and one surrounded by whitespace both dispatch to the meeting command.
        assertTrue(parser.parseCommand("listm") instanceof ListMeetingCommand);
        assertTrue(parser.parseCommand(" \tlistm \t ") instanceof ListMeetingCommand);
    }

    @Test
    public void parseCommand_listMeetingWithArguments_throwsParseException() {
        // EP: an unsupported flag and an unexpected positional index are invalid listm arguments.
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListMeetingCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, message, () -> parser.parseCommand("listm --all"));
        assertThrows(ParseException.class, message, () -> parser.parseCommand("listm 1"));
    }

    @Test
    public void parseCommand_listCommandsWithTabBeforeTag_throwsParseException() {
        // EP: a tab before the first tag is an invalid separator for both listc and listm.
        String clientMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListClientCommand.MESSAGE_USAGE);
        String meetingMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListMeetingCommand.MESSAGE_USAGE);

        assertThrows(ParseException.class, clientMessage, () -> parser.parseCommand("listc\tt/work"));
        assertThrows(ParseException.class, meetingMessage, () -> parser.parseCommand("listm\tt/work"));
    }

    @Test
    public void parseCommand_listMeetingWrongCase_suggestsCorrectCommand() {
        // EP: mixed-case and uppercase spellings are invalid variants of the case-sensitive listm command.
        String message = ListMeetingCommand.MESSAGE_UNKNOWN_COMMAND;
        assertThrows(ParseException.class, message, () -> parser.parseCommand("Listm"));
        assertThrows(ParseException.class, message, () -> parser.parseCommand("LISTM"));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
