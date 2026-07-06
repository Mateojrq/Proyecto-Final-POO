package model;

public class Profesor extends Usuario {
    private String especialidad;
    public Profesor() { super(); }
    public Profesor(int id, String nombre, String correo, String password, String especialidad) {
        super(id, nombre, correo, password, "PROFESOR");
        this.especialidad = especialidad;
    }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    @Override
    public String obtenerAccesos() { return "Gestión de tareas y calificaciones."; }
}