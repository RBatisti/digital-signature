package controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import session.SessionManager;
import java.awt.*;
import java.io.IOException;

import static main.Main.changeScreen;
import static service.AuthService.checkLogin;
import static service.AuthService.loadUser;

public class Login {
    @FXML
    private TextField cpf;

    @FXML
    private PasswordField password;

    @FXML
    private void login() {
        if (checkLogin(cpf.getText(), password.getText())) {
            SessionManager.getInstance().setUser(loadUser(cpf.getText(), password.getText()));
            cpf.setText("");
            password.setText("");
            changeScreen("sign");
        }
    }

    @FXML
    private void goToMain() {
        changeScreen("main");
    }

    @FXML
    private void goToCreate() {
        changeScreen("createUser");
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
