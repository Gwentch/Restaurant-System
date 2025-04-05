package cafe94.system;

import javafx.application.Application;
import javafx.stage.Stage;
import cafe94.system.utils.SceneManager;
import cafe94.system.utils.AppState;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        AppState.initialize();
        SceneManager.setStage(primaryStage);
        AppState.initialize();
        SceneManager.switchTo("standard/WelcomeLogin.fxml");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
