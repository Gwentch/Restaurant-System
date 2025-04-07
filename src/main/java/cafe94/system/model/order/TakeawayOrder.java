package cafe94.system.model.order;

import java.util.List;

/**
 * Represents a Takeaway order that includes a pickup time.
 */
public class TakeawayOrder extends Order {

    private String pickupTime;

    public TakeawayOrder(int customerID) {
        super(customerID);
    }

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

    @Override
    public String toFileString() {
        return String.format("%d;%d;%s;%s;%s;;;;-1;%s;%s",
                getOrderID(),
                getCustomerID(),
                getOrderType(),
                isCompleted(),
                pickupTime == null ? "" : pickupTime,
                itemsToString(),
                getStatus());
    }
}
