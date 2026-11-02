package seedu.address.model.meeting;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Represents the name of a meeting.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class MeetingName {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid name. Meeting names must not be blank and may only contain printable ASCII characters "
            + "(letters, digits, spaces or punctuation). Tabs are treated as spaces.";

    public static final String MESSAGE_KEYWORD_CONSTRAINTS =
            "Invalid keyword. Keywords may only contain printable ASCII characters.";

    private static final String VALIDATION_REGEX = "[A-Za-z' -]+";
    private static final String KEYWORD_VALIDATION_REGEX = "[\\x20-\\x7E]+";

    public final String name;

    /**
     * Creates a meeting name. Leading/trailing spaces are removed and
     * repeated spaces and tabs are collapsed into one space.
     *
     * @param name A valid meeting name.
     */
    public MeetingName(String name) {
        requireNonNull(name);
        if (!isValidName(name)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.name = normalise(name);
    }

    /**
     * Returns true if the given string is a valid meeting name.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        return test.matches(VALIDATION_REGEX) && !normalise(test).isEmpty();
    }

    private static String normalise(String s) {
        return s.trim().replaceAll("\\s+", " ");
    }

    /**
     * Returns true if the given keyword contains only characters allowed in a meeting name.
     */
    public static boolean isValidKeyword(String keyword) {
        return keyword.matches(KEYWORD_VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof MeetingName)) {
            return false;
        }

        return name.equalsIgnoreCase(((MeetingName) other).name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
