package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addRemark_success() {
        Remark remark = new Remark("Likes baseball");
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, remark);
        Person original = model.getFilteredPersonList().get(0);
        Person edited = new PersonBuilder(original).withRemark(remark.value).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, edited);

        String expectedMessage = String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS, Messages.format(edited));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clearRemark_success() {
        Person original = model.getFilteredPersonList().get(0);
        Person withRemark = new PersonBuilder(original).withRemark("Old note").build();
        model.setPerson(original, withRemark);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(withRemark, original);

        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        String expectedMessage = String.format(RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS,
                Messages.format(original));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person original = model.getFilteredPersonList().get(0);
        Person edited = new PersonBuilder(original).withRemark("Filtered person").build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, edited);

        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, edited.getRemark());
        String expectedMessage = String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS, Messages.format(edited));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_failure() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        RemarkCommand command = new RemarkCommand(invalidIndex, new Remark("Note"));
        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        RemarkCommand first = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("One"));
        assertTrue(first.equals(new RemarkCommand(INDEX_FIRST_PERSON, new Remark("One"))));
        assertFalse(first.equals(new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Two"))));
        assertFalse(first.equals(new RemarkCommand(Index.fromOneBased(2), new Remark("One"))));
        assertFalse(first.equals(null));
    }
}
