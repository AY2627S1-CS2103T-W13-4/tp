package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditMeetingCommand;
import seedu.address.logic.commands.EditMeetingCommand.EditMeetingDescriptor;
import seedu.address.model.meeting.MeetingDate;
import seedu.address.model.meeting.MeetingName;
import seedu.address.model.meeting.MeetingTime;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditMeetingDescriptorBuilder;

public class EditMeetingCommandParserTest {

    private static final String VALID_NAME = "Project Review";
    private static final String VALID_DATE = "08/10/2026";
    private static final String VALID_START_TIME = "1400";
    private static final String VALID_END_TIME = "1500";
    private static final String VALID_TAG_URGENT = "urgent";
    private static final String VALID_TAG_WORK = "work";

    private static final String NAME_DESC = " " + PREFIX_NAME + VALID_NAME;
    private static final String DATE_DESC = " " + PREFIX_DATE + VALID_DATE;
    private static final String START_TIME_DESC = " " + PREFIX_START_TIME + VALID_START_TIME;
    private static final String END_TIME_DESC = " " + PREFIX_END_TIME + VALID_END_TIME;
    private static final String TAG_DESC_URGENT = " " + PREFIX_TAG + VALID_TAG_URGENT;
    private static final String TAG_DESC_WORK = " " + PREFIX_TAG + VALID_TAG_WORK;
    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditMeetingCommand.MESSAGE_USAGE);

    private final EditMeetingCommandParser parser = new EditMeetingCommandParser();

    @Test
    public void parse_missingParts_failure() {
        assertParseFailure(parser, NAME_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1", EditMeetingCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        assertParseFailure(parser, "-1" + NAME_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0" + NAME_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 unexpected text", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 x/value", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1 " + PREFIX_NAME + "Project 2", MeetingName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 " + PREFIX_DATE + "31/02/2026", MeetingDate.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 " + PREFIX_START_TIME + "2500", MeetingTime.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 " + PREFIX_END_TIME, MeetingTime.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 " + PREFIX_TAG + "urgent!", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_DESC_URGENT + TAG_EMPTY, Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = Index.fromOneBased(2);
        String userInput = targetIndex.getOneBased() + END_TIME_DESC + NAME_DESC
                + START_TIME_DESC + TAG_DESC_URGENT + DATE_DESC + TAG_DESC_WORK;
        EditMeetingDescriptor descriptor = new EditMeetingDescriptorBuilder()
                .withName(VALID_NAME)
                .withDate(VALID_DATE)
                .withStartTime(VALID_START_TIME)
                .withEndTime(VALID_END_TIME)
                .withTags(VALID_TAG_URGENT, VALID_TAG_WORK)
                .build();

        assertParseSuccess(parser, userInput, new EditMeetingCommand(targetIndex, descriptor));
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = Index.fromOneBased(1);
        EditMeetingDescriptor descriptor = new EditMeetingDescriptorBuilder()
                .withDate(VALID_DATE)
                .withEndTime(VALID_END_TIME)
                .build();

        assertParseSuccess(parser, targetIndex.getOneBased() + DATE_DESC + END_TIME_DESC,
                new EditMeetingCommand(targetIndex, descriptor));
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        Index targetIndex = Index.fromOneBased(1);

        assertParseSuccess(parser, targetIndex.getOneBased() + NAME_DESC,
                new EditMeetingCommand(targetIndex,
                        new EditMeetingDescriptorBuilder().withName(VALID_NAME).build()));
        assertParseSuccess(parser, targetIndex.getOneBased() + DATE_DESC,
                new EditMeetingCommand(targetIndex,
                        new EditMeetingDescriptorBuilder().withDate(VALID_DATE).build()));
        assertParseSuccess(parser, targetIndex.getOneBased() + START_TIME_DESC,
                new EditMeetingCommand(targetIndex,
                        new EditMeetingDescriptorBuilder().withStartTime(VALID_START_TIME).build()));
        assertParseSuccess(parser, targetIndex.getOneBased() + END_TIME_DESC,
                new EditMeetingCommand(targetIndex,
                        new EditMeetingDescriptorBuilder().withEndTime(VALID_END_TIME).build()));
        assertParseSuccess(parser, targetIndex.getOneBased() + TAG_DESC_URGENT,
                new EditMeetingCommand(targetIndex,
                        new EditMeetingDescriptorBuilder().withTags(VALID_TAG_URGENT).build()));
        assertParseSuccess(parser, targetIndex.getOneBased() + TAG_EMPTY,
                new EditMeetingCommand(targetIndex,
                        new EditMeetingDescriptorBuilder().withTags().build()));
    }

    @Test
    public void parse_repeatedFields_failure() {
        String allFields = NAME_DESC + DATE_DESC + START_TIME_DESC + END_TIME_DESC;

        assertParseFailure(parser, "1" + NAME_DESC + allFields,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, "1" + DATE_DESC + allFields,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DATE));
        assertParseFailure(parser, "1" + START_TIME_DESC + allFields,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_START_TIME));
        assertParseFailure(parser, "1" + END_TIME_DESC + allFields,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_END_TIME));
    }
}
