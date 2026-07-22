package controller;

import dao.EntregaDAO;
import dao.TareaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.Entrega;
import model.Tarea;
import model.Usuario;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

/**
 * Pantalla de Entregas. El Estudiante sube/edita/elimina sus propias
 * entregas; el Profesor consulta las entregas de sus tareas y las
 * califica; el Administrador solo consulta.
 */
public class EntregaController implements VistaConUsuario {

    @FXML private ComboBox<Tarea> cbTarea;
    @FXML private Label lblArchivo;
    @FXML private Button btnSeleccionarArchivo;
    @FXML private HBox filaCalificacionEditable;
    @FXML private TextField txtCalificacion;
    @FXML private TextArea txtComentario;
    @FXML private VBox boxSoloLecturaCalificacion;
    @FXML private Label lblCalificacionSoloLectura;
    @FXML private Label lblComentarioSoloLectura;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnDescargar;
    @FXML private Label lblModoSoloLectura;

    @FXML private TableView<Entrega> tabla;
    @FXML private TableColumn<Entrega, String> colTarea;
    @FXML private TableColumn<Entrega, String> colEstudiante;
    @FXML private TableColumn<Entrega, String> colFecha;
    @FXML private TableColumn<Entrega, String> colEstado;
    @FXML private TableColumn<Entrega, String> colCalificacion;

    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final TareaDAO tareaDAO = new TareaDAO();
    private final ObservableList<Entrega> datos = FXCollections.observableArrayList();

    private Usuario usuarioActual;
    private Entrega entregaSeleccionada;
    private File archivoElegido;

