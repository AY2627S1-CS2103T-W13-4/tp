package seedu.address.model.meeting;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Represents the time of a meeting.
 */
public class MeetingTime {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("HHmm");

    private final LocalTime time;

    /**
     * Creates a meeting time from the given time string.
     *
     * @param time The time in HHMM format.
     */
    public MeetingTime(String time) {
        try {
            if (!time.trim().matches("\\d{4}")) {
                throw new IllegalArgumentException();
            }

            this.time = LocalTime.parse(time.trim(), FORMATTER);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid time. Please use HHMM in 24-hour format.");
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
