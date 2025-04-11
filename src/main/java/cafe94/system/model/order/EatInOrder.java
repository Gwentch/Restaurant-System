package cafe94.system.model.order;

import java.util.List;

/**
 * Represents an Eat-In order in the Cafe94 system.
 * <p>
 * This type of order is placed by a waiter for a customer dining in the restaurant.
 * Unlike takeaway or delivery orders, it does not require pickup time, delivery address,
 * or driver assignment.
 * </p>
 * <p>
 * Inherits from the abstract {@link Order} superclass and provides its own serialization format.
 * </p>
 */
public class EatInOrder extends Order {

    /**
     * Constructs a new Eat-In order for the specified customer.
     *
     * @param customerId the ID of the customer dining in
     */
    public EatInOrder(int customerId) {
        super(customerId);
    }

    /**
     * Constructs a fully populated Eat-In order, typically used when loading from file.
     *
     * @param orderId     the order ID
     * @param customerId  the customer ID
     * @param completed   whether the order is completed
     * @param items       list of order items
     * @param status      current status of the order
     */
    public EatInOrder(int orderId, int customerId, boolean completed, List<OrderItem> items, OrderStatus status) {
        super(orderId, customerId);
        this.setCompleted(completed);
        this.getItems().addAll(items);
        this.setStatus(status);
    }

    /**
     * Returns the order type of this order.
     *
     * @return {@code OrderType.EAT_IN}
     */
    @Override
    public OrderType getOrderType() {
        return OrderType.EAT_IN;
    }

    /**
     * Serializes this order to a string suitable for file storage.
     *
     * @return a semicolon-delimited string representing the Eat-In order
     */
    @Override
    public String toFileString() {
        return String.format("%d;%d;%s;%s;;;;-1;%s;%s",
                getOrderID(),
                getCustomerID(),
                getOrderType().name(),
                isCompleted(),
                itemsToString(),
                getStatus().name());
    }
}
