package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditMeetingCommand.EditMeetingDescriptor;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditMeetingDescriptorBuilder;

public class EditMeetingCommandTest {

    private static final Index FIRST_INDEX = Index.fromOneBased(1);
    private static final Index SECOND_INDEX = Index.fromOneBased(2);

    private Model model;
    private Meeting firstMeeting;
    private Meeting secondMeeting;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        firstMeeting = meeting("Project Review", "08/10/2026", "1400", "1500", "urgent", "work");
        secondMeeting = meeting("Team Sync", "09/10/2026", "0900", "1000");
        model.addMeeting(firstMeeting);
        model.addMeeting(secondMeeting);
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        EditMeetingDescriptor descriptor = new EditMeetingDescriptor();

        assertThrows(NullPointerException.class, () -> new EditMeetingCommand(null, descriptor));
    }

    @Test
    public void constructor_nullDescriptor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EditMeetingCommand(FIRST_INDEX, null));
    }

    @Test
    public void execute_allFieldsSpecified_success() {
        Meeting editedMeeting = meeting("Client Review", "10/10/2026", "1100", "1200", "client");
        EditMeetingDescriptor descriptor = new EditMeetingDescriptorBuilder(editedMeeting).build();
        EditMeetingCommand command = new EditMeetingCommand(FIRST_INDEX, descriptor);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setMeeting(firstMeeting, editedMeeting);
        CommandResult expectedResult = new CommandResult(
                String.format(EditMeetingCommand.MESSAGE_EDIT_MEETING_SUCCESS, editedMeeting), true);

        assertCommandSuccess(command, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecified_success() {
        Meeting editedMeeting = meeting("Project Review", "08/10/2026", "1300", "1600", "urgent", "work");
        EditMeetingDescriptor descriptor = new EditMeetingDescriptorBuilder()
                .withStartTime("1300")
                .withEndTime("1600")
                .build();
        EditMeetingCommand command = new EditMeetingCommand(FIRST_INDEX, descriptor);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setMeeting(firstMeeting, editedMeeting);
        CommandResult expectedResult = new CommandResult(
                String.format(EditMeetingCommand.MESSAGE_EDIT_MEETING_SUCCESS, editedMeeting), true);

        assertCommandSuccess(command, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_sameValues_success() {
        EditMeetingDescriptor descriptor = new EditMeetingDescriptorBuilder(firstMeeting).build();
        EditMeetingCommand command = new EditMeetingCommand(FIRST_INDEX, descriptor);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        CommandResult expectedResult = new CommandResult(
                String.format(EditMeetingCommand.MESSAGE_EDIT_MEETING_SUCCESS, firstMeeting), true);

        assertCommandSuccess(command, model, expectedResult, expectedModel);

        Meeting meetingWithEditedTags = meeting("Project Review", "08/10/2026", "1400", "1500", "client");
        descriptor = new EditMeetingDescriptorBuilder().withTags("client").build();
        command = new EditMeetingCommand(FIRST_INDEX, descriptor);
        expectedModel.setMeeting(firstMeeting, meetingWithEditedTags);
        expectedResult = new CommandResult(
                String.format(EditMeetingCommand.MESSAGE_EDIT_MEETING_SUCCESS, meetingWithEditedTags), true);

        assertCommandSuccess(command, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_duplicateMeeting_throwsCommandException() {
        EditMeetingDescriptor descriptor = new EditMeetingDescriptorBuilder(firstMeeting).build();
        EditMeetingCommand command = new EditMeetingCommand(SECOND_INDEX, descriptor);

        assertCommandFailure(command, model, EditMeetingCommand.MESSAGE_DUPLICATE_MEETING);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        EditMeetingCommand command = new EditMeetingCommand(Index.fromOneBased(3),
                new EditMeetingDescriptorBuilder().withName("Updated Meeting").build());

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_MEETING_INDEX);
    }

    @Test
    public void equals() {
        EditMeetingDescriptor descriptor = new EditMeetingDescriptorBuilder()
                .withName("Updated Meeting")
                .build();
        EditMeetingCommand standardCommand = new EditMeetingCommand(FIRST_INDEX, descriptor);

        assertTrue(standardCommand.equals(standardCommand));
        assertTrue(standardCommand.equals(
                new EditMeetingCommand(FIRST_INDEX, new EditMeetingDescriptor(descriptor))));
        assertFalse(standardCommand.equals(new EditMeetingCommand(SECOND_INDEX, descriptor)));
        assertFalse(standardCommand.equals(new EditMeetingCommand(FIRST_INDEX,
                new EditMeetingDescriptorBuilder().withDate("10/10/2026").build())));
        assertFalse(standardCommand.equals(null));
        assertFalse(standardCommand.equals(new ClearCommand()));
    }

    @Test
    public void toStringMethod() {
        EditMeetingDescriptor descriptor = new EditMeetingDescriptorBuilder()
                .withName("Updated Meeting")
                .build();
        EditMeetingCommand command = new EditMeetingCommand(FIRST_INDEX, descriptor);
        String expected = EditMeetingCommand.class.getCanonicalName() + "{index=" + FIRST_INDEX
                + ", editMeetingDescriptor=" + descriptor + "}";

        assertEquals(expected, command.toString());
    }

    private Meeting meeting(String name, String date, String startTime, String endTime, String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        return new Meeting(new MeetingName(name), new MeetingDate(date),
                new MeetingTime(startTime), new MeetingTime(endTime), tagSet);
    }
}