    @FXML
    public void initialize() {
        colTarea.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTituloTarea()));
        colEstudiante.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreEstudiante()));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaEntrega() != null ? d.getValue().getFechaEntrega().toLocalDate().toString() : ""));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));
        colCalificacion.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCalificacion() != null ? String.valueOf(d.getValue().getCalificacion()) : "-"));

        tabla.setItems(datos);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            entregaSeleccionada = actual;
            if (actual != null) {
                cargarEnFormulario(actual);
            }
        });
    }

    @Override
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        boolean estudiante = Usuario.ROL_ESTUDIANTE.equals(usuario.getRol());
        boolean profesor = Usuario.ROL_PROFESOR.equals(usuario.getRol());

        cbTarea.setVisible(estudiante);
        cbTarea.setManaged(estudiante);
        btnSeleccionarArchivo.setVisible(estudiante);
        btnSeleccionarArchivo.setManaged(estudiante);
        btnGuardar.setVisible(estudiante);
        btnGuardar.setManaged(estudiante);
        btnEliminar.setVisible(estudiante);
        btnEliminar.setManaged(estudiante);

        // El Profesor ve los campos EDITABLES de calificacion; Estudiante y
        // Administrador ven un texto de solo lectura (mas claro que un campo
        // deshabilitado, que parece un formulario roto).
        filaCalificacionEditable.setVisible(profesor);
        filaCalificacionEditable.setManaged(profesor);
        boxSoloLecturaCalificacion.setVisible(!profesor);
        boxSoloLecturaCalificacion.setManaged(!profesor);

        btnActualizar.setVisible(estudiante || profesor);
        btnActualizar.setManaged(estudiante || profesor);
        btnActualizar.setText(profesor ? "Guardar calificacion" : "Actualizar entrega");
        btnLimpiar.setVisible(estudiante || profesor);
        btnLimpiar.setManaged(estudiante || profesor);
        lblModoSoloLectura.setVisible(!estudiante && !profesor);
        lblModoSoloLectura.setManaged(!estudiante && !profesor);

        if (estudiante) {
            cbTarea.setItems(FXCollections.observableArrayList(tareaDAO.listar()));
        }

        cargarDatos();
    }

    private void cargarDatos() {
        datos.clear();
        switch (usuarioActual.getRol()) {
            case Usuario.ROL_ESTUDIANTE -> datos.addAll(entregaDAO.listarPorEstudiante(usuarioActual.getId()));
            case Usuario.ROL_PROFESOR -> datos.addAll(entregaDAO.listarPorProfesor(usuarioActual.getId()));
            default -> datos.addAll(entregaDAO.listar());
        }
    }

    private void cargarEnFormulario(Entrega entrega) {
        lblArchivo.setText(entrega.getNombreArchivo() != null ? entrega.getNombreArchivo() : "(sin archivo)");
        txtCalificacion.setText(entrega.getCalificacion() != null ? String.valueOf(entrega.getCalificacion()) : "");
        txtComentario.setText(entrega.getComentario() != null ? entrega.getComentario() : "");
        lblCalificacionSoloLectura.setText(entrega.getCalificacion() != null
                ? String.valueOf(entrega.getCalificacion()) : "Sin calificar todavia");
        lblComentarioSoloLectura.setText(entrega.getComentario() != null && !entrega.getComentario().isBlank()
                ? entrega.getComentario() : "(sin comentario del profesor)");
        archivoElegido = null;
        cbTarea.getItems().stream()
                .filter(t -> t.getId() == entrega.getIdTarea())
                .findFirst()
                .ifPresent(cbTarea::setValue);
    }

    @FXML
    private void seleccionarArchivo() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecciona el archivo de tu entrega");
        File archivo = chooser.showOpenDialog(lblArchivo.getScene().getWindow());
        if (archivo != null) {
            archivoElegido = archivo;
            lblArchivo.setText(archivo.getName());
        }
    }

    @FXML
    private void guardar() {
        if (cbTarea.getValue() == null) {
            mostrarError("Selecciona la tarea que vas a entregar.");
            return;
        }
        if (archivoElegido == null) {
            mostrarError("Selecciona el archivo que vas a subir.");
            return;
        }
        if (entregaDAO.existeEntrega(cbTarea.getValue().getId(), usuarioActual.getId())) {
            mostrarError("Ya registraste una entrega para esta tarea.");
            return;
        }

        Entrega entrega = new Entrega();
        entrega.setIdTarea(cbTarea.getValue().getId());
        entrega.setIdEstudiante(usuarioActual.getId());
        entrega.setComentario(txtComentario.getText().trim());
        if (!asignarArchivo(entrega)) {
            return;
        }

        if (entregaDAO.guardar(entrega)) {
            cargarDatos();
            limpiar();
        } else {
            mostrarError("No se pudo registrar la entrega.");
        }
    }

    @FXML
    private void actualizar() {
        if (entregaSeleccionada == null) {
            mostrarError("Selecciona primero una entrega de la tabla.");
            return;
        }

        if (Usuario.ROL_PROFESOR.equals(usuarioActual.getRol())) {
            actualizarComoProfesor();
        } else {
            actualizarComoEstudiante();
        }
    }

    private void actualizarComoProfesor() {
        String texto = txtCalificacion.getText().trim();
        if (texto.isEmpty()) {
            mostrarError("Ingresa una calificacion.");
            return;
        }
        double calificacion;
        try {
            calificacion = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            mostrarError("La calificacion debe ser un numero.");
            return;
        }
        if (calificacion < 0 || calificacion > 20) {
            mostrarError("La calificacion debe estar entre 0 y 20.");
            return;
        }

        if (entregaDAO.calificar(entregaSeleccionada.getId(), calificacion, txtComentario.getText().trim())) {
            cargarDatos();
            limpiar();
        } else {
            mostrarError("No se pudo guardar la calificacion.");
        }
    }

    private void actualizarComoEstudiante() {
        if (Entrega.ESTADO_CALIFICADO.equals(entregaSeleccionada.getEstado())) {
            mostrarError("Esta entrega ya fue calificada y no se puede modificar.");
            return;
        }
        if (!asignarArchivo(entregaSeleccionada)) {
            return;
        }
        if (entregaDAO.actualizar(entregaSeleccionada)) {
            cargarDatos();
            limpiar();
        } else {
            mostrarError("No se pudo actualizar la entrega.");
        }
    }

    @FXML
    private void eliminar() {
        if (entregaSeleccionada == null) {
            mostrarError("Selecciona primero una entrega de la tabla.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Seguro que deseas eliminar esta entrega?");
        confirmacion.getButtonTypes().setAll(
                new ButtonType("Si", ButtonData.YES),
                new ButtonType("No", ButtonData.NO));

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get().getButtonData() == ButtonData.YES) {
            if (entregaDAO.eliminar(entregaSeleccionada.getId())) {
                cargarDatos();
                limpiar();
            } else {
                mostrarError("No se pudo eliminar la entrega.");
            }
        }
    }

    @FXML
    private void descargarArchivo() {
        if (entregaSeleccionada == null || entregaSeleccionada.getArchivo() == null) {
            mostrarError("Esta entrega no tiene un archivo adjunto.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar archivo");
        chooser.setInitialFileName(entregaSeleccionada.getNombreArchivo());
        File destino = chooser.showSaveDialog(lblArchivo.getScene().getWindow());
        if (destino != null) {
            try {
                Files.write(destino.toPath(), entregaSeleccionada.getArchivo());
            } catch (IOException e) {
                mostrarError("No se pudo guardar el archivo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void limpiar() {
        cbTarea.setValue(null);
        lblArchivo.setText("(sin archivo)");
        txtCalificacion.clear();
        txtComentario.clear();
        lblCalificacionSoloLectura.setText("Sin calificar todavia");
        lblComentarioSoloLectura.setText("(sin comentario del profesor)");
        archivoElegido = null;
        entregaSeleccionada = null;
        tabla.getSelectionModel().clearSelection();
    }

    private boolean asignarArchivo(Entrega entrega) {
        if (archivoElegido != null) {
            try {
                entrega.setArchivo(Files.readAllBytes(archivoElegido.toPath()));
                entrega.setNombreArchivo(archivoElegido.getName());
            } catch (IOException e) {
                mostrarError("No se pudo leer el archivo seleccionado: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private void mostrarError(String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
    }
}