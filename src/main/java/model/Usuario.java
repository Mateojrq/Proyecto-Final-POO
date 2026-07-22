package model;

/**
 * Usuario del sistema: hereda de Persona y agrega los datos necesarios
 * para autenticacion (contrasena y rol). Es la clase padre directa de
 * Administrador, Profesor y Estudiante.
 */
public class Usuario extends Persona {

    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_PROFESOR = "PROFESOR";
    public static final String ROL_ESTUDIANTE = "ESTUDIANTE";

    private String contrasena;
    private String rol;
    private boolean verificado;

    public Usuario() {
        super();
    }

    public Usuario(int id, String nombres, String apellidos, String correo,
                   String contrasena, String rol) {
        super(id, nombres, apellidos, correo);
        this.contrasena = contrasena;
        this.rol = rol;
    }

    @Override
    public String getDescripcionRol() {
        return "Usuario del sistema CNM";
    }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isVerificado() { return verificado; }
    public void setVerificado(boolean verificado) { this.verificado = verificado; }
}