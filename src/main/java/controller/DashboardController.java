package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.io.IOException;


public class DashboardController {
    public Pane dynamicPage;
//    public StackPane stackPane;
//    public ImageView imageView;



    public void getDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/HomeView.fxml"));
            Pane newContent = loader.load();

            // Clear previous content and set the new content
            dynamicPage.getChildren().clear();
            dynamicPage.getChildren().add(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public void initialize() {
        getDashboard(null);
   }
}
