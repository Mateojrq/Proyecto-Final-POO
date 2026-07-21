package controller;

import dao.EstudianteDAO;
import dao.ProfesorDAO;
import dao.UsuarioDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Estudiante;
import model.Profesor;
import util.Navegador;

import java.io.IOException;

public class RegistroController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private PasswordField txtConfirmar;
    @FXML private ComboBox<String> cbRol;
    @FXML private TextField txtDatoExtra;      // especialidad (Profesor) o curso (Estudiante)
    @FXML private Label lblDatoExtra;
    @FXML private Label lblError;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ProfesorDAO profesorDAO = new ProfesorDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();

    @FXML
    public void initialize() {
        cbRol.getItems().addAll("Profesor", "Estudiante");
        cbRol.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            if ("Profesor".equals(actual)) {
                lblDatoExtra.setText("Especialidad:");
            } else if ("Estudiante".equals(actual)) {
                lblDatoExtra.setText("Curso / nivel:");
            }
        });
        cbRol.getSelectionModel().selectFirst();
    }

    @FXML
    private void registrar(ActionEvent event) {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText();
        String confirmar = txtConfirmar.getText();
        String rol = cbRol.getValue();
        String datoExtra = txtDatoExtra.getText().trim();

        // ---- Validaciones obligatorias ----
        if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty()
                || contrasena.isEmpty() || datoExtra.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }
        if (!correo.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            mostrarError("Ingresa un correo valido.");
            return;
        }
        if (contrasena.length() < 6) {
            mostrarError("La contrasena debe tener al menos 6 caracteres.");
            return;
        }
        if (!contrasena.equals(confirmar)) {
            mostrarError("Las contrasenas no coinciden.");
            return;
        }
        if (usuarioDAO.existeCorreo(correo)) {
            mostrarError("Ya existe una cuenta registrada con ese correo.");
            return;
        }

        boolean guardado;
        if ("Profesor".equals(rol)) {
            Profesor profesor = new Profesor(0, nombres, apellidos, correo, contrasena, datoExtra);
            guardado = profesorDAO.guardar(profesor);
        } else {
            Estudiante estudiante = new Estudiante(0, nombres, apellidos, correo, contrasena, datoExtra);
            guardado = estudianteDAO.guardar(estudiante);
        }

        if (!guardado) {
            mostrarError("No se pudo completar el registro. Intenta nuevamente.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cuenta creada correctamente. Ya puedes iniciar sesion.");
        alert.showAndWait();
        irALogin(event);
    }

    @FXML
    private void irALogin(ActionEvent event) {
        try {
            Stage stage = (Stage) txtNombres.getScene().getWindow();
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
