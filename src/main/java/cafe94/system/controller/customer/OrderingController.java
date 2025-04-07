package cafe94.system.controller.customer;

import cafe94.system.model.order.DeliveryOrder;
import cafe94.system.model.order.TakeawayOrder;
import cafe94.system.data.DataSaver;
import cafe94.system.model.menu.MenuItem;
import cafe94.system.model.order.OrderType;
import cafe94.system.model.order.OrderItem;
import cafe94.system.model.order.*;
import cafe94.system.model.user.Customer;
import cafe94.system.utils.AppState;
import cafe94.system.utils.SceneManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.List;

public class OrderingController {

    // === FXML ===
    @FXML private TextField customerId;
    @FXML private ChoiceBox<OrderType> orderType;

    @FXML private TableView<MenuItem> specialsTable;
    @FXML private TableColumn<MenuItem, String> specialNameColumn;
    @FXML private TableColumn<MenuItem, Double> specialPriceColumn;
    @FXML private Spinner<Integer> specialQuantitySpinner;

    @FXML private TableView<MenuItem> menuTable;
    @FXML private TableColumn<MenuItem, String> menuNameColumn;
    @FXML private TableColumn<MenuItem, Double> menuPriceColumn;
    @FXML private Spinner<Integer> menuQuantitySpinner;

    @FXML private TableView<OrderItem> basketTable;
    @FXML private TableColumn<OrderItem, String> basketNameColumn;
    @FXML private TableColumn<OrderItem, Integer> basketQuantityColumn;
    @FXML private TableColumn<OrderItem, Double> basketPriceColumn;
    @FXML private TableColumn<OrderItem, Double> basketSubtotalColumn;

    @FXML private Label orderTotalLabel;

    private static final String ORDERS_FILE = "src/main/resources/data/orders.txt";

    private final ObservableList<MenuItem> dailySpecials = FXCollections.observableArrayList();
    private final ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
    private final ObservableList<OrderItem> basket = FXCollections.observableArrayList();

    private Customer customer;
    private OrderManaged orderManaged;
    private List<MenuItem> initialMenuItems;
    private List<MenuItem> initialDailySpecials;
    private boolean initialized = false;

    // === INIT ===
    @FXML
    public void initialize() {
        orderType.setItems(FXCollections.observableArrayList(OrderType.TAKEAWAY, OrderType.DELIVERY));
        orderType.getSelectionModel().selectFirst();

        menuNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        menuPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        menuTable.setItems(menuItems);
        menuQuantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));

        specialNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        specialPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        specialsTable.setItems(dailySpecials);
        specialQuantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));

        basketNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMenuItem().getName()));
        basketQuantityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        basketPriceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getMenuItem().getPrice()).asObject());
        basketSubtotalColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());
        basketTable.setItems(basket);

        // clear selection logic
        specialsTable.setOnMouseClicked(e -> clearOtherSelections(menuTable, basketTable));
        menuTable.setOnMouseClicked(e -> clearOtherSelections(specialsTable, basketTable));
        basketTable.setOnMouseClicked(e -> clearOtherSelections(specialsTable, menuTable));

        updateOrderTotal();
        initialized = true;
        applySetup(); // If setup() ran before initialize, now apply data
    }

    private void clearOtherSelections(TableView<?>... tables) {
        for (TableView<?> table : tables) {
            table.getSelectionModel().clearSelection();
        }
    }

    // Setup Data
    public void setup(Customer customer) {
        this.customer = customer;
        this.orderManaged = AppState.orderManaged;
        this.initialMenuItems = AppState.menuItems;
        this.initialDailySpecials = AppState.dailySpecials;
        applySetup();
    }

    private void applySetup() {
        if (!initialized || customer == null) {
            return;
        }
        customerId.setText(String.valueOf(customer.getId()));
        menuItems.setAll(initialMenuItems);
        dailySpecials.setAll(initialDailySpecials);
    }

    // Basket Actions
    @FXML private void handleAddMenu() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        addToBasket(selected, menuQuantitySpinner.getValue());
    }

    @FXML private void handleAddSpecial() {
        MenuItem selected = specialsTable.getSelectionModel().getSelectedItem();
        addToBasket(selected, specialQuantitySpinner.getValue());
    }

    private void addToBasket(MenuItem item, int qty) {
        if (item == null) return;

        for (OrderItem orderItem : basket) {
            if (orderItem.getMenuItem().equals(item)) {
                basket.remove(orderItem);
                basket.add(new OrderItem(item, orderItem.getQuantity() + qty));
                updateOrderTotal();
                return;
            }
        }
        basket.add(new OrderItem(item, qty));
        updateOrderTotal();
    }

    @FXML private void handleRemoveSelected() {
        OrderItem selected = basketTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            basket.remove(selected);
            updateOrderTotal();
        }
    }

    // Order Confirmation
    @FXML private void handleConfirmOrder() {
        if (basket.isEmpty()) {
            showInfo("Basket Empty", "Please add items before placing an order.");
            return;
        }

        Order order;
        OrderType selectedType = orderType.getSelectionModel().getSelectedItem();

        if (selectedType == OrderType.TAKEAWAY) {
            order = new TakeawayOrder(customer.getId());
        } else if (selectedType == OrderType.DELIVERY) {
            order = new DeliveryOrder(customer.getId());
        } else {
            showInfo("Invalid Order Type", "Please select a valid order type.");
            return;
        }



        // Add basket items
        basket.forEach(item -> order.addItem(item.getMenuItem(), item.getQuantity()));

        // Load popup and handle confirmation
        Pair<Stage, OrderDetailsPopupController> result =
                SceneManager.loadPopup("order/OrderDetailsPopup.fxml", "Order Details", 500, 600);
        if (result == null) return;

        result.getValue().setup(order, customer);
        result.getKey().showAndWait();

        if (!result.getValue().isConfirmed()) return;

        // Apply order-type-specific fields
        if (order instanceof TakeawayOrder) {
            ((TakeawayOrder) order).setPickupTime(result.getValue().getPickupTime());
        } else if (order instanceof DeliveryOrder) {
            ((DeliveryOrder) order).setDeliveryAddress(result.getValue().getDeliveryAddress());
            ((DeliveryOrder) order).setEstimatedDeliveryTime(result.getValue().getEstimatedDeliveryTime());
        }


        // Save and clear
        orderManaged.addOrder(order);
        DataSaver.saveOrders(ORDERS_FILE, AppState.orderManaged.getAllOrders());
        showInfo("Order Successful", "Order ID: " + order.getOrderID() + "\nThank you for ordering with Cafe94!");
        basket.clear();
        updateOrderTotal();
        menuQuantitySpinner.getValueFactory().setValue(1);
        specialQuantitySpinner.getValueFactory().setValue(1);
    }


    @FXML
    private void handleBack() {
        SceneManager.switchToWithControllerAndSetup("customer/CustomerDashboard.fxml", c -> {
            ((CustomerDashboardController) c).setup(
                    AppState.loggedInCustomer,
                    AppState.orderManaged,
                    AppState.menuItems,
                    AppState.dailySpecials
            );
        });
    }

    // helper method
    private void updateOrderTotal() {
        double total = basket.stream().mapToDouble(OrderItem::getSubtotal).sum();
        orderTotalLabel.setText(String.format("Total: £%.2f", total));
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cafe94 - " + title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
