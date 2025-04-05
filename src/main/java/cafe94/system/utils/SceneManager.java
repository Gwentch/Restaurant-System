package cafe94.system.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.util.Pair;

import java.io.IOException;
import java.util.function.Consumer;

public class SceneManager {


    public static Stage primaryStage;

    // Set the primary stage once from Main
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Switch to a scene by FXML path (without controller access)
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
     * Switch and return controller to allow data passing
     */
    public static <T> T switchToWithController(String fxmlPath) {
        if (primaryStage == null) {
            showError("Stage Not Set", "Please call SceneManager.setStage(stage) before switching scenes.");
            return null;
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
            return loader.getController();

        } catch (IOException e) {
            showError("Scene Switch Failed", "Could not load: " + fxmlPath);
            return null;
        }
    }

    /**
     * Switches to the given FXML scene and applies setup logic on the controller.
     * The setup logic is deferred to ensure all @FXML elements are initialized.
     *
     * @param fxmlPath        the path to the FXML file
     * @param controllerSetup a lambda to apply setup on the controller
     * @param <T>             the type of the controller
     */
    public static <T> void switchToWithControllerAndSetup(String fxmlPath, Consumer<T> controllerSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            controllerSetup.accept(controller);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(SceneManager.class.getResource("/style/style.css").toExternalForm());

            primaryStage.setScene(scene);

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Scene Switch Failed", "Could not load: " + fxmlPath);
        }
    }

    /**
     * Pop-up window (custom size)
     */
    public static <T> Pair<Stage, T> loadPopup(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            Stage popupStage = new Stage();
            popupStage.setTitle(STR."Cafe94 - \{title}");
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
     * Helper
     */
    private static String getTitleFromPath(String path) {
        String fileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        return capitalize(fileName.replaceAll("([A-Z])", " $1").trim());
    }

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
