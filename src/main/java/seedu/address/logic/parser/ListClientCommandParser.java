package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import seedu.address.logic.commands.ListClientCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses the arguments of the listc command.
 */
public class ListClientCommandParser implements Parser<ListClientCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ListClientCommand
     * and returns an ListClientCommand object for execution.
     *
     * @param args The text to be parsed.
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public ListClientCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, CliSyntax.getAllPrefixes());
        if (!argMultimap.getPreamble().isEmpty() || !argMultimap.containsOnlyPrefixes(PREFIX_TAG)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListClientCommand.MESSAGE_USAGE));
        }
        Set<Tag> tags = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        return new ListClientCommand(tags);
    }
}
