package cafe94.system.data;

import cafe94.system.model.menu.MenuItem;
import cafe94.system.model.order.OrderStatus;
import cafe94.system.model.order.OrderType;
import cafe94.system.model.user.*;
import cafe94.system.model.order.Order;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    /* ===========================
       Load Customers
    ============================ */
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

    /* ===========================
       Load Staff (with subclass refactoring)
    ============================ */
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
                    String role = parts[3].trim().toLowerCase();
                    String password = parts[4].trim();
                    double hoursToWork = parts.length > 5 ? Double.parseDouble(parts[5].trim()) : 0.0;
                    double totalHoursWorked = parts.length > 6 ? Double.parseDouble(parts[6].trim()) : 0.0;

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
                            System.out.println("Unknown staff role: " + role + ", skipping...");
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

    /* ===========================
       Load Menu Items
    ============================ */
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

    /* ===========================
       Load Daily Specials
    ============================ */
    public static List<MenuItem> loadDailySpecials(String filePath, List<MenuItem> menuItems) {
        List<MenuItem> specials = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (MenuItem item : menuItems) {
                    if (item.getName().equalsIgnoreCase(line.trim())) {
                        specials.add(item);
                    }
                }
            }
            System.out.println("Daily specials loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading daily specials: " + e.getMessage());
        }
        return specials;
    }




}
