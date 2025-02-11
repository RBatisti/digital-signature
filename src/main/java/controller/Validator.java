package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.User;
import session.SessionManager;
import utils.Cryptography;
import utils.DataBase;

import java.io.File;
import java.security.PublicKey;
import java.util.Map;

import static main.Main.changeScreen;

public class Validator {
    private static String hash;

    @FXML
    private void goToMain() {
        changeScreen("main");
    }

    @FXML
    private Label labelStatus;

    @FXML
    private Label labelName;

    @FXML
    private Label labelCpf;

    @FXML
    private VBox vBoxStatus;

    @FXML
    private TextField signature;

    @FXML
    public void inputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open your document");

        File selectedFile = fileChooser.showOpenDialog(main.Main.stage);

        hash = Cryptography.generateHashFile(selectedFile);
    }

    @FXML
    public void clickButton() {
        if (getInfo(signature.getText(), hash) != (null)) {
            labelCpf.setText("CPF: " + getInfo(signature.getText(), hash));
            labelName.setText("Name: " + DataBase.getName(getInfo(signature.getText(), hash)));
            labelStatus.setText("Status: Signed");

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Signed");
            alert.setHeaderText("Validate with sucess");
            alert.setContentText("Name: tem que preencher" + "CPF: " + getInfo(signature.getText(), hash));
            alert.showAndWait();
        }
        System.out.println(getInfo(signature.getText(), hash));
    }

    public static String getInfo(String signature, String hash) {
        Map<String, PublicKey> publicKeys = DataBase.getPublicKeys();

        for (Map.Entry<String, PublicKey> entry : publicKeys.entrySet()) {
            if (Cryptography.verifySignature(hash, signature, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
