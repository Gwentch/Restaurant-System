package cafe94.system.model.order;

import java.util.List;

/**
 * Represents a Delivery order in the Cafe94 system.
 * <p>
 * This order type includes additional fields specific to delivery:
 * the delivery address, estimated delivery time, and assigned driver ID.
 * </p>
 * <p>
 * Inherits base fields and behaviors from the {@link Order} superclass.
 * </p>
 */
public class DeliveryOrder extends Order {

    private String deliveryAddress;
    private String estimatedDeliveryTime;
    private int assignedStaffDriverID = -1;

    /**
     * Constructs a new delivery order for a customer with a unique order ID.
     *
     * @param customerID the ID of the customer placing the delivery order
     */
    public DeliveryOrder(int customerID) {
        super(customerID);
    }

    /**
     * Constructs a delivery order with all relevant fields, typically used when loading from file.
     *
     * @param orderID                the order ID
     * @param customerID            the customer ID
     * @param completed             whether the order is completed
     * @param items                 list of ordered items
     * @param status                current status of the order
     * @param deliveryAddress       delivery address for the order
     * @param estimatedDeliveryTime estimated delivery time (e.g., "18:00")
     * @param assignedDriverID      ID of the assigned driver (-1 if unassigned)
     */
    public DeliveryOrder(int orderID, int customerID, boolean completed, List<OrderItem> items, OrderStatus status,
                         String deliveryAddress, String estimatedDeliveryTime, int assignedDriverID) {
        super(orderID, customerID);
        this.setCompleted(completed);
        this.getItems().addAll(items);
        this.setStatus(status);
        this.deliveryAddress = deliveryAddress;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.assignedStaffDriverID = assignedDriverID;
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

    /**
     * Returns the order type of this order.
     *
     * @return {@code OrderType.DELIVERY}
     */
    @Override
    public OrderType getOrderType() {
        return OrderType.DELIVERY;
    }

    /**
     * Serializes this order to a string suitable for saving to file.
     *
     * @return semicolon-delimited string representing the delivery order
     */
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
