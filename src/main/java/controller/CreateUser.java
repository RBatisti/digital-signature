package controller;

import dao.DB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.awt.*;
import java.io.IOException;

import static dao.UserDAO.existCpf;
import static main.Main.changeScreen;
import static service.AuthService.createUser;
import static utils.Alerts.showMessage;

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
        clear();
        changeScreen("main");
    }

    @FXML
    private void goToLogin() {
        clear();
        changeScreen("login");
    }

    @FXML
    public void create() {
        if (DB.withoutConnection()) {
            return;
        }
        if (!fieldPassword1.getText().equals(fieldPassword2.getText())) {
            showMessage("Error", "Passwords are different", "check the password and try again", Alert.AlertType.ERROR);
            return;
        }
        if (fieldCpf.getText().length() != 11) {
            showMessage("Error", "CPF must contain 11 digits", "check the CPF and try again", Alert.AlertType.ERROR);
            return;
        }
        if (!isInteger(fieldCpf.getText())) {
            showMessage("Error", "CPF must contain just numbers", "check the CPF and try again", Alert.AlertType.ERROR);
            return;
        }
        if (existCpf(fieldCpf.getText())) {
            showMessage("Error", "cpf has already been used", "check the CPF and try again", Alert.AlertType.ERROR);
            return;
        }
        createUser(fieldCpf.getText(), fieldPassword1.getText(), fieldName.getText());
        clear();
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

    @FXML
    private void goToGithub() {
        try {
            Desktop.getDesktop().browse(java.net.URI.create("https://github.com/RBatisti/digital-signature"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void clear() {
        fieldName.setText("");
        fieldCpf.setText("");
        fieldPassword1.setText("");
        fieldPassword2.setText("");
    }
}
