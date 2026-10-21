package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing meeting in PingBook.
 */
public class EditMeetingCommand extends Command {

    public static final String COMMAND_WORD = "editm";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the meeting identified by the index number used in the displayed meeting list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: MEETING_INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_DATE + "DATE] "
            + "[" + PREFIX_START_TIME + "START_TIME] "
            + "[" + PREFIX_END_TIME + "END_TIME] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_DATE + "08/10/2026 "
            + PREFIX_START_TIME + "1400 "
            + PREFIX_END_TIME + "1500 "
            + PREFIX_TAG + "urgent";

    public static final String MESSAGE_EDIT_MEETING_SUCCESS = "Edited meeting: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_MEETING = "This meeting already exists in PingBook.";

    private final Index index;
    private final EditMeetingDescriptor editMeetingDescriptor;

    /**
     * Creates an {@code EditMeetingCommand} that edits the meeting at {@code index}.
     */
    public EditMeetingCommand(Index index, EditMeetingDescriptor editMeetingDescriptor) {
        requireNonNull(index);
        requireNonNull(editMeetingDescriptor);

        this.index = index;
        this.editMeetingDescriptor = new EditMeetingDescriptor(editMeetingDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Meeting> lastShownList = model.getMeetingList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_MEETING_INDEX);
        }

        Meeting meetingToEdit = lastShownList.get(index.getZeroBased());
        Meeting editedMeeting = createEditedMeeting(meetingToEdit, editMeetingDescriptor);

        if (!meetingToEdit.isSameMeeting(editedMeeting) && model.hasMeeting(editedMeeting)) {
            throw new CommandException(MESSAGE_DUPLICATE_MEETING);
        }

        model.setMeeting(meetingToEdit, editedMeeting);
        return new CommandResult(String.format(MESSAGE_EDIT_MEETING_SUCCESS, editedMeeting), true);
    }

    /**
     * Creates a meeting by applying the supplied edits to {@code meetingToEdit}.
     */
    private static Meeting createEditedMeeting(Meeting meetingToEdit,
            EditMeetingDescriptor editMeetingDescriptor) {
        assert meetingToEdit != null;

        MeetingName updatedName = editMeetingDescriptor.getName().orElse(meetingToEdit.getName());
        MeetingDate updatedDate = editMeetingDescriptor.getDate().orElse(meetingToEdit.getDate());
        MeetingTime updatedStartTime = editMeetingDescriptor.getStartTime()
                .orElse(meetingToEdit.getStartTime());
        MeetingTime updatedEndTime = editMeetingDescriptor.getEndTime().orElse(meetingToEdit.getEndTime());
        Set<Tag> updatedTags = editMeetingDescriptor.getTags().orElse(meetingToEdit.getTags());

        return new Meeting(updatedName, updatedDate, updatedStartTime, updatedEndTime, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditMeetingCommand otherEditMeetingCommand)) {
            return false;
        }

        return index.equals(otherEditMeetingCommand.index)
                && editMeetingDescriptor.equals(otherEditMeetingCommand.editMeetingDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editMeetingDescriptor", editMeetingDescriptor)
                .toString();
    }

    /**
     * Stores the meeting fields to replace during an edit.
     */
    public static class EditMeetingDescriptor {
        private MeetingName name;
        private MeetingDate date;
        private MeetingTime startTime;
        private MeetingTime endTime;
        private Set<Tag> tags;

        public EditMeetingDescriptor() {}

        /**
         * Creates a defensive copy of {@code toCopy}.
         */
        public EditMeetingDescriptor(EditMeetingDescriptor toCopy) {
            setName(toCopy.name);
            setDate(toCopy.date);
            setStartTime(toCopy.startTime);
            setEndTime(toCopy.endTime);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one meeting field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, date, startTime, endTime, tags);
        }

        public void setName(MeetingName name) {
            this.name = name;
        }

        public Optional<MeetingName> getName() {
            return Optional.ofNullable(name);
        }

        public void setDate(MeetingDate date) {
            this.date = date;
        }

        public Optional<MeetingDate> getDate() {
            return Optional.ofNullable(date);
        }

        public void setStartTime(MeetingTime startTime) {
            this.startTime = startTime;
        }

        public Optional<MeetingTime> getStartTime() {
            return Optional.ofNullable(startTime);
        }

        public void setEndTime(MeetingTime endTime) {
            this.endTime = endTime;
        }

        public Optional<MeetingTime> getEndTime() {
            return Optional.ofNullable(endTime);
        }

        /**
         * Sets {@code tags} as the replacement tags, using a defensive copy.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = tags == null ? null : new HashSet<>(tags);
        }

        /**
         * Returns the replacement tags, if they were specified.
         */
        public Optional<Set<Tag>> getTags() {
            return tags == null
                    ? Optional.empty()
                    : Optional.of(Collections.unmodifiableSet(tags));
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditMeetingDescriptor otherEditMeetingDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditMeetingDescriptor.name)
                    && Objects.equals(date, otherEditMeetingDescriptor.date)
                    && Objects.equals(startTime, otherEditMeetingDescriptor.startTime)
                    && Objects.equals(endTime, otherEditMeetingDescriptor.endTime)
                    && Objects.equals(tags, otherEditMeetingDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("date", date)
                    .add("startTime", startTime)
                    .add("endTime", endTime)
                    .add("tags", tags)
                    .toString();
        }
    }
}
