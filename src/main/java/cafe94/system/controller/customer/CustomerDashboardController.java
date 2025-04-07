package cafe94.system.controller.customer;

import cafe94.system.data.DataSaver;
import cafe94.system.model.menu.MenuItem;
import cafe94.system.model.order.OrderManaged;
import cafe94.system.model.user.Customer;
import cafe94.system.utils.AppState;
import cafe94.system.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class CustomerDashboardController {

    @FXML private VBox customerInfoBox;
    @FXML private Label loggedInAsLabel;
    @FXML private Label customerId;
    @FXML private Label nameLabel;
    @FXML private Label addressLabel;

    private Customer customer;
    private OrderManaged orderManaged;
    private List<MenuItem> menuItems;
    private List<MenuItem> dailySpecials;

    public void setup(Customer customer, OrderManaged orderManaged, List<MenuItem> menuItems, List<MenuItem> dailySpecials) {
        this.customer = customer;
        this.orderManaged = orderManaged;
        this.menuItems = menuItems;
        this.dailySpecials = dailySpecials;

        if (AppState.loggedInStaff != null) {
            loggedInAsLabel.setText("😊 " + AppState.loggedInCustomer.getFullName());
        }

        updateCustomerInfo(); // show welcome details
    }

    private void updateCustomerInfo() {
        if (customer != null) {
            customerId.setText("Customer ID: " + customer.getId());
            nameLabel.setText("Name: " + customer.getFullName());
            addressLabel.setText("Address: " + customer.getAddress());
        }
    }

    public void refreshMenuData() {
        // If you ever want to reload from file again
        menuItems = cafe94.system.data.DataLoader.loadMenu("src/main/resources/data/menu.txt");
        dailySpecials = cafe94.system.data.DataLoader.loadDailySpecials("src/main/resources/data/daily_special.txt", menuItems);
    }

    @FXML
    private void handlePlaceOrder() {
        refreshMenuData();
        SceneManager.switchToWithControllerAndSetup("order/OrderingView.fxml",
                (OrderingController c) -> c.setup(AppState.loggedInCustomer)
        );
    }

    @FXML
    private void handleOrderHistory() {
        SceneManager.switchToWithControllerAndSetup("order/OrderHistory.fxml",
                (OrderHistoryController c) -> c.setup(customer, orderManaged)
        );
    }

    @FXML
    private void handleBookTable() {
        SceneManager.switchToWithControllerAndSetup("booking/CustomerBooking.fxml",
                (CustomerBookingController c) -> c.setup(AppState.loggedInCustomer)
        );
    }

    @FXML
    private void handleLogout() {
        if (orderManaged != null) {
            DataSaver.saveOrders("src/main/resources/data/orders.txt", orderManaged.getAllOrders());
        }
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }
}
