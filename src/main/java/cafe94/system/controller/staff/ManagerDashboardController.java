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

/**
 * Controller for the Manager Dashboard in the Cafe94 system.
 * <p>
 * This dashboard enables the manager to:
 * <ul>
 *   <li>View, add, edit, and remove staff profiles</li>
 *   <li>Monitor outstanding orders and view order details</li>
 *   <li>Generate various business reports (e.g., top staff, popular items)</li>
 * </ul>
 *
 * The manager dashboard integrates with shared utilities like {@link ReportHelper},
 * {@link OutstandingOrderHelper}, and {@link DataSaver} for managing data display and persistence.
 */
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

    /**
     * Initializes the Manager Dashboard with order and staff data.
     * <p>
     * Loads the staff list and outstanding orders into their respective tables,
     * and displays the name of the currently logged-in staff member.
     *
     * @param orderManaged The order manager instance containing all current orders.
     */
    public void setup(OrderManaged orderManaged) {
        this.staffList = FXCollections.observableArrayList(AppState.allStaff);
        staffTable.setItems(staffList);
        ordersTable.setItems(FXCollections.observableArrayList(orderManaged.getOutstandingOrders()));

        if (AppState.loggedInStaff != null) {
            loggedInAsLabel.setText("😊 Welcome, " + AppState.loggedInStaff.getFullName());
        }
    }

    /**
     * Initializes the Manager Dashboard UI components.
     * <p>
     * Configures table columns, binds the remove button, and sets up
     * order and item table views using helper utilities.
     */
    @FXML
    private void initialize() {
        // Staff table columns
        idColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getId()).asObject());
        firstNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLastName()));
        roleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRole()));
        hoursToWorkCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getHoursToWork().stream().mapToDouble(Double::doubleValue).sum()).asObject()
        );

        totalHoursWorkedCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(
                        data.getValue().getTotalHoursWorked().stream().mapToDouble(Double::doubleValue).sum()
                ).asObject()
        );


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

    /**
     * Logs the manager out of the dashboard and returns to the login screen.
     */
    @FXML private void handleLogout() {
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }

    /**
     * Handles the action of adding a new staff member.
     * <p>
     * Opens a popup form for entering new staff details. If the user confirms
     * and saves the new staff, it is added to the table, synchronized with
     * the application state, and persisted to file.
     */
    @FXML private void handleAddStaff() {
        Pair<Stage, StaffDetailsPopupController> popup = SceneManager.loadPopup(
                "staff/StaffDetailsPopup.fxml",
                "Add New Staff",
                600, 500);

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

    /**
     * Handles the editing of a selected staff member.
     * <p>
     * Opens a pre-filled popup for editing the selected staff's details.
     * On confirmation, the changes are saved, the table is refreshed,
     * and data is persisted to file.
     */
    @FXML private void handleEditStaff() {
        Staff selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Please select a staff member to edit.");
            return;
        }

        Pair<Stage, StaffDetailsPopupController> popup = SceneManager.loadPopup(
                "staff/StaffDetailsPopup.fxml",
                "Edit Staff",
                600, 500);

        if (popup == null) {
            return;
        }

        StaffDetailsPopupController controller = popup.getValue();
        controller.setup(selected, false); // Setup staff details

        // Show popup window and wait until it closes
        popup.getKey().showAndWait();

        // After closing the popup, check if saved
        if (controller.isSaved()) {
            staffTable.refresh();  // update display
            syncStaffListToAppState();  // update shared data
            DataSaver.saveStaff(STAFF_PROFILE_FILE, staffList);  // persist to file
            System.out.println("✔ Staff edited and saved.");
        } else {
            System.out.println("Edit was cancelled or not saved.");
        }
    }

    /**
     * Handles the removal of a selected staff member.
     * <p>
     * Prompts for confirmation before deleting the selected staff member
     * from the table, application state, and file. Displays a confirmation
     * message upon successful removal.
     */
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

    /**
     * Generates a bar chart and textual report showing the most popular items
     * based on order data.
     */
    @FXML private void generateMostPopularItems() {
        ReportHelper.generateMostPopularItems(AppState.orderManaged, reportChart, reportSect, reportTabPane);
    }

    /**
     * Generates a report identifying the most active customers based on
     * the number of orders they have placed.
     */
    @FXML private void generateMostActiveCustomer() {
        ReportHelper.generateMostActiveCustomers(AppState.orderManaged, AppState.allCustomer, reportChart, reportSect, reportTabPane);
    }

    /**
     * Generates a report highlighting the busiest ordering time periods
     * across all orders.
     */
    @FXML private void generateBusiestPeriods() {
        ReportHelper.generateBusiestPeriods(AppState.orderManaged, reportChart, reportSect, reportTabPane);
    }

    /**
     * Generates a report identifying the staff member with the highest
     * number of hours worked.
     */
    @FXML private void generateTopStaff() {
        ReportHelper.generateTopStaff(AppState.allStaff, reportChart, reportSect, reportTabPane);
    }


    /**
     * Synchronizes the current staff list with the global {@link AppState}.
     * <p>
     * This ensures that any changes made locally (e.g., add/edit/remove)
     * are reflected across the application.
     */
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
