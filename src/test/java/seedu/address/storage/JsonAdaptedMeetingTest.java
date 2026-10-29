package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedMeeting.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalMeetings.CLIENT_REVIEW;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.tag.Tag;

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
    public void toModelType_taggedMeeting_preservesTagsThroughJson() throws Exception {
        // Round-trip testing: serializing and deserializing a tagged meeting must preserve its values and tags.
        Meeting tagged = new Meeting(CLIENT_REVIEW.getName(), CLIENT_REVIEW.getDate(),
                CLIENT_REVIEW.getStartTime(), CLIENT_REVIEW.getEndTime(),
                Set.of(new Tag("urgent"), new Tag("work")));
        String json = JsonUtil.toJsonString(new JsonAdaptedMeeting(tagged));
        Meeting restored = JsonUtil.fromJsonString(json, JsonAdaptedMeeting.class).toModelType();
        assertEquals(tagged, restored);
        assertEquals(tagged.getTags(), restored.getTags());
    }

    @Test
    public void toModelType_missingOrNullTags_loadsUntaggedMeeting() throws Exception {
        // EP: legacy JSON with an absent tags field and JSON with an explicit null both load as untagged meetings.
        String details = "{\"name\":\"Client Review\",\"date\":\"15/10/2026\","
                + "\"startTime\":\"0900\",\"endTime\":\"1000\"";
        Meeting missingTags = JsonUtil.fromJsonString(details + "}", JsonAdaptedMeeting.class).toModelType();
        Meeting nullTags = JsonUtil.fromJsonString(details + ",\"tags\":null}",
                JsonAdaptedMeeting.class).toModelType();
        assertTrue(missingTags.getTags().isEmpty());
        assertEquals(missingTags, nullTags);
    }

    @Test
    public void toModelType_duplicateTags_storesOnce() throws Exception {
        // EP: repeated identical tag entries must normalize to a single tag in the model.
        JsonAdaptedMeeting meeting = new JsonAdaptedMeeting(VALID_NAME, VALID_DATE, VALID_START_TIME, VALID_END_TIME,
                List.of(new JsonAdaptedTag("urgent"), new JsonAdaptedTag("urgent")));
        assertEquals(Set.of(new Tag("urgent")), meeting.toModelType().getTags());
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        // EP: empty, punctuated, multiword and null tag names are invalid stored values.
        for (String invalidTag : Arrays.asList("", "urgent!", "very urgent", null)) {
            JsonAdaptedMeeting meeting = new JsonAdaptedMeeting(VALID_NAME, VALID_DATE, VALID_START_TIME,
                    VALID_END_TIME, List.of(new JsonAdaptedTag(invalidTag)));
            assertThrows(IllegalValueException.class, Tag.MESSAGE_CONSTRAINTS, meeting::toModelType);
        }
        // EP: a null tag entry is invalid as well as an entry with a null tag name.
        JsonAdaptedMeeting nullTag = new JsonAdaptedMeeting(VALID_NAME, VALID_DATE, VALID_START_TIME,
                VALID_END_TIME, Arrays.asList((JsonAdaptedTag) null));
        assertThrows(IllegalValueException.class, Tag.MESSAGE_CONSTRAINTS, nullTag::toModelType);
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
