package cafe94.system.controller.staff;

import cafe94.system.data.DataSaver;
import cafe94.system.model.menu.MenuItem;
import cafe94.system.model.order.*;
import cafe94.system.model.user.Driver;
import cafe94.system.model.user.Staff;
import cafe94.system.utils.AppState;
import cafe94.system.utils.OutstandingOrderHelper;
import cafe94.system.utils.SceneManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChefDashboardController {

    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdCol;
    @FXML private TableColumn<Order, Integer> customerIdCol;
    @FXML private TableColumn<Order, String> orderTypeCol;
    @FXML private TableColumn<Order, String> orderStatusCol;

    @FXML private Button actionButton;
    @FXML private Label orderTotalLabel;

    @FXML private TableView<OrderItem> itemsTable;
    @FXML private TableColumn<OrderItem, String> itemNameCol;
    @FXML private TableColumn<OrderItem, Integer> qtyCol;
    @FXML private TableColumn<OrderItem, Double> priceCol;
    @FXML private TableColumn<OrderItem, Double> subtotalCol;

    @FXML private TableView<MenuItem> specialsTable;
    @FXML private TableColumn<MenuItem, String> specialNameCol;
    @FXML private TableColumn<MenuItem, Double> specialPriceCol;

    @FXML private ComboBox<MenuItem> menuComboBox;
    @FXML private TextField specialNameField;
    @FXML private TextField specialPriceField;
    @FXML private Label loggedInAsLabel;

    private static final String DAILY_SPECIALS_FILE = "src/main/resources/data/daily_special.txt";
    private static final String ORDERS_FILE = "src/main/resources/data/orders.txt";

    private final ObservableList<MenuItem> dailySpecials = FXCollections.observableArrayList();
    private final ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
    private final ObservableList<Order> chefOrders = FXCollections.observableArrayList();

    private OrderManaged orderManaged;
    private List<Staff> staffList;


    // Called from Main App to inject data
    public void setup(OrderManaged orderManaged, List<MenuItem> menuItemsFromFile, List<MenuItem> specialsFromFile) {
        this.orderManaged = orderManaged;
        this.menuItems.setAll(menuItemsFromFile);
        this.dailySpecials.setAll(specialsFromFile);
        this.staffList = AppState.allStaff;

        menuComboBox.setItems(menuItems);
        specialsTable.setItems(dailySpecials);

        if (AppState.loggedInStaff != null) {
            loggedInAsLabel.setText("😊 Welcome, " + AppState.loggedInStaff.getFullName());
        }

        loadOrders("My Role's Orders");
    }

    @FXML
    private void initialize() {
        // Set up the columns based on role
        OutstandingOrderHelper.setupOrderColumnsByRole(
                OutstandingOrderHelper.OrderTableType.CHEF,
                orderIdCol,
                customerIdCol,
                orderTypeCol,
                null,
                orderStatusCol
        );

        // Items table
        OutstandingOrderHelper.setupOrderItemColumns(itemNameCol, qtyCol, priceCol, subtotalCol);

        // Order selection listener
        OutstandingOrderHelper.setupOrderSelectionListener(
                ordersTable,
                itemsTable,
                orderTotalLabel,
                actionButton,
                OrderStatus.PENDING_PREP,
                null
        );

        // ComboBox for filtering
        OutstandingOrderHelper.setupFilterComboBox(filterComboBox, () ->
                loadOrders(filterComboBox.getValue()));

        // Daily Specials TableView
        specialsTable.setItems(dailySpecials);
        menuComboBox.setItems(menuItems);

        specialNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));
        specialPriceCol.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getPrice()));
    }


    private void loadOrders(String filterType) {
        if (orderManaged == null) {
            return;
        }

        List<Order> all = orderManaged.getOutstandingOrders();
        List<Order> filtered = "All Outstanding".equals(filterType)
                ? all
                : all.stream().filter(o -> o.getStatus() == OrderStatus.PENDING_PREP).toList();

        chefOrders.setAll(filtered);
        ordersTable.setItems(chefOrders);
    }

    @FXML
    private void handleAddFromMenu() {
        MenuItem selected = menuComboBox.getValue();
        if (selected != null && !dailySpecials.contains(selected)) {
            dailySpecials.add(selected);
            saveSpecials();
            showInfo("Item added from menu.");
        }
    }

    @FXML
    private void handleAddNewSpecial() {
        String name = specialNameField.getText().trim();
        String priceText = specialPriceField.getText().trim();

        if (!name.isEmpty() && priceText.matches("\\d+(\\.\\d{1,2})?")) {
            double price = Double.parseDouble(priceText);
            MenuItem newItem = new MenuItem(name, price);
            dailySpecials.add(newItem);
            saveSpecials();
            showInfo("New special item added.");
        } else {
            showError("Invalid input. Please enter a valid name and price.");
        }
    }

    @FXML
    private void handleRemoveSpecial() {
        MenuItem selected = specialsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dailySpecials.remove(selected);
            saveSpecials();
            showInfo("Item removed from daily specials.");
        }
    }

    @FXML
    private void handleLogout() {
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }

    @FXML
    private void handleMarkOrderComplete() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getStatus() != OrderStatus.PENDING_PREP) {
            return;
        }

        // Set new status based on order type
        selected.setStatus(selected.getType().getNextStatus());

        // Assign driver if DELIVERY
        if (selected.getType() == OrderType.DELIVERY) {
            List<Driver> drivers = getAvailableDrivers();
            if (!drivers.isEmpty()) {
                Driver assigned = drivers.get(new Random().nextInt(drivers.size()));
                selected.setAssignedStaffDriverID(assigned.getId());

                System.out.println("Assigned driver ID: " + assigned.getId() + " to order #" + selected.getOrderID());

            } else {
                showError("⚠ Currently no available drivers to assign!");
            }

        }

        // Save updated orders
        DataSaver.saveOrders(ORDERS_FILE, AppState.orderManaged.getAllOrders());

        showInfo("✔ Order #" + selected.getOrderID() + " marked as ready!");
        loadOrders(filterComboBox.getValue());



        // Clear UI
        ordersTable.getSelectionModel().clearSelection();
        itemsTable.getItems().clear();
        orderTotalLabel.setText("Total: £0.00");
        actionButton.setDisable(true);
    }


    private List<Driver> getAvailableDrivers() {
        List<Driver> drivers = new ArrayList<>();
        if (staffList == null) {
            return drivers;
        }

        for (Staff staff : AppState.allStaff) {
            if (staff instanceof Driver) {
                drivers.add((Driver) staff);
            }
        }
        return drivers;
    }

    // Helper methods
    private void saveSpecials() {
        DataSaver.saveDailySpecials(DAILY_SPECIALS_FILE, dailySpecials);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cafe94");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
