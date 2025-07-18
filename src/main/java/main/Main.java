package main;

import dao.DB;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main extends Application {
    public static Stage stage;


    private static StackPane root;
    private static Scene scene;

    private static final Map<String, Parent> screens = new HashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;

        // Main controller
        root = new StackPane();
        scene = new Scene(root, 500, 600);

        // Load screens
        screens.put("main", loadView("/view/main.fxml"));
        screens.put("login", loadView("/view/login.fxml"));
        screens.put("createUser", loadView("/view/createUser.fxml"));
        screens.put("validator", loadView("/view/validator.fxml"));
        screens.put("sign", loadView("/view/sign.fxml"));

        Image image = new Image(Objects.requireNonNull(getClass().getResource("/images/icon.png")).toExternalForm());
        primaryStage.getIcons().add(image);

        stage.setTitle("Digital Signature");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(600);
        stage.show();

        changeScreen("main");

        // Close connection at the end of execution
        Runtime.getRuntime().addShutdownHook(new Thread(DB::closeConnection));
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Parent loadView(String path) throws Exception {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(path)));
    }

    public static void changeScreen(String screenName) {
        Parent screen = screens.get(screenName);
        if (screen == null) {
            System.err.println("Screen not found: " + screenName);
            return;
        }

        Platform.runLater(() -> {
            root.getChildren().setAll(screen);
        });
    }
}
