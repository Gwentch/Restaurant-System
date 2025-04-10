package cafe94.system.model.order;

import cafe94.system.model.menu.MenuItem;
import cafe94.system.model.order.OrderItem;
import cafe94.system.model.order.OrderType;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract superclass representing a general order in Cafe94.
 * Specific types of orders such as EatIn, Takeaway, and Delivery should extend this class.
 */
public abstract class Order {

    private static int nextOrderID = 1;
    private final int orderID;
    private int customerID;
    private OrderStatus status = OrderStatus.PENDING_PREP;
    private boolean completed = false;

    protected final List<OrderItem> items = new ArrayList<>();

    // Constructor for new orders
    public Order(int customerID) {
        this.orderID = nextOrderID++;
        this.customerID = customerID;
    }

    // Constructor for loading from file (custom ID)
    public Order(int orderID, int customerID) {
        this.orderID = orderID;
        this.customerID = customerID;
    }

    // === Accessors ===
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

    public void addItem(MenuItem item, int quantity) {
        items.add(new OrderItem(item, quantity));
    }

    public double getTotalAmount() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    //  Static Method to Parse from File
    public static Order parseFromFile(String line, List<MenuItem> menuItems) {
        try {
            String[] parts = line.split(";", -1);
            if (parts.length < 10) return null;

            int orderID = Integer.parseInt(parts[0]);
            int customerID = Integer.parseInt(parts[1]);
            OrderType type = OrderType.valueOf(parts[2].toUpperCase());
            boolean completed = Boolean.parseBoolean(parts[3]);

            String pickupTime = parts[4];
            String deliveryAddress = parts[5];
            String estimatedDeliveryTime = parts[6];
            int assignedDriverID = parts[7].isEmpty() ? -1 : Integer.parseInt(parts[7]);

            String itemData = parts[8];
            OrderStatus status = OrderStatus.valueOf(parts[9].trim());

            List<OrderItem> items = new ArrayList<>();
            if (!itemData.isEmpty()) {
                String[] itemParts = itemData.split(",");
                for (String itemStr : itemParts) {
                    String[] itemDetail = itemStr.split("#");
                    if (itemDetail.length == 2) {
                        String itemName = itemDetail[0].trim();
                        int quantity = Integer.parseInt(itemDetail[1].trim());

                        MenuItem matchedItem = menuItems.stream()
                                .filter(m -> m.getName().equalsIgnoreCase(itemName))
                                .findFirst()
                                .orElse(null);

                        if (matchedItem != null) {
                            items.add(new OrderItem(matchedItem, quantity));
                        } else {
                            System.out.println("⚠ Menu item not found while loading: " + itemName);
                        }
                    }
                }
            }

            // Create the appropriate subclass of Order
            return switch (type) {
                case EAT_IN -> new EatInOrder(orderID, customerID, completed, items, status);
                case TAKEAWAY -> new TakeawayOrder(orderID, customerID, completed, items, status, pickupTime);
                case DELIVERY -> new DeliveryOrder(orderID, customerID, completed, items, status, deliveryAddress, estimatedDeliveryTime, assignedDriverID);
            };

        } catch (Exception e) {
            System.err.println("Failed to parse order: " + line);
            e.printStackTrace();
            return null;
        }
    }

    // Abstract methods to implement
    public abstract OrderType getOrderType();


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


    protected String itemsToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            sb.append(item.getMenuItem().getName()).append("#").append(item.getQuantity());
            if (i < items.size() - 1) sb.append(",");
        }
        return sb.toString();
    }

    // Handle OrderID increment tracking
    public static int getNextOrderID() {
        return nextOrderID;
    }

    public static void setNextOrderID(int nextID) {
        nextOrderID = nextID;
    }
}
