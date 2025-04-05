package cafe94.system.controller.staff;

import cafe94.system.model.user.Staff;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class StaffDetailsPopupController {

    @FXML private Label titleLabel;

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<Staff.StaffType> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML private TextField hoursToWorkField;
    @FXML private TextField totalHoursWorkedField;

    private Staff staff;
    private boolean saved = false;
    private boolean isNew = false;

    // Used by caller (e.g. ManagerDashboard) to check if Save was clicked
    public boolean isSaved() {
        return saved;
    }

    public void setup(Staff staff, boolean isNew) {
        this.staff = staff;
        this.isNew = isNew;

        // Set popup title
        titleLabel.setText(isNew ? "Add New Staff" : "Edit Staff Details");

        // Populate fields
        firstNameField.setText(staff.getFirstName());
        lastNameField.setText(staff.getLastName());
        roleComboBox.getItems().setAll(Staff.StaffType.values());
        roleComboBox.setValue(staff.getType());
        passwordField.setText(staff.getPassword());
        hoursToWorkField.setText(String.valueOf(staff.getHoursToWork()));

        // Handle totalHoursWorked
        if (isNew) {
            totalHoursWorkedField.setText("0");
            totalHoursWorkedField.setDisable(true); // Disable editing
        } else {
            totalHoursWorkedField.setText(String.valueOf(staff.getTotalHoursWorked()));
            totalHoursWorkedField.setDisable(false);
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) firstNameField.getScene().getWindow()).close();
    }

    @FXML
    private void handleSave() {
        try {
            // Validate input
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String password = passwordField.getText().trim();
            Staff.StaffType role = roleComboBox.getValue();
            double hoursToWork = Double.parseDouble(hoursToWorkField.getText().trim());
            double totalHoursWorked = isNew ? 0.0 : Double.parseDouble(totalHoursWorkedField.getText().trim());

            if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || role == null) {
                showAlert("Please fill in all required fields.");
                return;
            }

            // Set staff data
            staff.setFirstName(firstName);
            staff.setLastName(lastName);
            staff.setPassword(password);
            staff.setType(role);
            staff.setHoursToWork(hoursToWork);
            staff.setTotalHoursWorked(totalHoursWorked);

            saved = true;

            // Show different pop-up msg (add or edit staff)
            String successMessage = isNew
                    ? "Staff member added successfully!"
                    : "Staff details updated successfully.";

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(successMessage);
            alert.showAndWait();

            ((Stage) firstNameField.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            showAlert("Please enter correct format of working hours (e.g. 40.0)");
        }
    }


    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
