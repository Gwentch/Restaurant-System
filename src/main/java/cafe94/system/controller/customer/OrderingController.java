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

/**
 * Controller for the Ordering view in the Cafe94 system.
 * <p>
 * This controller allows customers (or staff on behalf of customers)
 * to browse the menu and daily specials, add items to a basket, and
 * place takeaway or delivery orders. It handles item selection, basket
 * management, total calculation, and order confirmation.
 * </p>
 *
 * Features include:
 * <ul>
 *   <li>Dynamic loading of menu and daily specials</li>
 *   <li>Basket management (add/remove items, subtotal tracking)</li>
 *   <li>Takeaway and delivery order creation with validation</li>
 *   <li>Popup confirmation with editable delivery/pickup info</li>
 *   <li>Data persistence of confirmed orders</li>
 * </ul>
 *
 * Integrates with {@link OrderManaged}, {@link DataSaver}, and {@link SceneManager}
 * to ensure consistent application state and persistence.
 */
public class OrderingController {

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


    /**
     * Initializes the ordering screen after the FXML UI components are loaded.
     * <p>
     * Binds menu and daily specials to tables, sets up quantity spinners,
     * basket item columns, and interaction behaviors like selection clearing.
     */
    @FXML
    private void initialize() {
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

    /**
     * Clears row selections in the provided TableViews to avoid multi-selection issues.
     *
     * @param tables The tables to clear selection from.
     */
    private void clearOtherSelections(TableView<?>... tables) {
        for (TableView<?> table : tables) {
            table.getSelectionModel().clearSelection();
        }
    }

    /**
     * Sets up the ordering view with the current customer, order manager,
     * and menu data. This method is typically called after scene loading
     * to initialize content before interaction.
     *
     * @param customer      The logged-in customer placing the order.
     * @param orderManaged  The shared order manager instance.
     * @param menuItems     The full list of available menu items.
     * @param dailySpecials The list of current daily specials.
     */
    public void setup(Customer customer, OrderManaged orderManaged, List<MenuItem> menuItems, List<MenuItem> dailySpecials) {
        this.customer = customer;
        this.orderManaged = orderManaged;
        this.initialMenuItems = menuItems;
        this.initialDailySpecials = dailySpecials;
        applySetup();
    }

    /**
     * Populates menu and specials tables once both the controller and scene are initialized.
     * Called from `initialize()` or `setup()` depending on execution order.
     */
    private void applySetup() {
        if (!initialized || customer == null) {
            return;
        }
        customerId.setText(String.valueOf(customer.getId()));
        menuItems.setAll(initialMenuItems);
        dailySpecials.setAll(initialDailySpecials);
    }

    /**
     * Adds a selected regular menu item to the basket, updating quantity if it already exists.
     */
    // Basket Actions
    @FXML private void handleAddMenu() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        addToBasket(selected, menuQuantitySpinner.getValue());
    }

    /**
     * Adds a selected daily special to the basket, updating quantity if already present.
     */
    @FXML private void handleAddSpecial() {
        MenuItem selected = specialsTable.getSelectionModel().getSelectedItem();
        addToBasket(selected, specialQuantitySpinner.getValue());
    }

    /**
     * Adds the specified menu item to the basket. If it already exists, increases the quantity.
     *
     * @param item The selected menu item to add.
     * @param qty  The quantity to add.
     */
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

    /**
     * Removes the selected item from the basket.
     */
    @FXML private void handleRemoveSelected() {
        OrderItem selected = basketTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            basket.remove(selected);
            updateOrderTotal();
        }
    }

    /**
     * Handles the order confirmation process.
     * <p>
     * Based on the selected order type, creates a new order,
     * opens a confirmation popup to gather additional info, and
     * saves the order to the system upon confirmation.
     */
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
            order.setStatus(OrderStatus.READY_TO_DELIVER);
            ((DeliveryOrder) order).setAssignedStaffDriverID(-1);
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

    /**
     * Returns the user to the Customer Dashboard, reinitializing the view with current state.
     */
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

    /**
     * Recalculates and displays the total cost of all items in the basket.
     */
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
