module org.example.gamify {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.gamify to javafx.fxml;
    exports org.example.gamify;
}