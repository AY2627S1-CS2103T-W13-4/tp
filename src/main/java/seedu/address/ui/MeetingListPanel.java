package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.meeting.Meeting;

/**
 * Panel containing all meetings in their displayed order.
 */
public class MeetingListPanel extends UiPart<Region> {
    private static final String FXML = "MeetingListPanel.fxml";

    @FXML
    private ListView<Meeting> meetingListView;

    /**
     * Creates a panel backed by the given observable meeting list.
     */
    public MeetingListPanel(ObservableList<Meeting> meetings) {
        super(FXML);
        meetingListView.setItems(meetings);
        meetingListView.setPlaceholder(new Label("No meetings to display."));
        meetingListView.setCellFactory(listView -> new MeetingListViewCell());
    }

    /**
     * Displays each meeting as a numbered card.
     */
    private static class MeetingListViewCell extends ListCell<Meeting> {
        @Override
        protected void updateItem(Meeting meeting, boolean empty) {
            super.updateItem(meeting, empty);
            setText(null);
            if (empty || meeting == null) {
                setGraphic(null);
            } else {
                setGraphic(new MeetingCard(meeting, getIndex() + 1).getRoot());
            }
        }
    }
}
