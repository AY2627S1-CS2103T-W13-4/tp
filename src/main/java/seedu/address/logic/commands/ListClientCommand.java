package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.tag.Tag;

/**
 * Lists all persons in the address book to the user.
 */
public class ListClientCommand extends Command {

    public static final String COMMAND_WORD = "listc";
    public static final String MESSAGE_SUCCESS = "Listed all persons.";

    private final Set<Tag> tags;

    /**
     * Creates an {@code ListClientCommand} to list the clients filtered by tags.
     * @param tags The tags to filter which clients are shown. Cannot be null.
     */
    public ListClientCommand(Set<Tag> tags) {
        assert tags != null;
        this.tags = tags;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ListClientCommand otherListClientCommand)) {
            return false;
        }

        return tags.equals(otherListClientCommand.tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("tags", tags)
                .toString();
    }
}
