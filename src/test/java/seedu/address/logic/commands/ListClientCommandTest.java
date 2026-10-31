package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalPersons.getTypicalPersons;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.tag.Tag;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListClientCommand.
 */
public class ListClientCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        // EP: an empty tag set on an already unfiltered list preserves all contacts.
        assertCommandSuccess(new ListClientCommand(new HashSet<>()), model,
                String.format(ListClientCommand.MESSAGE_SUCCESS_NO_TAGS, model.getFilteredPersonListSize()),
                expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        // State transition: an empty tag filter clears the current restriction and restores every contact.
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListClientCommand(new HashSet<>()), model,
                String.format(ListClientCommand.MESSAGE_SUCCESS_NO_TAGS, expectedModel.getFilteredPersonListSize()),
                expectedModel);
    }

    @Test
    public void execute_withTags_showsMatchingClients() throws Exception {
        // EP: a non-empty tag set keeps contacts containing all requested tags and excludes partial matches.
        Set<Tag> tags = Set.of(new Tag("friends"));
        ListClientCommand command = new ListClientCommand(tags);

        CommandResult result = command.execute(model);

        String formattedTags = tags.stream().map(tag -> tag.tagName).sorted().collect(Collectors.joining(", "));
        String expectedMessage = String.format(ListClientCommand.MESSAGE_SUCCESS,
                model.getFilteredPersonListSize(), formattedTags);
        assertEquals(new CommandResult(expectedMessage), result);
        assertEquals(List.of(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_withEmptyTags_showsAllClients() throws Exception {
        // EP: the empty tag partition is the explicit no-filter case and must display the complete list.
        ListClientCommand command = new ListClientCommand(Set.of());

        CommandResult result = command.execute(model);

        String expectedMessage = String.format(ListClientCommand.MESSAGE_SUCCESS_NO_TAGS,
                model.getFilteredPersonListSize());
        assertEquals(new CommandResult(expectedMessage), result);
        assertEquals(getTypicalPersons(), model.getFilteredPersonList());
    }

    @Test
    public void constructor_nullTags_throwsAssertionError() {
        // EP: null is outside the valid tag-set partition and is rejected at construction time.
        assertThrows(NullPointerException.class, () -> new ListClientCommand(null));
    }

    @Test
    public void equals() {
        // EP: equality depends on the complete tag set; null and unrelated object types are separate partitions.
        Set<Tag> friendsTag = Set.of(new Tag("friends"));
        Set<Tag> colleaguesTag = Set.of(new Tag("colleagues"));
        ListClientCommand listFirstCommand = new ListClientCommand(friendsTag);
        ListClientCommand listSecondCommand = new ListClientCommand(colleaguesTag);

        // same object -> returns true
        assertTrue(listFirstCommand.equals(listFirstCommand));

        // same values -> returns true
        ListClientCommand listFirstCommandCopy = new ListClientCommand(friendsTag);
        assertTrue(listFirstCommand.equals(listFirstCommandCopy));

        // different types -> returns false
        assertFalse(listFirstCommand.equals(1));

        // null -> returns false
        assertFalse(listFirstCommand.equals(null));

        // different tags -> returns false
        assertFalse(listFirstCommand.equals(listSecondCommand));
    }

    @Test
    public void toStringMethod() {
        // EP: the command representation includes the tag set that determines filtering behavior.
        Set<Tag> tags = Set.of(new Tag("friends"));
        ListClientCommand listClientCommand = new ListClientCommand(tags);
        String expected = ListClientCommand.class.getCanonicalName() + "{tags=" + tags + "}";

        assertEquals(expected, listClientCommand.toString());
    }
}
