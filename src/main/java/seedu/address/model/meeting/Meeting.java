package seedu.address.model.meeting;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

/**
 * Represents a meeting in PingBook.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Meeting {

    private final MeetingName name;
    private final MeetingDate date;
    private final MeetingTime startTime;
    private final MeetingTime endTime;

    /**
     * Creates a meeting with the given name, date, start time and end time.
     * Every field must be present and not null.
     */
    public Meeting(MeetingName name, MeetingDate date, MeetingTime startTime, MeetingTime endTime) {
        requireAllNonNull(name, date, startTime, endTime);
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public MeetingName getName() {
        return name;
    }

    public MeetingDate getDate() {
        return date;
    }

    public MeetingTime getStartTime() {
        return startTime;
    }

    public MeetingTime getEndTime() {
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
