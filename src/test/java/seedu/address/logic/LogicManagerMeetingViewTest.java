package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_CONTACT_LIST_NOT_VISIBLE;
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

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
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
        logic.execute("listm");

        assertContactCommandRejected("  delete 1  ");
    }

    @Test
    public void execute_deleteAfterFindAndListMeeting_rejectsHiddenFilteredContact() throws Exception {
        logic.execute("find Bob");
        logic.execute("listm");
        assertEquals(List.of(BOB), model.getFilteredPersonList());

        assertContactCommandRejected("delete 1");
    }

    @Test
    public void execute_editAfterFindAndListMeeting_rejectsHiddenFilteredContact() throws Exception {
        logic.execute("find Bob");
        logic.execute("listm");
        assertEquals(List.of(BOB), model.getFilteredPersonList());

        assertContactCommandRejected("edit 1 p/87654321");
    }

    @Test
    public void execute_listAfterMeetings_allowsDeleteFromAllContacts() throws Exception {
        logic.execute("find Bob");
        logic.execute("listm");
        assertContactCommandRejected("delete 1");

        CommandResult result = logic.execute("list");
        assertFalse(result.isShowMeetings());
        assertEquals(List.of(AMY, BOB), model.getFilteredPersonList());
        logic.execute("delete 1");
        assertEquals(List.of(BOB), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());

        logic.execute("listm");
        assertContactCommandRejected("delete 1");
    }

    @Test
    public void execute_findAfterMeetings_allowsEditUsingVisibleFilteredIndex() throws Exception {
        logic.execute("listm");

        CommandResult result = logic.execute("find Bob");
        assertFalse(result.isShowMeetings());
        assertEquals(List.of(BOB), model.getFilteredPersonList());
        logic.execute("edit 1 p/87654321");

        Person editedBob = new PersonBuilder(BOB).withPhone("87654321").build();
        assertEquals(List.of(AMY, editedBob), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_helpWhileShowingMeetings_keepsContactCommandsBlocked() throws Exception {
        logic.execute("listm");

        assertTrue(logic.execute("help").isShowHelp());

        assertContactCommandRejected("delete 1");
        assertContactCommandRejected("edit 1 p/87654321");
    }

    @Test
    public void execute_errorsWhileShowingMeetings_keepsContactCommandsBlocked() throws Exception {
        logic.execute("listm");

        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("unknown"));
        assertContactCommandRejected("delete 1");

        String duplicateAdd = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_PERSON, () -> logic.execute(duplicateAdd));
        assertContactCommandRejected("edit 1 p/87654321");
    }

    @Test
    public void execute_failedListSave_keepsMeetingGuardUntilListSucceeds() throws Exception {
        logic.execute("listm");
        addressBookStorage.failNextSave = true;

        assertThrows(CommandException.class, SAVE_ERROR, () -> logic.execute("list"));
        assertContactCommandRejected("delete 1");
        assertContactCommandRejected("edit 1 p/87654321");

        logic.execute("list");
        logic.execute("delete 1");
        assertEquals(List.of(BOB), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_failedListMeetingSave_keepsContactsAccessible() throws Exception {
        logic.execute("list");
        addressBookStorage.failNextSave = true;

        assertThrows(CommandException.class, SAVE_ERROR, () -> logic.execute("listm"));

        logic.execute("delete 1");
        assertEquals(List.of(BOB), model.getAddressBook().getPersonList());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_emptyMeetingList_stillBlocksContactCommands() throws Exception {
        model.deleteMeeting(model.getMeetingList().get(0));

        assertTrue(logic.execute("listm").isShowMeetings());
        assertTrue(model.getMeetingList().isEmpty());

        assertContactCommandRejected("delete 1");
        assertContactCommandRejected("edit 1 p/87654321");
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
