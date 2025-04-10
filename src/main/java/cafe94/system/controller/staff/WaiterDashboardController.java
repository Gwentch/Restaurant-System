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
import java.util.Arrays;
import java.util.List;

public class WaiterDashboardController {


    public Button backButton;
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

    @FXML
    private void handleAddMenu() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int qty = menuQuantitySpinner.getValue();
            basketItems.add(new OrderItem(selected, qty));
            refreshBasket();
        }
    }

    @FXML
    private void handleAddSpecial() {
        MenuItem selected = specialsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int qty = specialQuantitySpinner.getValue();
            basketItems.add(new OrderItem(selected, qty));
            refreshBasket();
        }
    }

    @FXML
    private void handleRemoveSelected() {
        OrderItem selected = basketTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            basketItems.remove(selected);
            refreshBasket();
        }
    }

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
        DataSaver.saveOrders("src/main/resources/data/orders.txt", AppState.orderManaged.getAllOrders());

        showInfo("✔ Eat-In Order placed successfully for customer ID: " + selectedCustomer.getId());
        basketItems.clear();
        refreshBasket();
    }


    private void refreshBasket() {
        basketTable.setItems(FXCollections.observableArrayList(basketItems));
        double total = basketItems.stream().mapToDouble(OrderItem::getSubtotal).sum();
        eatInOrderTotalLabel.setText(String.format("Total: £%.2f", total));
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

    @FXML
    private void handleLogout() {
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }

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
