---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# PingBook User Guide

PingBook is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, PingBook can help you manage contacts faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-W13-4/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your PingBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar pingbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `listc t/colleague` : Lists all contacts that have the tag `colleague`.

   * `addc n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to PingBook.

   * `deletec 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all entries.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `addc n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `[t/TAG]... ` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extra parameters that are defined by PingBook but not used for the command are rejected.<br>For example, `deletec 1 t/friend` is invalid.

* Extra parameters for commands that take no parameters, such as `help`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Field formats

Every command that adds, edits or filters by a field checks the value against the rules below. A value that breaks a
rule is rejected and nothing is changed.

Field | Prefix | Used by | Accepted | Rejected examples
------|--------|---------|----------|------------------
Contact name | `n/` | `addc`, `editc`, `findc` | English letters, digits and spaces only, starting with a letter or digit. | `Anne-Marie Tan` (hyphen), `Ravi s/o Kumar` (`/`), `Dr. Lim` (full stop)
Phone number | `p/` | `addc`, `editc` | Digits only, at least 3 digits long, with no upper limit. | `12` (too short), `+6591234567` (`+`), `9123 4567` (space), `9123-4567` (hyphen)
Email | `e/` | `addc`, `editc` | `local-part@domain`. The local part uses letters, digits and the special characters `+`, `_`, `.`, `-`, and cannot start or end with a special character. The domain is made up of labels separated by periods; each label starts and ends with a letter or digit, and the last label is at least 2 characters long. | `john.example.com` (no `@`), `john@example.c` (last label too short), `-john@example.com` (starts with `-`), `john@exam_ple.com` (`_` in the domain)
Address | `a/` | `addc`, `editc` | Any text, as long as it is not blank.<br>In `editc` only, a blank `a/` is also accepted and removes the address. | In `addc`, a blank `a/` (leave `a/` out to add a contact with no address)
Meeting name | `n/` | `addm`, `editm`, `findm` | English letters, spaces, hyphens (`-`) and apostrophes (`'`), with at least one letter. Repeated spaces are treated as one. | `Meeting123` (digits), `12345` (no letters), `Meeting!` (`!`)
Date | `d/` | `addm`, `editm` | A real calendar date in `DD/MM/YYYY` format. | `31/04/2026` (no such date), `29/02/2025` (not a leap year), `1/1/2026` (not two digits), `15-10-2026` (wrong separator)
Start time, end time | `st/`, `et/` | `addm`, `editm` | 24-hour `HHMM`, from `0000` to `2359`. The end time must be later than the start time. | `900` (three digits), `09:00` (colon), `2400` (no such time), `1260` (no such time)
Tag | `t/` | `addc`, `editc`, `addm`, `editm`, `listc`, `listm` | Letters and digits only, with no spaces. Tags are case-sensitive when stored, but `listc` and `listm` ignore case when filtering.<br>In `editc` and `editm` only, a blank `t/` is also accepted and removes all tags. | `best friend` (space), `vip!` (`!`), and a blank `t/` in `addc`, `addm`, `listc` or `listm`

* Spaces before and after a value are ignored.
* `findc` and `findm` check every keyword against the name rule above, so `findc Anne-Marie` and `findm 2026` are rejected.
* Contact names are stricter than meeting names. A meeting name allows hyphens (`-`) and apostrophes (`'`) but no
  digits, while a contact name allows digits but no hyphens or apostrophes.
* Each prefix can be used **at most once** in a command, except `t/`, which can be repeated. For example,
  `p/91234567 p/92345678` is rejected with the message
  `Multiple values specified for the following single-valued field(s): p/`.
* `force/` is a flag that takes no value, and it can be used at most once.
* PingBook reads a prefix such as `t/` or `p/` wherever it follows a space, so avoid typing a space followed by a
  prefix inside a value (for example, an address containing ` t/`), because the value is split there.

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a contact: `addc`

Adds a contact to PingBook.

Format: `addc n/NAME p/PHONE_NUMBER e/EMAIL [a/ADDRESS] [t/TAG]... `

