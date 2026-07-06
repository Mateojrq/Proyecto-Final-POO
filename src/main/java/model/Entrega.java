package model;

public class Entrega {
    private int idEntrega;
    private int idTarea;
    private int idEstudiante;
    private double calificacion;

    public Entrega() {}
    public Entrega(int idEntrega, int idTarea, int idEstudiante, double calificacion) {
        this.idEntrega = idEntrega;
        this.idTarea = idTarea;
        this.idEstudiante = idEstudiante;
        this.calificacion = calificacion;
    }
    public int getIdEntrega() { return idEntrega; }
    public void setIdEntrega(int idEntrega) { this.idEntrega = idEntrega; }
    public int getIdTarea() { return idTarea; }
    public void setIdTarea(int idTarea) { this.idTarea = idTarea; }
    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }
    public double getCalificacion() { return calificacion; }
    public void setCalificacion(double calificacion) { this.calificacion = calificacion; }
}