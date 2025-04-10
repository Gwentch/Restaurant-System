/**
 * Utility class for loading data from text files into memory for use in the Cafe94 system.
 */
package cafe94.system.data;

import cafe94.system.model.booking.Booking;
import cafe94.system.model.menu.MenuItem;
import cafe94.system.model.order.*;
import cafe94.system.model.user.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataLoader {

    /**
     * Loads menu items from a given text file.
     *
     * @param filePath the path to the menu file
     * @return a list of MenuItem objects
     */
    public static List<MenuItem> loadMenu(String filePath) {
        List<MenuItem> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    double price = Double.parseDouble(parts[1].trim());
                    items.add(new MenuItem(name, price));
                }
            }
            System.out.println("Menu loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading menu: " + e.getMessage());
        }
        return items;
    }

    /**
     * Loads daily special items from a given text file.
     *
     * @param filePath  the path to the daily special menu file.
     * @return a list of MenuItem objects
     */
    public static List<MenuItem> loadDailySpecials(String filePath, List<MenuItem> menuItems) {
        List<MenuItem> specials = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(";")) {
                    line = line.substring(0, line.indexOf(";"));
                }
                for (MenuItem item : menuItems) {
                    if (item.getName().equalsIgnoreCase(line.trim())) {
                        specials.add(item);
                        break; // good practice: stop once found
                    }
                }
            }
            System.out.println("Daily specials loaded: " + specials.size());
        } catch (IOException e) {
            System.out.println("Error loading daily specials: " + e.getMessage());
        }
        return specials;
    }


    /**
     * Loads all orders from a given file.
     *
     * @param path the file path
     * @return a list of Order objects
     */
    public static List<Order> loadOrders(String path, List<MenuItem> menuItems) {
        List<Order> orders = new ArrayList<>();
        int maxOrderID = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Order parsed = parseOrderWithMenu(line, menuItems);
                    if (parsed != null) {
                        orders.add(parsed);
                        maxOrderID = Math.max(maxOrderID, parsed.getOrderID());
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse order: " + line);
                }
            }
            Order.setNextOrderID(maxOrderID + 1);
        } catch (IOException e) {
            System.err.println("Error loading orders: " + e.getMessage());
        }

        return orders;
    }


    /**
     * Loads a list of customers from a file.
     *
     * @param path path to the customer file
     * @return list of customers
     */
    public static List<Customer> loadCustomers(String path) {
        List<Customer> customers = new ArrayList<>();
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String firstName = parts[1].trim();
                    String lastName = parts[2].trim();
                    String address = parts[3].trim();
                    String password = parts[4].trim();
                    customers.add(new Customer(id, firstName, lastName, address, password));

                    if (id > maxId) {
                        maxId = id;
                    }
                }
            }
            Customer.setCustomerIdCounter(maxId + 1);
        } catch (IOException e) {
            System.out.println("Error loading customerList: " + e.getMessage());
        }
        return customers;
    }


    /**
     * Loads a list of staff members from a file.
     *
     * @param filePath to the staff file
     * @return list of staff members (Manager, Chef, Waiter, Driver)
     */
    public static List<Staff> loadStaff(String filePath) {
        List<Staff> staffList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String firstName = parts[1].trim();
                    String lastName = parts[2].trim();
                    String password = parts[3].trim();
                    String role = parts[4].trim().toLowerCase();
                    List<Double> hoursToWork = parts.length > 5 ? Staff.parseHoursList(parts[5].trim()) : Collections.emptyList();
                    List<Double> totalHoursWorked = parts.length > 6 ? Staff.parseHoursList(parts[6].trim()) : Collections.emptyList();

                    Staff staff;
                    switch (role) {
                        case "chef":
                            staff = new Chef(id, firstName, lastName, password, hoursToWork, totalHoursWorked);
                            break;
                        case "manager":
                            staff = new Manager(id, firstName, lastName, password, hoursToWork, totalHoursWorked);
                            break;
                        case "waiter":
                            staff = new Waiter(id, firstName, lastName, password, hoursToWork, totalHoursWorked);
                            break;
                        case "driver":
                            staff = new Driver(id, firstName, lastName, password, hoursToWork, totalHoursWorked);
                            break;
                        default:
                            System.out.println("Unknown staff role: " + role);
                            continue;
                    }
                    staffList.add(staff);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading staff: " + e.getMessage());
        }
        return staffList;
    }

    /**
     * Loads bookings from the given file path.
     *
     * @param path the file path of the bookings file
     * @return list of bookings parsed from file
     */
    public static List<Booking> loadBookings(String path) {
        List<Booking> bookings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Booking booking = Booking.parseFromFile(line);
                if (booking != null) {
                    bookings.add(booking);
                }
            }
            System.out.println("Bookings loaded: " + bookings.size());
        } catch (IOException e) {
            System.out.println("No bookings file found. Refresh.");
        }
        return bookings;
    }

    // Helper method
    private static Order parseOrderWithMenu(String line, List<MenuItem> menuItems) {
        try {
            String[] parts = line.split(";", -1);
            if (parts.length < 10) return null;

            int orderID = Integer.parseInt(parts[0]);
            int customerID = Integer.parseInt(parts[1]);
            OrderType type = OrderType.valueOf(parts[2].toUpperCase());
            boolean completed = Boolean.parseBoolean(parts[3]);
            String pickupTime = parts[4].trim();
            String deliveryAddress = parts[5].trim();
            String estimatedDeliveryTime = parts[6].trim();
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

                        MenuItem matched = findMenuItemByName(itemName, menuItems);
                        if (matched != null) {
                            items.add(new OrderItem(matched, quantity));
                        } else {
                            System.out.println("Menu item not found in menu list: " + itemName + " (added with price £0.00)");
                        }
                    }
                }
            }

            return switch (type) {
                case EAT_IN -> new EatInOrder(orderID, customerID, completed, items, status);
                case TAKEAWAY -> new TakeawayOrder(orderID, customerID, completed, items, status, pickupTime);
                case DELIVERY -> new DeliveryOrder(orderID, customerID, completed, items, status, deliveryAddress, estimatedDeliveryTime, assignedDriverID);
            };

        } catch (Exception e) {
            System.err.println("Error parsing order: " + line);
            e.printStackTrace();
            return null;
        }
    }

    private static MenuItem findMenuItemByName(String name, List<MenuItem> menuItems) {
        for (MenuItem item : menuItems) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

}
