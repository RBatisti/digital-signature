package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import model.User;
import session.SessionManager;
import utils.Cryptography;

import java.io.File;

import static main.Main.changeScreen;

public class Sign {
    @FXML
    private Button buttonFile;

    @FXML
    private TextField code;


    @FXML
    private void buttonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open your document");

        File selectedFile = fileChooser.showOpenDialog(main.Main.stage);

        String hash = Cryptography.generateHashFile(selectedFile);

        System.out.println(Cryptography.generateHashFile(selectedFile));

        User user = SessionManager.getInstance().getUser();

        code.setText(Cryptography.sign(hash, user.getPrivateKey()));
    }

    @FXML
    private void goToMain() {
        changeScreen("main");
    }
}
