package cafe94.system.controller.staff;

import cafe94.system.model.user.Staff;
import cafe94.system.utils.AppState;
import cafe94.system.utils.SceneManager;
import cafe94.system.data.DataSaver;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class WorkingHoursPopupController {

    @FXML private VBox formContainer;

    private static final String STAFF_PROFILE_FILE = "src/main/resources/data/staff_profile.txt";
    private Staff currentStaff;
    private final List<TextField> toWorkFields = new ArrayList<>();
    private final List<TextField> workedFields = new ArrayList<>();
    private boolean saved = false;

    public boolean hasSaved() {
        return saved;
    }

    public void setStaff(Staff staff) {
        this.currentStaff = staff;
        loadWorkingHours();
    }

    private void loadWorkingHours() {
        formContainer.getChildren().clear();
        toWorkFields.clear(); // clear previous entries
        workedFields.clear(); // clear previous entries

        List<Double> toWorkList = currentStaff.getHoursToWork();
        List<Double> workedList = currentStaff.getTotalHoursWorked();

        for (int i = 0; i < 5; i++) {
            Label label = new Label("Day " + (i + 1));
            TextField toWorkField = new TextField();
            TextField workedField = new TextField();

            toWorkField.setPromptText("Hours to Work");
            workedField.setPromptText("Total Hours Worked");

            if (i < toWorkList.size()) {
                toWorkField.setText(String.valueOf(toWorkList.get(i)));
            }
            if (i < workedList.size()) {
                workedField.setText(String.valueOf(workedList.get(i)));
            }

            toWorkFields.add(toWorkField);
            workedFields.add(workedField);

            HBox row = new HBox(10, label, toWorkField, workedField);
            formContainer.getChildren().add(row);
        }
    }


    @FXML
    private void handleSave() {
        try {
            List<Double> toWorkList = new ArrayList<>();
            List<Double> workedList = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                String toWorkStr = toWorkFields.get(i).getText().trim();
                String workedStr = workedFields.get(i).getText().trim();

                // Treat empty fields as 0.0
                double toWork = toWorkStr.isEmpty() ? 0.0 : Double.parseDouble(toWorkStr);
                double worked = workedStr.isEmpty() ? 0.0 : Double.parseDouble(workedStr);

                // Validate the range
                if (toWork < 0 || toWork > 24 || worked < 0 || worked > 24) {
                    showAlert("Invalid input", "Hours must be between 0 and 24.");
                    return;
                }

                toWorkList.add(toWork);
                workedList.add(worked);
            }

            currentStaff.setHoursToWork(toWorkList);
            currentStaff.setTotalHoursWorked(workedList);

            DataSaver.saveStaff(STAFF_PROFILE_FILE, AppState.allStaff);
            saved = true;
            showInfo("Working hours saved successfully.");
            SceneManager.closePopup();
        } catch (NumberFormatException e) {
            showAlert("Format Error", "Please enter valid numeric values (e.g., 8.0)");
        }
    }


    @FXML
    private void handleCancel() {
        ((Stage) formContainer.getScene().getWindow()).close(); // Use any known node like formContainer, cancelButton, etc.
    }


    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
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
