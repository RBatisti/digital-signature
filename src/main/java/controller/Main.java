package controller;

import javafx.fxml.FXML;
import java.awt.*;
import java.io.IOException;

import static screencontroller.ScreenController.changeScreen;

public class Main {
    @FXML
    private void goToLogin() {
        changeScreen("login");
    }

    @FXML
    private void goToValidator() {
        changeScreen("validator");
    }

    @FXML
    private void goToGithub() {
        try {
            Desktop.getDesktop().browse(java.net.URI.create("https://github.com/RBatisti/digital-signature"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
