package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import session.SessionManager;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.PublicKey;
import java.util.ArrayList;

import static screencontroller.ScreenController.changeScreen;
import static screencontroller.ScreenController.getStage;
import static service.SignatureService.*;
import static utils.FileUtils.*;

public class Sign {
    @FXML
    private Label labelStatus;

    private File selectedFile;

    private byte[] signature;

    @FXML
    private void importFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open your file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF file", "*.pdf"));

        selectedFile = fileChooser.showOpenDialog(getStage());

        // Load all signatures
        if (selectedFile != null) {
            ArrayList<String> signatures = getSignatures(selectedFile);
            PublicKey publicKey = SessionManager.getInstance().getUser().getPublicKey();

            for (int i = 0; i < signatures.size(); i++) {
                byte[] timeBytes = getTime(selectedFile, i);
                if (verifySignature(generateFileHash(getOriginalFile(selectedFile)), timeBytes, signatures.get(i), publicKey)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("You can sign only once");
                    alert.setContentText("You have already sign this file");
                    alert.showAndWait();
                    selectedFile = null;
                    return;
                }
            }
        }
        labelStatus.setText("Status: file selected");
    }

    @FXML
    private void goToMain() {
        clear();
        changeScreen("main");
        SessionManager.getInstance().logout();
    }

    @FXML
    public void sign() {
        if (selectedFile == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("You need import first");
            alert.setContentText("You did not import the file");
            alert.showAndWait();
            return;
        }
        signature = signFile(selectedFile, SessionManager.getInstance().getUser().getPrivateKey());
        labelStatus.setText("Status: file signed");
    }

    @FXML
    public void clear() {
        selectedFile = null;
        signature = null;
        labelStatus.setText("Status: no file selected");
    }


    @FXML
    public void exportFile() {
        if (signature == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("You need to sign first");
            alert.setContentText("You did not sign the file");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save your file");

        // Defines the file name
        String originalName = selectedFile.getName();
        int lastDotIndex = originalName.lastIndexOf(".");
        String baseName = (lastDotIndex == -1) ? originalName : originalName.substring(0, lastDotIndex);
        String extension = (lastDotIndex == -1) ? originalName : originalName.substring(lastDotIndex + 1);
        fileChooser.setInitialFileName(baseName + "_signed");

        if (extension.equalsIgnoreCase("pdf")) {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF file", "*.pdf"));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("File format not supported");
            alert.setContentText("Only PDF is accept");
            alert.showAndWait();
            return;
        }

        // Save the file
        try {
            File file = fileChooser.showSaveDialog(getStage());
            if (file != null) {
                byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                Files.write(file.toPath(), fileContent);
                Files.write(file.toPath(), signature);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
