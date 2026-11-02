package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerSaveFailureTest {
    @TempDir
    public Path temporaryFolder;

    private final ModelManager model = new ModelManager();
    private final Meeting later = meeting("Later review", "10/10/2099", "work");
    private final Meeting hidden = meeting("Hidden call", "08/10/2099", "personal");
    private final Meeting earlier = meeting("Earlier review", "09/10/2099", "work");
    private FailingStorage storage;
    private Logic logic;

    @BeforeEach
    public void setUp() throws Exception {
        model.addPerson(new PersonBuilder(AMY).withTags("work").build());
        model.addPerson(new PersonBuilder(BOB).withTags("personal").build());
        List.of(later, hidden, earlier).forEach(model::addMeeting);
        storage = new FailingStorage(temporaryFolder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        logic.execute("listc");
    }

    @Test
    public void execute_mutationSaveFails_restoresDataAndRetryAppliesOnce() throws Exception {
        // EP: every data-changing command must roll back for both supported save-error categories.
        for (IOException failure : List.of(new IOException("write failed"), new AccessDeniedException("denied"))) {
            for (String command : List.of("deletec 1", "editc 1 n/Changed", "deletem 1",
                    "editm 1 d/11/10/2099 t/personal", "addc n/Carol p/91234567 e/carol@example.com",
                    "addm n/New meeting d/12/10/2099 st/0900 et/1000", "clear")) {
                AddressBook original = new AddressBook(model.getAddressBook());
                boolean meetingCommand = command.startsWith("deletem") || command.startsWith("editm");
                logic.execute(meetingCommand ? "listm t/work" : "listc");
                List<Person> persons = List.copyOf(model.getFilteredPersonList());
                List<Meeting> meetings = List.copyOf(model.getMeetingList());
                String saved = Files.readString(storage.getAddressBookFilePath());
                storage.failure = failure;

                assertThrows(CommandException.class, errorMessage(failure), () -> logic.execute(command));
                assertEquals(original, model.getAddressBook(), command);
                assertEquals(persons, model.getFilteredPersonList(), command);
                assertEquals(meetings, model.getMeetingList(), command);
                assertEquals(saved, Files.readString(storage.getAddressBookFilePath()), command);

                storage.failure = null;
                logic.execute(meetingCommand ? "listm t/work" : "listc");
                assertEquals(original, storage.readAddressBook().orElseThrow(), command);
                logic.execute(command);
                assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow(), command);
                assertFalse(original.equals(model.getAddressBook()), command);
                model.setAddressBook(original);
                logic.execute("listc");
            }
        }
    }

    @Test
    public void execute_repeatedFailedDelete_retryDeletesOriginalContactOnly() throws Exception {
        logic.execute("findc Bob");
        Person bob = model.getFilteredPersonList().get(0);
        AddressBook original = new AddressBook(model.getAddressBook());
        storage.failure = new IOException("write failed");

        for (int attempt = 0; attempt < 2; attempt++) {
            assertThrows(CommandException.class, () -> logic.execute("deletec 1"));
            assertEquals(original, model.getAddressBook());
            assertEquals(List.of(bob), model.getFilteredPersonList());
        }

        storage.failure = null;
        logic.execute("deletec 1");
        assertEquals(List.of(original.getPersonList().get(0)), model.getAddressBook().getPersonList());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_failedMeetingDelete_retryUsesOriginalSortedFilteredIndex() throws Exception {
        logic.execute("listm t/work");
        assertEquals(List.of(earlier, later), model.getMeetingList());
        storage.failure = new IOException("write failed");

        assertThrows(CommandException.class, () -> logic.execute("deletem 1"));
        assertEquals(List.of(earlier, later), model.getMeetingList());

        storage.failure = null;
        logic.execute("deletem 1");
        assertEquals(List.of(later, hidden), model.getAddressBook().getMeetingList());
        assertEquals(List.of(later), model.getMeetingList());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_failedViewCommands_restoreBothFiltersOrderingAndPanel() throws Exception {
        logic.execute("findc Bob");
        logic.execute("listm t/work");
        // BVA: moving the ordering clock across the earlier meeting's end reverses these two visible meetings.
        model.updateMeetingListOrder(LocalDateTime.of(2099, 10, 9, 10, 0));
        assertEquals(List.of(later, earlier), model.getMeetingList());
        List<Person> persons = List.copyOf(model.getFilteredPersonList());
        storage.failure = new IOException("write failed");

        for (String command : List.of("listc", "findc Amy", "listm", "findm Hidden")) {
            assertThrows(CommandException.class, () -> logic.execute(command));
            assertEquals(persons, model.getFilteredPersonList(), command);
            assertEquals(List.of(later, earlier), model.getMeetingList(), command);
            assertThrows(CommandException.class, Messages.MESSAGE_CONTACT_LIST_NOT_VISIBLE, () ->
                    logic.execute("deletec 1"));
        }

        storage.failure = null;
        logic.execute("listc");
        logic.execute("deletec 1");
        assertEquals(List.of(new PersonBuilder(BOB).withTags("personal").build()),
                model.getAddressBook().getPersonList());
    }

    private String errorMessage(IOException failure) {
        String format = failure instanceof AccessDeniedException
                ? LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT : LogicManager.FILE_OPS_ERROR_FORMAT;
        return String.format(format, failure.getMessage());
    }

    private static Meeting meeting(String name, String date, String tag) {
        return new Meeting(new MeetingName(name), new MeetingDate(date),
                new MeetingTime("0900"), new MeetingTime("1000"), Set.of(new Tag(tag)));
    }

    /**
     * Storage that retains its real JSON file while a controlled failure is active.
     */
    private static class FailingStorage extends JsonAddressBookStorage {
        private IOException failure;

        FailingStorage(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            if (failure != null) {
                throw failure;
            }
            super.saveAddressBook(addressBook);
        }
    }
}
