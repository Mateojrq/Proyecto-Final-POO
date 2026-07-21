package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Pequena utilidad reutilizada por Login, Registro y Dashboard para
 * cambiar la escena de la ventana principal sin repetir codigo.
 */
public final class Navegador {

    private Navegador() {
    }

    public static FXMLLoader cambiarEscena(Stage stage, String rutaFxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Navegador.class.getResource(rutaFxml));
        Parent raiz = loader.load();
        Scene escena = new Scene(raiz);
        escena.getStylesheets().add(Navegador.class.getResource("/css/estilos.css").toExternalForm());
        stage.setScene(escena);
        stage.centerOnScreen();
        return loader;
    }
}
