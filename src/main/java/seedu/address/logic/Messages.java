package seedu.address.logic;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_MEETING_INDEX = "The meeting index provided is invalid!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_INDEX = "The contact index provided is invalid!\n%1$s";
    public static final String MESSAGE_CONTACT_LIST_NOT_VISIBLE =
            "Cannot edit or delete contacts while viewing meetings. Run listc to display contacts first.";
    public static final String MESSAGE_MEETING_LIST_NOT_VISIBLE =
            "Cannot edit or delete meetings while viewing contacts. Run listm to display meetings first.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d contact(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    public static final String MESSAGE_MEETING_OVERLAP =
            "This meeting overlaps with: %1$s.\nAdd force/ to the command to save it anyway.";
    public static final String MESSAGE_MEETING_OVERLAP_WARNING =
            "Warning: this meeting overlaps with: %1$s.";
    public static final String MESSAGE_PERSON_HIDDEN_BY_FILTER =
            "Note: this contact is hidden by the current filter. Run listc to show all contacts.";
    public static final String MESSAGE_MEETING_HIDDEN_BY_FILTER =
            "Note: this meeting is hidden by the current filter. Run listm to show all meetings.";
    public static final String MESSAGE_FORCE_TAKES_NO_VALUE = "force/ does not take a value.";

    private static final int MAX_OVERLAPS_SHOWN = 3;

    /**
     * Formats {@code overlaps} as a semicolon-separated list, showing at most three meetings
     * followed by a count of the rest.
     */
    public static String formatOverlappingMeetings(List<Meeting> overlaps) {
        assert !overlaps.isEmpty();

        String shown = overlaps.stream()
                .limit(MAX_OVERLAPS_SHOWN)
                .map(meeting -> meeting.getName() + " (" + meeting.getDate() + " "
                        + meeting.getStartTime() + "-" + meeting.getEndTime() + ")")
                .collect(Collectors.joining("; "));
        int hidden = overlaps.size() - MAX_OVERLAPS_SHOWN;
        return hidden > 0 ? shown + "; +" + hidden + " more" : shown;
    }

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

}
