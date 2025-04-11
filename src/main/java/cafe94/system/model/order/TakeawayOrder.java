package cafe94.system.model.order;

import java.util.List;

/**
 * Represents a Takeaway order in the Cafe94 system.
 * <p>
 * This order type includes a customer-defined pickup time and does not
 * require delivery address or driver assignment.
 * </p>
 */
public class TakeawayOrder extends Order {

    private String pickupTime;

    /**
     * Constructs a new TakeawayOrder for the given customer ID.
     *
     * @param customerID the ID of the customer placing the order
     */
    public TakeawayOrder(int customerID) {
        super(customerID);
    }

    /**
     * Constructs a TakeawayOrder loaded from file with all fields initialized.
     *
     * @param orderID       the unique order ID
     * @param customerID    the customer ID
     * @param completed     whether the order is marked as completed
     * @param items         the list of order items
     * @param status        the current order status
     * @param pickupTime    the pickup time for the order
     */
    public TakeawayOrder(int orderID, int customerID, boolean completed, List<OrderItem> items, OrderStatus status, String pickupTime) {
        super(orderID, customerID);
        this.setCompleted(completed);
        this.getItems().addAll(items);
        this.setStatus(status);
        this.pickupTime = pickupTime;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    @Override
    public OrderType getOrderType() {
        return OrderType.TAKEAWAY;
    }

    /**
     * Converts this TakeawayOrder into a text-based representation suitable for file storage.
     *
     * @return the semicolon-delimited order string
     */
    @Override
    public String toFileString() {
        return String.format("%d;%d;%s;%s;%s;;;;-1;%s;%s",
                getOrderID(),
                getCustomerID(),
                getOrderType().name(),
                isCompleted(),
                pickupTime == null ? "" : pickupTime,
                itemsToString(),
                getStatus().name());
    }
}
