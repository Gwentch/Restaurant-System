package cafe94.system.utils;

import cafe94.system.model.order.*;
import cafe94.system.model.user.Customer;
import cafe94.system.model.user.Staff;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides reusable methods for generating various reports in the Cafe94 system.
 * All methods operate in a static utility fashion and require access to order or staff/customer data.
 */
public class ReportHelper {

    /**
     * Generates a chart and text summary of the most popular menu items across all orders.
     */
    public static void generateMostPopularItems(OrderManaged orderManaged, BarChart<String, Number> chart, TextArea summary, TabPane tabPane) {
        Map<String, Integer> itemCount = new HashMap<>();

        for (Order order : orderManaged.getAllOrders()) {
            for (OrderItem item : order.getItems()) {
                String itemName = item.getMenuItem().getName();
                int quantity = item.getQuantity();
                itemCount.put(itemName, itemCount.getOrDefault(itemName, 0) + quantity);
            }
        }

        List<Map.Entry<String, Integer>> topItems = itemCount.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(5)
                .toList();

        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top 5 Items");

        StringBuilder text = new StringBuilder("Most Popular Items:\n\n");
        for (Map.Entry<String, Integer> entry : topItems) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            text.append(entry.getKey()).append(" - ").append(entry.getValue()).append(" orders\n");
        }

        chart.getData().add(series);
        summary.setText(topItems.isEmpty() ? "No items have been ordered yet." : text.toString());
        tabPane.getSelectionModel().selectFirst();
    }

    /**
     * Generates a report of the most active customers by total number of orders.
     */
    public static void generateMostActiveCustomers(OrderManaged orderManaged, List<Customer> customerList,
                                                   BarChart<String, Number> chart, TextArea summary, TabPane tabPane) {
        Map<String, Integer> customerOrderCount = new HashMap<>();

        for (Customer customer : customerList) {
            int count = (int) orderManaged.getAllOrders().stream()
                    .filter(order -> order.getCustomerID() == customer.getId())
                    .count();

            if (count > 0) {
                String label = customer.getFullName() + " (ID: " + customer.getId() + ")";
                customerOrderCount.put(label, count);
            }
        }

        if (customerOrderCount.isEmpty()) {
            chart.getData().clear();
            summary.setText("No active customers yet.");
            return;
        }

        // Sort and limit to top 5
        List<Map.Entry<String, Integer>> topCustomers = customerOrderCount.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(5)
                .toList();

        // Chart
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top Customers");

        for (Map.Entry<String, Integer> entry : topCustomers) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chart.getData().add(series);

        // Summary
        StringBuilder sb = new StringBuilder("Most Active Customers:\n\n");
        for (Map.Entry<String, Integer> entry : topCustomers) {
            sb.append(entry.getKey()).append(" - ")
                    .append(entry.getValue()).append(" orders\n");
        }

        summary.setText(sb.toString());

        // Switch to chart tab
        tabPane.getSelectionModel().selectFirst();
    }

    /**
     * Generates a report of the busiest hours based on pickup or delivery times.
     */
    public static void generateBusiestPeriods(OrderManaged orderManaged,
                                              BarChart<String, Number> chart,
                                              TextArea summary,
                                              TabPane tabPane) {
        Map<String, Integer> hourCount = extractHourlyOrderCount(orderManaged);

        if (hourCount.isEmpty()) {
            chart.getData().clear();
            summary.setText("No time data available to calculate busiest periods.");
            return;
        }

        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Orders per Hour");

        for (Map.Entry<String, Integer> entry : hourCount.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey() + ":00", entry.getValue()));
        }

        chart.getData().add(series);

        StringBuilder report = new StringBuilder("📊 Busiest Periods:\n\n");
        for (Map.Entry<String, Integer> entry : hourCount.entrySet()) {
            report.append(entry.getKey()).append(":00 → ")
                    .append(entry.getValue()).append(" orders\n");
        }

        summary.setText(report.toString());
        tabPane.getSelectionModel().selectFirst();
    }


    /**
     * Generates a report of staff with the highest total hours worked.
     */
    public static void generateTopStaff(List<Staff> allStaff,
                                        BarChart<String, Number> chart, TextArea summary, TabPane tabPane) {
        List<Map.Entry<String, Double>> topStaff = allStaff.stream()
                .collect(Collectors.toMap(
                        Staff::getFullName,
                        staff -> staff.getTotalHoursWorked().stream().mapToDouble(Double::doubleValue).sum()
                ))
                .entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .toList();

        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Best Employee of Cafe94");

        StringBuilder text = new StringBuilder("🏆 Best Employee of Cafe94:\n\n");
        for (Map.Entry<String, Double> entry : topStaff) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            text.append(entry.getKey()).append(": ")
                    .append(String.format("%.2f", entry.getValue())).append(" hours worked\n");
        }

        chart.getData().add(series);
        summary.setText(topStaff.isEmpty() ? "No staff hours available." : text.toString());
        tabPane.getSelectionModel().selectFirst();
    }

    // Helper method
    private static Map<String, Integer> extractHourlyOrderCount(OrderManaged orderManaged) {
        Map<String, Integer> hourCount = new TreeMap<>();

        for (Order order : orderManaged.getAllOrders()) {
            String timeStr = null;

            if (order instanceof TakeawayOrder takeaway) {
                timeStr = takeaway.getPickupTime();
            } else if (order instanceof DeliveryOrder delivery) {
                timeStr = delivery.getEstimatedDeliveryTime();
            }

            if (timeStr == null || timeStr.isEmpty()) continue;

            String[] parts = timeStr.split(":");
            if (parts.length >= 1) {
                String hour = parts[0];
                if (hour.length() == 1) hour = "0" + hour; // pad to 2 digits
                hourCount.put(hour, hourCount.getOrDefault(hour, 0) + 1);
            }
        }

        return hourCount;
    }
}