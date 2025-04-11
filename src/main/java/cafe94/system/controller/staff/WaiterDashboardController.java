package cafe94.system.controller.staff;

import cafe94.system.data.DataSaver;
import cafe94.system.model.booking.Booking;
import cafe94.system.model.booking.BookingStatus;
import cafe94.system.model.order.*;
import cafe94.system.model.user.Customer;
import cafe94.system.utils.AppState;
import cafe94.system.utils.OutstandingOrderHelper;
import cafe94.system.utils.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import cafe94.system.model.menu.MenuItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Waiter Dashboard in the Cafe94 system.
 * <p>
 * This dashboard allows waiters to:
 * <ul>
 *   <li>Place eat-in orders on behalf of customers</li>
 *   <li>View and manage outstanding orders (approve for delivery order only)</li>
 *   <li>Manage customer bookings by approving them</li>
 * </ul>
 *
 * Integrates with {@link AppState}, {@link OutstandingOrderHelper}, and {@link DataSaver}
 * to support order filtering, menu item selection, and stateful UI updates.
 */
public class WaiterDashboardController {

    @FXML private Label eatInOrderTotalLabel;
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ChoiceBox<OrderType> orderType;
    @FXML private TableView<MenuItem> specialsTable, menuTable;
    @FXML private TableColumn<MenuItem, String> specialNameColumn, menuNameColumn;
    @FXML private TableColumn<MenuItem, Double> specialPriceColumn, menuPriceColumn;
    @FXML private Spinner<Integer> specialQuantitySpinner, menuQuantitySpinner;
    @FXML private TableView<OrderItem> basketTable;
    @FXML private TableColumn<OrderItem, String> basketNameColumn;
    @FXML private TableColumn<OrderItem, Integer> basketQuantityColumn;
    @FXML private TableColumn<OrderItem, Double> basketPriceColumn, basketSubtotalColumn;

    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdCol;
    @FXML private TableColumn<Order, Integer> customerIdCol;
    @FXML private TableColumn<Order, String> orderTypeCol;
    @FXML private TableColumn<Order, String> orderStatusCol;

    @FXML private TableView<OrderItem> itemsTable;
    @FXML private TableColumn<OrderItem, String> itemNameCol;
    @FXML private TableColumn<OrderItem, Integer> qtyCol;
    @FXML private TableColumn<OrderItem, Double> priceCol;
    @FXML private TableColumn<OrderItem, Double> subtotalCol;

    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, Integer> idCol, customerCol, guestCol, durationCol;
    @FXML private TableColumn<Booking, String> dateCol, timeCol, bookingStatusCol;
    @FXML private Label orderTotalLabel;
    @FXML private Label loggedInAsLabel;
    @FXML private Button approveButton;
    @FXML private Label reminderLabel;

    private static final String ORDERS_FILE = "src/main/resources/data/orders.txt";
    private final List<OrderItem> basketItems = new ArrayList<>();

    private OrderManaged orderManaged;
    private List<Booking> bookings;

    /**
     * Sets up the Waiter Dashboard with the shared order manager and populates
     * order tables. Also shows the logged-in staff member's name.
     *
     * @param orderManaged The order manager containing all outstanding orders.
     */
    public void setup(OrderManaged orderManaged) {
        this.orderManaged = orderManaged;

        if (AppState.loggedInStaff != null) {
            loggedInAsLabel.setText("😊 Welcome, " + AppState.loggedInStaff.getFullName());
        }

        loadOrders("My Role's Orders");
    }

