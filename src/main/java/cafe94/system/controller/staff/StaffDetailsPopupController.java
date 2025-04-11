package cafe94.system.controller.staff;

import cafe94.system.data.DataSaver;
import cafe94.system.model.user.Staff;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import cafe94.system.utils.SceneManager;
import javafx.util.Pair;
import cafe94.system.utils.AppState;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Staff Details popup window in the Cafe94 system.
 * <p>
 * This popup allows the manager to add a new staff member or edit details
 * of an existing one. Fields include name, role, password, and working hours.
 * </p>
 *
 * The controller also allows launching a separate popup to manage weekly
 * working hours and ensures input validation before saving.
 */
public class StaffDetailsPopupController {

    @FXML private Label titleLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<Staff.StaffType> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML private TextField hoursToWorkField;
    @FXML private TextField totalHoursWorkedField;

    private static final String STAFF_PROFILE_FILE = "src/main/resources/data/staff_profile.txt";

    private Staff currentStaff;
    private boolean saved = false;
    private boolean isNew = false;


    /**
     * Sets up the popup view with an existing or new staff member's details.
     * Fields are pre-filled for editing, or initialized for adding.
     *
     * @param staff The staff member being added or edited.
     * @param isNew true if adding a new staff member; false if editing.
     */
    public void setup(Staff staff, boolean isNew) {
        this.currentStaff = staff;
        this.isNew = isNew;

        titleLabel.setText(isNew ? "Add New Staff" : "Edit Staff Details");

        // Populate fields
        firstNameField.setText(staff.getFirstName());
        lastNameField.setText(staff.getLastName());
        roleComboBox.getItems().setAll(Staff.StaffType.values());
        roleComboBox.setValue(staff.getType());
        passwordField.setText(staff.getPassword());
        hoursToWorkField.setText(String.valueOf(
                staff.getHoursToWork().stream().mapToDouble(Double::doubleValue).sum()));
        totalHoursWorkedField.setText(String.valueOf(
                staff.getTotalHoursWorked().stream().mapToDouble(Double::doubleValue).sum()));

        // Handle totalHoursWorked
        if (isNew) {
            totalHoursWorkedField.setText("0");
            totalHoursWorkedField.setDisable(true); // Disable editing
        } else {
            totalHoursWorkedField.setText(String.valueOf(
                    staff.getTotalHoursWorked().stream().mapToDouble(Double::doubleValue).sum()));
            totalHoursWorkedField.setDisable(false);
        }
    }

    /**
     * Returns whether the staff details were successfully saved.
     *
     * @return true if saved; false otherwise
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Handles the Cancel button action.
     * Closes the current popup window without saving any changes.
     */
    @FXML
    private void handleCancel() {
        System.out.println("Cancel clicked — using direct stage close");
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the Save button action.
     * Validates user input, updates the staff object, saves the data to file,
     * and closes the popup if input is valid.
     * <p>
     * Displays alerts if the input is incomplete or incorrectly formatted.
     * </p>
     */
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

            // Update currentStaff object
            currentStaff.setFirstName(firstName);
            currentStaff.setLastName(lastName);
            currentStaff.setPassword(password);
            currentStaff.setType(role);
            List<Double> toWorkList = new ArrayList<>();
            toWorkList.add(hoursToWork);
            currentStaff.setHoursToWork(toWorkList);

            List<Double> workedList = new ArrayList<>();
            workedList.add(totalHoursWorked);
            currentStaff.setTotalHoursWorked(workedList);


            DataSaver.saveStaff(STAFF_PROFILE_FILE, AppState.allStaff);
            saved = true;

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

    /**
     * Handles the Edit Working Hours button action.
     * Opens the working hours popup to allow detailed editing of daily hours.
     * Updates the total "hours to work" in the main form if changes are saved.
     */
    @FXML
    private void handleEditWorkingHours() {
        Pair<Stage, WorkingHoursPopupController> pair = SceneManager.loadPopup(
                "staff/WorkingHoursPopup.fxml", "Working Hours", 500, 400);

        if (pair != null) {
            WorkingHoursPopupController controller = pair.getValue();
            controller.setStaff(currentStaff);
            pair.getKey().showAndWait();

            if (controller.hasSaved()) {
                showInfo("✔ Working hours updated.");

                double totalToWork = currentStaff.getHoursToWork().stream().mapToDouble(Double::doubleValue).sum();
                hoursToWorkField.setText(String.valueOf(totalToWork));
            }
        }
    }

    // Helper method
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
