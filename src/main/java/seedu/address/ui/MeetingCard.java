package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.meeting.Meeting;

/**
 * Displays a meeting's index, name, date and time range.
 */
public class MeetingCard extends UiPart<Region> {
    private static final String FXML = "MeetingListCard.fxml";

    @FXML
    private Label id;
    @FXML
    private Label name;
    @FXML
    private Label schedule;

    /**
     * Creates a card for the given meeting and displayed index.
     */
    public MeetingCard(Meeting meeting, int displayedIndex) {
        super(FXML);
        id.setText(displayedIndex + ". ");
        name.setText(meeting.getName().toString());
        schedule.setText(meeting.getDate() + "  " + meeting.getStartTime() + " - " + meeting.getEndTime());
    }
}