    /**
     * Initializes the Waiter Dashboard UI components.
     * <p>
     * Sets up all table views, combo boxes, listeners, and default values.
     * This method is automatically called by JavaFX after the FXML is loaded.
     * </p>
     */
    @FXML
    private void initialize() {
        loggedInAsLabel.setText("Logged in as: " + AppState.loggedInStaff.getFullName());
        // Eat-in order for customer
        customerComboBox.setItems(FXCollections.observableArrayList(AppState.allCustomer));
        customerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer c) {
                return c == null ? "" :  "[ID: " + c.getId() + "] "+ c.getFullName();
            }
            @Override
            public Customer fromString(String s) { return null; }
        });

        orderType.setItems(FXCollections.observableArrayList(OrderType.values()));

        orderType.setConverter(new StringConverter<>() {
            @Override
            public String toString(OrderType type) {
                return type == null ? "" : type.getDisplayName(); // shown in UI
            }

            @Override
            public OrderType fromString(String string) {
                for (OrderType type : OrderType.values()) {
                    if (type.getDisplayName().equals(string)) {
                        return type;
                    }
                }
                return null;
            }
        });

        orderTypeCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOrderType().getDisplayName()));

        orderType.setValue(OrderType.EAT_IN);
        orderType.setDisable(true);

        menuNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        menuPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        menuTable.setItems(FXCollections.observableArrayList(AppState.menuItems));
        menuQuantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));

        specialNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        specialPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        specialsTable.setItems(FXCollections.observableArrayList(AppState.dailySpecials));
        specialQuantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));

        basketNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMenuItem().getName()));
        basketQuantityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        basketPriceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getMenuItem().getPrice()).asObject());
        basketSubtotalColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        // clear selection logic
        specialsTable.setOnMouseClicked(e -> clearOtherSelections(menuTable, basketTable));
        menuTable.setOnMouseClicked(e -> clearOtherSelections(specialsTable, basketTable));
        basketTable.setOnMouseClicked(e -> clearOtherSelections(specialsTable, menuTable));


        // Booking table
        bookings = AppState.bookingList;
        bookingTable.setItems(FXCollections.observableArrayList(bookings));

        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getBookingID()).asObject());
        customerCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCustomerID()).asObject());
        guestCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getGuestNum()).asObject());
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));
        timeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTime()));
        durationCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getDurationHours()).asObject());
        bookingStatusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));

        // Order table based on role
        OutstandingOrderHelper.setupOrderColumnsByRole(
                OutstandingOrderHelper.OrderTableType.WAITER,
                orderIdCol,
                customerIdCol,
                orderTypeCol,
                null,
                orderStatusCol
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

        refreshBasket();
    }

    /**
     * Adds the selected regular menu item and specified quantity to the basket.
     * Refreshes the basket view to reflect the new item.
     */
    @FXML
    private void handleAddMenu() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int qty = menuQuantitySpinner.getValue();
            basketItems.add(new OrderItem(selected, qty));
            refreshBasket();
        }
    }

    /**
     * Adds the selected daily special item and specified quantity to the basket.
     * Refreshes the basket view to reflect the new item.
     */
    @FXML
    private void handleAddSpecial() {
        MenuItem selected = specialsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int qty = specialQuantitySpinner.getValue();
            basketItems.add(new OrderItem(selected, qty));
            refreshBasket();
        }
    }

    /**
     * Removes the selected item from the basket and updates the basket view.
     */
    @FXML
    private void handleRemoveSelected() {
        OrderItem selected = basketTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            basketItems.remove(selected);
            refreshBasket();
        }
    }

    /**
     * Confirms and places an Eat-In order for the selected customer
     * using the current basket items. Clears the basket after saving.
     */
    @FXML
    private void handleConfirmOrder() {
        Customer selectedCustomer = customerComboBox.getValue();
        if (selectedCustomer == null || basketItems.isEmpty()) {
            showInfo("Please select a customer and add items to the basket.");
            return;
        }

        Order newOrder = new EatInOrder(selectedCustomer.getId());
        newOrder.getItems().addAll(basketItems);
        AppState.orderManaged.addOrder(newOrder);
        DataSaver.saveOrders(ORDERS_FILE, AppState.orderManaged.getAllOrders());

        showInfo("✔ Eat-In Order placed successfully for customer ID: " + selectedCustomer.getId());
        basketItems.clear();
        refreshBasket();
    }

    /**
     * Refreshes the basket TableView and updates the total label based on current basket items.
     */
    private void refreshBasket() {
        basketTable.setItems(FXCollections.observableArrayList(basketItems));
        double total = basketItems.stream().mapToDouble(OrderItem::getSubtotal).sum();
        eatInOrderTotalLabel.setText(String.format("Total: £%.2f", total));
    }

    /**
     * Loads and filters outstanding orders based on the selected filter type.
     * Waiters typically see either all outstanding orders or those relevant to their role.
     *
     * @param filterType The filter criteria selected in the UI.
     */
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

    /**
     * Approves a delivery order that is currently in {@code PENDING_APPROVAL} status.
     * Once approved, its status is updated to {@code PENDING_PREP} and saved to file.
     */
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

    /**
     * Handles approval of a selected customer booking.
     * If the booking is not already approved, updates its status to APPROVED,
     * saves the changes, and refreshes the booking table.
     */
    @FXML
    private void handleBookingApprove() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Please select a booking to approve.");
            return;
        }

        if (selected.getStatus() == BookingStatus.APPROVED) {
            showInfo("This booking has already been approved.");
            return;
        }

        selected.setStatus(BookingStatus.APPROVED);
        DataSaver.saveBookings("src/main/resources/data/booking.txt", bookings);
        bookingTable.refresh(); // This makes the table row update with the new status

        showInfo("✔ Booking approved.");
    }

    /**
     * Handles the logout action for the waiter.
     * Redirects the user back to the welcome login screen.
     */
    @FXML
    private void handleLogout() {
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }

    /**
     * Clears selection across multiple tables to avoid accidental multi-selection.
     *
     * @param tables Tables to clear selection from.
     */
    // Helper method
    private void clearOtherSelections(TableView<?>... tables) {
        for (TableView<?> table : tables) {
            table.getSelectionModel().clearSelection();
        }
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cafe94");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
