package seedu.address.model.meeting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class MeetingTest {

    private static final MeetingName VALID_NAME = new MeetingName("Client Meeting");
    private static final MeetingDate VALID_DATE = new MeetingDate("15/10/2026");
    private static final MeetingTime VALID_START_TIME = new MeetingTime("0900");
    private static final MeetingTime VALID_END_TIME = new MeetingTime("1000");

    private Meeting createMeeting() {
        return new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME);
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Meeting(null, VALID_DATE, VALID_START_TIME, VALID_END_TIME));
        assertThrows(NullPointerException.class,
                () -> new Meeting(VALID_NAME, null, VALID_START_TIME, VALID_END_TIME));
        assertThrows(NullPointerException.class,
                () -> new Meeting(VALID_NAME, VALID_DATE, null, VALID_END_TIME));
        assertThrows(NullPointerException.class,
                () -> new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, null));
    }

    @Test
    public void getName_returnsName() {
        Meeting meeting = createMeeting();
        assertEquals(VALID_NAME, meeting.getName());
    }

    @Test
    public void getDate_returnsDate() {
        Meeting meeting = createMeeting();
        assertEquals(VALID_DATE, meeting.getDate());
    }

    @Test
    public void getStartTime_returnsStartTime() {
        Meeting meeting = createMeeting();
        assertEquals(VALID_START_TIME, meeting.getStartTime());
    }

    @Test
    public void getEndTime_returnsEndTime() {
        Meeting meeting = createMeeting();
        assertEquals(VALID_END_TIME, meeting.getEndTime());
    }

    @Test
    public void equals() {
        Meeting meeting = createMeeting();

        // Same values -> returns true
        assertTrue(meeting.equals(createMeeting()));

        // Same object -> returns true
        assertTrue(meeting.equals(meeting));

        // Null -> returns false
        assertFalse(meeting.equals(null));

        // Different type -> returns false
        assertFalse(meeting.equals(5));

        // Different name -> returns false
        assertFalse(meeting.equals(new Meeting(
                new MeetingName("Other Meeting"), VALID_DATE, VALID_START_TIME, VALID_END_TIME)));

        // Different date -> returns false
        assertFalse(meeting.equals(new Meeting(
                VALID_NAME, new MeetingDate("16/10/2026"), VALID_START_TIME, VALID_END_TIME)));

        // Different start time -> returns false
        assertFalse(meeting.equals(new Meeting(
                VALID_NAME, VALID_DATE, new MeetingTime("0930"), VALID_END_TIME)));

        // Different end time -> returns false
        assertFalse(meeting.equals(new Meeting(
                VALID_NAME, VALID_DATE, VALID_START_TIME, new MeetingTime("1030"))));
    }

    @Test
    public void hashCode_sameValues_returnsSameHashCode() {
        Meeting meeting = createMeeting();
        Meeting meetingCopy = createMeeting();

        assertEquals(meeting.hashCode(), meetingCopy.hashCode());
    }

    @Test
    public void toStringMethod() {
        Meeting meeting = createMeeting();

        String expected = "Client Meeting 15/10/2026 from 0900 to 1000";
        assertEquals(expected, meeting.toString());
    }
}
