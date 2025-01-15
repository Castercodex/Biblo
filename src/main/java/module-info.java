module com.example.biblo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires password4j;
    requires java.desktop;

    opens com.example.biblo to javafx.fxml;
    exports com.example.biblo;
    exports controller;
    opens controller to javafx.fxml;
}