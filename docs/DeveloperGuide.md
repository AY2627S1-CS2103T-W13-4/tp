---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

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

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


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

| Priority | As a …          | I want to …                                              | So that I can…                                                |
|----------|-----------------|----------------------------------------------------------|---------------------------------------------------------------|
| `* * *`  | secretary       | view a list of clients                                   | keep track of clients                                         |
| `* * *`  | secretary       | add clients with their email, mobile number, and name    | record clients' information                                   |
| `* * *`  | secretary       | delete clients                                           | keep the list free of information that is no longer useful    |
| `* * *`  | secretary       | delete meetings                                          | remove canceled meetings                                      |
| `* * *`  | secretary       | add meetings with their time, place, and attendees       | record meetings                                               |
| `* * *`  | secretary       | view a list of meetings                                  | focus entirely on immediate priorities                        |
| `* * *`  | first-time user | see a help guide containing all commands                 | know how to use the app                                       |
| `* * *`  | first-time user | see a client's meeting history                           | know when we last met                                         |
| `* * *`  | first-time user | add command aliases                                      | type frequently used commands quickly                         |
| `* * *`  | first-time user | record meeting outcomes after a meeting                  | remember what happened during the meeting                     |
| `* * *`  | first-time user | search for a client by name, company, or number          | find the clients I want                                       |
| `* * *`  | first-time user | receive a warning when meeting schedules overlap         | know if meeting schedules clash                               |
| `* *`    | secretary       | add tags to clients                                      | filter or search for groups of clients easily                 |
| `* *`    | secretary       | mark clients as favorite or pinned                       | easily access important clients                               |
| `* *`    | secretary       | view recently or frequently contacted clients            | easily find clients I am likely looking for                   |
| `* *`    | secretary       | edit client information                                  | keep client details up to date                                |
| `* *`    | secretary       | view when I am free                                      | know when to schedule a meeting                               |
| `* *`    | secretary       | add multiple emails and mobile numbers for each client   | track clients with multiple contact details                   |
| `* *`    | secretary       | view tasks for the day                                   | focus on immediate priorities                                 |
| `* *`    | secretary       | add recurring tasks                                      | avoid adding the same task every time                         |

### Use cases

