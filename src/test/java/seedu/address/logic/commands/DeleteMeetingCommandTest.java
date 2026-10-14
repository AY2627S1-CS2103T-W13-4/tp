package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_MEETING_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteMeetingCommand}.
 */
public class DeleteMeetingCommandTest {

    private Model model;
    private Meeting firstMeeting;
    private Meeting secondMeeting;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        firstMeeting = meeting("First meeting", "08/10/2026", "1000", "1100");
        secondMeeting = meeting("Second meeting", "09/10/2026", "1000", "1100");
        model.addMeeting(firstMeeting);
        model.addMeeting(secondMeeting);
    }

    @Test
    public void execute_validIndex_success() {
        DeleteMeetingCommand command = new DeleteMeetingCommand(Index.fromOneBased(1));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteMeeting(firstMeeting);
        String expectedMessage = String.format(DeleteMeetingCommand.MESSAGE_DELETE_MEETING_SUCCESS, firstMeeting);

        assertCommandSuccess(command, model, new CommandResult(expectedMessage, true), expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        DeleteMeetingCommand command = new DeleteMeetingCommand(Index.fromOneBased(3));

        assertCommandFailure(command, model, MESSAGE_INVALID_MEETING_INDEX);
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        Model emptyModel = new ModelManager();
        DeleteMeetingCommand command = new DeleteMeetingCommand(Index.fromOneBased(1));

        assertCommandFailure(command, emptyModel, MESSAGE_INVALID_MEETING_INDEX);
    }

    @Test
    public void equals() {
        DeleteMeetingCommand deleteFirstCommand = new DeleteMeetingCommand(Index.fromOneBased(1));
        DeleteMeetingCommand deleteSecondCommand = new DeleteMeetingCommand(Index.fromOneBased(2));

        assertEquals(deleteFirstCommand, deleteFirstCommand);
        assertEquals(deleteFirstCommand, new DeleteMeetingCommand(Index.fromOneBased(1)));
        assertNotEquals(deleteFirstCommand, deleteSecondCommand);
        assertNotEquals(1, deleteFirstCommand);
        assertNotEquals(null, deleteFirstCommand);
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteMeetingCommand command = new DeleteMeetingCommand(targetIndex);
        String expected = DeleteMeetingCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";

        assertEquals(expected, command.toString());
    }

    private Meeting meeting(String name, String date, String start, String end) {
        return new Meeting(new MeetingName(name), new MeetingDate(date), new MeetingTime(start), new MeetingTime(end));
    }
}
