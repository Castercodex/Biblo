package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import db.MongoDBConnection;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;

import static db.MongoDBConnection.*;



public class DashboardController {
    public Pane dynamicPage;
    public Button getDashboard;
    public Button getMembers;
    public Button loan;
    public Button books;
    
    public Text userNameLabel;
    private String loginEmail;

    @FXML
    private HBox hbox;
    @FXML private VBox booksContainer;

    @FXML private ImageView bookImage;
    @FXML private Label bookTitle;
    @FXML private Label bookLoaner;
    @FXML private Label borrowedDate;
    @FXML private Label returnDate;




    public void getDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/HomeView.fxml"));
            Pane newContent = loader.load();


            // Clear previous content and set the new content
            dynamicPage.getChildren().clear();
            dynamicPage.getChildren().add(newContent);

            clearActiveClasses();

            getDashboard.getStyleClass().add("active");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getMembers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/membersView.fxml"));
            Pane newContent = loader.load();

           clearActiveClasses();

           getMembers.getStyleClass().add("active");

            // Clear previous content and set the new content
            dynamicPage.getChildren().clear();
            dynamicPage.getChildren().add(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getLoan(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/loanView.fxml"));
            Pane newContent = loader.load();

            clearActiveClasses();
            loan.getStyleClass().add("active");

            // Clear previous content and set the new content
            dynamicPage.getChildren().clear();
            dynamicPage.getChildren().add(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void getBooks(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/booksView.fxml"));
            Pane newContent = loader.load();

            clearActiveClasses();
            books.getStyleClass().add("active");

            // Clear previous content and set the new content
            dynamicPage.getChildren().clear();
            dynamicPage.getChildren().add(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void setLoginEmail(String email) {
        this.loginEmail = email;
        // You can now use loginEmail in this controller
        System.out.println("Login email received in controller: " + loginEmail);
        fetchAndDisplayUserData();
    }

    public void fetchAndDisplayUserData() {
        // Debugging: Print the email before querying
        System.out.println("Fetching data for email: " + loginEmail);
        Document userDocument = MongoDBConnection.getUserByEmail(loginEmail);

        if (userDocument == null) {
            userNameLabel.setText("User not found");
            System.out.println("User not found for email: " + loginEmail);
        } else {
            // Get user data from the document
            String userName = userDocument.getString("fullName");

            if (userName == null) {
                System.out.println("User found, but 'name' is null.");
            }

            // Display the user name in the label
            userNameLabel.setText(userName);
            System.out.println("User found: " + userName);
        }
    }
    public class BorrowedBook {
        private String title;
        private String bookLoaner;
        private String code;
        private String borrowedDate;
        private String returnDate;
        private String status;

        public BorrowedBook(String title, String bookLoaner, String code, String borrowedDate, String returnDate, String status) {
            this.title = title;
            this.bookLoaner = bookLoaner;
            this.code = code;
            this.borrowedDate = borrowedDate;
            this.returnDate = returnDate;
            this.status = status;
        }

        // Getters and Setters

    }

    public void setBookData(Document book) {
        // Dynamically set the data to the UI components
        bookTitle.setText(book.getString("title"));
        bookLoaner.setText(book.getString("fullname"));
        borrowedDate.setText("Borrowed: " + book.getString("loanStartDate"));
        returnDate.setText("Return: " + book.getString("LoanReturnDate"));


        // Optionally set an image if needed
//        bookImage.setImage(new Image("path/to/image")); // Replace with your image path
    }


    public void initialize() {
        getDashboard(null);
//        MongoDBConnection mongoDB = new MongoDBConnection();
//        List<Document> books = mongoDB.getLoans();
//
//        for (Document book : books) {
//            // Populate data to the UI using the existing HBox
//            setBookData(book);
//        }
        loadBooksFromDatabase();


    }

    private void loadBooksFromDatabase() {
        MongoDBConnection mongoDB = new MongoDBConnection();
        List<Document> books = mongoDB.getLoans();

        for (Document book : books) {
            // Clone the HBox programmatically
            HBox bookHBox = cloneHBox(hbox);

            // Populate data into the cloned HBox
            ImageView bookImage = (ImageView) ((VBox) bookHBox.getChildren().get(0)).getChildren().get(0);
            VBox detailsBox = (VBox) bookHBox.getChildren().get(1);
            Label bookTitle = (Label) detailsBox.getChildren().get(0);
            Label bookLoaner = (Label) detailsBox.getChildren().get(1);
            HBox datesBox = (HBox) detailsBox.getChildren().get(3);
            Label borrowedDate = (Label) datesBox.getChildren().get(0);
            Label returnDate = (Label) datesBox.getChildren().get(1);

            // Set text data
            String title = book.getString("title");
            bookTitle.setText(title);
            bookLoaner.setText(book.getString("fullname"));
            borrowedDate.setText("Borrowed: " + book.getString("loanStartDate"));
            returnDate.setText("Return: " + book.getString("loanReturnDate"));

            // Fetch the image string by querying the database with the book title
            Document bookDetails = mongoDB.getBookByTitle(title);
            if (bookDetails != null && bookDetails.containsKey("image")) {
                String imagePath = bookDetails.getString("image"); // Assuming "image" is the field storing the image URL/path
                bookImage.setImage(new Image(imagePath));
            } else {
                // Set a default image if no image is found
                bookImage.setImage(new Image("path/to/default/image.png")); // Replace with your default image path
            }

            // Add the cloned HBox to the VBox container
            booksContainer.getChildren().add(bookHBox);
        }


    }


    private HBox cloneHBox(HBox original) {
        HBox clone = new HBox();
        clone.setSpacing(original.getSpacing());

        for (Node child : original.getChildren()) {
            Node clonedChild = cloneNode(child);
            clone.getChildren().add(clonedChild);
        }

        return clone;
    }

    private Node cloneNode(Node original) {
        if (original instanceof Label) {
            Label originalLabel = (Label) original;
            Label clone = new Label();
            clone.setText(originalLabel.getText());
            clone.setStyle(originalLabel.getStyle());
            clone.getStyleClass().addAll(originalLabel.getStyleClass());
            return clone;
        } else if (original instanceof ImageView) {
            ImageView originalImageView = (ImageView) original;
            ImageView clone = new ImageView();
            clone.setImage(originalImageView.getImage());
            clone.setFitWidth(originalImageView.getFitWidth());
            clone.setFitHeight(originalImageView.getFitHeight());
            return clone;
        } else if (original instanceof VBox) {
            VBox originalVBox = (VBox) original;
            VBox clone = new VBox();
            clone.setSpacing(originalVBox.getSpacing());
            for (Node child : originalVBox.getChildren()) {
                clone.getChildren().add(cloneNode(child));
            }
            return clone;
        } else if (original instanceof HBox) {
            return cloneHBox((HBox) original);
        }
        // Add more cases as needed for other Node types
        return original;
    }

    private void clearActiveClasses() {
        getDashboard.getStyleClass().remove("active");
        getMembers.getStyleClass().remove("active");
        loan.getStyleClass().remove("active");
        books.getStyleClass().remove("active");
    }

    public void logout(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.close(); // Close the modal

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ui/loginFormView.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
