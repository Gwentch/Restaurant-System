package cafe94.system.controller;

import cafe94.system.controller.customer.CustomerDashboardController;
import cafe94.system.controller.staff.ChefDashboardController;
import cafe94.system.controller.staff.DriverDashboardController;
import cafe94.system.controller.staff.ManagerDashboardController;
import cafe94.system.controller.staff.WaiterDashboardController;
import cafe94.system.utils.AppState;
import cafe94.system.data.DataLoader;
import cafe94.system.model.user.Customer;
import cafe94.system.model.user.Staff;
import cafe94.system.utils.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

/**
 * Controller for the Welcome Login screen in the Cafe94 system.
 * <p>
 * Handles both staff and customer authentication, with support for
 * manual customer ID input or staff profile selection. Navigates users to
 * the appropriate dashboard upon successful login.
 * </p>
 *
 * Features:
 * <ul>
 *   <li>Login support for all staff roles and customers</li>
 *   <li>Dynamic population of existing staff profile with IDs</li>
 *   <li>Toggle between manual login mode(Customer) and selection-based login(Staff)</li>
 *   <li>Customer registration navigation</li>
 * </ul>
 */
public class WelcomeLoginController {

    @FXML private ImageView logoCafe;
    @FXML private TextField idField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Staff> staffComboBox;
    @FXML private CheckBox manualLoginCheckBox;


    /**
     * Initializes the login screen with available staff profiles and logo image.
     * Sets up listeners for login mode toggling and staff selection behavior.
     */
    @FXML
    private void initialize() {
        idField.setDisable(true);
        // Load customerList and staff
        AppState.allCustomer = DataLoader.loadCustomers("src/main/resources/data/customer.txt");
        AppState.allStaff = DataLoader.loadStaff("src/main/resources/data/staff_profile.txt");

        // Load logo
        var url = getClass().getResource("/image/LogoCafe94.png");
        if (url != null) {
            logoCafe.setImage(new javafx.scene.image.Image(url.toExternalForm()));
        } else {
            System.out.println("Image not found!");
        }

        staffComboBox.setOnAction(event -> {
            Staff selected = staffComboBox.getValue();
            if (selected != null && !manualLoginCheckBox.isSelected()) {
                idField.setText(String.valueOf(selected.getId()));
                idField.setDisable(true);
            }
        });

        // Populate existing staff profile
        staffComboBox.setItems(FXCollections.observableArrayList(AppState.allStaff));
        staffComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Staff staff) {
                return staff == null ? "" : staff.getFullName();
            }

            @Override
            public Staff fromString(String string) {
                return null;
            }
        });

        staffComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selectedStaff) -> {
            if (selectedStaff != null) {
                idField.setText(String.valueOf(selectedStaff.getId()));
                idField.setDisable(true);
            } else {
                idField.clear();
                idField.setDisable(false);
            }
        });
    }

    /**
     * Handles login attempts for staff and customers.
     * Navigates to the corresponding dashboard if credentials are valid.
     *
     * @param event The login button click event.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String id = idField.getText().trim();
        String password = passwordField.getText().trim();

        if (id.isEmpty() || password.isEmpty()) {
            showAlert("Please enter both ID and password.");
            return;
        }

        // Staff Login
        for (Staff staff : AppState.allStaff) {
            if (String.valueOf(staff.getId()).equals(id) && staff.getPassword().equals(password)) {
                AppState.loggedInStaff = staff;
                System.out.println("Login successful as Staff");

                switch (staff.getType()) {
                    case CHEF:
                        SceneManager.switchToWithControllerAndSetup("staff/ChefDashboard.fxml",
                                (ChefDashboardController c) ->
                                        c.setup(AppState.orderManaged, AppState.menuItems, AppState.dailySpecials));
                        break;

                    case MANAGER:
                        SceneManager.switchToWithControllerAndSetup("staff/ManagerDashboard.fxml",
                                (ManagerDashboardController c) -> c.setup(AppState.orderManaged));
                        break;

                    case WAITER:
                        SceneManager.switchToWithControllerAndSetup("staff/WaiterDashboard.fxml",
                                (WaiterDashboardController c) -> c.setup(AppState.orderManaged));

                        break;

                    case DRIVER:
                        SceneManager.switchToWithControllerAndSetup("staff/DriverDashboard.fxml",
                                (DriverDashboardController c) -> c.setup(AppState.orderManaged));
                        break;

                }

                return;
            }
        }

        // Customer Login
        for (Customer customer : AppState.allCustomer) {
            if (String.valueOf(customer.getId()).equals(id) && customer.getPassword().equals(password)) {
                System.out.println("Login successful as Customer");

                AppState.loggedInCustomer = customer;

                SceneManager.switchToWithControllerAndSetup("customer/CustomerDashboard.fxml",
                        (CustomerDashboardController c) ->
                                c.setup(AppState.loggedInCustomer, AppState.orderManaged, AppState.menuItems, AppState.dailySpecials));
                return;
            }
        }

        showAlert("Invalid ID or Password. Please try again.");
    }


    /**
     * Toggles between manual ID input and ComboBox profile selection mode for staff.
     */
    @FXML
    private void handleToggleLoginMode() {
        boolean isManual = manualLoginCheckBox.isSelected();

        if (isManual) {
            // Manual mode: user types username
            idField.clear();
            idField.setDisable(false);
            staffComboBox.setDisable(true);
        } else {
            idField.setDisable(true);
            staffComboBox.setDisable(false);

            Staff selected = staffComboBox.getValue();
            if (selected != null) {
                idField.setText(String.valueOf(selected.getId()));

            }
        }
    }

    /**
     * Navigates the user to the customer registration screen.
     *
     * @param event The register button click event.
     */
    @FXML
    private void handleRegister(ActionEvent event) {
        SceneManager.switchTo("customer/RegisterCustomer.fxml");
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
