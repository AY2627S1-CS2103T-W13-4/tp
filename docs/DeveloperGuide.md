---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# PingBook Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103T-W13-4/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-W13-4/tp/tree/master/src/main/java/seedu/address/MainApp.java)) manages the app's launch, initialization, and shutdown. See the [Main component](#main-component) section for details.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `deletec 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### Main component

The `Main` component consists of the [`Main`](https://github.com/AY2627S1-CS2103T-W13-4/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-W13-4/tp/tree/master/src/main/java/seedu/address/MainApp.java) classes. It is responsible for launching PingBook, initializing and connecting the other components, starting the UI, and saving the user preferences when the app shuts down.

`Main` is the entry point of the app. Unlike `MainApp`, it does not extend the JavaFX `Application` class. This avoids the JavaFX launcher error that would otherwise occur because PingBook does not use JavaFX through the Java module system. `Main` delegates the launch to `MainApp` by calling `Application.launch(MainApp.class, args)`.

The sequence diagram below shows how `Main` launches PingBook and how `MainApp` initializes and starts the other components.

<puml src="diagrams/MainSequenceDiagram.puml" alt="Initialization of PingBook by the Main component" />

`MainApp` extends `Application` and coordinates the app's lifecycle. During `init()`, it initializes and connects the components in the following order:

1. It loads the user preferences from `preferences.json`. If the file is missing or cannot be loaded, it uses default preferences instead and attempts to save them to the file.
1. It creates the `Storage` component using storage objects for `preferences.json` and `data/addressbook.json`.
1. It creates the `Model` component using the user preferences and the address book data from `Storage`. If the address book data file is missing, it uses sample data; if the file cannot be loaded, it uses an empty address book.
1. It creates the `Logic` component using `Model` and `Storage`.
1. It creates the `UI` component using `Logic` and the address book data file path from `Storage`.

After initialization, JavaFX calls `start(Stage)`, which starts the `UI` with the primary stage. When the app shuts down, JavaFX calls `stop()`, which saves the latest user preferences from `Model` through `Storage`.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-W13-4/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, `MeetingListPanel`, and `StatusBarFooter`. The `PersonListPanel` and `MeetingListPanel` display contacts and meetings respectively, using `PersonCard` and `MeetingCard` to represent individual entries. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-W13-4/tp/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-W13-4/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on classes in the `Model` component because it displays `Person` and `Meeting` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-W13-4/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("deletec 1")` API call as an example.

<puml src="diagrams/DeleteClientSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `deletec 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteClientCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command text is passed to an `AddressBookParser` object, which creates a parser that matches the command (e.g., `DeleteClientCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteClientCommand`) which is executed by the `LogicManager`.
   Before executing the command, `LogicManager` checks whether the command is compatible with the currently displayed panel. For example, if the meetings panel is displayed, attempting to execute a client command such as `deletec` will result in a `CommandException`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that the code can require several interactions between the command object and the `Model` to complete the operation. For example, in the sequence diagram above, the command retrieves the filtered person list from `Model`, selects the person at the requested index, and then asks `Model` to delete that person.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddClientCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddClientCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddClientCommandParser` and `DeleteClientCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-W13-4/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="1000" />

[Open the full-size model diagram](diagrams/ModelClassDiagram.png){no-validation}


The `Model` component is implemented by `ModelManager`. It:

* stores PingBook's contacts and meetings in the `AddressBook` data class. Contacts are represented by `Person` objects in a `UniquePersonList`, while meetings are represented by `Meeting` objects in a `MeetingList`.
* represents each `Meeting` with a `MeetingName`, a `MeetingDate`, two `MeetingTime` values (`startTime` and `endTime`), and zero or more `Tag` objects.
* exposes contacts selected by the current filter through `getFilteredPersonList()` as an unmodifiable `ObservableList<Person>` view that the UI can observe.
* maintains `filteredMeetings`, a `FilteredList<Meeting>` over the stored meeting list, and `sortedMeetings`, a `SortedList<Meeting>` over that filtered view. `updateFilteredMeetingList(...)` changes the filter, while `updateMeetingListOrder(...)` changes the display order without reordering stored meetings.
* exposes `sortedMeetings` through `getMeetingList()` as an unmodifiable `ObservableList<Meeting>`. The UI receives this observable view through the `Logic` component and updates when its contents or order change.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-W13-4/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="650" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* saves contacts and meetings (including their tags) together in the address book JSON file, as the `persons` and `meetings` arrays. Data files without `meetings` or meeting `tags` still load.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of contacts
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Secretaries have to juggle multiple contacts at once. These contacts are usually scattered across spreadsheets, notebooks, and memory. The app gives them a fast, reliable place to find the right contact in seconds.

**Persona**: Bob is a secretary at a fast-paced firm, supporting multiple department heads and sales leads. He is responsible for continuous schedule adjustments, external client contacts, and managing internal team members across projects. His client contact information and meeting schedules are scattered across messy spreadsheets and notebooks. Locating specific contact details or finding free calendar slots is inefficient and prone to error. As he is comfortable typing basic commands on a laptop, he wants a command-line interface application where all the information is stored.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …          | I want to …                                                           | So that I can…                                             |
|----------|-----------------|-----------------------------------------------------------------------|------------------------------------------------------------|
| `* * *`  | secretary       | view a list of contacts                                               | keep track of contacts                                     |
| `* * *`  | secretary       | add contacts with their email, mobile number, and name                | record contacts' information                               |
| `* * *`  | secretary       | delete contacts                                                       | keep the list free of information that is no longer useful |
| `* * *`  | secretary       | view a list of meetings                                               | focus entirely on immediate priorities                     |
| `* * *`  | secretary       | add meetings with their date and time                                 | record meetings                                            |
| `* * *`  | secretary       | delete meetings                                                       | remove canceled meetings                                   |
| `* *`    | secretary       | edit contact and meeting information                                  | keep contact and meeting details up to date                |
| `* *`    | secretary       | search for a contact or meeting by name                               | find the contacts or meetings I want                       |
| `* *`    | secretary       | add tags to contacts and meetings                                     | filter or search for groups of contacts or meetings easily |
| `* *`    | secretary       | be warned when meeting schedules overlap, and override                | know about clashes but still schedule when intended        |
| `* *`    | secretary       | clear all contacts and meetings                                       | remove outdated information quickly                        |
| `* *`    | first-time user | see a help guide containing all commands                              | know how to use the app                                    |
| `*`      | first-time user | **Proposed:** add command aliases                                     | type frequently used commands quickly                      |
| `*`      | secretary       | **Proposed:** see a contact's meeting history                         | know when we last met                                      |
| `*`      | secretary       | **Proposed:** record meeting outcomes after a meeting                 | remember what happened during the meeting                  |
| `*`      | secretary       | **Proposed:** view recently or frequently contacted contacts          | easily find contacts I am likely looking for               |
| `*`      | secretary       | **Proposed:** view when I am free                                     | know when to schedule a meeting                            |
| `*`      | secretary       | **Proposed:** add multiple emails and mobile numbers for each contact | track contacts with multiple contact details               |
| `*`      | secretary       | **Proposed:** view tasks for the day                                  | focus on immediate priorities                              |
| `*`      | secretary       | **Proposed:** add recurring tasks                                     | avoid adding the same task every time                      |

### Use cases

(For all use cases below, the **System** is the `PingBook` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC1 - View and filter contacts**

**MSS**

1. User requests to view contacts, optionally providing one or more tags.
2. PingBook displays the contacts matching all supplied tags.

Use case ends.

**Extensions**

* 1a. A supplied tag is invalid or empty.

    * 1a1. PingBook shows an error message.

      Use case ends.

**Use case: UC2 - Add contact**

**MSS**

1. User provides a name, phone number, email address, and optionally an address and tags.
2. PingBook adds the new contact.

Use case ends.

**Extensions**

* 1a. A required field is missing or a supplied field is invalid.

    * 1a1. PingBook shows an error message.

      Use case ends.

* 2a. The contact already exists.

    * 2a1. PingBook shows an error message and does not add the contact.

      Use case ends.

**Use case: UC3 - Edit contact**

**MSS**

1. User requests to edit a contact using its displayed index and provides one or more fields to edit.
2. PingBook replaces the supplied name, phone number, email address, address, or tags. A blank `a/` or `t/` removes the contact's address or all tags instead.

Use case ends.

**Extensions**

* 1a. The contacts panel is not active.

    * 1a1. PingBook shows an error message.

      Use case ends.

* 1b. The index is invalid.

    * 1b1. PingBook shows an error message.

      Use case ends.

* 1c. No field is provided, a supplied field is invalid (see "Field formats" in the User Guide), or a single-valued prefix is repeated.

    * 1c1. PingBook shows an error message.

      Use case ends.

* 2a. The edited contact duplicates another contact.

    * 2a1. PingBook shows an error message and does not edit the contact.

      Use case ends.

**Use case: UC4 - Delete contact**

**MSS**

1. User requests to delete a contact using its displayed index.
2. PingBook deletes the selected contact.

Use case ends.

**Extensions**

* 1a. The contacts panel is not active.

    * 1a1. PingBook shows an error message.

      Use case ends.

* 1b. The index is invalid.

    * 1b1. PingBook shows an error message.

      Use case ends.

**Use case: UC5 - Find contact**

**MSS**

1. User provides one or more name keywords.
2. PingBook displays contacts whose names contain any of the keywords, ignoring case.

Use case ends.

**Extensions**

* 1a. There are no name keywords provided.

    * 1a1. PingBook shows an error message.

      Use case ends.

**Use case: UC6 - View and filter meetings**

**MSS**

1. User requests to view meetings, optionally providing one or more tags.
2. PingBook displays the meetings matching all supplied tags, ordering meetings whose end time has not passed before those whose end time has passed.

Use case ends.

**Extensions**

* 1a. A supplied tag is invalid.

    * 1a1. PingBook shows an error message.

      Use case ends.

**Use case: UC7 - Add meeting**

**MSS**

1. User provides a name, date, starting time, ending time, and optionally tags for a new meeting.
2. PingBook validates the meeting and saves it.

Use case ends.

**Extensions**

* 1a. A required field is missing, a supplied field is invalid, or the ending time is not later than the starting time.

    * 1a1. PingBook shows an error message.

      Use case ends.

* 2a. The meeting is a duplicate of an existing meeting.

    * 2a1. PingBook shows an error message and does not save the meeting.

      Use case ends.

* 2b. The meeting overlaps an existing meeting and `force/` was not provided.

    * 2b1. PingBook shows an error message and does not save the meeting.

      Use case ends.

* 2c. The meeting overlaps an existing meeting and `force/` was provided.

    * 2c1. PingBook saves the meeting and shows a warning listing the overlaps.

      Use case ends.

**Use case: UC8 - Edit meeting**

**MSS**

1. User requests to edit a meeting using its displayed index and provides one or more fields to edit.
2. PingBook replaces the supplied name, date, times, or tags and keeps unspecified fields unchanged.

Use case ends.

**Extensions**

* 1a. The meetings panel is not active.

    * 1a1. PingBook shows an error message.

      Use case ends.

* 1b. The index is invalid.

    * 1b1. PingBook shows an error message.

      Use case ends.

* 1c. No field is provided, a supplied field is invalid, or the ending time is not later than the starting time.

    * 1c1. PingBook shows an error message.

      Use case ends.

* 2a. The edited meeting is a duplicate of another meeting.

    * 2a1. PingBook shows an error message and does not edit the meeting.

      Use case ends.

* 2b. The edited meeting overlaps another meeting and `force/` was not provided.

    * 2b1. PingBook shows an error message and does not edit the meeting.

      Use case ends.

* 2c. The edited meeting overlaps another meeting and `force/` was provided.

    * 2c1. PingBook edits the meeting and shows a warning listing the overlaps.

      Use case ends.

**Use case: UC9 - Delete meeting**

**MSS**

1. User requests to delete a meeting using its displayed index.
2. PingBook deletes the selected meeting.

Use case ends.

**Extensions**

* 1a. The meetings panel is not active.

    * 1a1. PingBook shows an error message.

      Use case ends.

* 1b. The index is invalid.

    * 1b1. PingBook shows an error message.

      Use case ends.

**Use case: UC10 - Find meeting**

**MSS**

1. User provides one or more name keywords.
2. PingBook displays meetings whose names contain any of the keywords, ignoring case.

Use case ends.

**Extensions**

* 1a. There are no name keywords provided.

    * 1a1. PingBook shows an error message.

      Use case ends.

**Use case: UC11 - View user guide**

**MSS**

1. User requests to view the user guide.
2. PingBook displays the help window containing a link to the user guide.

Use case ends.

**Use case: UC12 - Clear entries**

**MSS**

1. User requests to clear PingBook.
2. PingBook removes all contacts and meetings.

Use case ends.

**Use case: UC13 - Exit application**

**MSS**

1. User requests to exit PingBook.
2. PingBook closes the application.

Use case ends.

**Design notes**

* Two meetings overlap when they are on the same date and `start < otherEnd && otherStart < end`
  (`Meeting#overlapsWith`). Touching boundaries do not overlap. Finished meetings are included.
* `Model#getOverlappingMeetings(meeting, excluded)` lists the overlaps; `editm` passes the meeting being edited as
  `excluded` so it is not compared with itself.
* `force/` is a valueless flag parsed by `ParserUtil#parseForceFlag`. Overlaps are computed on demand and never
  stored, so the JSON format is unchanged and saved files that already contain overlaps still load.


### Non-Functional Requirements

1.  PingBook should run on Windows, macOS, and Linux with Java 25 installed, from a single JAR file without an installer or an internet connection.
2.  PingBook should support at least 1000 contacts and 1000 meetings,
with commands completing within 1 second under typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Contact and meeting data should be stored in a human-editable text file. 
5.  Contact details and meeting information should remain on the user's computer. PingBook should not transmit them to a remote server or require an online account.
6.  All functions should remain usable at a screen resolution of 1280 × 720 with 150% scaling. At 1920 × 1080 with 100% or 125% scaling, controls and information should not overlap or be cut off.
7.  Command results, validation errors, and meeting status should be communicated in text, so users do not have to rely on colours or icons alone to understand them.


### Glossary

* **Contact (Client)**: A person whose contact information are managed by the application
* **Meeting**: A scheduled event with a name and time recorded by the application
* **Command alias**: A user-defined alternative name or abbreviation for an application command

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Prerequisite: Run PingBook in a folder without an existing data file.

   2. Test case: Launch the application.<br>
      Expected: The GUI opens with the sample contacts displayed.

2. Saving window preferences

   1. Resize and move the window, then close and relaunch PingBook.<br>
      Expected: The latest window size and position are restored.

### Adding a contact

1. Adding a valid contact

   1. Test case: `addc n/Test contact p/91234567 e/test@example.com t/test`<br>
      Expected: The contact is added and displayed with the supplied tag.

2. Rejecting invalid or duplicate contacts

   1. Test cases: `addc n/Test contact p/invalid e/test@example.com`, followed by the same valid `addc` command twice.<br>
      Expected: Invalid data is rejected. The duplicate contact is rejected and only one copy is stored.

### Finding and filtering contacts

1. Filtering contacts by tags

   1. Prerequisite: Add contacts with different combinations of tags.

   2. Test case: `listc t/friend t/colleague`<br>
      Expected: Only contacts containing both tags are displayed. Tag matching is case-insensitive.

2. Finding contacts by name

   1. Prerequisite: Add contacts whose names contain different keywords.

   2. Test case: `findc alex david`<br>
      Expected: Contacts matching either keyword are displayed, ignoring case.

### Deleting a contact

1. Deleting a contact while all contacts are being shown

    1. Prerequisite: List all contacts using the `listc` command, with multiple contacts in the list.

    2. Test case: `deletec 1`<br>
       Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

    3. Test cases: `deletec 0`, `deletec`, and `deletec x`<br>
       Expected: No contact is deleted. The status message shows error details.

### Adding a meeting

1. Validating and adding meetings

   1. Test cases: Add a valid meeting, then try a missing field, an invalid date, and an end time earlier than the start time.<br>
      Expected: The valid meeting is added. Each invalid command is rejected with an error message.

2. Detecting meeting overlaps

   1. Prerequisite: Add one meeting on a particular date.

   2. Test cases: Add an overlapping meeting without `force/`, then repeat it with `force/`.<br>
      Expected: The first command is rejected with the conflicting meeting listed. The second command saves the meeting and displays a warning.

   3. Test case: Add a meeting that starts exactly when another meeting ends, without overlapping with any other meetings.<br>
      Expected: The meeting is accepted because touching boundaries do not overlap.

### Finding and filtering meetings

1. Filtering and ordering meetings

   1. Prerequisite: Add meetings with different tags, including one whose end time has passed and one whose end time is in the future.

   2. Test cases: `listm`, `listm t/work`, and `listm t/work t/urgent`<br>
      Expected: Meetings are filtered by all supplied tags. Meetings whose end time has not passed appear before meetings whose end time has passed.

### Editing a meeting

1. Editing a meeting

   1. Prerequisite: Display meetings using `listm` and ensure that the first meeting displayed does not overlap with other meetings.

   2. Test cases: `editm 1 t/updated` and `editm 1 t/`<br>
      Expected: The first command replaces the meeting's tags and the second removes all tags.

### Deleting a meeting

1. Deleting a meeting while all meetings are being shown

   1. Prerequisite: List all meetings using the `listm` command, with multiple meetings in the list.

   2. Test case: `deletem 1`<br>
      Expected: The first meeting is deleted from the list. The status message shows the deleted meeting's details.

   3. Test cases: `deletem 0`, `deletem`, and `deletem x`<br>
      Expected: No meeting is deleted. The status message shows error details.
