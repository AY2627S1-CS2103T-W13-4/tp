package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FORCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.meeting.Meeting;

/**
 * Adds a meeting to PingBook.
 */
public class AddMeetingCommand extends Command {

    public static final String COMMAND_WORD = "addm";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a meeting to PingBook. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_DATE + "DATE "
            + PREFIX_START_TIME + "START_TIME "
            + PREFIX_END_TIME + "END_TIME "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_FORCE + "]\n"
            + "Overlapping meetings are rejected unless " + PREFIX_FORCE + " is given.\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Project Review "
            + PREFIX_DATE + "08/10/2026 "
            + PREFIX_START_TIME + "1400 "
            + PREFIX_END_TIME + "1500 "
            + PREFIX_TAG + "urgent "
            + PREFIX_TAG + "work";

    public static final String MESSAGE_SUCCESS = "New meeting added: %1$s";
    public static final String MESSAGE_DUPLICATE_MEETING = "This meeting already exists in PingBook.";

    private final Meeting toAdd;
    private final boolean isForced;

    /**
     * Creates an {@code AddMeetingCommand} to add the specified meeting, rejecting overlaps.
     */
    public AddMeetingCommand(Meeting meeting) {
        this(meeting, false);
    }

    /**
     * Creates an {@code AddMeetingCommand} to add the specified meeting.
     * If {@code isForced} is true, the meeting is added even if it overlaps existing meetings.
     */
    public AddMeetingCommand(Meeting meeting, boolean isForced) {
        requireNonNull(meeting);
        toAdd = meeting;
        this.isForced = isForced;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        assert Meeting.isValidTimeRange(toAdd.getStartTime(), toAdd.getEndTime());

        if (model.hasMeeting(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_MEETING);
        }

        List<Meeting> overlaps = model.getOverlappingMeetings(toAdd, null);
        if (!overlaps.isEmpty() && !isForced) {
            throw new CommandException(String.format(Messages.MESSAGE_MEETING_OVERLAP,
                    Messages.formatOverlappingMeetings(overlaps)));
        }

        model.addMeeting(toAdd);
        String feedback = String.format(MESSAGE_SUCCESS, toAdd);
        if (!overlaps.isEmpty()) {
            feedback += "\n" + String.format(Messages.MESSAGE_MEETING_OVERLAP_WARNING,
                    Messages.formatOverlappingMeetings(overlaps));
        }
        return new CommandResult(feedback, true);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddMeetingCommand otherAddMeetingCommand)) {
            return false;
        }

        return toAdd.equals(otherAddMeetingCommand.toAdd)
                && isForced == otherAddMeetingCommand.isForced;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .add("isForced", isForced)
                .toString();
    }
}
