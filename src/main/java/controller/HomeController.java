package controller;
import db.MongoDBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;

import java.util.List;


public class HomeController {


    public Text booksCount;
    public Text membersCount;

    public void initialize() {
        List<Document> books = MongoDBConnection.getBooks();
        List<Document> members = MongoDBConnection.getMembers();
        booksCount.setText(String.valueOf(books.size()));
        membersCount.setText(String.valueOf(members.size()));
    }


    public void openModal(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/modal/addModal.fxml"));
        Parent modalRoot = fxmlLoader.load();

        // Get the current stage from the event
        Stage mainStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL); // Make it a modal window
        modalStage.initOwner(mainStage); // Set the main stage as the owner
        modalStage.setScene(new Scene(modalRoot));
        modalStage.showAndWait(); // Blocks until modal is closed
    }



}
