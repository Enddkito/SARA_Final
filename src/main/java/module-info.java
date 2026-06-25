module com.example.sara_ap {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sara_ap to javafx.fxml;
    exports com.example.sara_ap;
    exports com.example.sara_ap.controller;
    opens com.example.sara_ap.controller to javafx.fxml;
}