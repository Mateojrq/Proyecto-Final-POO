package controller;

import dao.EntregaDAO;
import dao.EstudianteDAO;
import dao.ProfesorDAO;
import dao.TareaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.FileChooser;
import model.Estudiante;
import model.Profesor;
import model.Usuario;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Pantalla de administracion (solo visible para el rol ADMIN).
 * Contiene 3 secciones dentro de un TabPane: Gestion de Usuarios,
 * Reportes y Configuracion.
 */
public class AdministradorController implements VistaConUsuario {

    // ---- Tab Usuarios ----
    @FXML private ComboBox<String> cbTipoUsuario;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblDatoExtra;
    @FXML private TextField txtDatoExtra;
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colDato;

    // ---- Tab Reportes ----
    @FXML private Label lblTotalProfesores;
    @FXML private Label lblTotalEstudiantes;
    @FXML private Label lblTotalTareas;
    @FXML private Label lblTotalEntregas;
    @FXML private Label lblEntregasCalificadas;
    @FXML private Label lblPromedio;

    // ---- Tab Configuracion ----
    @FXML private TextField txtNombreCentro;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtTelefono;

    private final ProfesorDAO profesorDAO = new ProfesorDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final TareaDAO tareaDAO = new TareaDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();

    private final ObservableList<Usuario> datosUsuarios = FXCollections.observableArrayList();
    private Usuario usuarioSeleccionado;

