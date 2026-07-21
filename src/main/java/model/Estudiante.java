package model;

/**
 * Estudiante: consulta las Tareas asignadas y sube sus propias Entregas.
 * POLIMORFISMO: sobreescribe getDescripcionRol().
 */
public class Estudiante extends Usuario {

    private String curso;

    public Estudiante() {
        super();
        setRol(ROL_ESTUDIANTE);
    }

    public Estudiante(int id, String nombres, String apellidos, String correo,
                      String contrasena, String curso) {
        super(id, nombres, apellidos, correo, contrasena, ROL_ESTUDIANTE);
        this.curso = curso;
    }

    @Override
    public String getDescripcionRol() {
        return "Estudiante - entrega tareas asignadas";
    }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
}
