package model;

public class Estudiante extends Usuario {
    public Estudiante() { super(); }
    public Estudiante(int id, String nombre, String correo, String password) {
        super(id, nombre, correo, password, "ESTUDIANTE");
    }
    @Override
    public String obtenerAccesos() { return "Visualización y entrega de tareas."; }
}