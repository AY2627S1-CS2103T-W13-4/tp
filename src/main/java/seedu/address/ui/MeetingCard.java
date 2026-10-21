package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import seedu.address.model.meeting.Meeting;

/**
 * Displays a meeting's index, name, date, time range and tags.
 */
public class MeetingCard extends UiPart<Region> {
    private static final String FXML = "MeetingListCard.fxml";

    @FXML
    private Label id;
    @FXML
    private Label name;
    @FXML
    private Label schedule;
    @FXML
    private FlowPane tags;

    /**
     * Creates a card for the given meeting and displayed index.
     */
    public MeetingCard(Meeting meeting, int displayedIndex) {
        super(FXML);
        id.setText(displayedIndex + ". ");
        name.setText(meeting.getName().toString());
        schedule.setText(meeting.getDate() + "  " + meeting.getStartTime() + " - " + meeting.getEndTime());
        meeting.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        tags.setVisible(!meeting.getTags().isEmpty());
        tags.setManaged(!meeting.getTags().isEmpty());
    }
}
