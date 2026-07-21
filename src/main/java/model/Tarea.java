package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tarea (deber) publicada por un Profesor. Es el "recurso principal"
 * del sistema segun la tematica CNM (centro de nivelacion).
 */
public class Tarea {

    private int id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaEntrega;
    private int idProfesor;
    private String nombreProfesor;   // dato de solo lectura, viene de un JOIN con usuarios
    private byte[] archivo;
    private String nombreArchivo;
    private LocalDateTime fechaCreacion;

    public Tarea() {
    }

    public Tarea(int id, String titulo, String descripcion, LocalDate fechaEntrega, int idProfesor) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
        this.idProfesor = idProfesor;
    }

    @Override
    public String toString() {
        // usado por el ComboBox en la pantalla de Entregas
        return titulo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public int getIdProfesor() { return idProfesor; }
    public void setIdProfesor(int idProfesor) { this.idProfesor = idProfesor; }

    public String getNombreProfesor() { return nombreProfesor; }
    public void setNombreProfesor(String nombreProfesor) { this.nombreProfesor = nombreProfesor; }

    public byte[] getArchivo() { return archivo; }
    public void setArchivo(byte[] archivo) { this.archivo = archivo; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
