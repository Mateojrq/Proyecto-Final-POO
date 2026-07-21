package model;

/**
 * Administrador: rol con acceso total al sistema (gestion de usuarios,
 * reportes y configuracion). POLIMORFISMO: sobreescribe getDescripcionRol().
 */
public class Administrador extends Usuario {

    public Administrador() {
        super();
        setRol(ROL_ADMIN);
    }

    public Administrador(int id, String nombres, String apellidos, String correo, String contrasena) {
        super(id, nombres, apellidos, correo, contrasena, ROL_ADMIN);
    }

    @Override
    public String getDescripcionRol() {
        return "Administrador - acceso total al sistema";
    }
}
