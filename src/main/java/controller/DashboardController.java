package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import java.io.IOException;

import static db.MongoDBConnection.*;



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
