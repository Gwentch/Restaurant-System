package cafe94.system.controller.customer;

import cafe94.system.utils.AppState;
import cafe94.system.model.order.Order;
import cafe94.system.model.order.OrderItem;
import cafe94.system.model.order.OrderManaged;
import cafe94.system.model.user.Customer;
import cafe94.system.utils.SceneManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class OrderHistoryController {

    // -------- FXML --------
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;
    @FXML
    private TableColumn<Order, String> orderTypeColumn;
    @FXML
    private TableColumn<Order, Double> orderTotalColumn;

    @FXML
    private TableView<OrderItem> itemsTable;
    @FXML
    private TableColumn<OrderItem, String> itemNameColumn;
    @FXML
    private TableColumn<OrderItem, Integer> quantityColumn;
    @FXML
    private TableColumn<OrderItem, Double> unitPriceColumn;
    @FXML
    private TableColumn<OrderItem, Double> subtotalColumn;

    @FXML
    private Label totalLabel;

    // -------- Data --------
    private Customer customer;
    private OrderManaged orderManaged;

    @FXML
    private void initialize() {
        orderIdColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getOrderID()).asObject());
        orderTypeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOrderType().toString()));
        orderTotalColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalAmount()).asObject());

        itemNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMenuItem().getName()));
        quantityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        unitPriceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getMenuItem().getPrice()).asObject());
        subtotalColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selectedOrder) -> {
            if (selectedOrder != null) {
                itemsTable.setItems(FXCollections.observableArrayList(selectedOrder.getItems()));
                totalLabel.setText(String.format("Total: £%.2f", selectedOrder.getTotalAmount()));
            } else {
                itemsTable.getItems().clear();
                totalLabel.setText("Total: £0.00");
            }
        });
    }

    // -------- Setup method (replaces setX methods) --------
    public void setup(Customer customer, OrderManaged orderManaged) {
        this.customer = customer;
        this.orderManaged = orderManaged;
        reloadOrders();
    }

    private void reloadOrders() {
        if (customer == null || orderManaged == null) return;

        List<Order> customerOrders = orderManaged.findOrdersByCustomer(customer.getId());
        ordersTable.setItems(FXCollections.observableArrayList(customerOrders));
        itemsTable.getItems().clear();
        totalLabel.setText("Total: £0.00");
    }


    @FXML
    private void handleBack() {
        SceneManager.switchToWithControllerAndSetup("customer/CustomerDashboard.fxml",
                (CustomerDashboardController c) -> {
                    c.setup(AppState.loggedInCustomer, AppState.orderManaged, AppState.menuItems, AppState.dailySpecials);
                }
        );
    }
}



