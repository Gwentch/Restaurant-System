package cafe94.system.controller.customer;

import cafe94.system.data.DataLoader;
import cafe94.system.data.DataSaver;
import cafe94.system.model.booking.Booking;
import cafe94.system.model.booking.BookingStatus;
import cafe94.system.model.user.Customer;
import cafe94.system.utils.AppState;
import cafe94.system.utils.SceneManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;



/**
 * Controller for handling customer bookings in Cafe94.
 * Allows customers to request a table, view current bookings, and cancel if needed.
 */
public class CustomerBookingController {

    @FXML private TextField customerID;
    @FXML private TextField guestsField;
    @FXML private ComboBox<String>  timeComboBox;
    @FXML private TextField durationField;
    @FXML private DatePicker datePicker;
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, Integer> idCol, guestsCol, durationCol;
    @FXML private TableColumn<Booking, String> dateCol, timeCol, statusCol;

    private static final String BOOKING_FILE_PATH = "src/main/resources/data/booking.txt";

    private Customer customer;
    private ObservableList<Booking> bookings = FXCollections.observableArrayList();

    /**
     * Sets up the booking controller with the current logged-in customer and their bookings.
     *
     * @param customer the currently logged-in customer
     *
     */
    public void setup(Customer customer) {
        this.customer = customer;
        customerID.setText(String.valueOf(customer.getId()));
        customerID.setDisable(true);
        populateTimeSlots();
    }

    @FXML
    private void initialize() {
        bookings = FXCollections.observableArrayList(DataLoader.loadBookings(BOOKING_FILE_PATH));
        bookingTable.setItems(FXCollections.observableArrayList(bookings));
        setupTableColumns();
        refreshTable();
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getBookingID()).asObject());
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        timeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTime()));
        guestsCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getGuestNum()).asObject());
        durationCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDurationHours()).asObject());
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));
    }

    private void refreshTable() {
        bookingTable.setItems(FXCollections.observableArrayList(bookings));
    }


    /**
     * Handles the customer booking submission.
     * Validates inputs, creates a booking, saves it to file, and updates the table view.
     */
    /**
    * Handles submission of a new booking by validating inputs,
    * creating the booking, saving it to file, and updating the table view.
 */
    @FXML
    private void handleSubmit() {
        try {
            int guests = Integer.parseInt(guestsField.getText().trim());
            String date = datePicker.getValue().toString();
            String time = timeComboBox.getValue();

            if (time == null || time.isEmpty()) {
                showError("Please select a timeslot.");
                return;
            }

            int duration = 1;
            try {
                int d = Integer.parseInt(durationField.getText().trim());
                if (d > 0) duration = d;
            } catch (NumberFormatException ignored) {
                // fallback to default duration = 1
            }

            int bookingID = Booking.getNextBookingID();
            Booking booking = new Booking(bookingID, customer.getId(), guests, date, time, duration, BookingStatus.PENDING);

            bookings.add(booking);
            DataSaver.saveBookings(BOOKING_FILE_PATH, bookings);

            // Refresh table view
            bookingTable.setItems(FXCollections.observableArrayList(bookings));
            bookingTable.scrollTo(booking);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Booking Submitted");
            alert.setHeaderText("Booking Request Sent");
            alert.setContentText("✔ Thank you for your booking!\nYour booking ID is #" + bookingID + ". Please wait for confirmation.");
            alert.showAndWait();

            guestsField.clear();
            durationField.setText("1");

        } catch (Exception e) {
            showError("Something went wrong. Please check your input.");
        }
    }

    @FXML
    private void handleCancel() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Cancel");
            confirm.setHeaderText("Are you sure you want to cancel this booking?");
            confirm.setContentText("This action cannot be undone.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    selected.setStatus(BookingStatus.CANCELLED);
                    bookingTable.refresh();
                    DataSaver.saveBookings("src/main/resources/data/booking.txt", bookings);
                }
            });
        } else {
            showError("Please select a booking to cancel.");
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchToWithControllerAndSetup("customer/CustomerDashboard.fxml",
                (CustomerDashboardController controller) -> {
                    controller.setup(AppState.loggedInCustomer, AppState.orderManaged, AppState.menuItems, AppState.dailySpecials);
                }
        );
    }

    // Helper method
    @FXML
    /**
     * Populates the timeComboBox with 30-minute time slots between 11:00 and 22:00.
     */
    private void populateTimeSlots() {
        List<String> timeSlots = new ArrayList<>();
        for (int hour = 11; hour <= 21; hour++) {
            timeSlots.add(String.format("%02d:00", hour));
            timeSlots.add(String.format("%02d:30", hour));
        }
        timeSlots.add("22:00"); // Final slot
        timeComboBox.getItems().addAll(timeSlots);
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}