package controller;

import db.MongoDBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.util.List;
public class MembersController {

    @FXML
    private TableView<Member> tableContainer;
    @FXML
    private TableColumn<Member, String> tableID;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> emailCol;
    @FXML
    private TableColumn<Member, String> statusCol;
    @FXML
    private TableColumn<Member, String> dateCol;

    private final ObservableList<Member> memberData = FXCollections.observableArrayList();

    // Inner class representing Member details
    public static class Member {
        private final String id;
        private final String name;
        private final String email;
        private final String status;
        private final String date;

        public Member(String id, String name, String email, String status, String date) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.status = status;
            this.date = date;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getStatus() {
            return status;
        }

        public String getDate() {
            return date;
        }
    }

    @FXML
    public void initialize() {
        // Fetch data from the members collection in MongoDB
        List<Document> members = MongoDBConnection.getMembers();
        tableContainer.setFixedCellSize(60.0);

        // Configure TableView columns
        tableID.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        setTableColumnAlignmentToCenter(tableID);
        setTableColumnAlignmentToCenter(nameCol);
        setTableColumnAlignmentToCenter(emailCol);
        setTableColumnAlignmentToCenter(statusCol);
        setTableColumnAlignmentToCenter(dateCol);

        // Populate memberData with members from the database
        for (Document member : members) {
            String id = member.getObjectId("_id").toString();
            String name = member.getString("fullname");
            String email = member.getString("email");
            String status = member.getString("membership"); // Assuming status is a string
            String date = member.getString("join"); // Assuming date is stored as string

            memberData.add(new Member(id, name, email, status, date));
        }

        // Set the items in the TableView
        tableContainer.setItems(memberData);
    }

    private void setTableColumnAlignmentToCenter(TableColumn<Member, String> column) {
        column.setStyle("-fx-alignment: CENTER;");
        column.setCellFactory(tc -> {
            return new TableCell<Member, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER); // Align the text to center
                    }
                }
            };
        });
    }
    public void openModal(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/modal/addMemberModal.fxml"));
        Parent memberModalRoot = fxmlLoader.load();

        // Get the current stage from the event
        Stage mainStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL); // Make it a modal window
        modalStage.initOwner(mainStage); // Set the main stage as the owner
        modalStage.setScene(new Scene(memberModalRoot));
        modalStage.showAndWait(); // Blocks until modal is closed
    }
    public void openRemoveModal(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/modal/removeMember.fxml"));
        Parent memberModalRoot = fxmlLoader.load();

        // Get the current stage from the event
        Stage mainStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL); // Make it a modal window
        modalStage.initOwner(mainStage); // Set the main stage as the owner
        modalStage.setScene(new Scene(memberModalRoot));
        modalStage.showAndWait(); // Blocks until modal is closed
    }




}
