package controller;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.SignatureData;
import utils.Cryptography;
import utils.DataBase;
import utils.FileSigner;
import java.io.File;
import java.net.URL;
import java.security.PublicKey;
import java.util.*;

import static main.Main.changeScreen;

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
    private VBox vBoxStatus;

    @FXML
    private TextField signature;

    @FXML
    private ListView<String> listView;

    @FXML
    private Button buttonClear;

    @FXML
    private HBox hBox;


    ArrayList<SignatureData> listViewData = new ArrayList<>();

    @Override
    public void initialize(URL arg0, ResourceBundle agr1) {
        listView.getSelectionModel().selectedItemProperty().addListener(this::changed);
    }

    @FXML
    public void importFile() {
        Boolean signed = false;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open your document");

        file = fileChooser.showOpenDialog(main.Main.stage);

        ArrayList<String> signatures = FileSigner.getSignatures(file);

        Map<String, PublicKey> cpfAndKeys = DataBase.getPublicKeys();
        ArrayList<String> cpfs = new ArrayList<>(cpfAndKeys.keySet());

        int numberSignatures = 0;

        for (int i = 0; i < signatures.size(); i++) {
            byte[] timeBytes = FileSigner.getTime(file, i);
            for (int j = 0; j < cpfAndKeys.size(); j++) {
                if (Cryptography.verifySignature(Cryptography.generateFileHash(FileSigner.getOriginalFile(file)), timeBytes, signatures.get(i), cpfAndKeys.get(cpfs.get(j)))) {

                    SignatureData signatureData = new SignatureData(cpfs.get(j), DataBase.getName(cpfs.get(j)), new String(timeBytes));
                    System.out.println(cpfs.get(j));
                    numberSignatures += 1;
                    labelNumber.setText("Number of signatures: " + numberSignatures);
                    labelNumber.setVisible(true);
                    System.out.println("Number of signatures: " + numberSignatures);
                    if (numberSignatures == 1) {
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
}
