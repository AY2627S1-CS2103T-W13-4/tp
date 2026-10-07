package seedu.address.model.meeting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Represents a meeting in PingBook.
 */
public class Meeting {

    private final String name;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;

    /**
     * Creates a meeting with the given name, date, start time and end time.
     */
    public Meeting(String name, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Meeting)) {
            return false;
        }

        Meeting otherMeeting = (Meeting) other;
        return name.equals(otherMeeting.name)
                && date.equals(otherMeeting.date)
                && startTime.equals(otherMeeting.startTime)
                && endTime.equals(otherMeeting.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, startTime, endTime);
    }

    @Override
    public String toString() {
        return name + " " + date + " from " + startTime + " to " + endTime;
    }
}
