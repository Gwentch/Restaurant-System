/**
 * Utility class for saving application data to files.
 * Supports saving staff, customers, menu, specials, and orders.
 */

package cafe94.system.data;

import cafe94.system.model.booking.Booking;
import cafe94.system.model.menu.MenuItem;
import cafe94.system.model.order.Order;
import cafe94.system.model.user.Customer;
import cafe94.system.model.user.Staff;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataSaver {

    /**
     * Saves a list of staff to the specified file path.
     *
     * @param path       file path to save the staff list
     * @param staffList  list of staff to save
     */
    public static void saveStaff(String path, List<Staff> staffList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Staff staff : staffList) {
                writer.write(staff.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving staff: " + e.getMessage());
        }
    }

    /**
     * Saves a list of customers to the specified file path.
     *
     * @param path           file path to save the customer list
     * @param customerList   list of customers to save
     */
    public static void saveCustomers(String path, List<Customer> customerList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Customer customer : customerList) {
                writer.write(customer.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    /**
     * Saves menu items to a file.
     *
     * @param path        file path to save menu items
     * @param menuItems   list of menu items
     */
    public static void saveMenu(String path, List<MenuItem> menuItems) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (MenuItem item : menuItems) {
                writer.write(item.getName() + ";" + item.getPrice());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving menu: " + e.getMessage());
        }
    }

    /**
     * Saves daily specials to a file.
     *
     * @param path            file path to save specials
     * @param dailySpecials   list of daily specials
     */
    public static void saveDailySpecials(String path, List<MenuItem> dailySpecials) {
        saveMenu(path, dailySpecials); // Reuse saveMenu since same format
    }

    /**
     * Saves all orders to a file.
     *
     * @param path     file path to save orders
     * @param orders   list of orders
     */
    public static void saveOrders(String path, List<Order> orders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Order order : orders) {
                writer.write(order.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving orders: " + e.getMessage());
        }
    }

    /**
     * Saves a list of bookings to a file.
     *
     * @param path         the file path to save)
     * @param bookingList  the list of bookings to save
     */
    public static void saveBookings(String path, List<Booking> bookingList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (Booking booking : bookingList) {
                writer.write(booking.toFileString());
                writer.newLine();
            }
            System.out.println("Bookings saved to file: " + path);
        } catch (IOException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }

}
