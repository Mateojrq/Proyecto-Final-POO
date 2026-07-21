package controller;

import dao.ProfesorDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Profesor;
import model.Usuario;

/**
 * Pantalla "Mi Perfil" para el rol Profesor: permite ver y actualizar
 * los propios datos (incluida la contrasena). Reutiliza ProfesorDAO,
 * el mismo DAO que usa el Administrador para la gestion de usuarios.
 */
public class ProfesorController implements VistaConUsuario {

    @FXML private Label lblCorreo;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEspecialidad;
    @FXML private PasswordField txtNuevaContrasena;

    private final ProfesorDAO profesorDAO = new ProfesorDAO();
    private Profesor profesorActual;

    @Override
    public void setUsuarioActual(Usuario usuario) {
        if (usuario instanceof Profesor profesor) {
            this.profesorActual = profesor;
            lblCorreo.setText(profesor.getCorreo());
            txtNombres.setText(profesor.getNombres());
            txtApellidos.setText(profesor.getApellidos());
            txtEspecialidad.setText(profesor.getEspecialidad());
        }
    }

    @FXML
    private void guardarCambios() {
        if (txtNombres.getText().trim().isEmpty() || txtApellidos.getText().trim().isEmpty()
                || txtEspecialidad.getText().trim().isEmpty()) {
            mostrarError("Ningun campo puede quedar vacio.");
            return;
        }

        profesorActual.setNombres(txtNombres.getText().trim());
        profesorActual.setApellidos(txtApellidos.getText().trim());
        profesorActual.setEspecialidad(txtEspecialidad.getText().trim());

        boolean actualizado = profesorDAO.actualizar(profesorActual);

        String nuevaContrasena = txtNuevaContrasena.getText();
        if (!nuevaContrasena.isEmpty()) {
            if (nuevaContrasena.length() < 6) {
                mostrarError("La nueva contrasena debe tener al menos 6 caracteres.");
                return;
            }
            actualizado &= profesorDAO.actualizarContrasena(profesorActual.getId(), nuevaContrasena);
            txtNuevaContrasena.clear();
        }

        if (actualizado) {
            new Alert(Alert.AlertType.INFORMATION, "Perfil actualizado correctamente.").showAndWait();
        } else {
            mostrarError("No se pudo actualizar el perfil.");
        }
    }

    private void mostrarError(String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
    }
}
