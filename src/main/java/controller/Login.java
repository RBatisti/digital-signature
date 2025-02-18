package controller;

import dao.DB;
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
        if (DB.withoutConnection()) {
            return;
        }
        if (checkLogin(cpf.getText(), password.getText())) {
            SessionManager.getInstance().setUser(loadUser(cpf.getText(), password.getText()));
            clear();
            changeScreen("sign");
        } else {
            goToMain();
        }
    }

    private void clear() {
        cpf.setText("");
        password.setText("");
    }

    @FXML
    private void goToMain() {
        clear();
        changeScreen("main");
    }

    @FXML
    private void goToCreate() {
        clear();
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
