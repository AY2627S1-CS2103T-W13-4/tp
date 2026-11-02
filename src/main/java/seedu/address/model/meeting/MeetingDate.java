package seedu.address.model.meeting;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;

/**
 * Represents the date of a meeting.
 */
public class MeetingDate {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid date. Please use DD/MM/YYYY with a four-digit year from 0001 to 9999.";

    private static final String VALIDATION_REGEX = "[0-9]{2}/[0-9]{2}/[0-9]{4}";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private final LocalDate date;

    /**
     * Creates a meeting date from the given date string.
     *
     * @param date The date in DD/MM/YYYY format with a year from 0001 to 9999.
     * @throws IllegalArgumentException if the date is not valid.
     */
    public MeetingDate(String date) {
        requireNonNull(date);
        if (!isValidDate(date)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.date = LocalDate.parse(date.trim(), FORMATTER);
    }

    /**
     * Returns true if the given string is a valid calendar date in DD/MM/YYYY format
     * with a four-digit year from 0001 to 9999.
     */
    public static boolean isValidDate(String test) {
        requireNonNull(test);
        String trimmedDate = test.trim();
        if (!trimmedDate.matches(VALIDATION_REGEX)) {
            return false;
        }
        try {
            return LocalDate.parse(trimmedDate, FORMATTER).getYear() > 0;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof MeetingDate)) {
            return false;
        }

        MeetingDate otherDate = (MeetingDate) other;
        return date.equals(otherDate.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return date.format(FORMATTER);
    }
}
