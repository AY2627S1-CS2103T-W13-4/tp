package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_CONTACT_LIST_NOT_VISIBLE;
import static seedu.address.logic.Messages.MESSAGE_MEETING_LIST_NOT_VISIBLE;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddClientCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteClientCommand;
import seedu.address.logic.commands.DeleteMeetingCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListClientCommand;
import seedu.address.logic.commands.ListMeetingCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerMeetingViewTest {
    private static final IOException SAVE_EXCEPTION = new IOException("Test save failure");
    private static final String SAVE_ERROR =
            String.format(LogicManager.FILE_OPS_ERROR_FORMAT, SAVE_EXCEPTION.getMessage());

    @TempDir
    public Path temporaryFolder;

    private final ModelManager model = new ModelManager();
    private ControllableAddressBookStorage addressBookStorage;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        model.addPerson(AMY);
        model.addPerson(BOB);
        model.addMeeting(new Meeting(new MeetingName("Team meeting"), new MeetingDate("09/10/2099"),
                new MeetingTime("1500"), new MeetingTime("1600")));
        addressBookStorage = new ControllableAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        logic = new LogicManager(model, new StorageManager(addressBookStorage, userPrefsStorage));
    }

    @Test
    public void execute_deleteWhileShowingMeetings_rejectsWithoutChangingDataOrSaving() throws Exception {
        logic.execute(ListMeetingCommand.COMMAND_WORD);

        assertContactCommandRejected("  " + DeleteClientCommand.COMMAND_WORD + " 1  ");
    }

    @Test
    public void execute_deleteWhileShowingClients_rejectsWithoutChangingDataOrSaving() throws Exception {
        logic.execute(ListClientCommand.COMMAND_WORD);

        assertMeetingCommandRejected("  " + DeleteMeetingCommand.COMMAND_WORD + " 1  ");
    }

    @Test
    public void execute_deleteAfterFindAndListMeeting_rejectsHiddenFilteredContact() throws Exception {
        logic.execute(FindCommand.COMMAND_WORD + " Bob");
        logic.execute(ListMeetingCommand.COMMAND_WORD);
        assertEquals(List.of(BOB), model.getFilteredPersonList());

        assertContactCommandRejected(DeleteClientCommand.COMMAND_WORD + " 1");
    }

    @Test
    public void execute_editAfterFindAndListMeeting_rejectsHiddenFilteredContact() throws Exception {
        logic.execute(FindCommand.COMMAND_WORD + " Bob");
        logic.execute(ListMeetingCommand.COMMAND_WORD);
        assertEquals(List.of(BOB), model.getFilteredPersonList());

        assertContactCommandRejected(EditCommand.COMMAND_WORD + " 1 p/87654321");
    }

    @Test
    public void execute_listAfterMeetings_allowsDeleteFromAllContacts() throws Exception {
        logic.execute(FindCommand.COMMAND_WORD + " Bob");
        logic.execute(ListMeetingCommand.COMMAND_WORD);
        assertContactCommandRejected(DeleteClientCommand.COMMAND_WORD + " 1");

        CommandResult result = logic.execute(ListClientCommand.COMMAND_WORD);
        assertFalse(result.isShowMeetings());
        assertEquals(List.of(AMY, BOB), model.getFilteredPersonList());
        logic.execute(DeleteClientCommand.COMMAND_WORD + " 1");
        assertEquals(List.of(BOB), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());

        logic.execute(ListMeetingCommand.COMMAND_WORD);
        assertContactCommandRejected(DeleteClientCommand.COMMAND_WORD + " 1");
    }

    @Test
    public void execute_findAfterMeetings_allowsEditUsingVisibleFilteredIndex() throws Exception {
        logic.execute(ListMeetingCommand.COMMAND_WORD);

        CommandResult result = logic.execute(FindCommand.COMMAND_WORD + " Bob");
        assertFalse(result.isShowMeetings());
        assertEquals(List.of(BOB), model.getFilteredPersonList());
        logic.execute(EditCommand.COMMAND_WORD + " 1 p/87654321");

        Person editedBob = new PersonBuilder(BOB).withPhone("87654321").build();
        assertEquals(List.of(AMY, editedBob), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_helpWhileShowingMeetings_keepsContactCommandsBlocked() throws Exception {
        logic.execute(ListMeetingCommand.COMMAND_WORD);

        assertTrue(logic.execute(HelpCommand.COMMAND_WORD).isShowHelp());

        assertContactCommandRejected(DeleteClientCommand.COMMAND_WORD + " 1");
        assertContactCommandRejected(EditCommand.COMMAND_WORD + " 1 p/87654321");
    }

    @Test
    public void execute_errorsWhileShowingMeetings_keepsContactCommandsBlocked() throws Exception {
        logic.execute(ListMeetingCommand.COMMAND_WORD);

        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("unknown"));
        assertContactCommandRejected(DeleteClientCommand.COMMAND_WORD + " 1");

        String duplicateAdd = AddClientCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        assertThrows(CommandException.class,
                AddClientCommand.MESSAGE_DUPLICATE_PERSON, () -> logic.execute(duplicateAdd));
        assertContactCommandRejected(EditCommand.COMMAND_WORD + " 1 p/87654321");
    }

    @Test
    public void execute_failedListSave_keepsMeetingGuardUntilListSucceeds() throws Exception {
        logic.execute(ListMeetingCommand.COMMAND_WORD);
        addressBookStorage.failNextSave = true;

        assertThrows(CommandException.class, SAVE_ERROR, () -> logic.execute(ListClientCommand.COMMAND_WORD));
        assertContactCommandRejected(DeleteClientCommand.COMMAND_WORD + " 1");
        assertContactCommandRejected(EditCommand.COMMAND_WORD + " 1 p/87654321");

        logic.execute(ListClientCommand.COMMAND_WORD);
        logic.execute(DeleteClientCommand.COMMAND_WORD + " 1");
        assertEquals(List.of(BOB), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_failedListMeetingSave_keepsContactsAccessible() throws Exception {
        logic.execute(ListClientCommand.COMMAND_WORD);
        addressBookStorage.failNextSave = true;

        assertThrows(CommandException.class, SAVE_ERROR, () -> logic.execute(ListMeetingCommand.COMMAND_WORD));

        logic.execute(DeleteClientCommand.COMMAND_WORD + " 1");
        assertEquals(List.of(BOB), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_emptyMeetingList_stillBlocksContactCommands() throws Exception {
        model.deleteMeeting(model.getMeetingList().get(0));

        assertTrue(logic.execute(ListMeetingCommand.COMMAND_WORD).isShowMeetings());
        assertTrue(model.getMeetingList().isEmpty());

        assertContactCommandRejected(DeleteClientCommand.COMMAND_WORD + " 1");
        assertContactCommandRejected(EditCommand.COMMAND_WORD + " 1 p/87654321");
    }

    /**
     * Confirms that a hidden contact command changes neither the model, its filter nor the stored file.
     */
    private void assertContactCommandRejected(String commandText) throws Exception {
        AddressBook original = new AddressBook(model.getAddressBook());
        List<Person> originalFilteredPersons = List.copyOf(model.getFilteredPersonList());
        String originalFile = Files.readString(addressBookStorage.getAddressBookFilePath());
        int originalSaveAttempts = addressBookStorage.saveAttempts;

        assertThrows(CommandException.class, MESSAGE_CONTACT_LIST_NOT_VISIBLE, () -> logic.execute(commandText));

        assertEquals(original, model.getAddressBook());
        assertEquals(originalFilteredPersons, model.getFilteredPersonList());
        assertEquals(originalFile, Files.readString(addressBookStorage.getAddressBookFilePath()));
        assertEquals(originalSaveAttempts, addressBookStorage.saveAttempts);
    }

    /**
     * Confirms that a rejected meeting command changes neither the model nor the stored file.
     */
    private void assertMeetingCommandRejected(String commandText) throws Exception {
        AddressBook original = new AddressBook(model.getAddressBook());
        String originalFile = Files.readString(addressBookStorage.getAddressBookFilePath());
        int originalSaveAttempts = addressBookStorage.saveAttempts;

        assertThrows(CommandException.class, MESSAGE_MEETING_LIST_NOT_VISIBLE, () -> logic.execute(commandText));

        assertEquals(original, model.getAddressBook());
        assertEquals(originalFile, Files.readString(addressBookStorage.getAddressBookFilePath()));
        assertEquals(originalSaveAttempts, addressBookStorage.saveAttempts);
    }

    /**
     * Counts save attempts and can fail one save to check that the displayed panel state stays consistent.
     */
    private static class ControllableAddressBookStorage extends JsonAddressBookStorage {
        private boolean failNextSave;
        private int saveAttempts;

        ControllableAddressBookStorage(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            saveAttempts++;
            if (failNextSave) {
                failNextSave = false;
                throw SAVE_EXCEPTION;
            }
            super.saveAddressBook(addressBook);
        }
    }
}
