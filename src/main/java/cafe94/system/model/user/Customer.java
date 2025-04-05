package cafe94.system.model.user;

import cafe94.system.model.order.Order;
import java.util.ArrayList;
import java.util.List;


public class Customer extends User {
    private static int customerIdCounter = 1;   // Auto-increment customer ID
    private final String address;
    private final List<Order> orderHistory = new ArrayList<>();

    // Constructor for loading existing customerList from file
    public Customer(int id, String firstName, String lastName, String address, String password) {
        super(id, firstName, lastName, password);
        this.address = address;

        // Update counter to avoid duplicate IDs when registering new customerList
        if (id >= customerIdCounter) {
            customerIdCounter = id + 1;
        }
    }

    // Constructor for new customer registration
    public Customer(String firstName, String lastName, String address, String password) {
        super(customerIdCounter++, firstName, lastName, password);
        this.address = address;
    }

    // Set the correct next ID after existing customerList
    public static void setCustomerIdCounter(int nextId) {
        customerIdCounter = nextId;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public void addOrder(Order order) {
        orderHistory.add(order);
    }

    @Override
    public String getRole() {
        return "Customer";
    }
}

