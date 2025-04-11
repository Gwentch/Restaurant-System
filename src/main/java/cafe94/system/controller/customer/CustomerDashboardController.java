package cafe94.system.controller.customer;

import cafe94.system.data.DataSaver;
import cafe94.system.model.menu.MenuItem;
import cafe94.system.model.order.OrderManaged;
import cafe94.system.model.user.Customer;
import cafe94.system.utils.AppState;
import cafe94.system.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
/**
 * Controller class for the Customer Dashboard in the Cafe94 system.
 * <p>
 * This controller handles the display of customer information, navigation to
 * other customer-related views such as placing an order, viewing order history,
 * and booking a table. It also manages loading and refreshing of menu data and
 * saving order data upon logout.
 * </p>
 *
 * Responsibilities:
 * <ul>
 *   <li>Display logged-in customer details</li>
 *   <li>Allow customers to place orders and view daily specials</li>
 *   <li>Navigate to order history and table booking scenes</li>
 *   <li>Handle logout with order data persistence</li>
 * </ul>
 *
 * This controller uses {@link AppState}, {@link SceneManager}, and interacts
 * with various FXML views related to customer functionality.
 */
public class CustomerDashboardController {

    @FXML private Label loggedInAsLabel;
    @FXML private Label customerId;
    @FXML private Label nameLabel;
    @FXML private Label addressLabel;

    private static final String ORDERS_FILE = "src/main/resources/data/orders.txt";
    private static final String DAILY_SPECIALS_FILE = "src/main/resources/data/daily_special.txt";
    private static final String MENU_FILE = "src/main/resources/data/menu.txt";

    private Customer customer;
    private OrderManaged orderManaged;
    private List<MenuItem> menuItems;
    private List<MenuItem> dailySpecials;

    /**
     * Initializes the customer dashboard with the logged-in customer,
     * order manager, and menu data.
     *
     * @param customer The currently logged-in customer.
     * @param orderManaged The order manager instance to handle customer orders.
     * @param menuItems The list of available menu items.
     * @param dailySpecials The list of daily special items.
     */
    public void setup(Customer customer, OrderManaged orderManaged, List<MenuItem> menuItems, List<MenuItem> dailySpecials) {
        this.customer = customer;
        this.orderManaged = orderManaged;
        this.menuItems = menuItems;
        this.dailySpecials = dailySpecials;

        showLoggedInLabel();
        updateCustomerInfo(); // show welcome details
    }

    /**
     * Displays the name of the currently logged-in customer on the dashboard.
     */
    private void showLoggedInLabel() {
        if (AppState.loggedInCustomer != null) {
            loggedInAsLabel.setText("😊 " + AppState.loggedInCustomer.getFullName());
        }
    }

    /**
     * Updates the customer detail labels (ID, name, and address) with current information.
     */
    private void updateCustomerInfo() {
        if (customer != null) {
            customerId.setText("Customer ID: " + customer.getId());
            nameLabel.setText("Name: " + customer.getFullName());
            addressLabel.setText("Address: " + customer.getAddress());
        }
    }
    /**
     * Refreshes the menu and daily specials data from their respective files.
     * This method can be used to reload menu items dynamically if updates occur.
     */
    public void refreshMenuData() {
        // If you ever want to reload from file again
        menuItems = cafe94.system.data.DataLoader.loadMenu(MENU_FILE);
        dailySpecials = cafe94.system.data.DataLoader.loadDailySpecials(DAILY_SPECIALS_FILE, menuItems);
    }

    /**
     * Handles navigation to the ordering view.
     * Refreshes menu data before switching to ensure latest items are shown.
     */
    @FXML
    private void handlePlaceOrder() {
        refreshMenuData(); // updates menuItems and dailySpecials
        SceneManager.switchToWithControllerAndSetup("order/OrderingView.fxml",
                (OrderingController c) -> c.setup(AppState.loggedInCustomer, AppState.orderManaged, menuItems, dailySpecials)
        );
    }

    /**
     * Handles navigation to the order history view,
     * where the customer can view their past orders.
     */
    @FXML
    private void handleOrderHistory() {
        SceneManager.switchToWithControllerAndSetup("order/OrderHistory.fxml",
                (OrderHistoryController c) -> c.setup(customer, orderManaged)
        );
    }

    /**
     * Handles navigation to the booking view,
     * allowing the customer to request a table booking.
     */
    @FXML
    private void handleBookTable() {
        SceneManager.switchToWithControllerAndSetup("booking/CustomerBooking.fxml",
                (CustomerBookingController c) -> c.setup(AppState.loggedInCustomer)
        );
    }

    /**
     * Logs out the current customer and switches back to the welcome login screen.
     * Saves any outstanding orders before logout.
     */
    @FXML
    private void handleLogout() {
        if (orderManaged != null) {
            DataSaver.saveOrders(ORDERS_FILE, orderManaged.getAllOrders());
        }
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }
}
