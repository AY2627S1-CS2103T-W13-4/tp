package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FORCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditMeetingCommand;
import seedu.address.logic.commands.EditMeetingCommand.EditMeetingDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates an {@code EditMeetingCommand}.
 */
public class EditMeetingCommandParser implements Parser<EditMeetingCommand> {

    /**
     * Parses the given arguments in the context of an {@code EditMeetingCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public EditMeetingCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_NAME, PREFIX_DATE, PREFIX_START_TIME, PREFIX_END_TIME, PREFIX_TAG,
                PREFIX_FORCE);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditMeetingCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_NAME, PREFIX_DATE, PREFIX_START_TIME, PREFIX_END_TIME, PREFIX_FORCE);

        EditMeetingDescriptor editMeetingDescriptor = new EditMeetingDescriptor();
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editMeetingDescriptor.setName(
                    ParserUtil.parseMeetingName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
            editMeetingDescriptor.setDate(
                    ParserUtil.parseMeetingDate(argMultimap.getValue(PREFIX_DATE).get()));
        }
        if (argMultimap.getValue(PREFIX_START_TIME).isPresent()) {
            editMeetingDescriptor.setStartTime(
                    ParserUtil.parseMeetingTime(argMultimap.getValue(PREFIX_START_TIME).get()));
        }
        if (argMultimap.getValue(PREFIX_END_TIME).isPresent()) {
            editMeetingDescriptor.setEndTime(
                    ParserUtil.parseMeetingTime(argMultimap.getValue(PREFIX_END_TIME).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editMeetingDescriptor::setTags);

        if (!editMeetingDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditMeetingCommand.MESSAGE_NOT_EDITED);
        }

        if (editMeetingDescriptor.getStartTime().isPresent() && editMeetingDescriptor.getEndTime().isPresent()
                && !Meeting.isValidTimeRange(editMeetingDescriptor.getStartTime().get(),
                        editMeetingDescriptor.getEndTime().get())) {
            throw new ParseException(Meeting.MESSAGE_INVALID_TIME_RANGE);
        }
        boolean isForced = ParserUtil.parseForceFlag(argMultimap);

        return new EditMeetingCommand(index, editMeetingDescriptor, isForced);
    }

    /**
     * Parses {@code tags} as replacement tags when at least one tag prefix is present.
     * A single empty tag value represents an empty replacement set.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagValues = tags.size() == 1 && tags.contains("")
                ? Collections.emptySet()
                : tags;
        return Optional.of(ParserUtil.parseTags(tagValues));
    }
}
