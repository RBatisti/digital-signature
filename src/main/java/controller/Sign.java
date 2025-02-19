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

import static main.Main.changeScreen;
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
        fileChooser.setTitle("Open your document");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF file", "*.pdf"));

        selectedFile = fileChooser.showOpenDialog(main.Main.stage);

        if (selectedFile != null) {
            ArrayList<String> signatures = getSignatures(selectedFile);
            PublicKey publicKey = SessionManager.getInstance().getUser().getPublicKey();

            for (int i = 0; i < signatures.size(); i++) {
                byte[] timeBytes = getTime(selectedFile, i);
                if (verifySignature(generateFileHash(getOriginalFile(selectedFile)), timeBytes, signatures.get(i), publicKey)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("You can sign only once");
                    alert.setContentText("You have already sign this document");
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
        SessionManager.getInstance().loggout();
    }

    @FXML
    public void sign() {
        if (selectedFile == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("You need import first");
            alert.setContentText("You did not import the document");
            alert.showAndWait();
            return;
        }
        signature = signFile(selectedFile, SessionManager.getInstance().getUser().getPrivateKey());
        labelStatus.setText("Status: document signed");
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
            alert.setContentText("You did not sign the document");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save your document");

        String originalName = selectedFile.getName();
        int lastDotIndex = originalName.lastIndexOf(".");
        String baseName = (lastDotIndex == -1) ? originalName : originalName.substring(0, lastDotIndex);
        String extension = (lastDotIndex == -1) ? originalName : originalName.substring(lastDotIndex + 1);
        fileChooser.setInitialFileName(baseName + "_signed");

        if (extension.equalsIgnoreCase("pdf")) {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF file", "*.pdf"));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Document format not supported");
            alert.setContentText("Only PDF is accept");
            alert.showAndWait();
            return;
        }

        try {
            File file = fileChooser.showSaveDialog(main.Main.stage);
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
