package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utils.UserRepository;

import static main.Main.changeScreen;

public class CreateUser {
    @FXML
    private TextField name;

    @FXML
    private TextField cpf;

    @FXML
    private PasswordField password1;

    @FXML
    private PasswordField password2;


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
        if (!password1.getText().equals(password2.getText())) {
            showError("Error", "Passwors are different", "");
            return;
        }
        if (cpf.getText().length() != 11) {
            showError("Error", "CPF must contain 11 digits", "");
            return;
        }
        if (!isInteger(cpf.getText())) {
            showError("Error", "CPF must contain just numbers", "");
            return;
        }
        UserRepository.createUser(cpf.getText(), password1.getText(), name.getText());
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

}
