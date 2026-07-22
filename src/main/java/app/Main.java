package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        Parent raiz = loader.load();

        Scene escena = new Scene(raiz);
        escena.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());

        stage.setTitle("CNM - Iniciar sesion");
        stage.setScene(escena);
        stage.setMinWidth(1150);
        stage.setMinHeight(750);
        stage.setWidth(1280);
        stage.setHeight(820);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}