package seedu.address.model.meeting;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of meetings that maintains the meetings in PingBook.
 */
public class MeetingList implements Iterable<Meeting> {

    private final ObservableList<Meeting> internalList = FXCollections.observableArrayList();

    /**
     * Returns true if the list contains the given meeting.
     *
     * @param meeting The meeting to check.
     * @return True if the meeting exists in the list.
     */
    public boolean contains(Meeting meeting) {
        return internalList.contains(meeting);
    }

    /**
     * Adds a meeting to the list.
     *
     * @param meeting The meeting to add.
     */
    public void add(Meeting meeting) {
        internalList.add(meeting);
    }

    /**
     * Removes the given meeting from the list.
     *
     * @param meeting The meeting to remove.
     */
    public void remove(Meeting meeting) {
        internalList.remove(meeting);
    }

    /**
     * Returns the meeting at the specified index.
     *
     * @param index The index of the meeting.
     * @return The meeting at the specified index.
     */
    public Meeting get(int index) {
        return internalList.get(index);
    }

    /**
     * Replaces the contents of the meeting list with the given list.
     *
     * @param meetings The meetings to replace the current list with.
     */
    public void setMeetings(List<Meeting> meetings) {
        internalList.setAll(meetings);
    }

    /**
     * Returns the number of meetings in the list.
     *
     * @return The number of meetings.
     */
    public int size() {
        return internalList.size();
    }

    /**
     * Returns an unmodifiable view of the meeting list.
     *
     * @return An unmodifiable observable list of meetings.
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
