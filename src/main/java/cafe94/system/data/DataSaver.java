package cafe94.system.data;

import cafe94.system.model.menu.MenuItem;
import cafe94.system.model.order.Order;
import cafe94.system.model.order.OrderItem;
import cafe94.system.model.user.Customer;
import cafe94.system.model.user.Staff;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataSaver {

    /* =========================
       Save Customers
    ========================== */
    public static void saveCustomers(String path, List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Customer c : customers) {
                writer.write(c.getId() + ";" + c.getFirstName() + ";" + c.getLastName() + ";" +
                        c.getAddress() + ";" + c.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving customerList: " + e.getMessage());
        }
    }

    /* =========================
       Save Staff
    ========================== */
    public static void saveStaff(String filePath, List<Staff> staffList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Staff staff : staffList) {
                writer.write(staff.getId() + ";" +
                        staff.getFirstName() + ";" +
                        staff.getLastName() + ";" +
                        staff.getRole() + ";" +
                        staff.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving staff: " + e.getMessage());
        }
    }



    // Save Orders
    public static void saveOrders(String path, List<Order> orders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Order o : orders) {
                writer.write(o.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }


    public static void saveDailySpecials(String filePath, List<MenuItem> dailySpecials) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (MenuItem item : dailySpecials) {
                writer.write(item.getName());
                writer.newLine();
            }
            System.out.println("Daily specials saved.");
        } catch (IOException e) {
            System.out.println("Error saving daily specials: " + e.getMessage());
        }
    }
}

