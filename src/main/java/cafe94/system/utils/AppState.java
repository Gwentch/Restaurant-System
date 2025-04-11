package cafe94.system.utils;

import cafe94.system.data.DataLoader;
import cafe94.system.model.booking.Booking;
import cafe94.system.model.menu.MenuItem;
import cafe94.system.model.order.OrderManaged;
import cafe94.system.model.user.Customer;
import cafe94.system.model.user.Staff;

import java.util.List;

/**
 * AppState holds shared application state that is accessible across
 * all controllers in the Cafe94 system.
 *
 * This includes:
 * <ul>
 *   <li>Currently logged-in customer or staff</li>
 *   <li>All loaded data: customers, staff, menu, specials, orders, bookings</li>
 *   <li>Shared order manager for consistent order tracking</li>
 * </ul>
 */
public class AppState {
    public static Staff loggedInStaff;
    public static List<Staff> allStaff;
    public static Customer loggedInCustomer;
    public static List<Customer> allCustomer;
    public static List<MenuItem> menuItems;
    public static List<MenuItem> dailySpecials;
    public static OrderManaged orderManaged;
    public static List<Booking> bookingList;

    /**
     * Loads and initializes all application-wide shared data.
     * This method should be called once during application startup.
     */
    public static void initialize() {
        allCustomer = DataLoader.loadCustomers("src/main/resources/data/customer.txt");
        allStaff = DataLoader.loadStaff("src/main/resources/data/staff_profile.txt");

        menuItems = DataLoader.loadMenu("src/main/resources/data/menu.txt");
        dailySpecials = DataLoader.loadDailySpecials("src/main/resources/data/daily_special.txt", menuItems);

        orderManaged = new OrderManaged();
        orderManaged.loadOrdersFromFile("src/main/resources/data/orders.txt", menuItems);


        bookingList = DataLoader.loadBookings("src/main/resources/data/booking.txt");

    }
}

