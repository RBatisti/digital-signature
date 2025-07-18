package screencontroller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScreenController {
    private static Stage stage;
    private static StackPane root;
    private static final Map<String, Parent> screens = new HashMap<>();

    public static void initialize(Stage primaryStage) throws Exception {
        stage = primaryStage;

        // Main controller
        root = new StackPane();
        Scene scene = new Scene(root, 500, 600);

        // Load screens
        screens.put("main", loadView("/view/main.fxml"));
        screens.put("login", loadView("/view/login.fxml"));
        screens.put("createUser", loadView("/view/createUser.fxml"));
        screens.put("validator", loadView("/view/validator.fxml"));
        screens.put("sign", loadView("/view/sign.fxml"));

        Image image = new Image(Objects.requireNonNull(ScreenController.class.getResource("/images/icon.png")).toExternalForm());
        stage.getIcons().add(image);

        stage.setTitle("Digital Signature");
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(600);
        stage.show();

        changeScreen("main");
    }

    public static Stage getStage() {
        return stage;
    }

    private static Parent loadView(String path) throws Exception {
        return FXMLLoader.load(Objects.requireNonNull(ScreenController.class.getResource(path)));
    }

    public static void changeScreen(String screenName) {
        Parent screen = screens.get(screenName);
        if (screen == null) {
            System.err.println("Screen not found: " + screenName);
            return;
        }

        root.getChildren().setAll(screen);
    }
}