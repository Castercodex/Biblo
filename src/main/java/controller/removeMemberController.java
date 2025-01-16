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

public class removeMemberController {
    public MenuButton memberId;
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
    private void handleSelection(ActionEvent event) {
        // Get the selected member's name
        MenuItem selectedItem = (MenuItem) event.getSource();
        String selectedMember = selectedItem.getText();
        String selectedMemberId = (String) selectedItem.getUserData();

        // You can now do something with the selected member
        System.out.println("Selected member: " + selectedMember);
        // Optionally, update the button text or perform any further actions
        memberId.setText(selectedMember);
        memberId.setId(selectedMemberId);
        System.out.println("Selected member ID: " + selectedMemberId);
        memberId.setText(selectedMember);
    }

    public void removeEntry(ActionEvent actionEvent) {
        String memberID= memberId.getId();
        boolean success = MongoDBConnection.removeMember( memberID);
        if (success) {
            message.setText("Member has been removed");
            System.out.println("Removed Member with ID: " + memberID);
        }
        else {
            System.out.println("Failed to remove Member with ID: " + memberID);
            message.setText("Failed to remove Member with ID: " + memberID);
        }



    }


    public void clearInputs(ActionEvent actionEvent) {
        memberId.setText("");
        message.setText("");

    }

    public void initialize() {
        List<Member> members = getMembersFromDb();
        memberId.getItems().clear();


        for (Member member : members) {
            MenuItem menuItem = new MenuItem("Member: " + member.getName());
            menuItem.setUserData(member.getId());
            menuItem.setOnAction(this::handleSelection);  // Set action for each item
            memberId.getItems().add(menuItem);
        }
    }
    public void closeModal(ActionEvent actionEvent) {
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.close(); // Close the modal

    }
}
