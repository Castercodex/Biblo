package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static db.MongoDBConnection.authRegister;
import static db.MongoDBConnection.signupError;

public class RegisterFormController {
    public TextField passwordField;
    public TextField emailField;
    public Label errorLabel;
    public TextField nameField;
    public Button actionBtn;

    public void registerUser(ActionEvent actionEvent) throws IOException {
        // Disable the button after the first click to prevent multiple submissions
        actionBtn.setDisable(true);

        // Call authRegister once and store the result
        boolean isRegistered = authRegister(nameField.getText(), emailField.getText(), passwordField.getText());

        // Reactivate the button after processing
        actionBtn.setDisable(false);

        if (isRegistered) {
            // Load the Dashboard UI on successful registration
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ui/Dashboard.fxml")));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            System.out.println("Successfully registered");
        } else {
            // Display the error message for failed registration
            errorLabel.setVisible(true);
            errorLabel.setText(signupError);
            errorLabel.getStyleClass().clear();
            errorLabel.getStyleClass().add("warning");
        }
    }

    public void loginPage(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ui/loginFormView.fxml")));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }
}