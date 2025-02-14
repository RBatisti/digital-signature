module digital_signature{
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.ooxml;
    requires org.bouncycastle.provider;
    requires java.sql;
    requires kernel;
    requires io;
    requires java.desktop;

    opens main;
    opens controller to javafx.fxml;
    exports controller;
    exports main;
}