package seedu.address.model.meeting;

import static java.util.Objects.requireNonNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;

/**
 * Represents the time of a meeting.
 */
public class MeetingTime {

    public static final String MESSAGE_CONSTRAINTS = "Invalid time. Please use HHMM in 24-hour format.";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("HHmm")
                .withResolverStyle(ResolverStyle.STRICT);

    private final LocalTime time;

    /**
     * Creates a meeting time from the given time string.
     *
     * @param time The time in HHMM format.
     * @throws IllegalArgumentException if the time is not valid.
     */
    public MeetingTime(String time) {
        requireNonNull(time);
        if (!isValidTime(time)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }
        this.time = LocalTime.parse(time.trim(), FORMATTER);
    }

    /**
     * Returns true if the given string is a valid time in HHMM 24-hour format.
     */
    public static boolean isValidTime(String test) {
        requireNonNull(test);
        String trimmed = test.trim();
        if (!trimmed.matches("\\d{4}")) {
            return false;
        }
        try {
            LocalTime.parse(trimmed, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof MeetingTime)) {
            return false;
        }

        MeetingTime otherTime = (MeetingTime) other;
        return time.equals(otherTime.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time);
    }

    @Override
    public String toString() {
        return time.format(FORMATTER);
    }
}
