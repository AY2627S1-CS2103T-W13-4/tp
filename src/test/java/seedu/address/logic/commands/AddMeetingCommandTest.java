package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.tag.Tag;

public class AddMeetingCommandTest {

    private static final Meeting PROJECT_REVIEW = new Meeting(
            new MeetingName("Project Review"), new MeetingDate("08/10/2026"),
            new MeetingTime("1400"), new MeetingTime("1500"));
    private static final Meeting TEAM_SYNC = new Meeting(
            new MeetingName("Team Sync"), new MeetingDate("09/10/2026"),
            new MeetingTime("0900"), new MeetingTime("1000"));

    private static final Meeting OVERLAPPING = new Meeting(
            new MeetingName("Overlap Chat"), PROJECT_REVIEW.getDate(),
            new MeetingTime("1430"), new MeetingTime("1530"));
    private static final Meeting ADJACENT = new Meeting(
            new MeetingName("Adjacent Chat"), PROJECT_REVIEW.getDate(),
            new MeetingTime("1500"), new MeetingTime("1600"));

    @Test
    public void constructor_nullMeeting_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddMeetingCommand(null));
    }

    @Test
    public void execute_newMeeting_addSuccessful() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        expectedModel.addMeeting(PROJECT_REVIEW);
        CommandResult expectedResult = new CommandResult(
                String.format(AddMeetingCommand.MESSAGE_SUCCESS, PROJECT_REVIEW), true);

        assertCommandSuccess(new AddMeetingCommand(PROJECT_REVIEW), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_duplicateMeeting_throwsCommandException() {
        Model model = new ModelManager();
        model.addMeeting(PROJECT_REVIEW);

        assertCommandFailure(new AddMeetingCommand(PROJECT_REVIEW), model,
                AddMeetingCommand.MESSAGE_DUPLICATE_MEETING);
    }

    @Test
    public void execute_duplicateMeetingWithDifferentTags_throwsCommandException() {
        Model model = new ModelManager();
        model.addMeeting(PROJECT_REVIEW);
        Meeting tagged = new Meeting(PROJECT_REVIEW.getName(), PROJECT_REVIEW.getDate(),
                PROJECT_REVIEW.getStartTime(), PROJECT_REVIEW.getEndTime(), Set.of(new Tag("urgent")));
        assertCommandFailure(new AddMeetingCommand(tagged), model, AddMeetingCommand.MESSAGE_DUPLICATE_MEETING);
    }

    @Test
    public void equals() {
        AddMeetingCommand addProjectReviewCommand = new AddMeetingCommand(PROJECT_REVIEW);
        AddMeetingCommand addTeamSyncCommand = new AddMeetingCommand(TEAM_SYNC);

        assertTrue(addProjectReviewCommand.equals(addProjectReviewCommand));
        assertTrue(addProjectReviewCommand.equals(new AddMeetingCommand(PROJECT_REVIEW)));
        assertFalse(addProjectReviewCommand.equals(addTeamSyncCommand));
        assertFalse(addProjectReviewCommand.equals(new AddMeetingCommand(PROJECT_REVIEW, true)));
        assertFalse(addProjectReviewCommand.equals(null));
        assertFalse(addProjectReviewCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        AddMeetingCommand addMeetingCommand = new AddMeetingCommand(PROJECT_REVIEW);
        String expected = AddMeetingCommand.class.getCanonicalName() + "{toAdd=" + PROJECT_REVIEW
                + ", isForced=false}";
        assertEquals(expected, addMeetingCommand.toString());
    }

    @Test
    public void execute_overlappingMeetingWithoutForce_throwsCommandException() {
        Model model = new ModelManager();
        model.addMeeting(PROJECT_REVIEW);
        Model expectedModel = new ModelManager();
        expectedModel.addMeeting(PROJECT_REVIEW);

        String expectedMessage = String.format(Messages.MESSAGE_MEETING_OVERLAP,
                Messages.formatOverlappingMeetings(List.of(PROJECT_REVIEW)));
        assertCommandFailure(new AddMeetingCommand(OVERLAPPING), model, expectedMessage);
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_overlappingMeetingWithForce_addsWithWarning() {
        Model model = new ModelManager();
        model.addMeeting(PROJECT_REVIEW);
        Model expectedModel = new ModelManager();
        expectedModel.addMeeting(PROJECT_REVIEW);
        expectedModel.addMeeting(OVERLAPPING);

        String expectedMessage = String.format(AddMeetingCommand.MESSAGE_SUCCESS, OVERLAPPING) + "\n"
                + String.format(Messages.MESSAGE_MEETING_OVERLAP_WARNING,
                        Messages.formatOverlappingMeetings(List.of(PROJECT_REVIEW)));
        assertCommandSuccess(new AddMeetingCommand(OVERLAPPING, true), model,
                new CommandResult(expectedMessage, true), expectedModel);
    }

    @Test
    public void execute_forceWithoutOverlap_addsWithoutWarning() {
        Model model = new ModelManager();
        model.addMeeting(PROJECT_REVIEW);
        Model expectedModel = new ModelManager();
        expectedModel.addMeeting(PROJECT_REVIEW);
        expectedModel.addMeeting(TEAM_SYNC);

        assertCommandSuccess(new AddMeetingCommand(TEAM_SYNC, true), model,
                new CommandResult(String.format(AddMeetingCommand.MESSAGE_SUCCESS, TEAM_SYNC), true),
                expectedModel);
    }

    @Test
    public void execute_adjacentMeetingWithoutForce_addSuccessful() {
        Model model = new ModelManager();
        model.addMeeting(PROJECT_REVIEW);
        Model expectedModel = new ModelManager();
        expectedModel.addMeeting(PROJECT_REVIEW);
        expectedModel.addMeeting(ADJACENT);

        assertCommandSuccess(new AddMeetingCommand(ADJACENT), model,
                new CommandResult(String.format(AddMeetingCommand.MESSAGE_SUCCESS, ADJACENT), true),
                expectedModel);
    }

    @Test
    public void execute_manyOverlaps_capsListedMeetings() {
        Model model = new ModelManager();
        for (int i = 0; i < 5; i++) {
            model.addMeeting(new Meeting(new MeetingName("Busy " + (char) ('A' + i)), PROJECT_REVIEW.getDate(),
                    new MeetingTime("1400"), new MeetingTime("1500")));
        }

        try {
            new AddMeetingCommand(OVERLAPPING).execute(model);
        } catch (CommandException e) {
            assertTrue(e.getMessage().contains("+2 more"));
            return;
        }
        throw new AssertionError("Expected CommandException");
    }
}
