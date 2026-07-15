module com.ejcrud.proyectfinalpoo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens controller to javafx.fxml;
    opens app to javafx.fxml;

    exports app;
}