package cafe94.system.model.order;

import java.util.List;

/**
 * Represents a Delivery order including address, delivery time, and assigned driver.
 */
public class DeliveryOrder extends Order {

    private String deliveryAddress;
    private String estimatedDeliveryTime;
    private int assignedStaffDriverID = -1;

    public DeliveryOrder(int customerID) {
        super(customerID);
    }

    public DeliveryOrder(int orderID, int customerID, boolean completed, List<OrderItem> items, OrderStatus status,
                         String deliveryAddress, String estimatedDeliveryTime, int assignedDriverID) {
        super(orderID, customerID);
        this.setCompleted(completed);
        this.getItems().addAll(items);
        this.setStatus(status);
        this.deliveryAddress = deliveryAddress;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.assignedStaffDriverID = assignedStaffDriverID;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public int getAssignedStaffDriverID() {
        return assignedStaffDriverID;
    }

    public void setAssignedStaffDriverID(int assignedStaffDriverID) {
        this.assignedStaffDriverID = assignedStaffDriverID;
    }

    @Override
    public OrderType getOrderType() {
        return OrderType.DELIVERY;
    }

    @Override
    public String toFileString() {
        return String.format("%d;%d;%s;%s;;%s;%s;%d;%s;%s",
                getOrderID(),
                getCustomerID(),
                getOrderType().name(),
                isCompleted(),
                deliveryAddress == null ? "" : deliveryAddress,
                estimatedDeliveryTime == null ? "" : estimatedDeliveryTime,
                assignedStaffDriverID,
                itemsToString(),
                getStatus().name());
    }
}
