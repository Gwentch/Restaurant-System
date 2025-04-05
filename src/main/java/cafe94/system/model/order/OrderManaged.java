package cafe94.system.model.order;

import cafe94.system.model.menu.MenuItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all orders in the Cafe94 system.
 */
public class OrderManaged {

    private final List<Order> orders;

    public OrderManaged() {
        this.orders = new ArrayList<>();
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public List<Order> getAllOrders() {
        return orders;
    }

    public List<Order> getOutstandingOrders() {
        List<Order> outstanding = new ArrayList<>();
        for (Order order : orders) {
            if (!order.isCompleted()) {
                outstanding.add(order);
            }
        }
        return outstanding;
    }

    /**
     * Saves all orders to the given file.
     */
    public void saveOrdersToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Order order : orders) {
                writer.write(order.toFileString());
                writer.newLine();
            }
            System.out.println("Orders saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }

    /**
     * Loads orders from file and parses them into Order objects.
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
     * Parses a single order line from file.
     */
    public static Order parseOrder(String line, List<MenuItem> menuItems) {
        try {
            String[] parts = line.split(";", -1);
            if (parts.length < 10) return null;

            int orderID = Integer.parseInt(parts[0]);
            int customerID = Integer.parseInt(parts[1]);
            OrderType type = OrderType.valueOf(parts[2]);
            boolean completed = Boolean.parseBoolean(parts[3]);
            String pickUpTime = parts[4];
            String deliveryAddress = parts[5];
            String estimatedDeliveryTime = parts[6];
            int assignedDriverID = parts[7].isEmpty() ? -1 : Integer.parseInt(parts[7].trim());

            Order order = new Order(orderID, customerID, type);
            order.setCompleted(completed);
            order.setPickUpTime(pickUpTime);
            order.setDeliveryAddress(deliveryAddress);
            order.setEstimatedDeliveryTime(estimatedDeliveryTime);
            order.setAssignedStaffDriverID(assignedDriverID);

            // Items
            if (!parts[8].isEmpty()) {
                String[] itemParts = parts[8].split(",");
                for (String itemData : itemParts) {
                    String[] itemSplit = itemData.split("#");
                    if (itemSplit.length == 2) {
                        String itemName = itemSplit[0].trim();
                        int quantity = Integer.parseInt(itemSplit[1].trim());

                        MenuItem matchedItem = findMenuItemByName(itemName, menuItems);
                        if (matchedItem != null) {
                            order.addItem(matchedItem, quantity);
                        } else {
                            System.out.println("Menu item not found: " + itemName);
                        }
                    }
                }
            }

            // Status
            order.setStatus(OrderStatus.valueOf(parts[9].trim()));

            return order;

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

    public List<Order> findOrdersByCustomer(int customerID) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (order.getCustomerID() == customerID) {
                result.add(order);
            }
        }
        return result;
    }
}
