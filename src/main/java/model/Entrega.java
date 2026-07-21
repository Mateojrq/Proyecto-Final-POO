package model;

import java.time.LocalDateTime;

/**
 * Entrega: relaciona un Estudiante con una Tarea (clave foranea a ambas).
 * El Estudiante la crea (sube archivo); el Profesor la califica.
 */
public class Entrega {

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_CALIFICADO = "CALIFICADO";

    private int id;
    private int idTarea;
    private String tituloTarea;        // solo lectura, viene de un JOIN con tareas
    private int idEstudiante;
    private String nombreEstudiante;   // solo lectura, viene de un JOIN con usuarios
    private byte[] archivo;
    private String nombreArchivo;
    private LocalDateTime fechaEntrega;
    private Double calificacion;
    private String comentario;
    private String estado;

    public Entrega() {
        this.estado = ESTADO_PENDIENTE;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdTarea() { return idTarea; }
    public void setIdTarea(int idTarea) { this.idTarea = idTarea; }

    public String getTituloTarea() { return tituloTarea; }
    public void setTituloTarea(String tituloTarea) { this.tituloTarea = tituloTarea; }

    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public byte[] getArchivo() { return archivo; }
    public void setArchivo(byte[] archivo) { this.archivo = archivo; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public Double getCalificacion() { return calificacion; }
    public void setCalificacion(Double calificacion) { this.calificacion = calificacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
