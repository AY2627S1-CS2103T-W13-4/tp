package seedu.address.model.meeting;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Represents the name of a meeting.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class MeetingName {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid name. Names should only contain alphabetic characters, spaces, hyphens or apostrophes.";

    private static final String VALIDATION_REGEX = "[A-Za-z' -]+";

    public final String name;

    /**
     * Creates a meeting name. Leading/trailing spaces are removed and
     * repeated spaces are collapsed into one.
     *
     * @param name A valid meeting name.
     */
    public MeetingName(String name) {
        requireNonNull(name);
        String normalised = normalise(name);
        if (!isValidName(normalised)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.name = normalised;
    }

    /**
     * Returns true if the given string is a valid meeting name.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        String normalised = normalise(test);
        return normalised.matches(VALIDATION_REGEX) && normalised.matches(".*[A-Za-z].*");
    }

    private static String normalise(String s) {
        return s.trim().replaceAll("\\s+", " ");
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
