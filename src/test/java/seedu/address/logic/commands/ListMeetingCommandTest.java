package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.meeting.exceptions.DuplicateMeetingException;

public class ListMeetingCommandTest {
    private static final Clock NOON = Clock.fixed(Instant.parse("2026-10-08T04:00:00Z"),
            ZoneId.of("Asia/Singapore"));

    private final ModelManager model = new ModelManager();
    private final ListMeetingCommand command = new ListMeetingCommand(NOON);

    @Test
    public void execute_emptyList_success() {
        CommandResult result = command.execute(model);
        assertEquals(ListMeetingCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertTrue(result.isShowMeetings());
        assertFalse(result.isShowHelp());
        assertFalse(result.isExit());
        assertTrue(model.getMeetingList().isEmpty());
    }

    @Test
    public void execute_mixedMeetings_ordersEachGroupChronologically() {
        Meeting yesterday = meeting("Yesterday", "07/10/2026", "1000", "1100");
        Meeting lastMonth = meeting("Last month", "30/09/2026", "1000", "1100");
        Meeting tomorrow = meeting("Tomorrow", "09/10/2026", "0900", "1000");
        Meeting ongoing = meeting("Ongoing", "08/10/2026", "1130", "1230");
        Meeting afternoon = meeting("Afternoon", "08/10/2026", "1500", "1600");
        Meeting endingNow = meeting("Ending now", "08/10/2026", "1100", "1200");
        List.of(yesterday, tomorrow, afternoon, lastMonth, endingNow, ongoing).forEach(model::addMeeting);
        model.addPerson(ALICE);
        model.updateFilteredPersonList(person -> false);
        AddressBook original = new AddressBook(model.getAddressBook());

        command.execute(model);

        assertEquals(List.of(ongoing, afternoon, tomorrow, lastMonth, yesterday, endingNow), model.getMeetingList());
        assertEquals(original, model.getAddressBook());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> model.getMeetingList().remove(0));
    }

    @Test
    public void execute_singleMeeting_showsMeeting() {
        Meeting onlyMeeting = meeting("Only meeting", "08/10/2026", "1500", "1600");
        model.addMeeting(onlyMeeting);
        command.execute(model);
        assertEquals(List.of(onlyMeeting), model.getMeetingList());
    }

    @Test
    public void execute_timeAdvances_refreshesFinishedGroup() {
        Meeting earlier = meeting("Earlier", "08/10/2026", "1300", "1400");
        Meeting later = meeting("Later", "08/10/2026", "1500", "1600");
        model.addMeeting(later);
        model.addMeeting(earlier);
        command.execute(model);
        assertEquals(List.of(earlier, later), model.getMeetingList());

        Clock twoPm = Clock.fixed(Instant.parse("2026-10-08T06:00:00Z"), NOON.getZone());
        new ListMeetingCommand(twoPm).execute(model);
        assertEquals(List.of(later, earlier), model.getMeetingList());
    }

    @Test
    public void execute_equalStartTimes_ordersByEndTimeThenName() {
        Meeting beta = meeting("Beta", "08/10/2026", "1500", "1700");
        Meeting alpha = meeting("Alpha", "08/10/2026", "1500", "1700");
        Meeting shorter = meeting("Shorter", "08/10/2026", "1500", "1600");
        List.of(beta, alpha, shorter).forEach(model::addMeeting);
        command.execute(model);
        assertEquals(List.of(shorter, alpha, beta), model.getMeetingList());
    }

    @Test
    public void execute_sourceChanges_existingViewUpdates() {
        Meeting later = meeting("Later", "08/10/2026", "1500", "1600");
        Meeting earlier = meeting("Earlier", "08/10/2026", "1300", "1400");
        model.addMeeting(later);
        command.execute(model);
        ObservableList<Meeting> displayedMeetings = model.getMeetingList();

        model.addMeeting(earlier);
        assertEquals(List.of(earlier, later), displayedMeetings);
        model.deleteMeeting(displayedMeetings.get(0));
        assertEquals(List.of(later), displayedMeetings);
        model.setAddressBook(new AddressBook());
        assertTrue(displayedMeetings.isEmpty());
    }

    @Test
    public void execute_duplicateAlreadyRejected_displaysOneMeeting() {
        Meeting meeting = meeting("Team meeting", "08/10/2026", "1500", "1600");
        model.addMeeting(meeting);
        Meeting duplicate = meeting(" TEAM   MEETING ", "08/10/2026", "1500", "1600");
        assertThrows(DuplicateMeetingException.class, () -> model.addMeeting(duplicate));
        command.execute(model);
        assertEquals(List.of(meeting), model.getMeetingList());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
        assertThrows(NullPointerException.class, () -> new ListMeetingCommand(null));
        assertThrows(NullPointerException.class, () -> model.updateMeetingListOrder(null));
    }

    private Meeting meeting(String name, String date, String start, String end) {
        return new Meeting(new MeetingName(name), new MeetingDate(date), new MeetingTime(start), new MeetingTime(end));
    }
}
