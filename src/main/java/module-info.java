module com.example.hangar {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.hangar to javafx.fxml;
    exports com.example.hangar;
}