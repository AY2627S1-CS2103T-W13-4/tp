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
    public void constructor_printableAsciiNames_success() {
        String[] names = {"Q4 Review", "1-on-1", "Budget 2027", "12345", "Design/Review", "/",
            "Meeting!", "Meeting@Client", "R&D ($100): #1_~", "\"Q4 Review\"", "C:\\Planning"};
        for (String name : names) {
            assertTrue(MeetingName.isValidName(name));
            assertEquals(name, new MeetingName(name).toString());
        }
    }

    @Test
    public void constructor_normalisesSpaces_success() {
        assertEquals("Client Meeting", new MeetingName("  Client   Meeting  ").toString());
        assertEquals("Client Meeting", new MeetingName("Client\tMeeting").toString());
        assertEquals("Q4 Review/Planning", new MeetingName(" \tQ4  \t Review/Planning\t ").toString());
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new MeetingName(""));
        assertThrows(IllegalArgumentException.class, () -> new MeetingName("   "));
        assertThrows(IllegalArgumentException.class, () -> new MeetingName("\t \t"));
    }

    @Test
    public void constructor_nonPrintableOrNonAsciiName_throwsIllegalArgumentException() {
        String[] names = {"Caf\u00e9", "Review\u00a0Meeting", "\u001bReview", "Review\u0000",
            "Review\nMeeting", "Review\rMeeting", "Review\fMeeting", "Review\u000bMeeting", "Review\u007f"};
        for (String name : names) {
            assertFalse(MeetingName.isValidName(name));
            assertThrows(IllegalArgumentException.class, () -> new MeetingName(name));
        }
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
        assertFalse(MeetingName.isValidName("\t \t"));
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

    @Test
    public void equalsAndHashCode_asciiNamesWithDifferentCaseAndSpacing_match() {
        MeetingName name = new MeetingName("Q4 Review/Planning!");
        MeetingName equivalent = new MeetingName(" q4  REVIEW/planning! ");
        assertEquals(name, equivalent);
        assertEquals(name.hashCode(), equivalent.hashCode());
    }
}
