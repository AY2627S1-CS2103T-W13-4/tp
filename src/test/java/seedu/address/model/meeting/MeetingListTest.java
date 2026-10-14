package seedu.address.model.meeting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.model.meeting.exceptions.DuplicateMeetingException;
import seedu.address.model.meeting.exceptions.MeetingNotFoundException;

public class MeetingListTest {

    private Meeting createMeeting(String name, String date, String start, String end) {
        return new Meeting(new MeetingName(name), new MeetingDate(date),
                new MeetingTime(start), new MeetingTime(end));
    }

    @Test
    public void add_validMeeting_success() {
        MeetingList list = new MeetingList();
        Meeting meeting = createMeeting("Client Meeting", "15/10/2026", "0900", "1000");

        list.add(meeting);

        assertEquals(1, list.size());
        assertTrue(list.contains(meeting));
        assertEquals(meeting, list.get(0));
    }

    @Test
    public void add_duplicateMeeting_throwsDuplicateMeetingException() {
        MeetingList list = new MeetingList();
        Meeting meeting = createMeeting("Client Meeting", "15/10/2026", "0900", "1000");
        list.add(meeting);

        assertThrows(DuplicateMeetingException.class, () -> list.add(meeting));
        assertEquals(1, list.size());
    }

    @Test
    public void add_null_throwsNullPointerException() {
        MeetingList list = new MeetingList();

        assertThrows(NullPointerException.class, () -> list.add(null));
    }

    @Test
    public void contains_null_throwsNullPointerException() {
        MeetingList list = new MeetingList();

        assertThrows(NullPointerException.class, () -> list.contains(null));
    }

    @Test
    public void remove_existingMeeting_success() {
        MeetingList list = new MeetingList();
        Meeting meeting = createMeeting("Client Meeting", "15/10/2026", "0900", "1000");
        list.add(meeting);

        list.remove(meeting);

        assertEquals(0, list.size());
        assertFalse(list.contains(meeting));
    }

    @Test
    public void remove_missingMeeting_throwsMeetingNotFoundException() {
        MeetingList list = new MeetingList();
        Meeting meeting = createMeeting("Client Meeting", "15/10/2026", "0900", "1000");

        assertThrows(MeetingNotFoundException.class, () -> list.remove(meeting));
    }

    @Test
    public void remove_null_throwsNullPointerException() {
        MeetingList list = new MeetingList();

        assertThrows(NullPointerException.class, () -> list.remove(null));
    }

    @Test
    public void get_validIndex_returnsMeeting() {
        MeetingList list = new MeetingList();
        Meeting meeting = createMeeting("Client Meeting", "15/10/2026", "0900", "1000");
        list.add(meeting);

        assertEquals(meeting, list.get(0));
    }

    @Test
    public void get_invalidIndex_throwsIndexOutOfBoundsException() {
        MeetingList list = new MeetingList();

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    public void setMeetings_validList_success() {
        MeetingList list = new MeetingList();
        Meeting first = createMeeting("Client Meeting", "15/10/2026", "0900", "1000");
        Meeting second = createMeeting("Project Review", "16/10/2026", "1100", "1200");

        list.setMeetings(Arrays.asList(first, second));

        assertEquals(2, list.size());
        assertEquals(first, list.get(0));
        assertEquals(second, list.get(1));
    }

    @Test
    public void setMeetings_duplicateMeetings_throwsDuplicateMeetingException() {
        MeetingList list = new MeetingList();
        Meeting meeting = createMeeting("Client Meeting", "15/10/2026", "0900", "1000");

        assertThrows(DuplicateMeetingException.class,
                () -> list.setMeetings(Arrays.asList(meeting, meeting)));
        assertEquals(0, list.size());
    }

    @Test
    public void setMeetings_duplicateMeetingsExistingList_remainsUnchanged() {
        MeetingList list = new MeetingList();
        Meeting original = createMeeting("Original Meeting", "15/10/2026", "0900", "1000");
        Meeting duplicate = createMeeting("Client Meeting", "16/10/2026", "1100", "1200");
        list.add(original);

        assertThrows(DuplicateMeetingException.class,
                () -> list.setMeetings(Arrays.asList(duplicate, duplicate)));

        assertEquals(1, list.size());
        assertEquals(original, list.get(0));
    }

    @Test
    public void setMeetings_null_throwsNullPointerException() {
        MeetingList list = new MeetingList();

        assertThrows(NullPointerException.class, () -> list.setMeetings(null));
    }

    @Test
    public void setMeetings_emptyList_clearsList() {
        MeetingList list = new MeetingList();
        list.add(createMeeting("Client Meeting", "15/10/2026", "0900", "1000"));

        list.setMeetings(Collections.emptyList());

        assertEquals(0, list.size());
    }

    @Test
    public void iterator_iteratesThroughMeetings() {
        MeetingList list = new MeetingList();
        Meeting first = createMeeting("Client Meeting", "15/10/2026", "0900", "1000");
        Meeting second = createMeeting("Project Review", "16/10/2026", "1100", "1200");
        list.add(first);
        list.add(second);

        Iterator<Meeting> iterator = list.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(first, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(second, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void asUnmodifiableObservableList_cannotModifyList() {
        MeetingList list = new MeetingList();
        Meeting meeting = createMeeting("Client Meeting", "15/10/2026", "0900", "1000");
        list.add(meeting);

        ObservableList<Meeting> observableList = list.asUnmodifiableObservableList();

        assertEquals(1, observableList.size());
        assertEquals(meeting, observableList.get(0));
        assertThrows(UnsupportedOperationException.class,
                () -> observableList.add(
                        createMeeting("Project Review", "16/10/2026", "1100", "1200")));
    }

    @Test
    public void equals() {
        Meeting first = createMeeting("Client Meeting", "15/10/2026", "0900", "1000");
        Meeting second = createMeeting("Project Review", "16/10/2026", "1100", "1200");

        MeetingList list = new MeetingList();
        list.add(first);

        MeetingList sameList = new MeetingList();
        sameList.add(first);

        MeetingList differentList = new MeetingList();
        differentList.add(second);

        assertTrue(list.equals(list));
        assertTrue(list.equals(sameList));
        assertFalse(list.equals(null));
        assertFalse(list.equals("list"));
        assertFalse(list.equals(differentList));
    }

    @Test
    public void hashCode_sameMeetings_returnsSameHashCode() {
        Meeting meeting = createMeeting("Client Meeting", "15/10/2026", "0900", "1000");

        MeetingList first = new MeetingList();
        first.add(meeting);

        MeetingList second = new MeetingList();
        second.add(meeting);

        assertEquals(first.hashCode(), second.hashCode());
    }
}
