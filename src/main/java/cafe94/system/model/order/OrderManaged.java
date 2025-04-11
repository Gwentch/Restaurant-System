package cafe94.system.model.order;

import cafe94.system.model.menu.MenuItem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all orders in the Cafe94 system, including adding, removing,
 * finding, and listing orders.
 */
public class OrderManaged {

    private final List<Order> orders;

    /**
     * Constructs an empty OrderManaged instance.
     */
    public OrderManaged() {
        this.orders = new ArrayList<>();
    }

    /**
     * Adds a new order to the list.
     *
     * @param order the order to add
     */
    public void addOrder(Order order) {
        orders.add(order);
    }

    /**
     * Returns all orders.
     *
     * @return list of all orders
     */
    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(orders);
    }


    /**
     * Returns a list of outstanding (not completed) orders.
     *
     * @return list of outstanding orders
     */
    public List<Order> getOutstandingOrders() {
        return orders.stream()
                .filter(order -> !order.isCompleted())
                .collect(Collectors.toList());
    }


    /**
     * Finds orders by the given customer ID.
     *
     * @param customerId the ID of the customer
     * @return list of orders belonging to that customer
     */
    public List<Order> findOrdersByCustomer(int customerId) {
        return orders.stream()
                .filter(order -> order.getCustomerID() == customerId)
                .collect(Collectors.toList());
    }


    /**
     * Loads orders from file and parses them into Order objects, using menu items for item price lookup.
     */
    public void loadOrdersFromFile(String filePath, List<MenuItem> menuItems) {
        orders.clear();
        int maxOrderID = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Order order = parseOrder(line, menuItems);
                if (order != null) {
                    orders.add(order);
                    maxOrderID = Math.max(maxOrderID, order.getOrderID());
                }
            }
            Order.setNextOrderID(maxOrderID + 1);
            System.out.println("Orders loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
    }

    /**
     * Parses a single order line from file, using the menu to resolve prices.
     */
    public static Order parseOrder(String line, List<MenuItem> menuItems) {
        try {
            String[] parts = line.split(";", -1);
            if (parts.length != 10) {
                System.out.println("Skipped line (expected 10 fields): " + Arrays.toString(parts));
                return null;
            }

            int orderID = Integer.parseInt(parts[0]);
            int customerID = Integer.parseInt(parts[1]);
            OrderType type = OrderType.valueOf(parts[2]);
            boolean completed = Boolean.parseBoolean(parts[3]);

            String pickupTime = parts[4].trim();
            String deliveryAddress = parts[5].trim();
            String estimatedTime = parts[6].trim();
            int assignedDriverID = parts[7].isEmpty() ? -1 : Integer.parseInt(parts[7]);

            // Parse items
            List<OrderItem> itemList = new ArrayList<>();
            if (!parts[8].isEmpty()) {
                String[] itemParts = parts[8].split(",");
                for (String itemData : itemParts) {
                    String[] itemSplit = itemData.trim().split("#");
                    if (itemSplit.length == 2) {
                        String itemName = itemSplit[0].trim();
                        int quantity = Integer.parseInt(itemSplit[1].trim());
                        MenuItem matchedItem = findMenuItemByName(itemName, menuItems);
                        if (matchedItem != null) {
                            itemList.add(new OrderItem(matchedItem, quantity));
                        } else {
                            System.out.println("Menu item not found: " + itemName);
                        }
                    }
                }
            }

            OrderStatus status = OrderStatus.valueOf(parts[9].trim());

            return switch (type) {
                case EAT_IN -> new EatInOrder(orderID, customerID, completed, itemList, status);
                case TAKEAWAY -> new TakeawayOrder(orderID, customerID, completed, itemList, status, pickupTime);
                case DELIVERY -> new DeliveryOrder(orderID, customerID, completed, itemList, status, deliveryAddress, estimatedTime, assignedDriverID);
            };

        } catch (Exception e) {
            System.out.println("Error parsing line: " + line);
            e.printStackTrace();
            return null;
        }
    }

    private static MenuItem findMenuItemByName(String name, List<MenuItem> menuItems) {
        for (MenuItem item : menuItems) {
            if (item.getName().equalsIgnoreCase(name)) return item;
        }
        return null;
    }
}
