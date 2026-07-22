package controller;

import dao.UsuarioDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.Navegador;

import java.io.IOException;

public class VerificarController {

    @FXML private Label lblCorreoDestino;
    @FXML private TextField txtCodigo;
    @FXML private Label lblError;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private String correo;

    public void setCorreo(String correo) {
        this.correo = correo;
        lblCorreoDestino.setText(correo);
    }

    @FXML
    private void verificar(ActionEvent event) {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            mostrarError("Ingresa el codigo que recibiste por correo.");
            return;
        }
        if (usuarioDAO.verificarCodigo(correo, codigo)) {
            new Alert(Alert.AlertType.INFORMATION, "Correo verificado. Ya puedes iniciar sesion.").showAndWait();
            irALogin(event);
        } else {
            mostrarError("Codigo incorrecto. Revisa tu correo e intenta de nuevo.");
        }
    }

    @FXML
    private void reenviarCodigo(ActionEvent event) {
        if (usuarioDAO.generarYEnviarCodigo(correo, "")) {
            new Alert(Alert.AlertType.INFORMATION, "Te reenviamos un nuevo codigo a tu correo.").showAndWait();
        } else {
            mostrarError("No se pudo reenviar el codigo. Revisa tu conexion o la configuracion de correo.");
        }
    }

    @FXML
    private void irALogin(ActionEvent event) {
        try {
            Stage stage = (Stage) txtCodigo.getScene().getWindow();
            Navegador.cambiarEscena(stage, "/view/login.fxml");
            stage.setTitle("CNM - Iniciar sesion");
        } catch (IOException e) {
            mostrarError("No se pudo volver al login: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}