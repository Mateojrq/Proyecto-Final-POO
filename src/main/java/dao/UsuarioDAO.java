package dao;

import db.Conexion;
import model.Administrador;
import model.Estudiante;
import model.Profesor;
import model.Usuario;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO especializado en autenticacion. No implementa ICRUD porque su
 * responsabilidad no es un CRUD de un recurso, sino validar credenciales
 * y apoyar el registro. El alta/edicion/borrado de cada tipo de usuario
 * vive en su DAO especifico (AdministradorDAO, ProfesorDAO, EstudianteDAO),
 * cada uno implementando ICRUD.
 */
public class UsuarioDAO {

    /**
     * Verifica credenciales contra la tabla usuarios y devuelve el objeto
     * concreto correspondiente (Administrador, Profesor o Estudiante).
     * Esto es POLIMORFISMO aplicado a la carga de datos: el metodo declara
     * que devuelve un Usuario, pero en tiempo de ejecucion el objeto real
     * es de una subclase distinta segun el rol guardado en la BD.
     */
    public Usuario login(String correo, String contrasenaPlano) {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashGuardado = rs.getString("contrasena");
                    if (PasswordUtil.verificar(contrasenaPlano, hashGuardado)) {
                        return mapearUsuario(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error en login: " + e.getMessage());
        }
        return null;
    }

    public boolean existeCorreo(String correo) {
        String sql = "SELECT 1 FROM usuarios WHERE correo = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al validar correo: " + e.getMessage());
            return true; // ante la duda, no dejar continuar el registro
        }
    }

    /**
     * Fabrica el objeto correcto (Administrador/Profesor/Estudiante) a
     * partir de una fila de la tabla usuarios.
     */
    static Usuario mapearUsuario(ResultSet rs) throws SQLException {
        String rol = rs.getString("rol");
        int id = rs.getInt("id");
        String nombres = rs.getString("nombres");
        String apellidos = rs.getString("apellidos");
        String correo = rs.getString("correo");
        String contrasena = rs.getString("contrasena");

        return switch (rol) {
            case Usuario.ROL_ADMIN -> new Administrador(id, nombres, apellidos, correo, contrasena);
            case Usuario.ROL_PROFESOR -> new Profesor(id, nombres, apellidos, correo, contrasena, rs.getString("especialidad"));
            case Usuario.ROL_ESTUDIANTE -> new Estudiante(id, nombres, apellidos, correo, contrasena, rs.getString("curso"));
            default -> new Usuario(id, nombres, apellidos, correo, contrasena, rol);
        };
    }
}
