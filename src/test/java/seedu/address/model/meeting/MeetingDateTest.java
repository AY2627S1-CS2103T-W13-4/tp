package seedu.address.model.meeting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class MeetingDateTest {

    @Test
    public void constructor_validDate_success() {
        MeetingDate date = new MeetingDate("15/10/2026");
        assertEquals(LocalDate.of(2026, 10, 15), date.getDate());
        assertEquals("15/10/2026", date.toString());
    }

    @Test
    public void constructor_dateWithSpaces_success() {
        MeetingDate date = new MeetingDate(" 15/10/2026 ");
        assertEquals("15/10/2026", date.toString());
    }

    @Test
    public void constructor_leapYear_success() {
        assertEquals("29/02/2024", new MeetingDate("29/02/2024").toString());
    }

    @Test
    public void constructor_invalidDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new MeetingDate(""));
        assertThrows(IllegalArgumentException.class, () -> new MeetingDate("31/04/2026"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingDate("29/02/2025"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingDate("32/01/2026"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingDate("15/13/2026"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingDate("15-10-2026"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingDate("1/1/2026"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingDate("abc"));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MeetingDate(null));
    }

    @Test
    public void isValidDate() {
        assertTrue(MeetingDate.isValidDate("15/10/2026"));
        assertTrue(MeetingDate.isValidDate("29/02/2024"));
        assertTrue(MeetingDate.isValidDate(" 15/10/2026 "));

        assertFalse(MeetingDate.isValidDate(""));
        assertFalse(MeetingDate.isValidDate("31/04/2026"));
        assertFalse(MeetingDate.isValidDate("29/02/2025"));
        assertFalse(MeetingDate.isValidDate("15/13/2026"));
        assertFalse(MeetingDate.isValidDate("15-10-2026"));
        assertFalse(MeetingDate.isValidDate("1/1/2026"));
    }

    @Test
    public void isValidDate_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> MeetingDate.isValidDate(null));
    }

    @Test
    public void equals() {
        MeetingDate date = new MeetingDate("15/10/2026");

        assertTrue(date.equals(date));
        assertTrue(date.equals(new MeetingDate("15/10/2026")));
        assertFalse(date.equals(null));
        assertFalse(date.equals("15/10/2026"));
        assertFalse(date.equals(new MeetingDate("16/10/2026")));
    }

    @Test
    public void hashCode_sameDate_returnsSameHashCode() {
        assertEquals(new MeetingDate("15/10/2026").hashCode(),
                new MeetingDate("15/10/2026").hashCode());
    }
}
