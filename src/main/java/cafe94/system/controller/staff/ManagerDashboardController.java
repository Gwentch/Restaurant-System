package cafe94.system.controller.staff;

import cafe94.system.data.DataSaver;
import cafe94.system.model.order.*;
import cafe94.system.model.user.Staff;
import cafe94.system.utils.AppState;
import cafe94.system.utils.OutstandingOrderHelper;
import cafe94.system.utils.ReportHelper;
import cafe94.system.utils.SceneManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;


public class ManagerDashboardController {

    @FXML private Label loggedInAsLabel;

    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, Integer> idColumn;
    @FXML private TableColumn<Staff, String> firstNameColumn;
    @FXML private TableColumn<Staff, String> lastNameColumn;
    @FXML private TableColumn<Staff, String> roleColumn;
    @FXML private TableColumn<Staff, Double> hoursToWorkCol;
    @FXML private TableColumn<Staff, Double> totalHoursWorkedCol;

    @FXML private Button removeStaffButton;
    @FXML private TextArea reportSect;
    @FXML private BarChart<String, Number> reportChart;
    @FXML private TabPane reportTabPane;
    @FXML private Label totalLabel;

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdCol;
    @FXML private TableColumn<Order, Integer> customerIdCol;
    @FXML private TableColumn<Order, String> orderTypeCol;
    @FXML private TableColumn<Order, String> addressCol;
    @FXML private TableColumn<Order, String> orderStatusCol;

    @FXML private TableView<OrderItem> itemsTable;
    @FXML private TableColumn<OrderItem, String> itemNameCol;
    @FXML private TableColumn<OrderItem, Integer> qtyCol;
    @FXML private TableColumn<OrderItem, Double> priceCol;
    @FXML private TableColumn<OrderItem, Double> subtotalCol;

    private static final String STAFF_PROFILE_FILE = "src/main/resources/data/staff_profile.txt";
    private ObservableList<Staff> staffList;


    public void setup(OrderManaged orderManaged) {
        this.staffList = FXCollections.observableArrayList(AppState.allStaff);
        staffTable.setItems(staffList);
        ordersTable.setItems(FXCollections.observableArrayList(orderManaged.getOutstandingOrders()));

        if (AppState.loggedInStaff != null) {
            loggedInAsLabel.setText("😊 Welcome, " + AppState.loggedInStaff.getFullName());
        }
    }


    @FXML
    public void initialize() {
        // Staff table columns
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        firstNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLastName()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));
        hoursToWorkCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getHoursToWork()).asObject());
        totalHoursWorkedCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalHoursWorked()).asObject());

        removeStaffButton.disableProperty().bind(staffTable.getSelectionModel().selectedItemProperty().isNull());

        // Orders table columns via helper
        OutstandingOrderHelper.setupOrderColumnsByRole(
                OutstandingOrderHelper.OrderTableType.MANAGER,
                orderIdCol, customerIdCol, orderTypeCol, addressCol, orderStatusCol);

        // Order item table
        OutstandingOrderHelper.setupOrderItemColumns(itemNameCol, qtyCol, priceCol, subtotalCol);

        // Selection listener
        OutstandingOrderHelper.setupOrderSelectionListener(ordersTable,
                itemsTable,
                totalLabel,
                null,
                null,
                null
        );
    }

    @FXML private void handleLogout() {
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }

    @FXML private void handleAddStaff() {
        Pair<Stage, StaffDetailsPopupController> popup = SceneManager.loadPopup(
                "staff/StaffDetailsPopup.fxml",
                "Add New Staff",
                350, 350);

        if (popup == null) {
            return;
        }

        Staff newStaff = new Staff("", "", Staff.StaffType.CHEF, "");
        popup.getValue().setup(newStaff, true);
        popup.getKey().showAndWait();

        if (popup.getValue().isSaved()) {
            staffList.add(newStaff);
            syncStaffListToAppState();
            DataSaver.saveStaff(STAFF_PROFILE_FILE, staffList);
        }
    }

    @FXML private void handleEditStaff() {
        Staff selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Please select a staff member to edit.");
            return;
        }

        Pair<Stage, StaffDetailsPopupController> popup = SceneManager.loadPopup(
                "staff/StaffDetailsPopup.fxml",
                "Edit Staff",
                350, 350);

        if (popup == null) {
            return;
        }

        popup.getValue().setup(selected, false);
        popup.getKey().showAndWait();

        if (popup.getValue().isSaved()) {
            staffTable.refresh();
            syncStaffListToAppState();
            DataSaver.saveStaff(STAFF_PROFILE_FILE, staffList);
        }
    }

    @FXML private void handleRemoveStaff() {
        Staff selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Please select a staff member to remove.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Remove " + selected.getFullName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                staffList.remove(selected);
                staffTable.refresh();
                syncStaffListToAppState();
                DataSaver.saveStaff(STAFF_PROFILE_FILE, staffList);
                showInfo("Staff removed successfully.");
            }
        });
    }

    @FXML private void generateMostPopularItems() {
        ReportHelper.generateMostPopularItems(AppState.orderManaged, reportChart, reportSect, reportTabPane);
    }

    @FXML private void generateMostActiveCustomer() {
        ReportHelper.generateMostActiveCustomers(AppState.orderManaged, AppState.allCustomer, reportChart, reportSect, reportTabPane);
    }

    @FXML private void generateBusiestPeriods() {
        ReportHelper.generateBusiestPeriods(AppState.orderManaged, reportChart, reportSect, reportTabPane);
    }

    @FXML private void generateTopStaff() {
        ReportHelper.generateTopStaff(AppState.allStaff, reportChart, reportSect, reportTabPane);
    }


    // Helper method
    private void syncStaffListToAppState() {
        AppState.allStaff.clear();
        AppState.allStaff.addAll(staffList);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
