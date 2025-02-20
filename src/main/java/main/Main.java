package main;

import dao.DB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static Stage stage;

    private static Scene mainScene;
    private static Scene createUserScene;
    private static Scene validator;
    private static Scene login;
    private static Scene sign;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;

        // Load screens
        mainScene = loadScene("/view/main.fxml");
        login = loadScene("/view/login.fxml");
        createUserScene = loadScene("/view/createUser.fxml");
        validator = loadScene("/view/validator.fxml");
        sign = loadScene("/view/sign.fxml");

        primaryStage.setTitle("Digital Signature");

        Image image = new Image(Objects.requireNonNull(getClass().getResource("/images/icon.png")).toExternalForm());
        primaryStage.getIcons().add(image);

        changeScreen("main");

        // Fix the screen size
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(600);
        primaryStage.setMaxWidth(500);
        primaryStage.setMaxHeight(600);

        primaryStage.show();

        // Close connection at the end of execution
        Runtime.getRuntime().addShutdownHook(new Thread(DB::closeConnection));
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Scene loadScene(String path) throws Exception {
        Parent fxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(path)));
        return new Scene(fxml, 500, 600);
    }

    public static void changeScreen(String screen) {
        switch (screen) {
            case "main":
                stage.setScene(mainScene);
                break;

            case "login":
                stage.setScene(login);
                break;

            case "createUser":
                stage.setScene(createUserScene);
                break;

            case "validator":
                stage.setScene(validator);
                break;

            case "sign":
                stage.setScene(sign);
                break;
        }
    }
}
