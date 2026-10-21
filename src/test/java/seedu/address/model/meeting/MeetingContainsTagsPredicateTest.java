package seedu.address.model.meeting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;

public class MeetingContainsTagsPredicateTest {
    @Test
    public void test_requiresAllWholeTagsIgnoringCase() {
        MeetingContainsTagsPredicate predicate = new MeetingContainsTagsPredicate(Set.of(new Tag("urgent"),
                new Tag("WORK")));
        assertTrue(predicate.test(meeting(Set.of(new Tag("Urgent"), new Tag("work"), new Tag("extra")))));
        assertFalse(predicate.test(meeting(Set.of(new Tag("urgent")))));
        assertFalse(predicate.test(meeting(Set.of(new Tag("urgentMeeting"), new Tag("work")))));
        assertFalse(predicate.test(meeting(Set.of())));
    }

    @Test
    public void test_doesNotMatchMeetingName() {
        assertFalse(new MeetingContainsTagsPredicate(Set.of(new Tag("Urgent"))).test(meeting(Set.of())));
    }

    @Test
    public void test_emptyFilter_matchesEveryMeeting() {
        MeetingContainsTagsPredicate predicate = new MeetingContainsTagsPredicate(Set.of());
        assertTrue(predicate.isEmpty());
        assertTrue(predicate.test(meeting(Set.of())));
        assertTrue(predicate.test(meeting(Set.of(new Tag("urgent")))));
    }

    @Test
    public void constructor_copiesRequestedTags() {
        Set<Tag> tags = new HashSet<>(Set.of(new Tag("urgent")));
        MeetingContainsTagsPredicate predicate = new MeetingContainsTagsPredicate(tags);
        tags.clear();
        assertFalse(predicate.isEmpty());
        assertFalse(predicate.test(meeting(Set.of())));
    }

    @Test
    public void nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MeetingContainsTagsPredicate(null));
        Set<Tag> tags = new HashSet<>();
        tags.add(null);
        assertThrows(NullPointerException.class, () -> new MeetingContainsTagsPredicate(tags));
        assertThrows(NullPointerException.class, () -> new MeetingContainsTagsPredicate(Set.of()).test(null));
    }

    @Test
    public void equalsAndHashCode_ignoreTagCaseOrderAndRepetition() {
        MeetingContainsTagsPredicate predicate = new MeetingContainsTagsPredicate(Set.of(new Tag("urgent"),
                new Tag("work")));
        MeetingContainsTagsPredicate equivalent = new MeetingContainsTagsPredicate(Set.of(new Tag("WORK"),
                new Tag("Urgent"), new Tag("urgent")));
        assertEquals(predicate, equivalent);
        assertEquals(predicate.hashCode(), equivalent.hashCode());
        assertEquals(predicate, predicate);
        assertFalse(predicate.equals(new MeetingContainsTagsPredicate(Set.of(new Tag("work")))));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("urgent"));
    }

    private Meeting meeting(Set<Tag> tags) {
        return new Meeting(new MeetingName("Urgent Review"), new MeetingDate("25/10/2026"),
                new MeetingTime("1400"), new MeetingTime("1500"), tags);
    }
}
