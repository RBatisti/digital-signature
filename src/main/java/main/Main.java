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
import screencontroller.ScreenController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        ScreenController.initialize(primaryStage);

        // Close connection at the end of execution
        Runtime.getRuntime().addShutdownHook(new Thread(DB::closeConnection));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
