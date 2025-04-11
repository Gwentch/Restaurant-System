package cafe94.system.model.user;

import cafe94.system.model.order.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the Cafe94 system.
 * Each customer has a unique ID, personal details, address, and order history.
 */
public class Customer extends User {
    private static int customerIdCounter = 1;   // Auto-increment customer ID
    private final String address;
    private final List<Order> orderHistory = new ArrayList<>();

    /**
     * Constructor for loading an existing customer from file.
     *
     * @param id        the customer's unique ID
     * @param firstName the customer's first name
     * @param lastName  the customer's last name
     * @param address   the customer's address
     * @param password  the customer's password
     */
    public Customer(int id, String firstName, String lastName, String address, String password) {
        super(id, firstName, lastName, password);
        this.address = address;

        // Update counter to avoid duplicate IDs when registering new customerList
        if (id >= customerIdCounter) {
            customerIdCounter = id + 1;
        }
    }

    /**
     * Constructor for new customer registration.
     *
     * @param firstName the customer's first name
     * @param lastName  the customer's last name
     * @param address   the customer's address
     * @param password  the customer's password
     */
    public Customer(String firstName, String lastName, String address, String password) {
        super(customerIdCounter++, firstName, lastName, password);
        this.address = address;
    }

    /**
     * Sets the customer ID counter explicitly (used when loading from file).
     *
     * @param nextId the next available customer ID
     */
    public static void setCustomerIdCounter(int nextId) {
        customerIdCounter = nextId;
    }

    public String getAddress() {
        return address;
    }


    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public String toFileString() {
        return String.format("%d;%s;%s;%s;%s", getId(), getFirstName(), getLastName(), getAddress(), getPassword());
    }

}

