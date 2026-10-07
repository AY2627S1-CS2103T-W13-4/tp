package seedu.address.model.meeting;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;

/**
 * Represents the date of a meeting.
 */
public class MeetingDate {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private final LocalDate date;

    /**
     * Creates a meeting date from the given date string.
     *
     * @param date The date in DD/MM/YYYY format.
     */
    public MeetingDate(String date) {
        try {
            this.date = LocalDate.parse(date.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date. Please use DD/MM/YYYY.");
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
