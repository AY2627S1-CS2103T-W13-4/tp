package seedu.address.model.meeting;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.meeting.exceptions.DuplicateMeetingException;
import seedu.address.model.meeting.exceptions.MeetingNotFoundException;

/**
 * A list of meetings that does not allow duplicates.
 * Two meetings are duplicates if they are equal (same name, date, start and end time).
 */
public class MeetingList implements Iterable<Meeting> {

    private final ObservableList<Meeting> internalList = FXCollections.observableArrayList();

    /**
     * Returns true if the list contains the given meeting.
     */
    public boolean contains(Meeting meeting) {
        requireNonNull(meeting);
        return internalList.contains(meeting);
    }

    /**
     * Adds a meeting to the list.
     *
     * @throws DuplicateMeetingException if the meeting already exists.
     */
    public void add(Meeting meeting) {
        requireNonNull(meeting);
        if (contains(meeting)) {
            throw new DuplicateMeetingException();
        }
        internalList.add(meeting);
    }

    /**
     * Removes the given meeting from the list.
     *
     * @throws MeetingNotFoundException if the meeting is not in the list.
     */
    public void remove(Meeting meeting) {
        requireNonNull(meeting);
        if (!internalList.remove(meeting)) {
            throw new MeetingNotFoundException();
        }
    }

    /**
     * Returns the meeting at the specified index.
     */
    public Meeting get(int index) {
        return internalList.get(index);
    }

    /**
     * Replaces the contents of this list with {@code meetings}.
     *
     * @throws DuplicateMeetingException if {@code meetings} contains duplicates.
     */
    public void setMeetings(List<Meeting> meetings) {
        requireNonNull(meetings);
        if (new HashSet<>(meetings).size() != meetings.size()) {
            throw new DuplicateMeetingException();
        }
        internalList.setAll(meetings);
    }

    /**
     * Returns the number of meetings in the list.
     */
    public int size() {
        return internalList.size();
    }

    /**
     * Returns an unmodifiable view of the meeting list.
     */
    public ObservableList<Meeting> asUnmodifiableObservableList() {
        return FXCollections.unmodifiableObservableList(internalList);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof MeetingList otherMeetingList)) {
            return false;
        }

        return internalList.equals(otherMeetingList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public Iterator<Meeting> iterator() {
        return internalList.iterator();
    }
}
