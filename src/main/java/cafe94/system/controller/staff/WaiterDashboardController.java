package cafe94.system.controller.staff;

import cafe94.system.data.DataSaver;
import cafe94.system.model.order.*;
import cafe94.system.utils.AppState;
import cafe94.system.utils.OutstandingOrderHelper;
import cafe94.system.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class WaiterDashboardController {

    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdCol;
    @FXML private TableColumn<Order, Integer> customerIdCol;
    @FXML private TableColumn<Order, String> orderTypeCol;
    @FXML private TableColumn<Order, String> statusCol;

    @FXML private TableView<OrderItem> itemsTable;
    @FXML private TableColumn<OrderItem, String> itemNameCol;
    @FXML private TableColumn<OrderItem, Integer> qtyCol;
    @FXML private TableColumn<OrderItem, Double> priceCol;
    @FXML private TableColumn<OrderItem, Double> subtotalCol;

    @FXML private Label orderTotalLabel;
    @FXML private Label loggedInAsLabel;
    @FXML private Button approveButton;
    @FXML private Label reminderLabel;

    private static final String ORDERS_FILE = "src/main/resources/data/orders.txt";

    private OrderManaged orderManaged;


    // Called from Main App to inject data
    public void setup(OrderManaged orderManaged) {
        this.orderManaged = orderManaged;

        if (AppState.loggedInStaff != null) {
            loggedInAsLabel.setText("😊 Welcome, " + AppState.loggedInStaff.getFullName());
        }

        loadOrders("My Role's Orders");
    }


    @FXML
    private void initialize() {
        loggedInAsLabel.setText("Logged in as: " + AppState.loggedInStaff.getFullName());

        // Order table based on role
        OutstandingOrderHelper.setupOrderColumnsByRole(
                OutstandingOrderHelper.OrderTableType.WAITER,
                orderIdCol,
                customerIdCol,
                orderTypeCol,
                null,
                statusCol
        );
        // Item table
        OutstandingOrderHelper.setupOrderItemColumns(itemNameCol, qtyCol, priceCol, subtotalCol);

        // Selection listener
        OutstandingOrderHelper.setupOrderSelectionListener(
                ordersTable,
                itemsTable,
                orderTotalLabel,
                approveButton,
                OrderStatus.PENDING_APPROVAL,
                reminderLabel
        );

        // Filter dropdown for role based or all order
        OutstandingOrderHelper.setupFilterComboBox(filterComboBox, () -> loadOrders(filterComboBox.getValue()));

        approveButton.setOnAction(e -> handleApprove());
    }

    private void loadOrders(String filterType) {
        if (orderManaged == null) {
            return;
        }

        List<Order> all = orderManaged.getOutstandingOrders();
        List<Order> filtered = switch (filterType) {
            case "All Outstanding" -> all;
            case "My Role's Orders" -> all.stream()
                    .filter(o -> o.getStatus() == OrderStatus.READY_TO_SERVE || o.getStatus() == OrderStatus.PENDING_APPROVAL)
                    .toList();
            default -> List.of();
        };

        ordersTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void handleApprove() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getStatus() != OrderStatus.PENDING_APPROVAL) {
            return;
        }

        selected.setStatus(OrderStatus.PENDING_PREP);
        showInfo("✔ Order approved and sent to kitchen for preparation.");

        DataSaver.saveOrders(ORDERS_FILE, AppState.orderManaged.getAllOrders());
        loadOrders(filterComboBox.getValue());
    }

    @FXML
    private void handleLogout() {
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cafe94");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
