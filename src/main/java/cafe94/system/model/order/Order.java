package cafe94.system.model.order;

import cafe94.system.model.menu.MenuItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract superclass representing a general order in the Cafe94 system.
 * <p>
 * This class defines common properties and behaviors for all types of orders,
 * including Eat-In, Takeaway, and Delivery orders. Each subclass must provide
 * its specific {@link OrderType} and override the serialization method if needed.
 * </p>
 */
public abstract class Order {

    private static int nextOrderID = 1;
    private final int orderID;
    private int customerID;
    private OrderStatus status = OrderStatus.PENDING_PREP;
    private boolean completed = false;

    protected final List<OrderItem> items = new ArrayList<>();

    /**
     * Constructor for new orders with auto-generated order ID.
     *
     * @param customerID the ID of the customer placing the order
     */
    public Order(int customerID) {
        this.orderID = nextOrderID++;
        this.customerID = customerID;
    }

    /**
     * Constructor for loading orders from file (with specified ID).
     *
     * @param orderID    the unique order ID from storage
     * @param customerID the ID of the customer
     */
    public Order(int orderID, int customerID) {
        this.orderID = orderID;
        this.customerID = customerID;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * Adds an item to this order.
     *
     * @param item     the menu item
     * @param quantity the quantity ordered
     */
    public void addItem(MenuItem item, int quantity) {
        items.add(new OrderItem(item, quantity));
    }

    /**
     * Calculates the total cost of all items in the order.
     *
     * @return the total amount
     */
    public double getTotalAmount() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    /**
     * Gets the next order ID value to assign.
     *
     * @return the next available order ID
     */
    public static int getNextOrderID() {
        return nextOrderID;
    }

    /**
     * Sets the next order ID value.
     *
     * @param nextID the new order ID starting point
     */
    public static void setNextOrderID(int nextID) {
        nextOrderID = nextID;
    }

    /**
     * Returns the type of this order (must be overridden by subclasses).
     *
     * @return the {@link OrderType} of this order
     */
    public abstract OrderType getOrderType();

    /**
     * Serializes the order into a semicolon-delimited string for file storage.
     * Includes type-specific fields such as pickup time or delivery details.
     *
     * @return a string representation of the order
     */
    public String toFileString() {
        String pickupTime = "";
        String deliveryAddress = "";
        String estimatedDeliveryTime = "";
        int assignedDriverID = -1;

        if (this instanceof TakeawayOrder takeaway) {
            pickupTime = takeaway.getPickupTime();
        } else if (this instanceof DeliveryOrder delivery) {
            deliveryAddress = delivery.getDeliveryAddress();
            estimatedDeliveryTime = delivery.getEstimatedDeliveryTime();
            assignedDriverID = delivery.getAssignedStaffDriverID();
        }

        // Escape semicolons in item names if any (optional defensive)
        String items = itemsToString().replace(";", ",");

        return String.join(";",
                String.valueOf(getOrderID()),                    // 0
                String.valueOf(getCustomerID()),                 // 1
                getOrderType().name(),                           // 2
                String.valueOf(isCompleted()),                   // 3
                pickupTime == null ? "" : pickupTime,            // 4
                deliveryAddress == null ? "" : deliveryAddress,  // 5
                estimatedDeliveryTime == null ? "" : estimatedDeliveryTime, // 6
                String.valueOf(assignedDriverID),                // 7
                items,                                           // 8
                getStatus().name()                               // 9
        );
    }

    /**
     * Converts the list of items to a file-friendly string format.
     * Each item is represented as {@code name#quantity}.
     *
     * @return a comma-separated string of items
     */
    protected String itemsToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            sb.append(item.getMenuItem().getName()).append("#").append(item.getQuantity());
            if (i < items.size() - 1) sb.append(",");
        }
        return sb.toString();
    }
}
