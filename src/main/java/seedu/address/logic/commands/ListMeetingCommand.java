package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.model.Model;
import seedu.address.model.meeting.MeetingContainsTagsPredicate;
import seedu.address.model.tag.Tag;

/**
 * Lists meetings matching all optional tag filters, with unfinished meetings first.
 */
public class ListMeetingCommand extends Command {

    public static final String COMMAND_WORD = "listm";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all meetings or filters by all supplied tags.\n"
            + "Parameters: [t/TAG]...\nExample: " + COMMAND_WORD + " t/urgent t/work";
    public static final String MESSAGE_SUCCESS = "Listed all meetings.";
    public static final String MESSAGE_FILTER_SUCCESS = "Listed %1$d meeting(s) matching all specified tags.";
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command. Did you mean \"listm\"?";

    private static final Logger logger = LogsCenter.getLogger(ListMeetingCommand.class);

    private final Clock clock;
    private final MeetingContainsTagsPredicate predicate;

    /**
     * Creates a command using the system clock in the default time zone.
     */
    public ListMeetingCommand() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a command using the given clock to determine which meetings have finished.
     *
     * @param clock The clock providing the current time and time zone.
     * @throws NullPointerException If {@code clock} is null.
     */
    public ListMeetingCommand(Clock clock) {
        this(clock, Set.of());
    }

    /**
     * Creates a command with a clock for ordering and optional tags for filtering.
     */
    public ListMeetingCommand(Clock clock, Set<Tag> tags) {
        this.clock = requireNonNull(clock);
        predicate = new MeetingContainsTagsPredicate(tags);
    }

    /**
     * Replaces the tag filter, refreshes meeting order and requests the meeting panel.
     * Uses a single reading of the clock for this execution. Does not change the stored meeting records.
     *
     * @param model The model containing the meetings to display.
     * @return A result containing the success message and selecting the meeting panel.
     * @throws NullPointerException If {@code model} is null.
     * @see Model#updateMeetingListOrder(LocalDateTime)
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredMeetingList(predicate);
        model.updateMeetingListOrder(LocalDateTime.now(clock));
        int displayedCount = model.getMeetingList().size();
        logger.info(() -> "Meeting list refreshed: tag filter applied=" + !predicate.isEmpty()
                + ", displayed meetings=" + displayedCount);
        String message = predicate.isEmpty() ? MESSAGE_SUCCESS : String.format(MESSAGE_FILTER_SUCCESS, displayedCount);
        return new CommandResult(message, true);
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof ListMeetingCommand otherCommand
                && clock.equals(otherCommand.clock) && predicate.equals(otherCommand.predicate);
    }
}
