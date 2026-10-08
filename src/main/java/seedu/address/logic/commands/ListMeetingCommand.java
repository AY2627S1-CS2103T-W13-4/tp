package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.LocalDateTime;

import seedu.address.model.Model;

/**
 * Lists all meetings, with unfinished meetings before finished meetings.
 */
public class ListMeetingCommand extends Command {

    public static final String COMMAND_WORD = "listm";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all meetings.\n"
            + "No parameters or flags are accepted.\nExample: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "Listed all meetings.";
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command. Did you mean \"listm\"?";

    private final Clock clock;

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
        this.clock = requireNonNull(clock);
    }

    /**
     * Refreshes the displayed meeting order and returns a result requesting the meeting panel.
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
        model.updateMeetingListOrder(LocalDateTime.now(clock));
        return new CommandResult(MESSAGE_SUCCESS, true);
    }
}
