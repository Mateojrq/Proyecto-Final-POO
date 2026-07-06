package model;

public class Tarea {
    private int idTarea;
    private String titulo;
    private String descripcion;
    private int idProfesor;

    public Tarea() {}
    public Tarea(int idTarea, String titulo, String descripcion, int idProfesor) {
        this.idTarea = idTarea;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.idProfesor = idProfesor;
    }
    public int getIdTarea() { return idTarea; }
    public void setIdTarea(int idTarea) { this.idTarea = idTarea; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getIdProfesor() { return idProfesor; }
    public void setIdProfesor(int idProfesor) { this.idProfesor = idProfesor; }
}