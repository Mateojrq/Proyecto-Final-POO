module com.ejcrud.proyectfinalpoo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ejcrud.proyectfinalpoo to javafx.fxml;
    exports com.ejcrud.proyectfinalpoo;
}