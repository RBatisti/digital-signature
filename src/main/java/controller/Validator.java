package controller;

import dao.DB;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import model.SignatureData;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.PublicKey;
import java.util.*;

import static dao.UserDAO.getName;
import static dao.UserDAO.getPublicKeys;
import static main.Main.changeScreen;
import static service.SignatureService.generateFileHash;
import static service.SignatureService.verifySignature;
import static utils.FileUtils.*;

public class Validator implements Initializable {
    private static File file;

    @FXML
    public Label labelStatus;

    @FXML
    private Label labelName;

    @FXML
    private Label labelCpf;

    @FXML
    private Label labelNumber;

    @FXML
    private Label labelDate;

    @FXML
    private ListView<String> listView;

    ArrayList<SignatureData> listViewData = new ArrayList<>();

    @Override
    public void initialize(URL arg0, ResourceBundle agr1) {
        listView.getSelectionModel().selectedItemProperty().addListener(this::changed);
    }

    @FXML
    public void importFile() {
        if(DB.withoutConnection()) {
            return;
        }

        boolean signed = false;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open your document");

        file = fileChooser.showOpenDialog(main.Main.stage);

        ArrayList<String> signatures = getSignatures(file);

        Map<String, PublicKey> cpfAndKeys = getPublicKeys();
        ArrayList<String> cpfs = new ArrayList<>(cpfAndKeys.keySet());

        int numberSignatures = 0;

        for (int i = 0; i < signatures.size(); i++) {
            byte[] timeBytes = getTime(file, i);
            for (int j = 0; j < cpfAndKeys.size(); j++) {
                if (verifySignature(generateFileHash(getOriginalFile(file)), timeBytes, signatures.get(i), cpfAndKeys.get(cpfs.get(j)))) {

                    SignatureData signatureData = new SignatureData(cpfs.get(j), getName(cpfs.get(j)), new String(timeBytes));
                    numberSignatures += 1;
                    labelNumber.setText("Number of signatures: " + numberSignatures);
                    labelNumber.setVisible(true);
                    if (numberSignatures == 1) {
                        signed = true;
                        labelStatus.setText("Status: signed");
                        labelName.setText("Name: " + signatureData.getName());
                        labelCpf.setText("CPF: " + signatureData.getCpf());
                        labelDate.setText("Date: " + signatureData.getDate());
                    }

                    listViewData.add(signatureData);
                    listView.getItems().add(signatureData.getName());
                    break;
                }
            }
        }
        if (!signed) {
            labelStatus.setText("Status: unsigned");
        }
    }

    public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
        if (listView.getSelectionModel().getSelectedIndex() != -1) {
            setElement(listView.getSelectionModel().getSelectedIndex());
        }
    }

    public void setElement(int index) {
        labelName.setText("Name: " + listViewData.get(index).getName());
        labelCpf.setText("CPF: " + listViewData.get(index).getCpf());
        labelDate.setText("Date: " + listViewData.get(index).getDate());
    }

    @FXML
    private void clear() {
        labelStatus.setText("Status: no file selected");
        labelNumber.setText("");
        labelNumber.setVisible(false);
        labelName.setText("");
        labelCpf.setText("");
        labelDate.setText("");
        listViewData = new ArrayList<>();
        listView.getItems().clear();
        file = null;
    }

    @FXML
    private void goToMain() {
        clear();
        changeScreen("main");
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
