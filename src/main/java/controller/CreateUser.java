package controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.awt.*;
import java.io.IOException;

import static main.Main.changeScreen;
import static service.AuthService.createUser;

public class CreateUser {
    @FXML
    private TextField fieldName;

    @FXML
    private TextField fieldCpf;

    @FXML
    private PasswordField fieldPassword1;

    @FXML
    private PasswordField fieldPassword2;


    @FXML
    private void goToMain() {
        changeScreen("main");
    }

    @FXML
    private void goToLogin() {
        changeScreen("login");
    }

    @FXML
    public void create() {
        if (!fieldPassword1.getText().equals(fieldPassword2.getText())) {
            showError("Error", "Passwors are different", "");
            return;
        }
        if (fieldCpf.getText().length() != 11) {
            showError("Error", "CPF must contain 11 digits", "");
            return;
        }
        if (!isInteger(fieldCpf.getText())) {
            showError("Error", "CPF must contain just numbers", "");
            return;
        }
        createUser(fieldCpf.getText(), fieldPassword1.getText(), fieldName.getText());
        goToMain();
    }


    public static boolean isInteger(String s) {
        if (s.isEmpty()){
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                }
            }
            if (Character.digit(s.charAt(i),10) < 0) {
                return false;
            }
        }
        return true;
    }

    public static void showError(String title, String header, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
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
