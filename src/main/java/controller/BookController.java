package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.util.List;
import db.MongoDBConnection;

public class BookController {
    @FXML
    private GridPane gridPane;  // Your GridPane in the FXML file

    public void initialize() {
        // Fetch the books from the database
        List<Document> books = MongoDBConnection.getBooks();  // Modify according to your DB logic

        // Populate GridPane with VBox containing ImageView and Labels
        int row = 0;
        int col = 0;

        for (Document book : books) {
            String imageUrl = book.getString("image");  // Assuming 'imageURL' field contains the URL of the image
            String title = book.getString("title");
            String author = book.getString("author");
            String publishedDate = book.getString("published");

            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Create an ImageView for the book image
                Image image = new Image(imageUrl);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);  // Set width for image
                imageView.setFitHeight(100); // Set height for image
                imageView.setPreserveRatio(true);  // Maintain aspect ratio of the image

                // Create Labels for the book details (title, author, and published date)
                Label titleLabel = new Label("Title: " + title);
                Label authorLabel = new Label("Author: " + author);
                Label dateLabel = new Label("Published: " + publishedDate);

                // Style labels (optional)
                titleLabel.setStyle("-fx-font-weight: bold;");
                authorLabel.setStyle("-fx-font-style: italic;");
                dateLabel.setStyle("-fx-text-fill: grey;");

                // Create a VBox to hold the image and labels
                VBox vbox = new VBox(5, imageView, titleLabel, authorLabel, dateLabel);
                vbox.setAlignment(Pos.CENTER);  // Center align the contents inside VBox

                // Add the VBox to the GridPane at the specified position
                gridPane.add(vbox, col, row);

                // Move to the next column
                col++;

                // If you exceed 3 columns (for a 4x3 grid), move to the next row
                if (col >= 4) {
                    col = 0;
                    row++;
                }
            }
        }
    }
    public void openModal(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/modal/addModal.fxml"));
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
