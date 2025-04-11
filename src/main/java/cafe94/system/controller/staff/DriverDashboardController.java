package cafe94.system.controller.staff;

import cafe94.system.data.DataSaver;
import cafe94.system.model.order.*;
import cafe94.system.utils.AppState;
import cafe94.system.utils.OutstandingOrderHelper;
import cafe94.system.utils.OutstandingOrderHelper.OrderTableType;
import cafe94.system.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

/**
 * Controller for the Driver Dashboard in the Cafe94 system.
 * <p>
 * This dashboard allows logged-in drivers to:
 * <ul>
 *   <li>View delivery orders assigned to them</li>
 *   <li>Mark orders as delivered, updating their status</li>
 *   <li>Refresh the order list manually</li>
 * </ul>
 *
 * The controller assigns unassigned delivery orders to the current driver
 * on load, and only displays orders with {@code READY_TO_DELIVER} status.
 * </p>
 *
 * Uses {@link OutstandingOrderHelper} for table configuration and
 * {@link DataSaver} for saving order updates.
 */
public class DriverDashboardController {

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdCol;
    @FXML private TableColumn<Order, Integer> customerIdCol;
    @FXML private TableColumn<Order, String> addressCol;
    @FXML private TableColumn<Order, String> statusCol;

    @FXML private TableView<OrderItem> itemsTable;
    @FXML private TableColumn<OrderItem, String> itemNameCol;
    @FXML private TableColumn<OrderItem, Integer> qtyCol;
    @FXML private TableColumn<OrderItem, Double> priceCol;
    @FXML private TableColumn<OrderItem, Double> subtotalCol;

    @FXML private Label orderTotalLabel;
    @FXML private Button markDeliveredButton;
    @FXML private Label loggedInAsLabel;

    private static final String ORDERS_FILE = "src/main/resources/data/orders.txt";
    private OrderManaged orderManaged;

    /**
     * Sets up the Driver Dashboard with the given {@link OrderManaged} instance.
     * Loads all outstanding delivery orders assigned to the current driver.
     *
     * @param orderManaged The shared order manager instance containing all orders.
     */
    public void setup(OrderManaged orderManaged) {
        this.orderManaged = orderManaged;
        if (AppState.loggedInStaff != null) {
            loggedInAsLabel.setText("😊 Welcome, " + AppState.loggedInStaff.getFullName());
        }

        loadDriverOrders();
    }

    /**
     * Initializes the controller components.
     */
    @FXML
    private void initialize() {
        OutstandingOrderHelper.setupOrderColumnsByRole(
                OrderTableType.DRIVER,
                orderIdCol,
                customerIdCol,
                null, // orderTypeCol not used for driver
                addressCol,
                statusCol);

        OutstandingOrderHelper.setupOrderItemColumns(
                itemNameCol, qtyCol, priceCol, subtotalCol);

        OutstandingOrderHelper.setupOrderSelectionListener(
                ordersTable,
                itemsTable,
                orderTotalLabel,
                markDeliveredButton,
                OrderStatus.READY_TO_DELIVER,
                null
        );

        markDeliveredButton.setOnAction(e -> handleMarkDelivered());
    }

    /**
     * Loads orders assigned to the current driver.
     */
    private void loadDriverOrders() {
        int driverId = AppState.loggedInStaff.getId();

        // Assign all READY_TO_DELIVER delivery orders without a driver
        for (Order o : orderManaged.getOutstandingOrders()) {
            if (o instanceof DeliveryOrder delivery &&
                    delivery.getStatus() == OrderStatus.READY_TO_DELIVER &&
                    delivery.getAssignedStaffDriverID() == -1) {
                delivery.setAssignedStaffDriverID(driverId);
            }
        }

        // Show only this driver's assigned orders
        List<Order> assigned = orderManaged.getOutstandingOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.READY_TO_DELIVER)
                .filter(o -> o instanceof DeliveryOrder d && d.getAssignedStaffDriverID() == driverId)
                .toList();

        ordersTable.setItems(FXCollections.observableArrayList(assigned));
    }


    /**
     * Marks the selected order as delivered.
     */
    private void handleMarkDelivered() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();

        if (selected != null && selected.getStatus() == OrderStatus.READY_TO_DELIVER) {
            selected.setStatus(OrderStatus.DELIVERED);
            selected.setCompleted(true);
            DataSaver.saveOrders(ORDERS_FILE, AppState.orderManaged.getAllOrders());
            showInfo("✔ Order #" + selected.getOrderID() + " marked as delivered.");
            loadDriverOrders();
        }
    }

    /**
     * Manually refreshes the table.
     */
    @FXML
    private void handleRefresh() {
        loadDriverOrders();
    }

    /**
     * Logs out the user.
     */
    @FXML
    private void handleLogout() {
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }

    /**
     * Displays an informational alert.
     * @param msg Message to display
     */
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cafe94");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}














