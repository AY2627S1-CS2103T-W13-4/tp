package seedu.address.model.meeting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;

/**
 * Tests the {@code Meeting} class.
 */
public class MeetingTest {

    private static final MeetingName VALID_NAME = new MeetingName("Client Meeting");
    private static final MeetingDate VALID_DATE = new MeetingDate("15/10/2026");
    private static final MeetingTime VALID_START_TIME = new MeetingTime("0900");
    private static final MeetingTime VALID_END_TIME = new MeetingTime("1000");

    /**
     * Creates a meeting with valid details.
     *
     * @return a meeting with valid details
     */
    private Meeting createMeeting() {
        return new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME);
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Meeting(null, VALID_DATE, VALID_START_TIME, VALID_END_TIME));
        assertThrows(NullPointerException.class, () -> new Meeting(VALID_NAME, null, VALID_START_TIME, VALID_END_TIME));
        assertThrows(NullPointerException.class, () -> new Meeting(VALID_NAME, VALID_DATE, null, VALID_END_TIME));
        assertThrows(NullPointerException.class, () -> new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, null));
    }

    @Test
    public void getName_returnsName() {
        assertEquals(VALID_NAME, createMeeting().getName());
    }

    @Test
    public void getDate_returnsDate() {
        assertEquals(VALID_DATE, createMeeting().getDate());
    }

    @Test
    public void getStartTime_returnsStartTime() {
        assertEquals(VALID_START_TIME, createMeeting().getStartTime());
    }

    @Test
    public void getEndTime_returnsEndTime() {
        assertEquals(VALID_END_TIME, createMeeting().getEndTime());
    }

    @Test
    public void constructor_withoutTags_createsEmptyTagSet() {
        assertTrue(createMeeting().getTags().isEmpty());
    }

    @Test
    public void constructor_tags_defensivelyCopiesSet() {
        Set<Tag> tags = new HashSet<>(Set.of(new Tag("urgent")));
        Meeting meeting = new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME, tags);
        tags.clear();
        assertEquals(Set.of(new Tag("urgent")), meeting.getTags());
    }

    @Test
    public void constructor_nullTags_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME, null));
        Set<Tag> tags = new HashSet<>();
        tags.add(null);
        assertThrows(NullPointerException.class, () ->
                new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME, tags));
    }

    @Test
    public void getTags_cannotModifySet() {
        Meeting meeting = new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME,
                Set.of(new Tag("urgent")));
        assertThrows(UnsupportedOperationException.class, () -> meeting.getTags().clear());
    }

    @Test
    public void isSameMeeting_ignoresTagsButComparesIdentityFields() {
        Meeting meeting = createMeeting();
        Meeting tagged = new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME,
                Set.of(new Tag("urgent")));
        assertTrue(meeting.isSameMeeting(meeting));
        assertTrue(meeting.isSameMeeting(tagged));
        assertTrue(tagged.isSameMeeting(meeting));
        assertFalse(meeting.isSameMeeting(null));
        assertFalse(meeting.isSameMeeting(new Meeting(new MeetingName("Other Meeting"), VALID_DATE,
                VALID_START_TIME, VALID_END_TIME)));
        assertFalse(meeting.isSameMeeting(new Meeting(VALID_NAME, new MeetingDate("16/10/2026"),
                VALID_START_TIME, VALID_END_TIME)));
        assertFalse(meeting.isSameMeeting(new Meeting(VALID_NAME, VALID_DATE,
                new MeetingTime("0930"), VALID_END_TIME)));
        assertFalse(meeting.isSameMeeting(new Meeting(VALID_NAME, VALID_DATE,
                VALID_START_TIME, new MeetingTime("1030"))));
    }

    @Test
    public void equalsAndHashCode_includeTagsRegardlessOfOrder() {
        Meeting first = new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME,
                Set.of(new Tag("urgent"), new Tag("work")));
        Meeting second = new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME,
                Set.of(new Tag("work"), new Tag("urgent")));
        Meeting differentTags = new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME,
                Set.of(new Tag("work")));
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(differentTags));
        assertFalse(first.equals(createMeeting()));
    }

    @Test
    public void toString_withTags_displaysSortedTags() {
        Meeting meeting = new Meeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME,
                Set.of(new Tag("work"), new Tag("urgent")));
        assertEquals("Client Meeting 15/10/2026 from 0900 to 1000 Tags: [urgent] [work]", meeting.toString());
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
                new MeetingName("Other Meeting"), VALID_DATE,
                VALID_START_TIME, VALID_END_TIME)));

        // Different date -> returns false
        assertFalse(meeting.equals(new Meeting(
                VALID_NAME, new MeetingDate("16/10/2026"),
                VALID_START_TIME, VALID_END_TIME)));

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

    private Meeting meetingAt(String date, String start, String end) {
        return new Meeting(VALID_NAME, new MeetingDate(date), new MeetingTime(start), new MeetingTime(end));
    }

    @Test
    public void overlapsWith() {
        Meeting base = meetingAt("15/10/2026", "0900", "1000");

        // partial, contained, containing, identical
        assertTrue(base.overlapsWith(meetingAt("15/10/2026", "0930", "1030")));
        assertTrue(base.overlapsWith(meetingAt("15/10/2026", "0830", "0930")));
        assertTrue(base.overlapsWith(meetingAt("15/10/2026", "0915", "0945")));
        assertTrue(base.overlapsWith(meetingAt("15/10/2026", "0800", "1100")));
        assertTrue(base.overlapsWith(base));

        // touching boundaries, disjoint, different date
        assertFalse(base.overlapsWith(meetingAt("15/10/2026", "1000", "1100")));
        assertFalse(base.overlapsWith(meetingAt("15/10/2026", "0800", "0900")));
        assertFalse(base.overlapsWith(meetingAt("15/10/2026", "1100", "1200")));
        assertFalse(base.overlapsWith(meetingAt("16/10/2026", "0900", "1000")));

        assertThrows(NullPointerException.class, () -> base.overlapsWith(null));
    }

    @Test
    public void isValidTimeRange() {
        assertTrue(Meeting.isValidTimeRange(new MeetingTime("0900"), new MeetingTime("0901")));
        assertFalse(Meeting.isValidTimeRange(new MeetingTime("0900"), new MeetingTime("0900")));
        assertFalse(Meeting.isValidTimeRange(new MeetingTime("1000"), new MeetingTime("0900")));
        assertThrows(NullPointerException.class, () -> Meeting.isValidTimeRange(null, new MeetingTime("0900")));
    }
}
