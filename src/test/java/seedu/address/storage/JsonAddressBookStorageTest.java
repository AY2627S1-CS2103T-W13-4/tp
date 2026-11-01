package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalMeetings.CLIENT_REVIEW;
import static seedu.address.testutil.TypicalMeetings.getTypicalAddressBookWithMeetings;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributeView;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.tag.Tag;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidAndValidPersonAddressBook.json"));
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void readAndSaveAddressBook_withMeetings_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBookWithMeetings.json");
        AddressBook original = getTypicalAddressBookWithMeetings();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify meetings, overwrite existing file, and read back
        original.removeMeeting(CLIENT_REVIEW);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));
    }

    @Test
    public void readAndSaveAddressBook_withMeetingTags_preservesTagsAfterReloadAndOverwrite() throws Exception {
        // Round-trip testing: reload tagged data, then overwrite it with untagged data to expose stale persisted tags.
        Path filePath = testFolder.resolve("TaggedMeetings.json");
        AddressBook addressBook = new AddressBook();
        Meeting tagged = new Meeting(CLIENT_REVIEW.getName(), CLIENT_REVIEW.getDate(),
                CLIENT_REVIEW.getStartTime(), CLIENT_REVIEW.getEndTime(),
                Set.of(new Tag("urgent"), new Tag("work")));
        addressBook.addMeeting(tagged);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        storage.saveAddressBook(addressBook);

        ReadOnlyAddressBook restored = new JsonAddressBookStorage(filePath).readAddressBook().orElseThrow();
        assertEquals(addressBook, new AddressBook(restored));
        assertEquals(tagged.getTags(), restored.getMeetingList().get(0).getTags());

        addressBook.removeMeeting(tagged);
        addressBook.addMeeting(CLIENT_REVIEW);
        storage.saveAddressBook(addressBook);
        restored = new JsonAddressBookStorage(filePath).readAddressBook().orElseThrow();
        assertEquals(Set.of(), restored.getMeetingList().get(0).getTags());
    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }

    @Test
    public void saveAddressBook_partialTemporaryWriteFails_preservesOriginalAndRemovesTemporaryFile() throws Exception {
        // Error guessing: an IOException after writing some bytes must not truncate the real data file.
        Path target = testFolder.resolve("addressbook.json");
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(target) {
            @Override
            void writeAddressBook(ReadOnlyAddressBook addressBook, Path temporaryFile) throws IOException {
                Files.writeString(temporaryFile, "partial JSON");
                throw new IOException("Interrupted write");
            }
        };

        assertFailedSavePreservesOriginal(failingStorage);
    }

    @Test
    public void saveAddressBook_atomicMoveUnsupported_preservesOriginalAndRemovesTemporaryFile() throws Exception {
        Path target = testFolder.resolve("addressbook.json");
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(target) {
            @Override
            void replaceAddressBook(Path temporaryFile, Path destination) throws IOException {
                throw new AtomicMoveNotSupportedException(temporaryFile.toString(), destination.toString(),
                        "Atomic replacement unavailable");
            }
        };

        assertFailedSavePreservesOriginal(failingStorage);
    }

    @Test
    public void saveAddressBook_failedFirstWrite_doesNotCreateTargetOrLeaveTemporaryFile() throws Exception {
        Path target = testFolder.resolve("addressbook.json");
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(target) {
            @Override
            void writeAddressBook(ReadOnlyAddressBook addressBook, Path temporaryFile) throws IOException {
                Files.writeString(temporaryFile, "partial JSON");
                throw new IOException("Interrupted first write");
            }
        };

        assertThrows(IOException.class, () -> failingStorage.saveAddressBook(getTypicalAddressBook()));
        assertFalse(Files.exists(target));
        try (Stream<Path> files = Files.list(testFolder)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    public void saveAddressBook_missingParentDirectories_createsParentsAndPublishesCompleteFile() throws Exception {
        Path target = testFolder.resolve("nested").resolve("data").resolve("addressbook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(target);
        AddressBook original = getTypicalAddressBookWithMeetings();

        storage.saveAddressBook(original);

        assertEquals(original, storage.readAddressBook().orElseThrow());
        try (Stream<Path> files = Files.list(target.getParent())) {
            assertEquals(List.of(target), files.toList());
        }
    }

    @Test
    public void saveAddressBook_readOnlyTarget_preservesFileAndAllowsRetryAfterPermissionRestored() throws Exception {
        Path target = testFolder.resolve("addressbook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(target);
        AddressBook original = getTypicalAddressBook();
        storage.saveAddressBook(original);
        String saved = Files.readString(target);
        DosFileAttributeView attributes = Files.getFileAttributeView(target, DosFileAttributeView.class);
        assumeTrue(attributes != null, "Requires DOS read-only file attributes");
        AddressBook edited = new AddressBook(original);
        edited.removePerson(ALICE);

        attributes.setReadOnly(true);
        try {
            assertTrue(attributes.readAttributes().isReadOnly());
            assertThrows(IOException.class, () -> storage.saveAddressBook(edited));
            assertEquals(saved, Files.readString(target));
            try (Stream<Path> files = Files.list(testFolder)) {
                assertEquals(List.of(target), files.toList());
            }
        } finally {
            attributes.setReadOnly(false);
        }

        storage.saveAddressBook(edited);
        assertEquals(edited, storage.readAddressBook().orElseThrow());
    }

    private void assertFailedSavePreservesOriginal(JsonAddressBookStorage failingStorage) throws Exception {
        Path target = failingStorage.getAddressBookFilePath();
        AddressBook original = getTypicalAddressBookWithMeetings();
        new JsonAddressBookStorage(target).saveAddressBook(original);
        String saved = Files.readString(target);
        AddressBook edited = new AddressBook(original);
        edited.removePerson(ALICE);

        assertThrows(IOException.class, () -> failingStorage.saveAddressBook(edited));

        assertEquals(saved, Files.readString(target));
        assertEquals(original, new JsonAddressBookStorage(target).readAddressBook().orElseThrow());
        try (Stream<Path> files = Files.list(testFolder)) {
            assertEquals(List.of(target), files.toList());
        }
    }
}
