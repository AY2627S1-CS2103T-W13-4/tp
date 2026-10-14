package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedMeeting.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalMeetings.CLIENT_REVIEW;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;

public class JsonAdaptedMeetingTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_DATE = "31/02/2026";
    private static final String INVALID_TIME = "2400";

    private static final String VALID_NAME = CLIENT_REVIEW.getName().toString();
    private static final String VALID_DATE = CLIENT_REVIEW.getDate().toString();
    private static final String VALID_START_TIME = CLIENT_REVIEW.getStartTime().toString();
    private static final String VALID_END_TIME = CLIENT_REVIEW.getEndTime().toString();

    @Test
    public void toModelType_validMeetingDetails_returnsMeeting() throws Exception {
        JsonAdaptedMeeting meeting = new JsonAdaptedMeeting(CLIENT_REVIEW);
        assertEquals(CLIENT_REVIEW, meeting.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedMeeting meeting =
                new JsonAdaptedMeeting(INVALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME);
        String expectedMessage = MeetingName.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, meeting::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedMeeting meeting = new JsonAdaptedMeeting(null, VALID_DATE, VALID_START_TIME, VALID_END_TIME);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, MeetingName.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, meeting::toModelType);
    }

    @Test
    public void toModelType_invalidDate_throwsIllegalValueException() {
        JsonAdaptedMeeting meeting =
                new JsonAdaptedMeeting(VALID_NAME, INVALID_DATE, VALID_START_TIME, VALID_END_TIME);
        String expectedMessage = MeetingDate.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, meeting::toModelType);
    }

    @Test
    public void toModelType_nullDate_throwsIllegalValueException() {
        JsonAdaptedMeeting meeting = new JsonAdaptedMeeting(VALID_NAME, null, VALID_START_TIME, VALID_END_TIME);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, MeetingDate.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, meeting::toModelType);
    }

    @Test
    public void toModelType_invalidStartTime_throwsIllegalValueException() {
        JsonAdaptedMeeting meeting =
                new JsonAdaptedMeeting(VALID_NAME, VALID_DATE, INVALID_TIME, VALID_END_TIME);
        String expectedMessage = MeetingTime.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, meeting::toModelType);
    }

    @Test
    public void toModelType_nullStartTime_throwsIllegalValueException() {
        JsonAdaptedMeeting meeting = new JsonAdaptedMeeting(VALID_NAME, VALID_DATE, null, VALID_END_TIME);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, MeetingTime.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, meeting::toModelType);
    }

    @Test
    public void toModelType_invalidEndTime_throwsIllegalValueException() {
        JsonAdaptedMeeting meeting =
                new JsonAdaptedMeeting(VALID_NAME, VALID_DATE, VALID_START_TIME, INVALID_TIME);
        String expectedMessage = MeetingTime.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, meeting::toModelType);
    }

    @Test
    public void toModelType_nullEndTime_throwsIllegalValueException() {
        JsonAdaptedMeeting meeting = new JsonAdaptedMeeting(VALID_NAME, VALID_DATE, VALID_START_TIME, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, MeetingTime.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, meeting::toModelType);
    }

}
