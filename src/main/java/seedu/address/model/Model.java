package seedu.address.model;

import java.time.LocalDateTime;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /** {@code Predicate} that matches every meeting. */
    Predicate<Meeting> PREDICATE_SHOW_ALL_MEETINGS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Returns true if the given meeting exists in the address book.
     */
    boolean hasMeeting(Meeting meeting);

    /**
     * Adds the given meeting.
     * {@code meeting} must not already exist in the address book.
     * Clears the meeting filter so the new meeting is visible.
     */
    void addMeeting(Meeting meeting);

    /**
     * Replaces {@code target} with {@code editedMeeting}.
     * {@code target} must exist and {@code editedMeeting} must not duplicate another meeting.
     */
    void setMeeting(Meeting target, Meeting editedMeeting);

    /**
     * Deletes the given meeting.
     * The meeting must exist in the address book.
     */
    void deleteMeeting(Meeting meeting);

    /** Returns an unmodifiable view of the filtered meeting list in its displayed order. */
    ObservableList<Meeting> getMeetingList();

    /**
     * Updates the meeting filter without modifying stored meetings or their display ordering rules.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredMeetingList(Predicate<Meeting> predicate);

    /**
     * Orders meetings with unfinished meetings first, then finished meetings, using {@code currentTime}.
     * A meeting is finished when its end time on its date is at or before {@code currentTime}.
     * Ongoing meetings belong to the unfinished group.
     * Each group is ordered by date and start time, then by end time and case-insensitive name to break ties.
     * Changes only the displayed order, leaving the stored meeting order unchanged.
     * The time used for grouping is refreshed on each call; it does not advance automatically.
     *
     * @param currentTime The local date and time used to determine which meetings have finished.
     * @throws NullPointerException If {@code currentTime} is null.
     */
    void updateMeetingListOrder(LocalDateTime currentTime);

}
