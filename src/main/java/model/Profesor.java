package model;

/**
 * Profesor: crea y administra Tareas (deberes) y califica las Entregas
 * de sus estudiantes. POLIMORFISMO: sobreescribe getDescripcionRol().
 */
public class Profesor extends Usuario {

    private String especialidad;

    public Profesor() {
        super();
        setRol(ROL_PROFESOR);
    }

    public Profesor(int id, String nombres, String apellidos, String correo,
                    String contrasena, String especialidad) {
        super(id, nombres, apellidos, correo, contrasena, ROL_PROFESOR);
        this.especialidad = especialidad;
    }

    @Override
    public String getDescripcionRol() {
        return "Profesor - gestiona tareas y califica entregas";
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}
