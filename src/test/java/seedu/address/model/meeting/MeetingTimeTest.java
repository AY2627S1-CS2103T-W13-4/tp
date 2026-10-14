package seedu.address.model.meeting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

public class MeetingTimeTest {

    @Test
    public void constructor_validTime_success() {
        MeetingTime time = new MeetingTime("0900");
        assertEquals(LocalTime.of(9, 0), time.getTime());
        assertEquals("0900", time.toString());
    }

    @Test
    public void constructor_boundaryTimes_success() {
        assertEquals("0000", new MeetingTime("0000").toString());
        assertEquals("1200", new MeetingTime("1200").toString());
        assertEquals("2359", new MeetingTime("2359").toString());
    }

    @Test
    public void constructor_timeWithSpaces_success() {
        assertEquals("0900", new MeetingTime(" 0900 ").toString());
    }

    @Test
    public void constructor_invalidTime_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new MeetingTime(""));
        assertThrows(IllegalArgumentException.class, () -> new MeetingTime("2400"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingTime("1260"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingTime("2500"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingTime("900"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingTime("09:00"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingTime("abcd"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingTime("12 00"));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MeetingTime(null));
    }

    @Test
    public void isValidTime() {
        assertTrue(MeetingTime.isValidTime("0000"));
        assertTrue(MeetingTime.isValidTime("0900"));
        assertTrue(MeetingTime.isValidTime("1200"));
        assertTrue(MeetingTime.isValidTime("2359"));
        assertTrue(MeetingTime.isValidTime(" 0900 "));

        assertFalse(MeetingTime.isValidTime(""));
        assertFalse(MeetingTime.isValidTime("2400"));
        assertFalse(MeetingTime.isValidTime("1260"));
        assertFalse(MeetingTime.isValidTime("900"));
        assertFalse(MeetingTime.isValidTime("09:00"));
        assertFalse(MeetingTime.isValidTime("abcd"));
        assertFalse(MeetingTime.isValidTime("12 00"));
    }

    @Test
    public void isValidTime_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> MeetingTime.isValidTime(null));
    }

    @Test
    public void equals() {
        MeetingTime time = new MeetingTime("0900");

        assertTrue(time.equals(time));
        assertTrue(time.equals(new MeetingTime("0900")));
        assertFalse(time.equals(null));
        assertFalse(time.equals("0900"));
        assertFalse(time.equals(new MeetingTime("1000")));
    }

    @Test
    public void hashCode_sameTime_returnsSameHashCode() {
        assertEquals(new MeetingTime("0900").hashCode(),
                new MeetingTime("0900").hashCode());
    }
}