* Each value must follow the [field formats](#field-formats).
* A contact is considered a duplicate if another contact has exactly the same name. Name comparison is case-sensitive, so names with different capitalisation are treated as distinct.
* For example, `John Doe` and `john doe` are treated as different names, while two contacts named `John Doe` are considered duplicates.

<box type="tip" seamless>

**Tip:** A contact can have any number of tags, including zero. The address is optional and can be left out.
</box>

Any active `listc`/`findc` filter stays active after adding. If the new contact does not match the filter, they are
saved but hidden, and the success message ends with a note saying so; run `listc` to show all contacts.

Examples:
* `addc n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `addc n/Mary Lim p/87654321 e/marylim@example.com t/premium` (no address)
* `addc n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Adding a meeting: `addm`

Adds a meeting to PingBook and displays the Meetings panel.

Format: `addm n/NAME d/DATE st/START_TIME et/END_TIME [t/TAG]... [force/]`

* Name, date, start time and end time are required. Each value must follow the [field formats](#field-formats).
* If the new meeting overlaps an existing meeting on the same date, it is not added and the error lists the
  conflicting meetings. Add `force/` (with no value, and used at most once) to add it anyway; the success message
  then ends with a warning listing the overlapping meetings. Using `force/` when nothing overlaps has no effect.
* Meetings overlap when they are on the same date and their time ranges intersect. A meeting that starts exactly
  when another ends (for example `1400`-`1500` and `1500`-`1600`) does not overlap. Finished meetings are included.
* Tags are optional. Use one `t/TAG` per tag. Repeated identical tags are stored once.
* Tags appear on the meeting card and are saved with the meeting. Older saved meetings without tags still load normally.
* Any active `listm` tag filter stays active after adding. If the new meeting does not match the filter, it is saved
  but hidden, and the success message ends with a note saying so; run `listm` to show all meetings.
* A meeting is considered a duplicate if its name, date, start time, and end time are the same as those of an
  existing meeting, regardless of tags. Name comparison is case-insensitive, and repeated spaces in names are ignored.

Examples:
* `addm n/Project Review d/08/10/2026 st/1400 et/1500`
* `addm d/14/10/2026 n/Client Check-in et/1030 st/0930`
* `addm n/Project Review d/25/10/2026 st/1400 et/1500 t/urgent t/work`
* `addm n/Late Call d/08/10/2026 st/1430 et/1530 force/` adds the meeting even if it overlaps an existing one.

### Editing a contact: `editc`

Edits an existing contact in PingBook.

Format: `editc INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]... `

* Edits the contact at the specified `INDEX`. The index refers to the index number shown in the displayed contact list. The index **must be a positive integer** 1, 2, 3, ...
* The contacts panel must be active. If you are viewing meetings, run `listc` or `findc KEYWORD` first.
* At least one of the optional fields must be provided.
* Each value must follow the [field formats](#field-formats). For example, a phone number needs at least 3 digits and cannot contain `+`, spaces or hyphens, and a name cannot contain hyphens.
* `n/`, `p/`, `e/` and `a/` can each be used at most once in the same command. Only `t/` can be repeated.
* Existing values will be updated to the input values.
* When editing tags, all of the contact's existing tags are removed; adding tags is not cumulative.
* To remove all of a contact's tags, enter `t/` without a tag after it.
* To remove a contact's address, enter `a/` without an address after it. The contact then has no address, as if they were added without one. Removing the address of a contact who already has none has no effect.
* Any active `listc`/`findc` filter stays active after editing.
  Editing a contact so that they no longer match the filter hides them from the displayed list, and the success message
  ends with a note saying so; run `listc` to show all contacts again.

Examples:
*  `editc 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st contact to be `91234567` and `johndoe@example.com` respectively.
*  `editc 2 n/Betsy Crower t/` Edits the name of the 2nd contact to be `Betsy Crower` and clears all existing tags.
*  `editc 3 a/` Removes the address of the 3rd contact.

These commands are rejected and leave the contact unchanged:

*  `editc 1 p/12` (the phone number has fewer than 3 digits)
*  `editc 1 p/+6591234567` (the phone number contains `+`)
*  `editc 1 n/Anne-Marie Tan` (the name contains a hyphen)
*  `editc 1 p/91234567 p/92345678` (`p/` is used twice)

### Editing a meeting: `editm`

Edits an existing meeting in PingBook.

Format: `editm MEETING_INDEX [n/NAME] [d/DATE] [st/START_TIME] [et/END_TIME] [t/TAG]... [force/]`

* Edits the meeting at the specified `MEETING_INDEX` in the displayed meeting list.
* The Meetings panel must be active. Run `listm` before editing a meeting.
* The index must be a positive integer: 1, 2, 3, ...
* At least one field must be provided.
* Fields that are not provided keep their existing values.
* Each value must follow the [field formats](#field-formats).
* When editing tags, all existing tags are replaced; adding tags is not cumulative.
* To remove all tags, enter `t/` without a tag after it.
* An edit that would make two meetings identical is rejected.
* The edited meeting must still end after it starts, and it is checked for overlaps with the other meetings (not
  with its own previous version) in the same way as `addm`. Add `force/` to save an overlapping edit anyway.
* Editing a meeting's date or time may move it to a different position in the sorted meeting list.
* Any active `listm` tag filter stays active after editing. An edit that makes the meeting stop matching the filter hides it,
  and the success message ends with a note saying so; run `listm` to show all meetings.

Examples:
* `editm 1 d/25/10/2026` changes only the date of the 1st displayed meeting.
* `editm 2 n/Client Review st/1500 et/1600` changes the name and times of the 2nd displayed meeting.
* `editm 3 t/urgent t/work` replaces all tags of the 3rd displayed meeting with `urgent` and `work`.
* `editm 1 t/` removes all tags from the 1st displayed meeting.

### Listing and filtering contacts: `listc`

Shows a list of all contacts in PingBook that match the given tags.

Format: `listc [t/TAG]...`

* `TAG` is optional and there can be multiple tags.
* Use spaces before each `t/TAG` argument. A tab alone cannot separate the arguments.
* Tags follow the [field formats](#field-formats), so empty tags (`listc t/`) are rejected.
* The command will only accept `TAG` fields. Other fields such as `NAME`, `DATE` are rejected.
* The command is case-sensitive. `listC` or `Listc` are considered invalid.
* A contact is shown only if they have all the tags given.
* If there are no tags provided, PingBook will display all contacts and a success message detailing the number of contacts listed.
* If there are tags provided, PingBook will display all matching contacts and a success message detailing the number of contacts listed and the tags given.

Examples:
* `listc`
* `listc t/friend t/colleagues`

### Listing and filtering meetings: `listm`

Shows all saved meetings in the Meetings panel, with numbered rows containing the name, date, start/end times and tags.

Format: `listm [t/TAG]...`

* Plain `listm` displays all meetings and clears any previous meeting filter.
* Supply one or more `t/TAG` arguments to display meetings containing **all** supplied tags.
  Matching uses complete tag names and ignores case: `t/urgent` matches `Urgent`, but does not match `urgentMeeting`.
* Each invocation replaces the previous filter. Repeated tags do not change the results.
* Tags follow the [field formats](#field-formats), so empty tags (`listm t/`) are rejected.
* Use an ordinary space (the Space key, `U+0020`) immediately before each `t/TAG` argument.
  A tab alone cannot separate the arguments.
* Leading and trailing ordinary spaces and tabs are ignored.
* Other Unicode space characters, such as EM SPACE (`U+2003`) and non-breaking space (`U+00A0`),
  are not supported for command spacing. If a pasted command contains these characters, replace them with ordinary spaces.
* Other arguments such as `listm 1` and `listm --all` are rejected.
* The command is case-sensitive. `Listm` and `LISTM` show `Unknown command. Did you mean "listm"?`.
* Ongoing and upcoming meetings appear first, ordered by date and start time.
  Finished meetings follow, also ordered from earliest to latest.
* A meeting is finished when its end time on its date is at or before the current local time on your computer.
  To refresh this grouping as time passes while retaining a tag filter, repeat the command with the same tag arguments.
  For example, after `listm t/work`, run `listm t/work` again. Running plain `listm` refreshes the grouping and clears
  the active tag filter, showing all meetings.
* Equal start times are ordered by end time, then name. Sorting does not change the order of records in the data file.
* An empty list displays `No meetings to display.`. Plain `listm` reports `Listed all meetings.`.
  A filtered list reports `Listed N meeting(s) matching all specified tags.`, including zero when no meetings match.
* Filtering only changes the display; all meetings remain saved. `editm` and `deletem` use the displayed row numbers.
  Editing tags can hide a meeting if it no longer matches the active filter. Deleting or editing keeps that filter active.
* Duplicate meetings are prevented by the shared model and rejected when loading data. Duplicates have the same
  name (ignoring case and repeated spaces), date, start time and end time; `listm` does not delete stored records.
* Use `listc` to return to the contacts panel. `help` and invalid commands keep the currently selected panel.
* Contact commands `deletec INDEX` and `editc INDEX ...` are rejected while the Meetings panel is active.
  Run `listc` to display contacts before editing or deleting them. You can also use `findc KEYWORD` to display
  matching contacts and then use the indices in those results.

Examples:
* `listm t/urgent` displays meetings tagged `urgent`, ignoring case.
* `listm t/urgent t/work` displays meetings containing both tags.
* `listm` returns to the complete meeting list.

### Locating contacts by name: `findc`

Finds contacts whose names contain any of the specified keywords.

Format: `findc KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Partial words match; for example, `Han` matches `Hans`.
* contacts matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `findc John` returns `john` and `John Doe`
* `findc alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'findc alex david'](images/findAlexDavidResult.png)

### Finding meetings by name: `findm`

Find meetings whose names contain any of the specified keywords.

Format: `findm KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive. For example, `findm project` matches a meeting named `Project Review`.
* Partial matches are supported. For example, `findm rev` matches a meeting named `Project Review`.
* Multiple keywords can be provided. A meeting is shown if its name matches **any** of the keywords.

Examples:
* `findm project` — finds meetings with `project` in their names.
* `findm project review` — finds meetings whose names contain either `project` or `review`.

### Deleting a contact: `deletec`

Deletes the specified contact from PingBook.

Format: `deletec INDEX`

* Deletes the contact at the specified `INDEX`.
* The index refers to the index number shown in the displayed contact list.
* The contacts panel must be active. If you are viewing meetings, run `listc` or `findc KEYWORD` first.
* The index **must be a positive integer** 1, 2, 3, ...

Examples:
* `listc` followed by `deletec 2` deletes the 2nd contact in PingBook.
* `findc Betsy` followed by `deletec 1` deletes the 1st contact in the results of the `findc` command.

### Deleting a meeting: `deletem`

Deletes the specified meeting from PingBook.

Format: `deletem MEETING_INDEX`

* Deletes the meeting at the specified `MEETING_INDEX`.
* The command is case-sensitive. Only `deletem` in lowercase is accepted. `DeleteM` and other variations show `Unknown command.`.
* The index refers to the index number shown in the displayed meeting list.
* The index **must be a positive integer** 1, 2, 3, ...
* Invalid indexes display `Invalid command format!` followed by the usage instructions for the command.
* `deletem` can only be executed when the meeting list is shown. Run `listm` before attempting to delete meetings.

Examples:
* `deletem 2` deletes the 2nd meeting in the meeting list.

### Clearing all entries: `clear`

Clears all entries from PingBook.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

PingBook automatically saves data after every command. You do not need to save manually.

### Editing the data file

PingBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, PingBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (PingBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause PingBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous PingBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

| Action             | Format, Examples                                                                                                                                                            |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Help**           | `help`                                                                                                                                                                      |
| **Add contact**    | `addc n/NAME p/PHONE_NUMBER e/EMAIL [a/ADDRESS] [t/TAG]... ` <br> e.g., `addc n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague` |
| **Add meeting**    | `addm n/NAME d/DATE st/START_TIME et/END_TIME [t/TAG]... [force/]` <br> e.g., `addm n/John Doe d/20/09/2026 st/1430 et/1530 t/work`                                         |
| **Edit contact**   | `editc INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]... `<br> e.g., `editc 2 n/James Lee e/jameslee@example.com`                                            |
| **Edit meeting**   | `editm INDEX [n/NAME] [d/DATE] [st/START_TIME] [et/END_TIME] [t/TAG]... [force/]`<br> e.g., `editm 2 d/25/10/2026 st/1500 et/1600 t/urgent`                                 |
| **List contacts**  | `listc [t/TAG]...`<br> e.g., `listc t/colleague t/friend`                                                                                                                   |
| **List meetings**  | `listm [t/TAG]...`<br> e.g., `listm t/urgent t/work`                                                                                                                        |
| **Find contacts**  | `findc KEYWORD [MORE_KEYWORDS]`<br> e.g., `findc James Jake`                                                                                                                |
| **Find meetings**  | `findm KEYWORD [MORE_KEYWORDS]`<br> e.g., `findm Project Review`                                                                                                            |
| **Delete contact** | `deletec INDEX`<br> e.g., `deletec 3`                                                                                                                                       |
| **Delete meeting** | `deletem INDEX`<br> e.g., `deletem 3`                                                                                                                                       |
| **Clear**          | `clear`                                                                                                                                                                     |
| **Exit**           | `exit`                                                                                                                                                                      |
