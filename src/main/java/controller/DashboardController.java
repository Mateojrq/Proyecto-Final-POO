package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.Usuario;
import util.Navegador;

import java.io.IOException;

/**
 * Controlador de la UNICA pantalla de dashboard (dashboard.fxml).
 * Reutilizacion de pantallas con POO: no existen tres dashboards distintos;
 * este mismo controlador decide, en base al objeto Usuario recibido via
 * setUsuario(), que botones se muestran y que vista se carga en el centro.
 */
public class DashboardController {

    @FXML private HBox headerBox;
    @FXML private Label lblBienvenida;
    @FXML private Label lblRol;
    @FXML private Button btnTareas;
    @FXML private Button btnEntregas;
    @FXML private Button btnAdministracion;
    @FXML private Button btnMiPerfil;
    @FXML private StackPane contentPane;

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        lblBienvenida.setText("Bienvenido, " + usuario.getNombreCompleto());
        lblRol.setText(usuario.getDescripcionRol());

        headerBox.getStyleClass().removeAll("header-admin", "header-profesor", "header-estudiante");

        switch (usuario.getRol()) {
            case Usuario.ROL_ADMIN -> {
                headerBox.getStyleClass().add("header-admin");
                mostrarBoton(btnAdministracion, true);
                mostrarBoton(btnMiPerfil, false);
            }
            case Usuario.ROL_PROFESOR -> {
                headerBox.getStyleClass().add("header-profesor");
                mostrarBoton(btnAdministracion, false);
                mostrarBoton(btnMiPerfil, true);
            }
            case Usuario.ROL_ESTUDIANTE -> {
                headerBox.getStyleClass().add("header-estudiante");
                mostrarBoton(btnAdministracion, false);
                mostrarBoton(btnMiPerfil, true);
            }
            default -> {
                mostrarBoton(btnAdministracion, false);
                mostrarBoton(btnMiPerfil, false);
            }
        }

        mostrarTareas();
    }

    private void mostrarBoton(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
    }

    @FXML
    private void mostrarTareas() {
        cargarVista("/view/tarea.fxml");
    }

    @FXML
    private void mostrarEntregas() {
        cargarVista("/view/entrega.fxml");
    }

    @FXML
    private void mostrarAdministracion() {
        cargarVista("/view/administrador.fxml");
    }

    @FXML
    private void mostrarMiPerfil() {
        if (Usuario.ROL_PROFESOR.equals(usuarioActual.getRol())) {
            cargarVista("/view/profesor.fxml");
        } else if (Usuario.ROL_ESTUDIANTE.equals(usuarioActual.getRol())) {
            cargarVista("/view/estudiante.fxml");
        }
    }

    private void cargarVista(String ruta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent vista = loader.load();

            Object controlador = loader.getController();
            if (controlador instanceof VistaConUsuario vistaConUsuario) {
                vistaConUsuario.setUsuarioActual(usuarioActual);
            }

            contentPane.getChildren().setAll(vista);
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista: " + e.getMessage());
        }
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            Stage stage = (Stage) headerBox.getScene().getWindow();
            Navegador.cambiarEscena(stage, "/view/login.fxml");
            stage.setTitle("CNM - Iniciar sesion");
        } catch (IOException e) {
            mostrarError("No se pudo cerrar sesion: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
    }
}
