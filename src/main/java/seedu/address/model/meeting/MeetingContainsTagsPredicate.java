package seedu.address.model.meeting;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.address.model.tag.Tag;

/**
 * Matches meetings containing all requested tags, ignoring tag-name case.
 * An empty tag set matches every meeting.
 */
public class MeetingContainsTagsPredicate implements Predicate<Meeting> {
    private final Set<String> requiredTags;

    /**
     * Creates a matching rule using a defensive, case-normalised copy of {@code tags}.
     */
    public MeetingContainsTagsPredicate(Set<Tag> tags) {
        requireAllNonNull(tags);
        requiredTags = tags.stream()
                .map(tag -> tag.tagName.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns true if no tag filter was requested.
     */
    public boolean isEmpty() {
        return requiredTags.isEmpty();
    }

    @Override
    public boolean test(Meeting meeting) {
        requireNonNull(meeting);
        return requiredTags.stream().allMatch(required -> meeting.getTags().stream()
                .anyMatch(tag -> tag.tagName.equalsIgnoreCase(required)));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof MeetingContainsTagsPredicate otherPredicate
                && requiredTags.equals(otherPredicate.requiredTags);
    }

    @Override
    public int hashCode() {
        return requiredTags.hashCode();
    }
}
