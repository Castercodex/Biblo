package controller;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import db.MongoDBConnection;

import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.bson.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static db.MongoDBConnection.*;


public class addLoanController {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

//    public MenuButton addLoan;
    public MenuButton userMenu;
    public MenuButton bookMenu;
    public DatePicker dateBorrowed;
    public DatePicker dateReturn;
    public Label message;

    public class Member {
        private String id;
        private String name;

        public Member(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
    public class Book {
        private String id;
        private String title;

        public Book(String id, String name) {
            this.id = id;
            this.title = name;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }

    public List<Member> getMembersFromDb() {
        List<Member> members = new ArrayList<>();
        try {
            MongoCollection<Document> membersCollection = database.getCollection("members");
            FindIterable<Document> documents = membersCollection.find();

            for (Document doc : documents) {
                String id = doc.getObjectId("_id").toString();  // Get the member's ID
                String name = doc.getString("fullname");
                members.add((new Member(id, name)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return members;
    }

    public List<Book> getBooksFromDb() {
        List<Book> books = new ArrayList<>();
        try {
            MongoCollection<Document> booksCollection = database.getCollection("books");
            FindIterable<Document> documents = booksCollection.find();

            for (Document doc : documents) {
                String id = doc.getObjectId("_id").toString();
                String title = doc.getString("title");
                books.add((new Book(id,title)));  // Assuming each member has a "name" field
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }



    public void initialize() {
        dateReturn.setConverter(new javafx.util.converter.LocalDateStringConverter(DATE_FORMATTER, DATE_FORMATTER));
        dateBorrowed.setConverter(new javafx.util.converter.LocalDateStringConverter(DATE_FORMATTER, DATE_FORMATTER));

        List<Member> members = getMembersFromDb();
        List<Book> books = getBooksFromDb();

        System.out.println(members);

        userMenu.getItems().clear();
        bookMenu.getItems().clear();

        for (Member member : members) {
             MenuItem menuItem = new MenuItem(member.getName());
            menuItem.setUserData(member.getId());
            menuItem.setOnAction(this::handleMemberSelection);  // Set action for each item
            userMenu.getItems().add(menuItem);
        }
        for (Book book : books) {
            MenuItem bookItem = new MenuItem(book.getTitle());
            bookItem.setUserData(book.getId());
            bookItem.setOnAction(this::handleBookSelection);
            bookMenu.getItems().add(bookItem);

        }

    }
    private void handleMemberSelection(ActionEvent event) {
        // Get the selected member's name
        MenuItem selectedItem = (MenuItem) event.getSource();
        String selectedMember = selectedItem.getText();
        String selectedMemberId = (String) selectedItem.getUserData();

        // You can now do something with the selected member
        System.out.println("Selected member: " + selectedMember);
        // Optionally, update the button text or perform any further actions
        userMenu.setText(selectedMember);
        userMenu.setId(selectedMemberId);
        System.out.println("Selected member ID: " + selectedMemberId);
        userMenu.setText(selectedMember);
    }

    private void handleBookSelection(ActionEvent event) {
        // Get the selected books name
        MenuItem selectedItem = (MenuItem) event.getSource();
        String selectedBookId = (String) selectedItem.getUserData();
        String selectedBook = selectedItem.getText();

        // You can now do something with the selected book
        System.out.println("Selected member: " + selectedBook);
        // Optionally, update the button text or perform any further actions
        bookMenu.setText(selectedBook);
        bookMenu.setId(selectedBookId);
        System.out.println("Selected member ID: " + selectedBookId);
        bookMenu.setText(selectedBook);

    }

    public void addEntry(ActionEvent actionEvent) {
        message.getStyleClass().clear();
        clearFieldStyles();

        boolean hasError = false;

        // Validate input fields
        if (userMenu.getText().isEmpty()) {
            userMenu.getStyleClass().add("error");
            hasError = true;
        }
        if (bookMenu.getText().isEmpty()) {
            bookMenu.getStyleClass().add("error");
            hasError = true;
        }

        String borrowDate = null;
        String returnDate = null;

        try {
            String borrowDateInput = dateBorrowed.getEditor().getText();
            String returnDateInput = dateReturn.getEditor().getText();
            if (borrowDateInput != null && !borrowDateInput.isEmpty() && returnDateInput != null && !returnDateInput.isEmpty()) {
                // Attempt to parse the manually entered date using the custom formatter
                borrowDate = LocalDate.parse(borrowDateInput, DATE_FORMATTER).toString();
                returnDate = LocalDate.parse(returnDateInput, DATE_FORMATTER).toString();
            } else if (dateBorrowed.getValue() != null) {
                // Format the selected date from the DatePicker into "dd/MM/yyyy"
                borrowDate = dateBorrowed.getValue().format(DATE_FORMATTER);
            } else {
                // No date entered
                dateBorrowed.getEditor().getStyleClass().add("error");
                hasError = true;
            }
        } catch (DateTimeParseException e) {
            // Handle invalid date format
            dateBorrowed.getEditor().getStyleClass().add("error");
            hasError = true;
        }
        if (hasError) {
            message.getStyleClass().add("warning");
            message.setText("Please fill in all required fields correctly.");
            return;
        }

        String memberID= userMenu.getId();
        String bookID= bookMenu.getId();
        String memberName= userMenu.getText();
        String title = bookMenu.getText();



        // Add Member to the database
        Boolean success = addLoan(memberID, memberName, bookID, title, borrowDate, returnDate);
        clearFields();

        if (success) {
            message.getStyleClass().add("success");
            message.setText("Entry added successfully");
        } else {
            message.getStyleClass().add("warning");
            message.setText("Entry already exists");
        }

    }
    public void clearFields() {
        userMenu.setText("");
        bookMenu.setText("");
        dateBorrowed.setValue(null);
        dateReturn.setValue(null);
        dateBorrowed.getEditor().clear();
        dateReturn.getEditor().clear();
    }

    public void clearInputs(ActionEvent actionEvent) {
        clearFields();
    }
    private void clearFieldStyles() {
        // Remove error styles from all input fields
        userMenu.getStyleClass().remove("error");
        bookMenu.getStyleClass().remove("error");
        dateBorrowed.getEditor().getStyleClass().remove("error");
        dateReturn.getEditor().getStyleClass().remove("error");

    }

    public void closeModal(ActionEvent actionEvent) {
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.close(); // Close the modal
    }
}
