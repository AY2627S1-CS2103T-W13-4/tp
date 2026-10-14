package seedu.address.model.meeting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class MeetingNameTest {

    @Test
    public void constructor_validName_success() {
        assertEquals("Client Meeting", new MeetingName("Client Meeting").toString());
        assertEquals("Review", new MeetingName("Review").toString());
        assertEquals("Client's Meeting", new MeetingName("Client's Meeting").toString());
        assertEquals("Follow-up", new MeetingName("Follow-up").toString());
    }

    @Test
    public void constructor_normalisesSpaces_success() {
        assertEquals("Client Meeting", new MeetingName("  Client   Meeting  ").toString());
        assertEquals("Client Meeting", new MeetingName("Client\tMeeting").toString());
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new MeetingName(""));
        assertThrows(IllegalArgumentException.class, () -> new MeetingName("   "));
        assertThrows(IllegalArgumentException.class, () -> new MeetingName("12345"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingName("Meeting123"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingName("Meeting!"));
        assertThrows(IllegalArgumentException.class, () -> new MeetingName("Meeting@Client"));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MeetingName(null));
    }

    @Test
    public void isValidName() {
        assertTrue(MeetingName.isValidName("Client Meeting"));
        assertTrue(MeetingName.isValidName("Review"));
        assertTrue(MeetingName.isValidName("Client's Meeting"));
        assertTrue(MeetingName.isValidName("Follow-up"));
        assertTrue(MeetingName.isValidName("  Client   Meeting  "));

        assertFalse(MeetingName.isValidName(""));
        assertFalse(MeetingName.isValidName("   "));
        assertFalse(MeetingName.isValidName("12345"));
        assertFalse(MeetingName.isValidName("Meeting123"));
        assertFalse(MeetingName.isValidName("Meeting!"));
    }

    @Test
    public void isValidName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> MeetingName.isValidName(null));
    }

    @Test
    public void equals() {
        MeetingName name = new MeetingName("Client Meeting");

        assertTrue(name.equals(name));
        assertTrue(name.equals(new MeetingName("Client Meeting")));
        assertTrue(name.equals(new MeetingName("CLIENT MEETING")));
        assertFalse(name.equals(null));
        assertFalse(name.equals("Client Meeting"));
        assertFalse(name.equals(new MeetingName("Other Meeting")));
    }

    @Test
    public void hashCode_caseInsensitiveNames_returnsSameHashCode() {
        assertEquals(new MeetingName("Client Meeting").hashCode(),
                new MeetingName("CLIENT MEETING").hashCode());
    }
}
