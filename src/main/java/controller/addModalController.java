package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static db.MongoDBConnection.addBook;

public class addModalController {

    public TextField bookTitle;
    public TextField authorName;
    public TextField urlField;
    public DatePicker datePub;
    public TextField category;
    public Label message;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void initialize() {
        // Set the DatePicker format to "dd/MM/yyyy"
        datePub.setConverter(new javafx.util.converter.LocalDateStringConverter(DATE_FORMATTER, DATE_FORMATTER));
    }

    public void closeModal(ActionEvent actionEvent) {
        // Get the current stage (the modal)
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.close(); // Close the modal
    }

    public void addBookEvent(ActionEvent actionEvent) {
        // Clear any existing styles and messages
        message.getStyleClass().clear();
        clearFieldStyles();

        boolean hasError = false;

        // Validate input fields
        if (bookTitle.getText().isEmpty()) {
            bookTitle.getStyleClass().add("error");
            hasError = true;
        }
        if (authorName.getText().isEmpty()) {
            authorName.getStyleClass().add("error");
            hasError = true;
        }
        if (urlField.getText().isEmpty()) {
            urlField.getStyleClass().add("error");
            hasError = true;
        }
        if (category.getText().isEmpty()) {
            category.getStyleClass().add("error");
            hasError = true;
        }


        // Validate DatePicker value (handle manual input and invalid dates)
        String pub = null;

        try {
            String dateInput = datePub.getEditor().getText();
            if (dateInput != null && !dateInput.isEmpty()) {
                // Attempt to parse the manually entered date using the custom formatter
                pub = LocalDate.parse(dateInput, DATE_FORMATTER).toString();
            } else if (datePub.getValue() != null) {
                // Format the selected date from the DatePicker into "dd/MM/yyyy"
                pub = datePub.getValue().format(DATE_FORMATTER);
            } else {
                // No date entered
                datePub.getEditor().getStyleClass().add("error");
                hasError = true;
            }
        } catch (DateTimeParseException e) {
            // Handle invalid date format
            datePub.getEditor().getStyleClass().add("error");
            hasError = true;
        }

        // Show error message if validation fails
        if (hasError) {
            message.getStyleClass().add("warning");
            message.setText("Please fill in all required fields correctly.");
            return;
        }

        // Get other field values
        String title = bookTitle.getText();
        String author = authorName.getText();
        String url = urlField.getText();
        String cat = category.getText();

        // Add book to the database
        Boolean success = addBook(title, author, cat, url, pub);
        clearFields();
        if (success) {
            message.getStyleClass().add("success");
            message.setText("Book added successfully");
        } else {
            message.getStyleClass().add("warning");
            message.setText("Book already exists in the library");
        }
    }

    public void clearFields() {
        // Clear all input fields
        bookTitle.clear();
        authorName.clear();
        urlField.clear();
        category.clear();
        datePub.setValue(null);
        datePub.getEditor().clear();
    }

    public void clearInputs(ActionEvent actionEvent) {
        clearFields();
        message.getStyleClass().clear();
        message.setText("");
        clearFieldStyles();
    }

    private void clearFieldStyles() {
        // Remove error styles from all input fields
        bookTitle.getStyleClass().remove("error");
        authorName.getStyleClass().remove("error");
        urlField.getStyleClass().remove("error");
        category.getStyleClass().remove("error");
        datePub.getEditor().getStyleClass().remove("error");
    }
}
