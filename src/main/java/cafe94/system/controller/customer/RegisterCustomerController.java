package cafe94.system.controller.customer;

import cafe94.system.data.DataSaver;
import cafe94.system.model.user.Customer;
import cafe94.system.utils.AppState;
import cafe94.system.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for handling customer registration logic.
 */
public class RegisterCustomerController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField addressField;
    @FXML private PasswordField passwordField;

    private static final String CUSTOMER_FILE = "src/main/resources/data/customer.txt";

    /**
     * Called when user clicks "Register".
     * Validates input, creates customer, saves to file, and returns to login screen.
     */
    @FXML
    private void handleRegister() {
        String first = firstNameField.getText().trim();
        String last = lastNameField.getText().trim();
        String address = addressField.getText().trim();
        String password = passwordField.getText().trim();

        if (first.isEmpty() || last.isEmpty() || address.isEmpty() || password.isEmpty()) {
            showAlert("Warning","All fields are required.");
            return;
        }

        Customer newCustomer = new Customer(first, last, address, password);
        AppState.allCustomer.add(newCustomer);
        DataSaver.saveCustomers(CUSTOMER_FILE, AppState.allCustomer);

        showAlert("Registration successful!",
                "Your Customer ID is: " + newCustomer.getId() + "\nPlease remember this ID to login 😊");
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }

    /**
     * Returns to the login page without saving data.
     */
    @FXML
    private void handleCancel() {
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }

    /**
     * Displays a simple info alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cafe94");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
