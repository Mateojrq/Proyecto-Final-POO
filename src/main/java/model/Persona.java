package model;

import java.io.Serializable;

/**
 * Clase base ABSTRACTA de la jerarquia de personas del sistema CNM.
 * <p>
 * Pilar HERENCIA: Usuario extiende de esta clase, y a su vez Administrador,
 * Profesor y Estudiante extienden de Usuario. Todos heredan estos atributos
 * y este comportamiento comun.
 * <p>
 * Pilar ENCAPSULAMIENTO: todos los atributos son privados; el unico acceso
 * es a traves de los metodos get/set.
 */
public abstract class Persona implements Serializable {

    private int id;
    private String nombres;
    private String apellidos;
    private String correo;

    public Persona() {
    }

    public Persona(int id, String nombres, String apellidos, String correo) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
    }

    /**
     * Metodo ABSTRACTO: cada subclase concreta decide como describe su
     * propio rol dentro del sistema. Esto es lo que permite el POLIMORFISMO:
     * el mismo mensaje (getDescripcionRol) produce una respuesta distinta
     * segun el tipo real del objeto en tiempo de ejecucion.
     */
    public abstract String getDescripcionRol();

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    // ----------------- Encapsulamiento: get/set -----------------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}
