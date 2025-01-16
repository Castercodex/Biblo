package controller;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import db.MongoDBConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static db.MongoDBConnection.database;

public class removeLoanController {
    public MenuButton loanId;
    public Label message;



    public class Loan {
        private String id;
        private String loaner;
        private String title;

        public Loan(String id, String loaner, String title) {
            this.id = id;
            this.loaner = loaner;
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
        public String getLoaner() {
            return loaner;
        }
    }

    public List<Loan> getLoansFromDb() {
        List<Loan> loans = new ArrayList<>();
        try {
            MongoCollection<Document> membersCollection = database.getCollection("loans");
            FindIterable<Document> documents = membersCollection.find();

            for (Document doc : documents) {
                String id = doc.getObjectId("_id").toString();  // Get the member's ID
                String loaner = doc.getString("fullname");
                String title = doc.getString("title");

                loans.add((new Loan(id, loaner, title)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loans;
    }
    private void handleSelection(ActionEvent event) {
        // Get the selected member's name
        MenuItem selectedItem = (MenuItem) event.getSource();
        String selectedMember = selectedItem.getText();
        String selectedMemberId = (String) selectedItem.getUserData();

        // You can now do something with the selected member
        System.out.println("Selected member: " + selectedMember);
        // Optionally, update the button text or perform any further actions
        loanId.setText(selectedMember);
        loanId.setId(selectedMemberId);
        System.out.println("Selected member ID: " + selectedMemberId);
        loanId.setText(selectedMember);
    }

    public void removeEntry(ActionEvent actionEvent) {
        String loanID= loanId.getId();
        boolean success = MongoDBConnection.removeLoan( loanID);
        if (success) {
            message.setText("Loan has been removed");
            System.out.println("Removed loan with ID: " + loanID);
        }
        else {
            System.out.println("Failed to remove loan with ID: " + loanID);
            message.setText("Failed to remove loan with ID: " + loanID);
        }



    }


    public void clearInputs(ActionEvent actionEvent) {
        loanId.setText("");
        message.setText("");

    }

    public void initialize() {
        List<Loan> loans = getLoansFromDb();
        loanId.getItems().clear();


        for (Loan loan : loans) {
            MenuItem menuItem = new MenuItem("Member: " + loan.getLoaner() + "  Book: " + loan.getTitle());
            menuItem.setUserData(loan.getId());
            menuItem.setOnAction(this::handleSelection);  // Set action for each item
            loanId.getItems().add(menuItem);
        }
    }
    public void closeModal(ActionEvent actionEvent) {
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.close(); // Close the modal

    }
}
