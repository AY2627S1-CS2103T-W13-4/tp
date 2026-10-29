package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.TypicalMeetings;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final Path TYPICAL_MEETINGS_FILE = TEST_DATA_FOLDER.resolve("typicalMeetingsAddressBook.json");
    private static final Path INVALID_MEETING_FILE = TEST_DATA_FOLDER.resolve("invalidMeetingAddressBook.json");
    private static final Path DUPLICATE_MEETING_FILE = TEST_DATA_FOLDER.resolve("duplicateMeetingAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_typicalMeetingsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_MEETINGS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalMeetingsAddressBook = TypicalMeetings.getTypicalAddressBookWithMeetings();
        assertEquals(addressBookFromFile, typicalMeetingsAddressBook);
        // EP: legacy meeting records without tags must load with empty tag sets.
        assertTrue(addressBookFromFile.getMeetingList().stream().allMatch(meeting -> meeting.getTags().isEmpty()));
    }

    @Test
    public void toModelType_sameMeetingDifferentTags_throwsIllegalValueException() {
        // EP: stored meetings with identical identity fields but different tags are invalid duplicate records.
        JsonAdaptedMeeting first = new JsonAdaptedMeeting("Client Review", "15/10/2026", "0900", "1000",
                List.of(new JsonAdaptedTag("urgent")));
        JsonAdaptedMeeting second = new JsonAdaptedMeeting("Client Review", "15/10/2026", "0900", "1000",
                List.of(new JsonAdaptedTag("work")));
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(), List.of(first, second));
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_MEETING,
                data::toModelType);
    }

    @Test
    public void toModelType_overlappingMeetings_loadsSuccessfully() throws Exception {
        JsonAdaptedMeeting first = new JsonAdaptedMeeting("Client Review", "15/10/2026", "0900", "1000",
                List.of());
        JsonAdaptedMeeting second = new JsonAdaptedMeeting("Sales Sync", "15/10/2026", "0930", "1030",
                List.of());
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(), List.of(first, second));

        assertEquals(2, data.toModelType().getMeetingList().size());
    }

    @Test
    public void toModelType_invalidMeetingFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_MEETING_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateMeetings_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_MEETING_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_MEETING,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_fileWithoutMeetings_loadsEmptyMeetingList() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        assertTrue(addressBookFromFile.getMeetingList().isEmpty());
    }

}
