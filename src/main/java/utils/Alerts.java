package utils;

import javafx.scene.control.Alert;

public class Alerts {
    public static void showMessage(String title, String header, String message, Alert.AlertType type) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