(For all use cases below, the **System** is the `PingBook` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC1 - View clients**

**MSS**

1.  User requests to view clients.
2.  PingBook shows a list of clients.

    Use case ends.

**Use case: UC2 - Add client**

**MSS**

1.  User provides name, phone number, and email for a new client.
2.  PingBook adds the new client.

Use case ends.


**Extensions**

* 1a. There is a missing field.

    * 1a1. PingBook shows an error message.
  
      Use case ends.

**Use case: UC3 - Delete client**

**MSS**

1.  User requests to delete a client.
2.  PingBook asks the user for confirmation.
3.  User confirms to delete.
4.  PingBook deletes the client.

    Use case ends.

**Extensions**

* 1a. The client does not exist.

    * 1a1. PingBook shows an error message.

      Use case ends.

* 3a. The user does not confirm.

     Use case ends.

**Use case: UC4 - View meetings**

**MSS**

1.  User requests to view meetings.
2.  PingBook shows a list of meetings.

    Use case ends.

**Use case: UC5 - Add meeting**

**MSS**

1.  User provides name, starting time, and ending time for a new meeting.
2.  PingBook adds the new meeting.

    Use case ends.

**Extensions**

* 1a. There is a missing field.

    * 1a1. PingBook shows an error message.

      Use case ends.

* 1b. Starting or ending time is invalid.

    * 1b1. PingBook shows an error message.

      Use case ends.

* 1c. Starting time is after the ending time.

    * 1c1. PingBook shows an error message.

      Use case ends.

**Use case: UC6 - Delete meeting**

**MSS**

1.  User requests to delete a meeting.
2.  PingBook asks the user for confirmation.
3.  User confirms to delete.
4.  PingBook deletes the meeting.

    Use case ends.

**Extensions**

* 1a. The meeting does not exist.

    * 1a1. PingBook shows an error message.

      Use case ends.

* 3a. The user does not confirm.

  Use case ends.

**Use case: UC7 - Tag client**

**MSS**

1.  User requests to add a tag to a client.
2.  PingBook adds a tag to the client.

    Use case ends.

**Extensions**

* 1a. The client does not exist.

    * 1a1. PingBook shows an error message.

      Use case ends.

* 1b. Tag is not provided or empty.

    * 1b1. PingBook shows an error message.

      Use case ends.

**Use case: UC8 - Pin client**

**MSS**

1.  User requests to pin a client.
2.  PingBook pins the client.

    Use case ends.

**Extensions**

* 1a. The client does not exist.

    * 1a1. PingBook shows an error message.

      Use case ends.

**Use case: UC9 - Sort clients based on a criterion**

**MSS**

1.  User requests to sort a client based on a criterion.
2.  PingBook displays a list of clients sorted based on the criterion.

    Use case ends.

**Extensions**

* 1a. Criterion is not provided, empty, or not defined by PingBook.

    * 1a1. PingBook shows an error message.

      Use case ends.

**Use case: UC10 - Edit client**

**MSS**

1.  User requests to edit a client.
2.  PingBook displays the client to be edited.
3.  User requests to edit the fields. 
4.  PingBook edits the fields.

    Use case ends.

**Extensions**

* 1a. The client does not exist.

    * 1a1. PingBook shows an error message.

      Use case ends.

* 3a. The fields requested for edit are invalid or empty.

    * 3a1. PingBook shows an error message.

      Use case ends.

**Use case: UC11 - Tag client**

**MSS**

1. User selects a client.
2. User provides a tag for the client.
3. PingBook adds the tag to the client.

Use case ends.

**Use case: UC12 - Add contact details**

**MSS**

1. User selects a client.
2. User provides an email address or phone number for the client.
3. PingBook adds the contact detail to the client.

Use case ends.

**Extensions**

* 2a. The contact detail is invalid.

* 2a1. PingBook shows an error message.

Use case ends.

* 2b. The contact detail already exists for the client.

* 2b1. PingBook shows an error message.

Use case ends.

**Use case: UC13 - View prioritised client list**

**MSS**

1. User requests to view the client list.
2. PingBook displays the clients, with recently contacted clients at the top.

Use case ends.

**Extensions**

* 2a. A client has no contact history.

* 2a1. PingBook places the client according to the available information.

Use case ends.

**Use case: UC14 - View user guide**

**MSS**

1. User requests to view the user guide.
2. PingBook displays a guide containing the basic commands.
3. User reads the guide.

Use case ends.

**Extensions**

* 1a. The user guide is unavailable.

* 1a1. PingBook shows an error message.

Use case ends.

**Use case: UC15 - Create command alias**

**MSS**

1. User provides an existing command and a new alias.
2. PingBook creates the alias for the command.
3. User can use the alias to execute the command.

Use case ends.

**Extensions**

* 1a. The command does not exist.

* 1a1. PingBook shows an error message.

Use case ends.

* 1b. The alias is already in use.

* 1b1. PingBook shows an error message.

Use case ends.

**Use case: UC16 - View coworker's meetings**

**MSS**

1. User provides the name of a coworker.
2. PingBook displays the coworker's scheduled meetings.
3. User views the coworker's schedule.

Use case ends.

**Extensions**

* 1a. The coworker does not exist.

* 1a1. PingBook shows an error message.

Use case ends.

* 2a. The coworker has no scheduled meetings.

* 2a1. PingBook informs the user that the coworker has no scheduled meetings.

Use case ends.

**Use case: UC17 - Detect meeting overlap**

**MSS**

1. User provides the details of a new meeting.
2. PingBook checks the new meeting against existing meetings.
3. PingBook detects that there is no overlap.
4. PingBook adds the new meeting.

Use case ends.

**Extensions**

* 3a. The new meeting overlaps with an existing meeting.

* 3a1. PingBook shows a warning about the scheduling conflict.

* 3a2. User provides a different meeting time.

* 3a3. PingBook checks the new meeting against existing meetings again.

Use case resumes at step 3.

**Use case: UC18 - Receive meeting alert**

**MSS**

1. User has an upcoming meeting.
2. PingBook detects that the meeting is approaching.
3. PingBook displays an alert containing the meeting details.
4. User views the alert.

Use case ends.

**Extensions**

* 1a. There are no upcoming meetings.

* 1a1. PingBook does not display an alert.

Use case ends.

**Use case: UC19 - Prioritise task**

**MSS**

1. User selects a task.
2. User provides an importance level or tag for the task.
3. PingBook assigns the importance level or tag to the task.
4. PingBook displays the task with its assigned priority.

Use case ends.

**Extensions**

* 2a. The importance level or tag is invalid.

* 2a1. PingBook shows an error message.

Use case ends.

**Use case: UC20 - Record meeting outcome**

**MSS**

1. User selects a completed meeting.
2. User provides the outcome of the meeting.
3. User provides any decisions or follow-up actions.
4. PingBook saves the meeting outcome.

Use case ends.

**Extensions**

* 2a. The meeting outcome is empty.

* 2a1. PingBook shows an error message.

Use case ends.

**Use case: UC21 - View client meeting history**

**MSS**

1. User selects a client.
2. User requests to view the client's meeting history.
3. PingBook displays the client's meetings in chronological order.
4. User views the meeting history.

Use case ends.

**Extensions**

* 1a. The client does not exist.

* 1a1. PingBook shows an error message.

Use case ends.

* 3a. The client has no meeting history.

* 3a1. PingBook informs the user that there are no previous meetings with the client.

Use case ends.



### Non-Functional Requirements

1.  PingBook should run on Windows, macOS, and Linux with Java 25 installed, from a single JAR file without an installer or an internet connection.
2.  PingBook should support at least 1000 clients and 1000 meetings,
with commands completing within 1 second under typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Client and meeting data should be stored in a human-editable text file. 
5.  Client contact details and meeting information should remain on the user's computer. PingBook should not transmit them to a remote server or require an online account.
6.  All functions should remain usable at a screen resolution of 1280 × 720 with 150% scaling. At 1920 × 1080 with 100% or 125% scaling, controls and information should not overlap or be cut off.
7.  Command results, validation errors, and meeting status should be communicated in text, so users do not have to rely on colours or icons alone to understand them.

*{More to be added}*

### Glossary

* **Client**: A person whose contact information and meeting records are managed by the application
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

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
