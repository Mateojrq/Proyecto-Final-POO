package controller;

import dao.TareaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.FileChooser;
import model.Tarea;
import model.Usuario;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Pantalla CRUD de Tareas (deberes). El MISMO fxml/controlador se reutiliza
 * para los tres roles: el Profesor tiene CRUD completo sobre sus propias
 * tareas; Estudiante y Administrador solo pueden consultar y descargar.
 */
public class TareaController implements VistaConUsuario {

    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private DatePicker dpFechaEntrega;
    @FXML private Label lblArchivo;
    @FXML private Button btnSeleccionarArchivo;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnDescargar;
    @FXML private Label lblModoSoloLectura;

    @FXML private TableView<Tarea> tabla;
    @FXML private TableColumn<Tarea, String> colTitulo;
    @FXML private TableColumn<Tarea, String> colProfesor;
    @FXML private TableColumn<Tarea, String> colFecha;
    @FXML private TableColumn<Tarea, String> colArchivo;

    private final TareaDAO tareaDAO = new TareaDAO();
    private final ObservableList<Tarea> datos = FXCollections.observableArrayList();

    private Usuario usuarioActual;
    private Tarea tareaSeleccionada;
    private File archivoElegido;

    @FXML
    public void initialize() {
        colTitulo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitulo()));
        colProfesor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreProfesor()));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getFechaEntrega())));
        colArchivo.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getNombreArchivo() != null ? d.getValue().getNombreArchivo() : "(sin archivo)"));

        tabla.setItems(datos);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            tareaSeleccionada = actual;
            if (actual != null) {
                cargarEnFormulario(actual);
            }
        });
    }

    @Override
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        boolean esProfesor = Usuario.ROL_PROFESOR.equals(usuario.getRol());

        txtTitulo.setDisable(!esProfesor);
        txtDescripcion.setDisable(!esProfesor);
        dpFechaEntrega.setDisable(!esProfesor);
        btnSeleccionarArchivo.setVisible(esProfesor);
        btnSeleccionarArchivo.setManaged(esProfesor);
        btnGuardar.setVisible(esProfesor);
        btnGuardar.setManaged(esProfesor);
        btnActualizar.setVisible(esProfesor);
        btnActualizar.setManaged(esProfesor);
        btnEliminar.setVisible(esProfesor);
        btnEliminar.setManaged(esProfesor);
        btnLimpiar.setVisible(esProfesor);
        btnLimpiar.setManaged(esProfesor);
        lblModoSoloLectura.setVisible(!esProfesor);
        lblModoSoloLectura.setManaged(!esProfesor);

        cargarDatos();
    }

    private void cargarDatos() {
        datos.clear();
        if (Usuario.ROL_PROFESOR.equals(usuarioActual.getRol())) {
            datos.addAll(tareaDAO.listarPorProfesor(usuarioActual.getId()));
        } else {
            datos.addAll(tareaDAO.listar());
        }
    }

    private void cargarEnFormulario(Tarea tarea) {
        txtTitulo.setText(tarea.getTitulo());
        txtDescripcion.setText(tarea.getDescripcion());
        dpFechaEntrega.setValue(tarea.getFechaEntrega());
        lblArchivo.setText(tarea.getNombreArchivo() != null ? tarea.getNombreArchivo() : "(sin archivo)");
        archivoElegido = null;
    }

    @FXML
    private void seleccionarArchivo() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecciona el archivo del deber");
        File archivo = chooser.showOpenDialog(txtTitulo.getScene().getWindow());
        if (archivo != null) {
            archivoElegido = archivo;
            lblArchivo.setText(archivo.getName());
        }
    }

    @FXML
    private void guardar() {
        if (!validarCampos()) {
            return;
        }
        Tarea tarea = new Tarea(0, txtTitulo.getText().trim(), txtDescripcion.getText().trim(),
                dpFechaEntrega.getValue(), usuarioActual.getId());
        if (!asignarArchivoSiExiste(tarea)) {
            return;
        }

        if (tareaDAO.guardar(tarea)) {
            cargarDatos();
            limpiar();
        } else {
            mostrarError("No se pudo guardar la tarea.");
        }
    }

    @FXML
    private void actualizar() {
        if (tareaSeleccionada == null) {
            mostrarError("Selecciona primero una tarea de la tabla.");
            return;
        }
        if (!validarCampos()) {
            return;
        }
        tareaSeleccionada.setTitulo(txtTitulo.getText().trim());
        tareaSeleccionada.setDescripcion(txtDescripcion.getText().trim());
        tareaSeleccionada.setFechaEntrega(dpFechaEntrega.getValue());
        if (!asignarArchivoSiExiste(tareaSeleccionada)) {
            return;
        }

        if (tareaDAO.actualizar(tareaSeleccionada)) {
            cargarDatos();
            limpiar();
        } else {
            mostrarError("No se pudo actualizar la tarea.");
        }
    }

    @FXML
    private void eliminar() {
        if (tareaSeleccionada == null) {
            mostrarError("Selecciona primero una tarea de la tabla.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas eliminar la tarea \"" + tareaSeleccionada.getTitulo() + "\"?");
        confirmacion.getButtonTypes().setAll(
                new ButtonType("Si", ButtonData.YES),
                new ButtonType("No", ButtonData.NO));

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get().getButtonData() == ButtonData.YES) {
            if (tareaDAO.eliminar(tareaSeleccionada.getId())) {
                cargarDatos();
                limpiar();
            } else {
                mostrarError("No se pudo eliminar la tarea.");
            }
        }
    }

    @FXML
    private void descargarArchivo() {
        if (tareaSeleccionada == null || tareaSeleccionada.getArchivo() == null) {
            mostrarError("Esta tarea no tiene un archivo adjunto.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar archivo");
        chooser.setInitialFileName(tareaSeleccionada.getNombreArchivo());
        File destino = chooser.showSaveDialog(txtTitulo.getScene().getWindow());
        if (destino != null) {
            try {
                Files.write(destino.toPath(), tareaSeleccionada.getArchivo());
            } catch (IOException e) {
                mostrarError("No se pudo guardar el archivo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void limpiar() {
        txtTitulo.clear();
        txtDescripcion.clear();
        dpFechaEntrega.setValue(null);
        lblArchivo.setText("(sin archivo)");
        archivoElegido = null;
        tareaSeleccionada = null;
        tabla.getSelectionModel().clearSelection();
    }

    private boolean asignarArchivoSiExiste(Tarea tarea) {
        if (archivoElegido != null) {
            try {
                tarea.setArchivo(Files.readAllBytes(archivoElegido.toPath()));
                tarea.setNombreArchivo(archivoElegido.getName());
            } catch (IOException e) {
                mostrarError("No se pudo leer el archivo seleccionado: " + e.getMessage());
                return false;
            }
        } else if (tareaSeleccionada != null) {
            tarea.setArchivo(tareaSeleccionada.getArchivo());
            tarea.setNombreArchivo(tareaSeleccionada.getNombreArchivo());
        }
        return true;
    }

    private boolean validarCampos() {
        if (txtTitulo.getText().trim().isEmpty() || txtDescripcion.getText().trim().isEmpty()) {
            mostrarError("El titulo y la descripcion no pueden quedar vacios.");
            return false;
        }
        if (dpFechaEntrega.getValue() == null) {
            mostrarError("Selecciona la fecha de entrega.");
            return false;
        }
        if (dpFechaEntrega.getValue().isBefore(LocalDate.now())) {
            mostrarError("La fecha de entrega no puede ser anterior a hoy.");
            return false;
        }
        return true;
    }

    private void mostrarError(String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
    }
}
