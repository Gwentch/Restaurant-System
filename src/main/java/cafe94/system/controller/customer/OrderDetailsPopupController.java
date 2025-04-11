package cafe94.system.controller.customer;

import cafe94.system.model.order.DeliveryOrder;
import cafe94.system.model.order.Order;
import cafe94.system.model.order.OrderItem;
import cafe94.system.model.order.TakeawayOrder;
import cafe94.system.model.user.Customer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controller for the Order Details pop-up used during order confirmation in the Cafe94 system.
 * <p>
 * This controller handles:
 * <ul>
 *     <li>Displaying order items and totals</li>
 *     <li>Capturing additional information for takeaway and delivery orders</li>
 *     <li>Validating user inputs such as pickup time and delivery address</li>
 *     <li>Returning confirmation status and user-entered values to the caller</li>
 * </ul>
 * It supports both {@link TakeawayOrder} and {@link DeliveryOrder} types, dynamically
 * showing relevant input fields.
 */
public class OrderDetailsPopupController {

    @FXML private Label orderIdLabel;
    @FXML private VBox takeawayPane;
    @FXML private VBox deliveryPane;
    @FXML private ComboBox<String> pickupTimeCombo;
    @FXML private TextField deliveryAddress;
    @FXML private CheckBox useProfileAddressCheckBox;
    @FXML private ComboBox<String> estimatedDeliveryTimeCombo;

    @FXML private TableView<OrderItem> orderTable;
    @FXML private TableColumn<OrderItem, String> itemNameColumn;
    @FXML private TableColumn<OrderItem, Integer> itemQtyColumn;
    @FXML private TableColumn<OrderItem, String> itemPriceColumn;
    @FXML private TableColumn<OrderItem, String> itemSubtotalColumn;
    @FXML private Label orderTotalLabel;

    private Customer customer;
    public Order previewOrder;
    private boolean confirmed = false;

    private static final List<String> AVAILABLE_TIMES = List.of(
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00",
            "15:00", "16:00", "17:00", "18:00", "19:00", "20:00");

    /**
     * Initializes the pop-up with order details and the associated customer.
     * Displays appropriate fields based on the order type (Takeaway or Delivery).
     *
     * @param order    The order being reviewed and confirmed.
     * @param customer The customer placing the order.
     */
    public void setup(Order order, Customer customer) {
        this.customer = customer;
        this.previewOrder = order;

        orderIdLabel.setText("Order ID: " + order.getOrderID());
        setupTimeSelectors();

        takeawayPane.setVisible(false);
        deliveryPane.setVisible(false);

        if (order instanceof TakeawayOrder) {
            takeawayPane.setVisible(true);
            pickupTimeCombo.setItems(FXCollections.observableArrayList(AVAILABLE_TIMES));
            pickupTimeCombo.getSelectionModel().selectFirst();

        } else if (order instanceof DeliveryOrder) {
            deliveryPane.setVisible(true);
            deliveryAddress.setText(customer.getAddress());
            useProfileAddressCheckBox.setSelected(true);
            deliveryAddress.setDisable(true);
            estimatedDeliveryTimeCombo.setItems(FXCollections.observableArrayList(AVAILABLE_TIMES));
            estimatedDeliveryTimeCombo.getSelectionModel().selectFirst();
        }


        populateOrderTable(order);
    }

    /**
     * Initializes the time selection ComboBoxes to be non-editable.
     * This ensures users can only select from predefined times.
     */
    private void setupTimeSelectors() {
        pickupTimeCombo.setEditable(false);
        estimatedDeliveryTimeCombo.setEditable(false);
    }

    /**
     * Populates the order summary table with the items from the given order.
     * Also calculates and updates the total price label.
     *
     * @param order The order whose items should be displayed.
     */
    private void populateOrderTable(Order order) {
        orderTable.setItems(FXCollections.observableArrayList(order.getItems()));
        itemNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMenuItem().getName()));
        itemQtyColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        itemPriceColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.format("£%.2f", cell.getValue().getMenuItem().getPrice())));
        itemSubtotalColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.format("£%.2f", cell.getValue().getSubtotal())));

        double total = order.getItems().stream().mapToDouble(OrderItem::getSubtotal).sum();
        orderTotalLabel.setText(String.format("Total: £%.2f", total));
    }

    /**
     * Toggles whether to auto-fill the delivery address with the customer's profile address.
     * This is triggered when the "Use Profile Address" checkbox is selected or deselected.
     */
    @FXML
    private void handleUseProfileAddress() {
        boolean useProfile = useProfileAddressCheckBox.isSelected();
        deliveryAddress.setDisable(useProfile);
        deliveryAddress.setText(useProfile ? customer.getAddress() : "");
    }

    /**
     * Validates the user's input and confirms the order details if valid.
     * This includes time selection and address validation depending on order type.
     * If validation passes, the confirmation window is closed.
     */
    @FXML
    private void handleConfirm() {
        if (takeawayPane.isVisible() && isTimeInvalid(pickupTimeCombo)) {
            showAlert("Please select a valid pickup time.");
            return;
        }

        if (deliveryPane.isVisible()) {
            if (deliveryAddress.getText().isBlank()) {
                showAlert("Please enter the delivery address.");
                return;
            }
            if (isTimeInvalid(estimatedDeliveryTimeCombo)) {
                showAlert("Please select a valid estimated delivery time.");
                return;
            }
        }

        confirmed = true;
        close();
    }

    /**
     * Checks whether the given time string is in the format HH:mm.
     *
     * @param timeStr The time string to validate.
     * @return true if the format is valid, false otherwise.
     */
    private boolean isValidTimeFormat(String timeStr) {
        return timeStr != null && timeStr.matches("\\d{2}:\\d{2}");
    }

    /**
     * Checks whether the selected time in the given ComboBox is invalid.
     *
     * @param comboBox The ComboBox to validate.
     * @return true if the selected time is not in a valid format.
     */
    private boolean isTimeInvalid(ComboBox<String> comboBox) {
        return !isValidTimeFormat(comboBox.getValue());
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Closes the current popup window.
     */
    private void close() {
        Stage stage = (Stage) orderTable.getScene().getWindow();
        stage.close();
    }

    /**
     * Returns whether the order has been confirmed by the user.
     *
     * @return true if the user confirmed the order; false otherwise.
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Returns the selected pickup time for takeaway orders.
     *
     * @return The pickup time in HH:mm format.
     */
    public String getPickupTime() {
        return pickupTimeCombo.getValue();
    }

    /**
     * Returns the delivery address entered or selected for delivery orders.
     *
     * @return The trimmed delivery address string.
     */
    public String getDeliveryAddress() {
        return deliveryAddress.getText().trim();
    }

    /**
     * Returns the selected estimated delivery time for delivery orders.
     *
     * @return The estimated delivery time in HH:mm format.
     */
    public String getEstimatedDeliveryTime() {
        return estimatedDeliveryTimeCombo.getValue();
    }
}
