package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingContainsTagsPredicate;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void setMeeting_existingMeeting_replacesMeeting() {
        Meeting original = meeting("Project Review", "08/10/2026", "1400", "1500");
        Meeting edited = meeting("Updated Review", "09/10/2026", "1500", "1600");
        modelManager.addMeeting(original);

        modelManager.setMeeting(original, edited);

        assertEquals(List.of(edited), modelManager.getMeetingList());
    }

    @Test
    public void updateFilteredMeetingList_keepsStorageAndContactsUnchanged() {
        Meeting hidden = meeting("Hidden", "25/10/2026", "0900", "1000");
        Meeting visible = new Meeting(hidden.getName(), hidden.getDate(), new MeetingTime("1400"),
                new MeetingTime("1500"), Set.of(new Tag("urgent")));
        modelManager.addMeeting(hidden);
        modelManager.addMeeting(visible);
        modelManager.addPerson(ALICE);
        AddressBook original = new AddressBook(modelManager.getAddressBook());

        modelManager.updateFilteredMeetingList(new MeetingContainsTagsPredicate(Set.of(new Tag("urgent"))));

        assertEquals(List.of(visible), modelManager.getMeetingList());
        assertEquals(original, modelManager.getAddressBook());
        assertEquals(List.of(ALICE), modelManager.getFilteredPersonList());
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getMeetingList().clear());
        assertThrows(NullPointerException.class, () -> modelManager.updateFilteredMeetingList(null));
    }

    @Test
    public void updateFilteredMeetingList_existingViewReflectsTagEditsAndDeletion() {
        Meeting untagged = meeting("Review", "25/10/2026", "1400", "1500");
        Meeting tagged = new Meeting(untagged.getName(), untagged.getDate(), untagged.getStartTime(),
                untagged.getEndTime(), Set.of(new Tag("urgent")));
        modelManager.addMeeting(untagged);
        List<Meeting> displayed = modelManager.getMeetingList();
        modelManager.updateFilteredMeetingList(new MeetingContainsTagsPredicate(Set.of(new Tag("urgent"))));
        assertTrue(displayed.isEmpty());

        modelManager.setMeeting(untagged, tagged);
        assertEquals(List.of(tagged), displayed);
        modelManager.setMeeting(tagged, untagged);
        assertTrue(displayed.isEmpty());
        modelManager.setMeeting(untagged, tagged);
        modelManager.deleteMeeting(tagged);
        assertTrue(displayed.isEmpty());
    }

    @Test
    public void addMeeting_clearsFilterSoNewMeetingIsVisible() {
        Meeting first = meeting("First", "25/10/2026", "1400", "1500");
        Meeting second = meeting("Second", "26/10/2026", "1400", "1500");
        modelManager.addMeeting(first);
        modelManager.updateFilteredMeetingList(meeting -> false);
        modelManager.addMeeting(second);
        assertEquals(List.of(first, second), modelManager.getMeetingList());
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }

    private Meeting meeting(String name, String date, String startTime, String endTime) {
        return new Meeting(new MeetingName(name), new MeetingDate(date),
                new MeetingTime(startTime), new MeetingTime(endTime));
    }

    @Test
    public void getOverlappingMeetings_returnsOnlyOverlapsAndHonoursExclusion() {
        Meeting existing = new Meeting(new MeetingName("Existing"), new MeetingDate("15/10/2026"),
                new MeetingTime("0900"), new MeetingTime("1000"));
        Meeting other = new Meeting(new MeetingName("Other"), new MeetingDate("15/10/2026"),
                new MeetingTime("1000"), new MeetingTime("1100"));
        Meeting candidate = new Meeting(new MeetingName("Candidate"), new MeetingDate("15/10/2026"),
                new MeetingTime("0930"), new MeetingTime("1030"));
        modelManager.addMeeting(existing);
        modelManager.addMeeting(other);

        assertEquals(List.of(existing, other), modelManager.getOverlappingMeetings(candidate, null));
        assertEquals(List.of(other), modelManager.getOverlappingMeetings(candidate, existing));
        assertThrows(NullPointerException.class, () -> modelManager.getOverlappingMeetings(null, null));
    }
}
