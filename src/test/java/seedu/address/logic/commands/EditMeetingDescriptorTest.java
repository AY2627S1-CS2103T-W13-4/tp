package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditMeetingCommand.EditMeetingDescriptor;
import seedu.address.testutil.EditMeetingDescriptorBuilder;

public class EditMeetingDescriptorTest {

    private static final EditMeetingDescriptor PROJECT_REVIEW = new EditMeetingDescriptorBuilder()
            .withName("Project Review")
            .withDate("08/10/2026")
            .withStartTime("1400")
            .withEndTime("1500")
            .withTags("work")
            .build();

    @Test
    public void isAnyFieldEdited_noFields_returnsFalse() {
        assertFalse(new EditMeetingDescriptor().isAnyFieldEdited());
    }

    @Test
    public void isAnyFieldEdited_fieldPresent_returnsTrue() {
        assertTrue(new EditMeetingDescriptorBuilder().withName("Project Review").build().isAnyFieldEdited());
        assertTrue(new EditMeetingDescriptorBuilder().withTags("work").build().isAnyFieldEdited());
    }

    @Test
    public void equals() {
        EditMeetingDescriptor copy = new EditMeetingDescriptor(PROJECT_REVIEW);
        assertTrue(PROJECT_REVIEW.equals(copy));
        assertTrue(PROJECT_REVIEW.equals(PROJECT_REVIEW));
        assertFalse(PROJECT_REVIEW.equals(null));
        assertFalse(PROJECT_REVIEW.equals(1));

        assertFalse(PROJECT_REVIEW.equals(
                new EditMeetingDescriptorBuilder(PROJECT_REVIEW).withName("Team Sync").build()));
        assertFalse(PROJECT_REVIEW.equals(
                new EditMeetingDescriptorBuilder(PROJECT_REVIEW).withDate("09/10/2026").build()));
        assertFalse(PROJECT_REVIEW.equals(
                new EditMeetingDescriptorBuilder(PROJECT_REVIEW).withStartTime("1300").build()));
        assertFalse(PROJECT_REVIEW.equals(
                new EditMeetingDescriptorBuilder(PROJECT_REVIEW).withEndTime("1600").build()));
        assertFalse(PROJECT_REVIEW.equals(
                new EditMeetingDescriptorBuilder(PROJECT_REVIEW).withTags("urgent").build()));
    }

    @Test
    public void toStringMethod() {
        EditMeetingDescriptor descriptor = new EditMeetingDescriptor();
        String expected = EditMeetingDescriptor.class.getCanonicalName()
                + "{name=null, date=null, startTime=null, endTime=null, tags=null}";

        assertEquals(expected, descriptor.toString());
    }
}
