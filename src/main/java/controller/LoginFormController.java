package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static db.MongoDBConnection.authLogin;

public class LoginFormController {
    public TextField passwordField;
    public TextField emailField;
    public Label errorLabel;


    public void authenticate(ActionEvent event) throws IOException {

        boolean isAuthenticated = authLogin(emailField.getText(), passwordField.getText());
        if (isAuthenticated) {
            errorLabel.setText("Login Successful");
            errorLabel.getStyleClass().clear();
            errorLabel.getStyleClass().add("success");
            passwordField.clear();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ui/Dashboard.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else {
            errorLabel.setText("Invalid Email or Password");
            errorLabel.getStyleClass().clear();
            errorLabel.getStyleClass().add("warning");
            passwordField.clear();
            emailField.requestFocus();
            emailField.getStyleClass().add("btn-warning");
            passwordField.getStyleClass().add("btn-warning");

        }
    }

    public void registerPage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ui/registerFormView.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
}