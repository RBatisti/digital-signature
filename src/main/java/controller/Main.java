package controller;

import javafx.fxml.FXML;
import utils.DataBase;
import utils.UserRepository;

import static main.Main.changeScreen;

public class Main {
    @FXML
    private void goToLogin() {
        changeScreen("login");
    }

    @FXML
    private void goToValidator() {
        changeScreen("validator");
    }
}
