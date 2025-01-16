package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static db.MongoDBConnection.addMember;


public class addMemberModal {

    public DatePicker datePub;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public TextField fullname;
    public TextField email;
    public TextField status;
    public Button addBtn;
    public Button clearBtn;
    public Label message;

    public void initialize() {
        // Set the DatePicker format to "dd/MM/yyyy"
        datePub.setConverter(new javafx.util.converter.LocalDateStringConverter(DATE_FORMATTER, DATE_FORMATTER));
    }


    public void clearInputs(ActionEvent actionEvent) {
    }

    public void addMemberEvent(ActionEvent actionEvent) {
        // Clear any existing styles and messages
        message.getStyleClass().clear();
        clearFieldStyles();

        boolean hasError = false;

        // Validate input fields
        if (fullname.getText().isEmpty()) {
            fullname.getStyleClass().add("error");
            hasError = true;
        }
        if (email.getText().isEmpty()) {
            email.getStyleClass().add("error");
            hasError = true;
        }

        if (status.getText().isEmpty()) {
            status.setText("Active");
            hasError = false;
        }


        // Validate DatePicker value (handle manual input and invalid dates)
        String joinDate = null;

        try {
            String dateInput = datePub.getEditor().getText();
            if (dateInput != null && !dateInput.isEmpty()) {
                // Attempt to parse the manually entered date using the custom formatter
                joinDate = LocalDate.parse(dateInput, DATE_FORMATTER).toString();
            } else if (datePub.getValue() != null) {
                // Format the selected date from the DatePicker into "dd/MM/yyyy"
                joinDate = datePub.getValue().format(DATE_FORMATTER);
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
        String fullName = fullname.getText();
        String emailAddress = email.getText();
        String memberStatus = status.getText();

        // Add Member to the database
        Boolean success = addMember(fullName, emailAddress, memberStatus, joinDate);
        clearFields();
        if (success) {
            message.getStyleClass().add("success");
            message.setText("Member added successfully");
        } else {
            message.getStyleClass().add("warning");
            message.setText("Member already exists");
        }
    }
    public void clearFields() {
        // Clear all input fields
       fullname.clear();
        email.clear();
        status.clear();
        datePub.setValue(null);
        datePub.getEditor().clear();
    }

    public void closeModal(ActionEvent actionEvent) {
        // Get the current stage (the modal)
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.close(); // Close the modal
    }

    private void clearFieldStyles() {
        // Remove error styles from all input fields
        fullname.getStyleClass().remove("error");
        email.getStyleClass().remove("error");
        datePub.getEditor().getStyleClass().remove("error");

    }

}
