package seedu.address.model.meeting;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.tag.Tag;

/**
 * Represents a meeting in PingBook.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Meeting {

    private final MeetingName name;
    private final MeetingDate date;
    private final MeetingTime startTime;
    private final MeetingTime endTime;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Creates a meeting with the given name, date, start time and end time.
     * Every field must be present and not null.
     */
    public Meeting(MeetingName name, MeetingDate date, MeetingTime startTime, MeetingTime endTime) {
        this(name, date, startTime, endTime, Collections.emptySet());
    }

    /**
     * Creates a meeting with the given details and tags.
     * Every field and tag must be present and not null.
     */
    public Meeting(MeetingName name, MeetingDate date, MeetingTime startTime, MeetingTime endTime, Set<Tag> tags) {
        requireAllNonNull(name, date, startTime, endTime, tags);
        requireAllNonNull(tags);
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tags.addAll(tags);
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

    /**
     * Returns an unmodifiable view of this meeting's tags.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both meetings have the same name, date and times, regardless of tags.
     * This defines meeting identity for duplicate prevention.
     */
    public boolean isSameMeeting(Meeting otherMeeting) {
        return otherMeeting != null
                && name.equals(otherMeeting.name)
                && date.equals(otherMeeting.date)
                && startTime.equals(otherMeeting.startTime)
                && endTime.equals(otherMeeting.endTime);
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
        return isSameMeeting(otherMeeting) && tags.equals(otherMeeting.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, startTime, endTime, tags);
    }

    @Override
    public String toString() {
        String details = name + " " + date + " from " + startTime + " to " + endTime;
        if (tags.isEmpty()) {
            return details;
        }
        return details + " Tags: " + tags.stream()
                .map(Tag::toString)
                .sorted()
                .collect(Collectors.joining(" "));
    }
}
