package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("91")); // less than 3 numbers
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("91a 21534")); // unsupported characters

        // valid phone numbers
        assertTrue(Phone.isValidPhone("911")); // exactly 3 numbers
        assertTrue(Phone.isValidPhone("93121534"));
        assertTrue(Phone.isValidPhone("124293842033123")); // long phone numbers
        assertTrue(Phone.isValidPhone("+65 (9123) 4567"));
        assertTrue(Phone.isValidPhone("+65-9123-4567"));
    }

    @Test
    public void isValidPhone_allowsFormattingCharacters() {
        // plus sign
        assertTrue(Phone.isValidPhone("+6591234567"));
        // hyphens
        assertTrue(Phone.isValidPhone("9123-4567"));
        // parentheses
        assertTrue(Phone.isValidPhone("(9123)4567"));
        // spaces within the phone number
        assertTrue(Phone.isValidPhone("9123 4567"));
        // multiple formatting characters
        assertTrue(Phone.isValidPhone("+65 (9123) 4567"));
    }

    @Test
    public void isValidPhone_requiresAtLeastThreeDigits() {
        // formatting characters without digits
        assertFalse(Phone.isValidPhone("+() -"));
        // fewer than 3 digits
        assertFalse(Phone.isValidPhone("+12"));
        // formatting characters do not count as digits
        assertFalse(Phone.isValidPhone("()- 12"));
        // exactly 3 digits with formatting characters
        assertTrue(Phone.isValidPhone("+1-23"));
    }

    @Test
    public void isValidPhone_rejectsUnsupportedCharacters() {
        assertFalse(Phone.isValidPhone("+65.91234567")); // full stop
        assertFalse(Phone.isValidPhone("9123/4567")); // slash
        assertFalse(Phone.isValidPhone("9123a4567")); // alphabetic character
    }

    @Test
    public void equals() {
        Phone phone = new Phone("999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("995")));
    }
}
