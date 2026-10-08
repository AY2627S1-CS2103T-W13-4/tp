package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;

public class AddMeetingCommandTest {

    private static final Meeting PROJECT_REVIEW = new Meeting(
            new MeetingName("Project Review"), new MeetingDate("08/10/2026"),
            new MeetingTime("1400"), new MeetingTime("1500"));
    private static final Meeting TEAM_SYNC = new Meeting(
            new MeetingName("Team Sync"), new MeetingDate("09/10/2026"),
            new MeetingTime("0900"), new MeetingTime("1000"));

    @Test
    public void constructor_nullMeeting_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddMeetingCommand(null));
    }

    @Test
    public void execute_newMeeting_addSuccessful() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        expectedModel.addMeeting(PROJECT_REVIEW);

        assertCommandSuccess(new AddMeetingCommand(PROJECT_REVIEW), model,
                String.format(AddMeetingCommand.MESSAGE_SUCCESS, PROJECT_REVIEW), expectedModel);
    }

    @Test
    public void execute_duplicateMeeting_throwsCommandException() {
        Model model = new ModelManager();
        model.addMeeting(PROJECT_REVIEW);

        assertCommandFailure(new AddMeetingCommand(PROJECT_REVIEW), model,
                AddMeetingCommand.MESSAGE_DUPLICATE_MEETING);
    }

    @Test
    public void equals() {
        AddMeetingCommand addProjectReviewCommand = new AddMeetingCommand(PROJECT_REVIEW);
        AddMeetingCommand addTeamSyncCommand = new AddMeetingCommand(TEAM_SYNC);

        assertTrue(addProjectReviewCommand.equals(addProjectReviewCommand));
        assertTrue(addProjectReviewCommand.equals(new AddMeetingCommand(PROJECT_REVIEW)));
        assertFalse(addProjectReviewCommand.equals(addTeamSyncCommand));
        assertFalse(addProjectReviewCommand.equals(null));
        assertFalse(addProjectReviewCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        AddMeetingCommand addMeetingCommand = new AddMeetingCommand(PROJECT_REVIEW);
        String expected = AddMeetingCommand.class.getCanonicalName() + "{toAdd=" + PROJECT_REVIEW + "}";
        assertEquals(expected, addMeetingCommand.toString());
    }
}
