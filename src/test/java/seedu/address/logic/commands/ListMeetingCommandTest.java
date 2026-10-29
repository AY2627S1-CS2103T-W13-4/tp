package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.meeting.exceptions.DuplicateMeetingException;
import seedu.address.model.tag.Tag;

public class ListMeetingCommandTest {
    private static final Clock NOON = Clock.fixed(Instant.parse("2026-10-08T04:00:00Z"),
            ZoneId.of("Asia/Singapore"));

    private final ModelManager model = new ModelManager();
    private final ListMeetingCommand command = new ListMeetingCommand(NOON);

    @Test
    public void execute_emptyList_success() {
        // BVA (boundary value analysis): zero meetings is the minimum list size and must still show the meeting panel.
        CommandResult result = command.execute(model);
        assertEquals(ListMeetingCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertTrue(result.isShowMeetings());
        assertFalse(result.isShowHelp());
        assertFalse(result.isExit());
        assertTrue(model.getMeetingList().isEmpty());
    }

    @Test
    public void execute_tagFilter_matchesAllTagsAndPreservesSorting() {
        // EP: meetings with all requested tags match regardless of case; a meeting missing one tag is hidden.
        Meeting later = taggedMeeting("Later", "09/10/2026", "1400", "1500", "urgent", "work");
        Meeting hidden = taggedMeeting("Hidden", "08/10/2026", "0900", "1000", "urgent");
        Meeting finished = taggedMeeting("Finished", "07/10/2026", "0900", "1000", "urgent", "work");
        Meeting earlier = taggedMeeting("Earlier", "08/10/2026", "1400", "1500", "Urgent", "Work");
        List.of(later, hidden, finished, earlier).forEach(model::addMeeting);
        AddressBook original = new AddressBook(model.getAddressBook());

        CommandResult result = new ListMeetingCommand(NOON, Set.of(new Tag("urgent"), new Tag("work")))
                .execute(model);

        assertEquals(List.of(earlier, later, finished), model.getMeetingList());
        assertEquals(String.format(ListMeetingCommand.MESSAGE_FILTER_SUCCESS, 3), result.getFeedbackToUser());
        assertTrue(result.isShowMeetings());
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_newFilterReplacesPreviousAndPlainListClearsFilter() {
        // State transition: replace one tag filter with another, then clear the filter with plain listm.
        Meeting urgent = taggedMeeting("Urgent", "08/10/2026", "1400", "1500", "urgent");
        Meeting work = taggedMeeting("Work", "09/10/2026", "1400", "1500", "work");
        List.of(urgent, work).forEach(model::addMeeting);
        new ListMeetingCommand(NOON, Set.of(new Tag("urgent"))).execute(model);
        assertEquals(List.of(urgent), model.getMeetingList());
        new ListMeetingCommand(NOON, Set.of(new Tag("work"))).execute(model);
        assertEquals(List.of(work), model.getMeetingList());

        CommandResult result = command.execute(model);

        assertEquals(List.of(urgent, work), model.getMeetingList());
        assertEquals(ListMeetingCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
    }

    @Test
    public void execute_noMatches_displaysEmptyMeetingPanelWithCount() {
        // BVA: zero matches from a non-empty source must show an empty meeting panel and report count zero.
        model.addMeeting(meeting("Untagged", "08/10/2026", "1400", "1500"));
        CommandResult result = new ListMeetingCommand(NOON, Set.of(new Tag("missing"))).execute(model);
        assertTrue(model.getMeetingList().isEmpty());
        assertTrue(result.isShowMeetings());
        assertEquals(String.format(ListMeetingCommand.MESSAGE_FILTER_SUCCESS, 0), result.getFeedbackToUser());
        assertEquals(1, model.getAddressBook().getMeetingList().size());
    }

    @Test
    public void equals_comparesFilterAndClock() {
        // EP: equal filter and clock, changed filter, changed clock, null and another type.
        ListMeetingCommand filtered = new ListMeetingCommand(NOON, Set.of(new Tag("urgent")));
        assertEquals(filtered, filtered);
        assertEquals(filtered, new ListMeetingCommand(NOON, Set.of(new Tag("URGENT"))));
        assertFalse(filtered.equals(command));
        assertFalse(filtered.equals(new ListMeetingCommand(Clock.systemUTC(), Set.of(new Tag("urgent")))));
        assertFalse(filtered.equals(null));
        assertFalse(filtered.equals("listm"));
    }

    @Test
    public void execute_mixedMeetings_ordersEachGroupChronologically() {
        // EP: ongoing and future meetings precede finished meetings; dates span different days and months.
        Meeting yesterday = meeting("Yesterday", "07/10/2026", "1000", "1100");
        Meeting lastMonth = meeting("Last month", "30/09/2026", "1000", "1100");
        Meeting tomorrow = meeting("Tomorrow", "09/10/2026", "0900", "1000");
        Meeting ongoing = meeting("Ongoing", "08/10/2026", "1130", "1230");
        Meeting afternoon = meeting("Afternoon", "08/10/2026", "1500", "1600");
        // BVA: an end time exactly equal to now belongs to the finished group.
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
        // BVA: one meeting is the smallest non-empty list and must be displayed unchanged.
        Meeting onlyMeeting = meeting("Only meeting", "08/10/2026", "1500", "1600");
        model.addMeeting(onlyMeeting);
        command.execute(model);
        assertEquals(List.of(onlyMeeting), model.getMeetingList());
    }

    @Test
    public void execute_timeAdvances_refreshesFinishedGroup() {
        // BVA: advancing the clock to exactly a meeting's end time moves it into the finished group.
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
    public void execute_timeAdvances_existingViewReceivesOrderUpdates() {
        // BVA: at the exact end time, reordering must also notify observers of the existing view.
        Meeting earlier = meeting("Earlier", "08/10/2026", "1300", "1400");
        Meeting later = meeting("Later", "08/10/2026", "1500", "1600");
        List.of(later, earlier).forEach(model::addMeeting);
        AddressBook original = new AddressBook(model.getAddressBook());
        ObservableList<Meeting> displayedMeetings = model.getMeetingList();
        List<List<Meeting>> observedOrders = new ArrayList<>();
        displayedMeetings.addListener((ListChangeListener<Meeting>) change ->
                observedOrders.add(List.copyOf(change.getList())));
        assertEquals(List.of(later, earlier), displayedMeetings);

        command.execute(model);

        assertEquals(List.of(earlier, later), displayedMeetings);
        assertTrue(observedOrders.contains(List.of(earlier, later)));
        observedOrders.clear();

        Clock twoPm = Clock.fixed(Instant.parse("2026-10-08T06:00:00Z"), NOON.getZone());
        new ListMeetingCommand(twoPm).execute(model);

        assertEquals(List.of(later, earlier), displayedMeetings);
        assertTrue(observedOrders.contains(List.of(later, earlier)));
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_sameInstantDifferentZones_groupsUsingClockZone() {
        // EP: the same instant in different clock zones gives different local times for grouping meetings.
        Meeting morning = meeting("Morning", "08/10/2026", "0900", "1000");
        Meeting afternoon = meeting("Afternoon", "08/10/2026", "1500", "1600");
        List.of(afternoon, morning).forEach(model::addMeeting);
        AddressBook original = new AddressBook(model.getAddressBook());

        command.execute(model);
        assertEquals(List.of(afternoon, morning), model.getMeetingList());

        Clock utc = NOON.withZone(ZoneId.of("UTC"));
        new ListMeetingCommand(utc).execute(model);
        assertEquals(List.of(morning, afternoon), model.getMeetingList());
        assertEquals(original, model.getAddressBook());
    }

    @Test
    public void execute_equalDateAndTimes_ordersNamesIgnoringCase() {
        // EP: equal dates and times require the name tie-breaker; mixed-case names expose case-sensitive sorting.
        Meeting beta = meeting("Beta", "08/10/2026", "1500", "1600");
        Meeting alpha = meeting("alpha", "08/10/2026", "1500", "1600");
        List.of(beta, alpha).forEach(model::addMeeting);

        command.execute(model);

        assertEquals(List.of(alpha, beta), model.getMeetingList());
    }

    @Test
    public void execute_equalStartTimes_ordersByEndTimeThenName() {
        // EP: equal starts with different end times, then equal start and end times with different names.
        Meeting beta = meeting("Beta", "08/10/2026", "1500", "1700");
        Meeting alpha = meeting("Alpha", "08/10/2026", "1500", "1700");
        Meeting shorter = meeting("Shorter", "08/10/2026", "1500", "1600");
        List.of(beta, alpha, shorter).forEach(model::addMeeting);
        command.execute(model);
        assertEquals(List.of(shorter, alpha, beta), model.getMeetingList());
    }

    @Test
    public void execute_sourceChanges_existingViewUpdates() {
        // State transition: add, delete and reset the source while retaining the same observable view.
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
        // EP: case and spacing variants of the same meeting name are duplicates and must not create another row.
        Meeting meeting = meeting("Team meeting", "08/10/2026", "1500", "1600");
        model.addMeeting(meeting);
        Meeting duplicate = meeting(" TEAM   MEETING ", "08/10/2026", "1500", "1600");
        assertThrows(DuplicateMeetingException.class, () -> model.addMeeting(duplicate));
        command.execute(model);
        assertEquals(List.of(meeting), model.getMeetingList());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        // EP: null model, clock and ordering time are invalid inputs to their respective operations.
        assertThrows(NullPointerException.class, () -> command.execute(null));
        assertThrows(NullPointerException.class, () -> new ListMeetingCommand(null));
        assertThrows(NullPointerException.class, () -> model.updateMeetingListOrder(null));
    }

    private Meeting meeting(String name, String date, String start, String end) {
        return new Meeting(new MeetingName(name), new MeetingDate(date), new MeetingTime(start), new MeetingTime(end));
    }

    private Meeting taggedMeeting(String name, String date, String start, String end, String... tags) {
        Set<Tag> tagSet = Arrays.stream(tags).map(Tag::new).collect(Collectors.toSet());
        return new Meeting(new MeetingName(name), new MeetingDate(date), new MeetingTime(start), new MeetingTime(end),
                tagSet);
    }
}
