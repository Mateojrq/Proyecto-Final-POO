module ProyectoFinalPOO.CNM {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens app to javafx.graphics;
    opens controller to javafx.fxml;

    exports app;
    exports model;
    exports dao;
    exports db;
    exports util;
    exports controller;
}
