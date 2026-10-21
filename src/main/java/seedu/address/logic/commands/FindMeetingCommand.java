package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;

import seedu.address.model.Model;

/**
 * Finds meetings whose names contain any of the specified keywords.
 * Matching is case-insensitive.
 */
public class FindMeetingCommand extends Command {

    public static final String COMMAND_WORD = "findm";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds meetings whose names contain any of the specified keywords.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " project";

    public static final String MESSAGE_SUCCESS = "Listed matching meetings.";

    private final List<String> keywords;

    /**
     * Creates a FindMeetingCommand using the given keywords.
     *
     * @param keywords The keywords to search for.
     */
    public FindMeetingCommand(List<String> keywords) {
        this.keywords = List.copyOf(requireNonNull(keywords));
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        model.updateFilteredMeetingList(meeting -> keywords.stream()
                .anyMatch(keyword -> meeting.getName().name.toLowerCase(Locale.ROOT)
                        .contains(keyword.toLowerCase(Locale.ROOT))));

        return new CommandResult(MESSAGE_SUCCESS, true);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof FindMeetingCommand otherCommand)) {
            return false;
        }

        return keywords.equals(otherCommand.keywords);
    }

    @Override
    public int hashCode() {
        return keywords.hashCode();
    }
}
