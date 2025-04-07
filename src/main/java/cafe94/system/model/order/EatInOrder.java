package cafe94.system.model.order;

import java.util.List;

/**
 * Represents an Eat-In order made by a waiter on behalf of a customer dining in.
 * This order does not require pickup time or delivery address.
 */
public class EatInOrder extends Order {

    public EatInOrder(int customerId) {
        super(customerId);
    }

    public EatInOrder(int orderId, int customerId, boolean completed, List<OrderItem> items, OrderStatus status) {
        super(orderId, customerId);
        this.setCompleted(completed);
        this.getItems().addAll(items);
        this.setStatus(status);
    }

    @Override
    public OrderType getOrderType() {
        return OrderType.EAT_IN;
    }

    @Override
    public String toFileString() {
        return String.format("%d;%d;%s;%s;;;;-1;%s;%s",
                getOrderID(),
                getCustomerID(),
                getOrderType(),
                isCompleted(),
                itemsToString(),
                getStatus());
    }
}
