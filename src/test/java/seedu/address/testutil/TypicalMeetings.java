package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;

/**
 * A utility class containing a list of {@code Meeting} objects to be used in tests.
 */
public class TypicalMeetings {

    public static final Meeting CLIENT_REVIEW = new Meeting(new MeetingName("Alice Pauline"),
            new MeetingDate("20/09/2026"), new MeetingTime("1430"), new MeetingTime("1530"));
    public static final Meeting SALES_SYNC = new Meeting(new MeetingName("Benson Meier"),
            new MeetingDate("21/09/2026"), new MeetingTime("0900"), new MeetingTime("1000"));
    public static final Meeting QUARTERLY_PLANNING = new Meeting(new MeetingName("Carl Kurz"),
            new MeetingDate("22/09/2026"), new MeetingTime("1300"), new MeetingTime("1500"));

    private TypicalMeetings() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical persons and meetings.
     */
    public static AddressBook getTypicalAddressBookWithMeetings() {
        AddressBook ab = TypicalPersons.getTypicalAddressBook();
        for (Meeting meeting : getTypicalMeetings()) {
            ab.addMeeting(meeting);
        }
        return ab;
    }

    public static List<Meeting> getTypicalMeetings() {
        return new ArrayList<>(Arrays.asList(CLIENT_REVIEW, SALES_SYNC, QUARTERLY_PLANNING));
    }
}
