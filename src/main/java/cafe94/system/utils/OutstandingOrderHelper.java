package cafe94.system.utils;

import cafe94.system.model.order.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

/**
 * Utility class for setting up order-related table views across different staff roles.
 */
public class OutstandingOrderHelper {

    /**
     * Enum representing the type of order table required.
     */
    public enum OrderTableType {
        CHEF,
        WAITER,
        DRIVER,
        MANAGER
    }

    /**
     * Set up the order columns based on the staff role.
     */
    public static void setupOrderColumnsByRole(
            OrderTableType roleType,
            TableColumn<Order, Integer> orderIdCol,
            TableColumn<Order, Integer> customerIdCol,
            TableColumn<Order, String> orderTypeCol,
            TableColumn<Order, String> addressCol,
            TableColumn<Order, String> statusCol
    ) {
        switch (roleType) {
            case CHEF, WAITER -> {
                if (orderIdCol != null) {
                    orderIdCol.setCellValueFactory(data ->
                            new SimpleIntegerProperty(data.getValue().getOrderID()).asObject());
                }
                if (customerIdCol != null) {
                    customerIdCol.setCellValueFactory(data ->
                            new SimpleIntegerProperty(data.getValue().getCustomerID()).asObject());
                }
                if (orderTypeCol != null) {
                    orderTypeCol.setCellValueFactory(data ->
                            new SimpleStringProperty(data.getValue().getOrderType().getDisplayName()));
                }
                if (statusCol != null) {
                    statusCol.setCellValueFactory(data ->
                            new SimpleStringProperty(data.getValue().getStatus().getDisplayName()));
                }
            }

            case DRIVER -> {
                if (orderIdCol != null) {
                    orderIdCol.setCellValueFactory(data ->
                            new SimpleIntegerProperty(data.getValue().getOrderID()).asObject());
                }
                if (customerIdCol != null) {
                    customerIdCol.setCellValueFactory(data ->
                            new SimpleIntegerProperty(data.getValue().getCustomerID()).asObject());
                }
                if (addressCol != null) {
                    addressCol.setCellValueFactory(data -> {
                        if (data.getValue() instanceof DeliveryOrder delivery) {
                            return new SimpleStringProperty(delivery.getDeliveryAddress());
                        } else {
                            return new SimpleStringProperty("-");
                        }
                    });

                    if (statusCol != null) {
                        statusCol.setCellValueFactory(data ->
                                new SimpleStringProperty(data.getValue().getStatus().getDisplayName()));
                    }
                }
            }

            case MANAGER -> {
                if (orderIdCol != null) {
                    orderIdCol.setCellValueFactory(data ->
                            new SimpleIntegerProperty(data.getValue().getOrderID()).asObject());
                }
                if (customerIdCol != null) {
                    customerIdCol.setCellValueFactory(data ->
                            new SimpleIntegerProperty(data.getValue().getCustomerID()).asObject());
                }
                if (orderTypeCol != null) {
                    orderTypeCol.setCellValueFactory(data ->
                            new SimpleStringProperty(data.getValue().getOrderType().getDisplayName()));
                }
                if (addressCol != null) {
                    addressCol.setCellValueFactory(data -> {
                        if (data.getValue() instanceof DeliveryOrder delivery) {
                            return new SimpleStringProperty(delivery.getDeliveryAddress());
                        } else {
                            return new SimpleStringProperty("-");
                        }
                    });

                    if (statusCol != null) {
                        statusCol.setCellValueFactory(data ->
                                new SimpleStringProperty(data.getValue().getStatus().getDisplayName()));
                    }
                }
            }
        }
    }

    /**
     * Set up the order item table columns (common for all staff).
     */
    public static void setupOrderItemColumns(TableColumn<OrderItem, String> itemNameCol,
                                             TableColumn<OrderItem, Integer> qtyCol,
                                             TableColumn<OrderItem, Double> priceCol,
                                             TableColumn<OrderItem, Double> subtotalCol) {
        itemNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMenuItem().getName()));
        qtyCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        priceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getMenuItem().getPrice()).asObject());
        subtotalCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());
    }

    /**
     * Optional filter combo box for roles that need order filtering.
     */
    public static void setupFilterComboBox(ComboBox<String> comboBox, Runnable onChangeAction) {
        comboBox.getItems().setAll("My Role's Orders", "All Outstanding");
        comboBox.setValue("My Role's Orders");
        comboBox.setOnAction(e -> onChangeAction.run());
    }

    /**
     * Generic method for updating order item table and total label based on selection.
     */
    public static void setupOrderSelectionListener(
            TableView<Order> ordersTable,
            TableView<OrderItem> itemsTable,
            Label totalLabel,
            Button actionButton,
            OrderStatus requiredStatusForAction,
            Label reminderLabel
    ) {
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                itemsTable.setItems(FXCollections.observableArrayList(selected.getItems()));
                double total = calculateOrderTotal(selected);
                totalLabel.setText(String.format("Total: £%.2f", total));

                boolean enableAction = selected.getStatus() == requiredStatusForAction;
                if (actionButton != null) {
                    actionButton.setDisable(!enableAction);
                }
                if (reminderLabel != null) {
                    reminderLabel.setVisible(!enableAction);
                }

            } else {
                itemsTable.getItems().clear();
                totalLabel.setText("Total: £0.00");

                if (actionButton != null) actionButton.setDisable(true);
                if (reminderLabel != null) reminderLabel.setVisible(true);
            }
        });
    }


    /**
     * Calculate total for selected order.
     */
    public static double calculateOrderTotal(Order order) {
        return order.getItems().stream()
                .mapToDouble(i -> i.getMenuItem().getPrice() * i.getQuantity())
                .sum();
    }
}
