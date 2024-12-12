module com.example.powerflowcontroller {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.example.powerflowcontroller to javafx.fxml;
    exports com.example.powerflowcontroller;
}