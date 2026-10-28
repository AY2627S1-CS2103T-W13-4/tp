package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;

public class FindMeetingCommandTest {

    private static final Meeting PROJECT_REVIEW = new Meeting(
            new MeetingName("Project Review"), new MeetingDate("08/11/2026"),
            new MeetingTime("0900"), new MeetingTime("1000"));

    private static final Meeting TEAM_SYNC = new Meeting(
            new MeetingName("Team Sync"), new MeetingDate("09/11/2026"),
            new MeetingTime("1000"), new MeetingTime("1100"));

    private static final Meeting BUDGET_REVIEW = new Meeting(
            new MeetingName("Budget Review"), new MeetingDate("10/11/2026"),
            new MeetingTime("1100"), new MeetingTime("1200"));

    private final Model model = new ModelManager();

    @Test
    public void execute_partialKeyword_matchesMeetingName() {
        model.addMeeting(PROJECT_REVIEW);
        model.addMeeting(TEAM_SYNC);

        CommandResult result = new FindMeetingCommand(List.of("ject")).execute(model);

        assertEquals(Set.of("Project Review"), getDisplayedMeetingNames());
        assertEquals(FindMeetingCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertTrue(result.isShowMeetings());
    }

    @Test
    public void execute_keywordCaseInsensitive_matchesMeetingName() {
        model.addMeeting(PROJECT_REVIEW);

        new FindMeetingCommand(List.of("pRoJeCt")).execute(model);

        assertEquals(Set.of("Project Review"), getDisplayedMeetingNames());
    }

    @Test
    public void execute_multipleKeywords_matchesAnyKeyword() {
        model.addMeeting(PROJECT_REVIEW);
        model.addMeeting(TEAM_SYNC);
        model.addMeeting(BUDGET_REVIEW);

        new FindMeetingCommand(List.of("project", "budget")).execute(model);

        assertEquals(Set.of("Project Review", "Budget Review"), getDisplayedMeetingNames());
    }

    @Test
    public void execute_noMatchingKeyword_returnsEmptyList() {
        model.addMeeting(PROJECT_REVIEW);
        model.addMeeting(TEAM_SYNC);

        new FindMeetingCommand(List.of("appointment")).execute(model);

        assertTrue(model.getMeetingList().isEmpty());
    }

    @Test
    public void execute_emptyKeywords_returnsEmptyList() {
        model.addMeeting(PROJECT_REVIEW);
        model.addMeeting(TEAM_SYNC);

        new FindMeetingCommand(List.of()).execute(model);

        assertTrue(model.getMeetingList().isEmpty());
    }

    @Test
    public void execute_doesNotModifyStoredMeetings() {
        model.addMeeting(PROJECT_REVIEW);
        model.addMeeting(TEAM_SYNC);

        new FindMeetingCommand(List.of("project")).execute(model);

        assertEquals(2, model.getAddressBook().getMeetingList().size());
        assertEquals(Set.of("Project Review"), getDisplayedMeetingNames());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        FindMeetingCommand command = new FindMeetingCommand(List.of("project"));

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals() {
        FindMeetingCommand command = new FindMeetingCommand(List.of("project", "review"));
        FindMeetingCommand sameCommand = new FindMeetingCommand(List.of("project", "review"));
        FindMeetingCommand differentCommand = new FindMeetingCommand(List.of("team"));

        assertEquals(command, command);
        assertEquals(command, sameCommand);
        assertEquals(command.hashCode(), sameCommand.hashCode());
        assertNotEquals(command, differentCommand);
        assertNotEquals(command, null);
        assertNotEquals(command, "findm project");
    }

    private Set<String> getDisplayedMeetingNames() {
        return model.getMeetingList().stream()
                .map(meeting -> meeting.getName().name)
                .collect(Collectors.toSet());
    }
}
