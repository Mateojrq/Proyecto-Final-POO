package model;

public class Administrador extends Usuario {
    public Administrador() { super(); }
    public Administrador(int id, String nombre, String correo, String password) {
        super(id, nombre, correo, password, "ADMIN");
    }
    @Override
    public String obtenerAccesos() { return "Acceso total al sistema."; }
}