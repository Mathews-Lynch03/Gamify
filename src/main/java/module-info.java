module org.example.gamify {
    requires javafx.controls;
    requires javafx.fxml;


    opens Java.gamify to javafx.fxml;

}