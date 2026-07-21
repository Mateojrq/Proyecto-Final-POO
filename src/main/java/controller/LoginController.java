package controller;

import dao.UsuarioDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Usuario;
import util.Navegador;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void iniciarSesion(ActionEvent event) {
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Ingresa tu correo y tu contrasena.");
            return;
        }

        Usuario usuario = usuarioDAO.login(correo, contrasena);
        if (usuario == null) {
            mostrarError("Correo o contrasena incorrectos.");
            return;
        }

        try {
            Stage stage = (Stage) txtCorreo.getScene().getWindow();
            var loader = Navegador.cambiarEscena(stage, "/view/dashboard.fxml");
            DashboardController dashboard = loader.getController();
            dashboard.setUsuario(usuario);
            stage.setTitle("CNM - Centro de Nivelacion");
        } catch (IOException e) {
            mostrarError("No se pudo abrir el sistema: " + e.getMessage());
        }
    }

    @FXML
    private void irARegistro(ActionEvent event) {
        try {
            Stage stage = (Stage) txtCorreo.getScene().getWindow();
            Navegador.cambiarEscena(stage, "/view/registro.fxml");
            stage.setTitle("CNM - Crear cuenta");
        } catch (IOException e) {
            mostrarError("No se pudo abrir el registro: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}
