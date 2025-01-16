package controller;

import db.MongoDBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LoanController {

    @FXML
    private TableView<HomeController.Loan> tableContainer;
    @FXML
    private TableColumn<HomeController.Loan, String> tableID;
    @FXML
    private TableColumn<HomeController.Loan, String> titleCol;
    @FXML
    private TableColumn<HomeController.Loan, String> memberCol;
    @FXML
    private TableColumn<HomeController.Loan, String> overdueCol;
    @FXML
    private TableColumn<HomeController.Loan, String> returnCol;

    private final ObservableList<HomeController.Loan> loanData = FXCollections.observableArrayList();

    // Inner class representing Loan details
    public static class Loan {
        private final String id;
        private final String title;
        private final String memberName;
        private final String overdue;
        private final String loanReturnDate;

        public Loan(String id, String title, String memberName, String overdue, String loanReturnDate) {
            this.id = id;
            this.title = title;
            this.memberName = memberName;
            this.overdue = overdue;
            this.loanReturnDate = loanReturnDate;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getMemberName() {
            return memberName;
        }

        public String getOverdue() {
            return overdue;
        }

        public String getLoanReturnDate() {
            return loanReturnDate;
        }
    }

    @FXML
    public void initialize() {
        tableContainer.setFixedCellSize(60.0);

        // Fetch data from the database
        List<Document> books = MongoDBConnection.getBooks();
        List<Document> members = MongoDBConnection.getMembers();
        List<Document> borrowed = MongoDBConnection.getLoans();

        // Configure TableView columns
        tableID.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        memberCol.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        overdueCol.setCellValueFactory(new PropertyValueFactory<>("overdue"));
        returnCol.setCellValueFactory(new PropertyValueFactory<>("loanReturnDate"));

        setTableColumnAlignmentToCenter(tableID);
        setTableColumnAlignmentToCenter(titleCol);
        setTableColumnAlignmentToCenter(memberCol);
        setTableColumnAlignmentToCenter(overdueCol);
        setTableColumnAlignmentToCenter(returnCol);

        // Populate loanData with all borrowed books, regardless of overdue status
        for (Document loan : borrowed) {
            String id = loan.getObjectId("_id").toString();
            String title = loan.getString("title");
            String memberName = loan.getString("fullname");
            String loanStartDate = loan.getString("loanStartDate");
            String loanReturnDate = loan.getString("loanReturnDate");

            String overdueStatus = checkLoanStatus(loanStartDate, loanReturnDate);

            // Add all borrowed books to the loanData
            loanData.add(new HomeController.Loan(id, title, memberName, overdueStatus, loanReturnDate));
        }

        // Set the items in the TableView
        tableContainer.setItems(loanData);

        // Highlight overdue rows
        tableContainer.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(HomeController.Loan loan, boolean empty) {
                super.updateItem(loan, empty);
                if (loan == null || empty) {
                    setStyle("");
                    getStyleClass().removeAll("overdue-row", "normal-row");
                } else if (loan.getOverdue().equals("Yes")) {
                    setStyle("-fx-background-color: #ffcccc;"); // Light red background for overdue
                } else {
                    setStyle(""); // Default styling for non-overdue
                }
            }
        });
    }

    private void setTableColumnAlignmentToCenter(TableColumn<HomeController.Loan, String> column) {
        column.setStyle("-fx-alignment: CENTER;");
        column.setCellFactory(tc -> {
            return new TableCell<HomeController.Loan, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER); // Align the text to center
                    }
                }
            };
        });
    }

    private String checkLoanStatus(String loanStartDate, String loanReturnDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate returnDate = LocalDate.parse(loanReturnDate, formatter);
        LocalDate today = LocalDate.now();

        if (today.isAfter(returnDate)) {
            long overdueDays = ChronoUnit.DAYS.between(returnDate, today);
            return "Yes"; // Overdue
        }
        return "No"; // Not overdue
    }

    // Custom cell formatting for the Overdue column (apply red/green text)
    @FXML
    private void setOverdueColumnStyle() {
        overdueCol.setCellFactory(column -> new TableCell<HomeController.Loan, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Yes".equals(item)) {
                        setStyle("-fx-text-fill: red;"); // Red for overdue
                    } else {
                        setStyle("-fx-text-fill: green;"); // Green for on time
                    }
                }
            }
        });
    }

    public void openModal(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/modal/addLoan.fxml"));
        Parent memberModalRoot = fxmlLoader.load();

        // Get the current stage from the event
        Stage mainStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL); // Make it a modal window
        modalStage.initOwner(mainStage); // Set the main stage as the owner
        modalStage.setScene(new Scene(memberModalRoot));
        modalStage.showAndWait(); // Blocks until modal is closed
    }
    public void openRemoveModal(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/modal/removeLoan.fxml"));
        Parent memberModalRoot = fxmlLoader.load();

        // Get the current stage from the event
        Stage mainStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL); // Make it a modal window
        modalStage.initOwner(mainStage); // Set the main stage as the owner
        modalStage.setScene(new Scene(memberModalRoot));
        modalStage.showAndWait(); // Blocks until modal is closed
    }
}
