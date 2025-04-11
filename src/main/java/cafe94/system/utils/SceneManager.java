package cafe94.system.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * SceneManager is a utility class for managing scene transitions and popups within the Cafe94 JavaFX application.
 * <p>
 * It provides static methods for:
 * <ul>
 *   <li>Switching to new scenes with or without controller setup</li>
 *   <li>Loading modal popups with custom sizing</li>
 *   <li>Centralized error handling and display</li>
 * </ul>
 * <p>
 * It ensures all views are loaded consistently, with centralized title formatting and scene setup.
 */
public class SceneManager {

    public static Stage primaryStage;

    /**
     * Sets the application's primary stage.
     *
     * @param stage the main application window passed from the JavaFX application start method
     */
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Switches to a new scene based on the given FXML path. Does not provide controller access.
     *
     * @param fxmlPath the path to the FXML file (relative to /fxml directory, e.g., "customer/CustomerDashboard.fxml")
     */
    public static void switchTo(String fxmlPath) {
        if (primaryStage == null) {
            showError("Stage Not Set", "Please call SceneManager.setStage(stage) before switching scenes.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("Cafe94 - " + getTitleFromPath(fxmlPath));
            primaryStage.show();
        } catch (IOException e) {
            showError("Scene Switch Failed", "Could not load: " + fxmlPath);
        }
    }


    /**
     * Switches to the given scene and applies setup logic on the controller.
     * Use this if you need to inject data into the controller before showing the scene.
     *
     * @param fxmlPath        path to the FXML file
     * @param controllerSetup a lambda to configure the controller after loading
     * @param <T>             the type of the controller
     */
    public static <T> void switchToWithControllerAndSetup(String fxmlPath, Consumer<T> controllerSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            controllerSetup.accept(controller);

            Scene scene = new Scene(root);

            primaryStage.setScene(scene);

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Scene Switch Failed", "Could not load: " + fxmlPath);
        }
    }

    /**
     * Loads a popup window (modal) from the specified FXML file and returns its Stage and controller.
     *
     * @param fxmlPath the FXML file path (relative to /fxml)
     * @param title    the popup window title
     * @param width    desired width of the popup
     * @param height   desired height of the popup
     * @param <T>      the type of the controller
     * @return a {@code Pair<Stage, Controller>} containing the popup stage and its controller, or null on failure
     */
    public static <T> Pair<Stage, T> loadPopup(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            Stage popupStage = new Stage();
            popupStage.setTitle("Cafe94 - " +title);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(root, width, height));
            popupStage.setResizable(false);
            popupStage.centerOnScreen();
            popupStage.sizeToScene();

            return new Pair<>(popupStage, controller);
        } catch (IOException e) {
            showError("Popup Failed", "Could not load popup: " + fxmlPath);
            return null;
        }
    }

    /**
     * Converts an FXML file path into a readable title string.
     * For example, "ManagerDashboard.fxml" becomes "Manager Dashboard".
     *
     * @param path the FXML file path
     * @return formatted title string
     */
    private static String getTitleFromPath(String path) {
        String fileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        return capitalize(fileName.replaceAll("([A-Z])", " $1").trim());
    }

    /**
     * Capitalizes the first letter of the given string.
     *
     * @param str the input string
     * @return string with the first letter capitalized
     */
    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
