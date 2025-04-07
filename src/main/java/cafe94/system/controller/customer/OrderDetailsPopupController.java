package cafe94.system.controller.customer;

import cafe94.system.model.order.DeliveryOrder;
import cafe94.system.model.order.Order;
import cafe94.system.model.order.OrderItem;
import cafe94.system.model.order.OrderType;
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
            "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"
    );


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

    private void setupTimeSelectors() {
        pickupTimeCombo.setEditable(false);
        estimatedDeliveryTimeCombo.setEditable(false);
    }

    private void populateOrderTable(Order order) {
        orderTable.setItems(FXCollections.observableArrayList(order.getItems()));
        itemNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMenuItem().getName()));
        itemQtyColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        itemPriceColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.format("£%.2f", cell.getValue().getMenuItem().getPrice())));
        itemSubtotalColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.format("£%.2f", cell.getValue().getSubtotal())));

        double total = order.getItems().stream().mapToDouble(OrderItem::getSubtotal).sum();
        orderTotalLabel.setText(String.format("Total: £%.2f", total));
    }

    @FXML
    private void handleUseProfileAddress() {
        boolean useProfile = useProfileAddressCheckBox.isSelected();
        deliveryAddress.setDisable(useProfile);
        deliveryAddress.setText(useProfile ? customer.getAddress() : "");
    }

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


    private boolean isValidTimeFormat(String timeStr) {
        return timeStr != null && timeStr.matches("\\d{2}:\\d{2}");
    }

    private boolean isTimeInvalid(ComboBox<String> comboBox) {
        return !isValidTimeFormat(comboBox.getValue());
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void close() {
        Stage stage = (Stage) orderTable.getScene().getWindow();
        stage.close();
    }


    public boolean isConfirmed() {
        return confirmed;
    }

    public String getPickupTime() {
        return pickupTimeCombo.getValue();
    }

    public String getDeliveryAddress() {
        return deliveryAddress.getText().trim();
    }

    public String getEstimatedDeliveryTime() {
        return estimatedDeliveryTimeCombo.getValue();
    }
}
