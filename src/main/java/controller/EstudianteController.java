package controller;

import dao.EstudianteDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Estudiante;
import model.Usuario;

/**
 * Pantalla "Mi Perfil" para el rol Estudiante: permite ver y actualizar
 * los propios datos (incluida la contrasena). Reutiliza EstudianteDAO,
 * el mismo DAO que usa el Administrador para la gestion de usuarios.
 */
public class EstudianteController implements VistaConUsuario {

    @FXML private Label lblCorreo;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtCurso;
    @FXML private PasswordField txtNuevaContrasena;

    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private Estudiante estudianteActual;

    @Override
    public void setUsuarioActual(Usuario usuario) {
        if (usuario instanceof Estudiante estudiante) {
            this.estudianteActual = estudiante;
            lblCorreo.setText(estudiante.getCorreo());
            txtNombres.setText(estudiante.getNombres());
            txtApellidos.setText(estudiante.getApellidos());
            txtCurso.setText(estudiante.getCurso());
        }
    }

    @FXML
    private void guardarCambios() {
        if (txtNombres.getText().trim().isEmpty() || txtApellidos.getText().trim().isEmpty()
                || txtCurso.getText().trim().isEmpty()) {
            mostrarError("Ningun campo puede quedar vacio.");
            return;
        }

        estudianteActual.setNombres(txtNombres.getText().trim());
        estudianteActual.setApellidos(txtApellidos.getText().trim());
        estudianteActual.setCurso(txtCurso.getText().trim());

        boolean actualizado = estudianteDAO.actualizar(estudianteActual);

        String nuevaContrasena = txtNuevaContrasena.getText();
        if (!nuevaContrasena.isEmpty()) {
            if (nuevaContrasena.length() < 6) {
                mostrarError("La nueva contrasena debe tener al menos 6 caracteres.");
                return;
            }
            actualizado &= estudianteDAO.actualizarContrasena(estudianteActual.getId(), nuevaContrasena);
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
