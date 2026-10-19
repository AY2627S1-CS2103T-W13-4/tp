package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Lists all persons in the address book to the user.
 */
public class ListClientCommand extends Command {

    public static final String COMMAND_WORD = "listc";
    public static final String MESSAGE_SUCCESS = "Listed all persons.";

    private final Predicate<Person> predicateFilterByTags;
    private final Set<Tag> tags;

    /**
     * Creates an {@code ListClientCommand} to list the clients filtered by tags.
     * @param tags The tags to filter which clients are shown. Cannot be null.
     */
    public ListClientCommand(Set<Tag> tags) {
        this.tags = Set.copyOf(requireNonNull(tags));
        this.predicateFilterByTags = person -> person.hasAllTags(this.tags);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicateFilterByTags);
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
