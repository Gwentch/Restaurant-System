package cafe94.system.model.order;

import cafe94.system.model.menu.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer order in the Cafe94 system.
 */
public class Order {


    private static int nextOrderID = 1;

    private final int orderID;
    private int customerID;
    private OrderType type;
    private OrderStatus status = OrderStatus.PENDING_PREP;
    private final List<OrderItem> items = new ArrayList<>();
    private boolean completed = false;

    // Order based on type
    private String pickUpTime = "";              // For TAKEAWAY
    private String deliveryAddress = "";         // For DELIVERY
    private String estimatedDeliveryTime = "";   // For DELIVERY
    private int assignedStaffDriverID = -1;      // For DELIVERY (Default to unassigned)

    // === Constructor (new order) ===
    public Order(int customerID, OrderType type) {
        this.orderID = nextOrderID++;
        this.customerID = customerID;
        this.type = type;
    }

    // === Constructor (loading from file) ===
    public Order(int orderID, int customerID, OrderType type) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.type = type;
    }

    // === Getters / Setters ===
    public int getOrderID() {
        return orderID;
    }

    public static int getNextOrderID() {
        return nextOrderID;
    }

    public static void setNextOrderID(int nextID) {
        nextOrderID = nextID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
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

    public String getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
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



    public void setAssignedStaffDriverID(int assignedStaffDriverID) {
        this.assignedStaffDriverID = assignedStaffDriverID;
    }


    public int getAssignedStaffDriverID() {
        return assignedStaffDriverID;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(MenuItem item, int quantity) {
        items.add(new OrderItem(item, quantity));
    }

    public double getTotalAmount() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }


    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(orderID).append(";")
                .append(customerID).append(";")
                .append(type).append(";")
                .append(completed).append(";")
                .append(pickUpTime).append(";")
                .append(deliveryAddress).append(";")
                .append(estimatedDeliveryTime).append(";")
                .append(assignedStaffDriverID).append(";");


        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            sb.append(item.getMenuItem().getName()).append("#").append(item.getQuantity());
            if (i < items.size() - 1) sb.append(",");
        }

        sb.append(";").append(status.name());
        return sb.toString();
    }




    @Override
    public String toString() {
            return orderID + ";" + customerID + ";" + type + ";" + status.name() + ";" +
                    assignedStaffDriverID + ";";
        }

    }
