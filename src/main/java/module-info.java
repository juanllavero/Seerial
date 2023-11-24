module com.example.executablelauncher {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.media;
    requires org.jsoup;
    requires javafx.swing;
    requires com.google.gson;

    opens com.example.executablelauncher to javafx.fxml;
    exports com.example.executablelauncher;
}