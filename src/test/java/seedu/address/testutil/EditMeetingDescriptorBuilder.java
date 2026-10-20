package seedu.address.testutil;

import seedu.address.logic.commands.EditMeetingCommand.EditMeetingDescriptor;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;

/**
 * Helps build {@code EditMeetingDescriptor} objects for tests.
 */
public class EditMeetingDescriptorBuilder {

    private final EditMeetingDescriptor descriptor;

    public EditMeetingDescriptorBuilder() {
        descriptor = new EditMeetingDescriptor();
    }

    public EditMeetingDescriptorBuilder(EditMeetingDescriptor descriptor) {
        this.descriptor = new EditMeetingDescriptor(descriptor);
    }

    /**
     * Creates a descriptor containing all fields from {@code meeting}.
     */
    public EditMeetingDescriptorBuilder(Meeting meeting) {
        descriptor = new EditMeetingDescriptor();
        descriptor.setName(meeting.getName());
        descriptor.setDate(meeting.getDate());
        descriptor.setStartTime(meeting.getStartTime());
        descriptor.setEndTime(meeting.getEndTime());
    }

    /**
     * Sets the meeting name in the descriptor.
     */
    public EditMeetingDescriptorBuilder withName(String name) {
        descriptor.setName(new MeetingName(name));
        return this;
    }

    /**
     * Sets the meeting date in the descriptor.
     */
    public EditMeetingDescriptorBuilder withDate(String date) {
        descriptor.setDate(new MeetingDate(date));
        return this;
    }

    /**
     * Sets the meeting start time in the descriptor.
     */
    public EditMeetingDescriptorBuilder withStartTime(String startTime) {
        descriptor.setStartTime(new MeetingTime(startTime));
        return this;
    }

    /**
     * Sets the meeting end time in the descriptor.
     */
    public EditMeetingDescriptorBuilder withEndTime(String endTime) {
        descriptor.setEndTime(new MeetingTime(endTime));
        return this;
    }

    public EditMeetingDescriptor build() {
        return descriptor;
    }
}