    @FXML
    public void initialize() {
        cbTipoUsuario.getItems().addAll("Profesor", "Estudiante");
        cbTipoUsuario.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            lblDatoExtra.setText("Profesor".equals(actual) ? "Especialidad:" : "Curso / nivel:");
            cargarListaUsuarios();
        });

        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreCompleto()));
        colCorreo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCorreo()));
        colDato.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue() instanceof Profesor p ? p.getEspecialidad()
                        : d.getValue() instanceof Estudiante e ? e.getCurso() : ""));

        tablaUsuarios.setItems(datosUsuarios);
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            usuarioSeleccionado = actual;
            if (actual != null) {
                cargarEnFormulario(actual);
            }
        });

        cbTipoUsuario.getSelectionModel().selectFirst();
        cargarConfiguracion();
    }

    @Override
    public void setUsuarioActual(Usuario usuario) {
        // Esta pantalla solo la usa el Administrador; no necesita distinguir roles.
        cargarListaUsuarios();
        cargarReportes();
    }

    // ================= Gestion de usuarios =================

    private void cargarListaUsuarios() {
        datosUsuarios.clear();
        if ("Profesor".equals(cbTipoUsuario.getValue())) {
            datosUsuarios.addAll(profesorDAO.listar());
        } else {
            datosUsuarios.addAll(estudianteDAO.listar());
        }
    }

    private void cargarEnFormulario(Usuario usuario) {
        txtNombres.setText(usuario.getNombres());
        txtApellidos.setText(usuario.getApellidos());
        txtCorreo.setText(usuario.getCorreo());
        txtContrasena.clear();
        if (usuario instanceof Profesor p) {
            txtDatoExtra.setText(p.getEspecialidad());
        } else if (usuario instanceof Estudiante e) {
            txtDatoExtra.setText(e.getCurso());
        }
    }

    @FXML
    private void guardarUsuario() {
        if (!validarCampos(true)) {
            return;
        }
        boolean guardado;
        if ("Profesor".equals(cbTipoUsuario.getValue())) {
            Profesor profesor = new Profesor(0, txtNombres.getText().trim(), txtApellidos.getText().trim(),
                    txtCorreo.getText().trim(), txtContrasena.getText(), txtDatoExtra.getText().trim());
            guardado = profesorDAO.guardar(profesor);
        } else {
            Estudiante estudiante = new Estudiante(0, txtNombres.getText().trim(), txtApellidos.getText().trim(),
                    txtCorreo.getText().trim(), txtContrasena.getText(), txtDatoExtra.getText().trim());
            guardado = estudianteDAO.guardar(estudiante);
        }

        if (guardado) {
            cargarListaUsuarios();
            limpiarFormularioUsuario();
        } else {
            mostrarError("No se pudo guardar el usuario. Verifica que el correo no este repetido.");
        }
    }

    @FXML
    private void actualizarUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarError("Selecciona primero un usuario de la tabla.");
            return;
        }
        if (!validarCampos(false)) {
            return;
        }

        usuarioSeleccionado.setNombres(txtNombres.getText().trim());
        usuarioSeleccionado.setApellidos(txtApellidos.getText().trim());
        usuarioSeleccionado.setCorreo(txtCorreo.getText().trim());

        boolean actualizado;
        if (usuarioSeleccionado instanceof Profesor profesor) {
            profesor.setEspecialidad(txtDatoExtra.getText().trim());
            actualizado = profesorDAO.actualizar(profesor);
        } else if (usuarioSeleccionado instanceof Estudiante estudiante) {
            estudiante.setCurso(txtDatoExtra.getText().trim());
            actualizado = estudianteDAO.actualizar(estudiante);
        } else {
            actualizado = false;
        }

        if (actualizado) {
            cargarListaUsuarios();
            limpiarFormularioUsuario();
        } else {
            mostrarError("No se pudo actualizar el usuario.");
        }
    }

    @FXML
    private void eliminarUsuario() {
        if (usuarioSeleccionado == null) {
            mostrarError("Selecciona primero un usuario de la tabla.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas eliminar a " + usuarioSeleccionado.getNombreCompleto() + "?");
        confirmacion.getButtonTypes().setAll(
                new ButtonType("Si", ButtonData.YES),
                new ButtonType("No", ButtonData.NO));

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get().getButtonData() != ButtonData.YES) {
            return;
        }

        boolean eliminado = usuarioSeleccionado instanceof Profesor
                ? profesorDAO.eliminar(usuarioSeleccionado.getId())
                : estudianteDAO.eliminar(usuarioSeleccionado.getId());

        if (eliminado) {
            cargarListaUsuarios();
            limpiarFormularioUsuario();
        } else {
            mostrarError("No se pudo eliminar el usuario.");
        }
    }

    @FXML
    private void limpiarFormularioUsuario() {
        txtNombres.clear();
        txtApellidos.clear();
        txtCorreo.clear();
        txtContrasena.clear();
        txtDatoExtra.clear();
        usuarioSeleccionado = null;
        tablaUsuarios.getSelectionModel().clearSelection();
    }

    private boolean validarCampos(boolean esNuevo) {
        if (txtNombres.getText().trim().isEmpty() || txtApellidos.getText().trim().isEmpty()
                || txtCorreo.getText().trim().isEmpty() || txtDatoExtra.getText().trim().isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return false;
        }
        if (esNuevo && txtContrasena.getText().length() < 6) {
            mostrarError("La contrasena debe tener al menos 6 caracteres.");
            return false;
        }
        return true;
    }

    // ================= Reportes =================

    private void cargarReportes() {
        List<?> profesores = profesorDAO.listar();
        List<?> estudiantes = estudianteDAO.listar();
        List<?> tareas = tareaDAO.listar();
        var entregas = entregaDAO.listar();

        long calificadas = entregas.stream().filter(e -> e.getCalificacion() != null).count();
        double promedio = entregas.stream()
                .filter(e -> e.getCalificacion() != null)
                .mapToDouble(e -> e.getCalificacion())
                .average().orElse(0.0);

        lblTotalProfesores.setText(String.valueOf(profesores.size()));
        lblTotalEstudiantes.setText(String.valueOf(estudiantes.size()));
        lblTotalTareas.setText(String.valueOf(tareas.size()));
        lblTotalEntregas.setText(String.valueOf(entregas.size()));
        lblEntregasCalificadas.setText(calificadas + " / " + entregas.size());
        lblPromedio.setText(String.format("%.2f", promedio));
    }

    @FXML
    private void descargarReporte() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar reporte");
        chooser.setInitialFileName("reporte_cnm.txt");
        File destino = chooser.showSaveDialog(lblTotalProfesores.getScene().getWindow());
        if (destino == null) {
            return;
        }
        try (FileWriter writer = new FileWriter(destino)) {
            writer.write("REPORTE CNM - Centro de Nivelacion\n");
            writer.write("Generado: " + LocalDateTime.now() + "\n\n");
            writer.write("Total de profesores: " + lblTotalProfesores.getText() + "\n");
            writer.write("Total de estudiantes: " + lblTotalEstudiantes.getText() + "\n");
            writer.write("Total de tareas: " + lblTotalTareas.getText() + "\n");
            writer.write("Total de entregas: " + lblTotalEntregas.getText() + "\n");
            writer.write("Entregas calificadas: " + lblEntregasCalificadas.getText() + "\n");
            writer.write("Promedio de calificaciones: " + lblPromedio.getText() + "\n");
        } catch (IOException e) {
            mostrarError("No se pudo generar el reporte: " + e.getMessage());
        }
    }

    // ================= Configuracion =================

    private void cargarConfiguracion() {
        Properties props = new Properties();
        File archivo = new File("config.properties");
        if (archivo.exists()) {
            try (InputStream in = Files.newInputStream(archivo.toPath())) {
                props.load(in);
                txtNombreCentro.setText(props.getProperty("nombreCentro", ""));
                txtDireccion.setText(props.getProperty("direccion", ""));
                txtTelefono.setText(props.getProperty("telefono", ""));
            } catch (IOException e) {
                System.err.println("No se pudo leer config.properties: " + e.getMessage());
            }
        } else {
            txtNombreCentro.setText("CNM - Centro de Nivelacion");
        }
    }

    @FXML
    private void guardarConfiguracion() {
        Properties props = new Properties();
        props.setProperty("nombreCentro", txtNombreCentro.getText().trim());
        props.setProperty("direccion", txtDireccion.getText().trim());
        props.setProperty("telefono", txtTelefono.getText().trim());

        try (OutputStream out = Files.newOutputStream(new File("config.properties").toPath())) {
            props.store(out, "Configuracion CNM");
            new Alert(Alert.AlertType.INFORMATION, "Configuracion guardada.").showAndWait();
        } catch (IOException e) {
            mostrarError("No se pudo guardar la configuracion: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
    }
}
