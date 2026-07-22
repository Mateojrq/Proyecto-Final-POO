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

        Usuario usuario = switch (rol) {
            case Usuario.ROL_ADMIN -> new Administrador(id, nombres, apellidos, correo, contrasena);
            case Usuario.ROL_PROFESOR -> new Profesor(id, nombres, apellidos, correo, contrasena, rs.getString("especialidad"));
            case Usuario.ROL_ESTUDIANTE -> new Estudiante(id, nombres, apellidos, correo, contrasena, rs.getString("curso"));
            default -> new Usuario(id, nombres, apellidos, correo, contrasena, rol);
        };
        usuario.setVerificado(rs.getBoolean("verificado"));
        return usuario;
    }

    /**
     * Genera un codigo aleatorio de 6 digitos, lo guarda en la BD y lo
     * envia por correo. Se usa tanto en el registro como para reenviar.
     */
    public boolean generarYEnviarCodigo(String correo, String nombres) {
        String codigo = String.format("%06d", new java.util.Random().nextInt(1_000_000));
        String sql = "UPDATE usuarios SET codigo_verificacion = ? WHERE correo = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setString(2, correo);
            if (ps.executeUpdate() == 0) {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al generar codigo: " + e.getMessage());
            return false;
        }
        return util.CorreoUtil.enviarCodigoVerificacion(correo, nombres, codigo);
    }

    /** Compara el codigo ingresado; si coincide, marca la cuenta como verificada. */
    public boolean verificarCodigo(String correo, String codigoIngresado) {
        String sql = "SELECT codigo_verificacion FROM usuarios WHERE correo = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                String guardado = rs.getString("codigo_verificacion");
                if (guardado == null || !guardado.equals(codigoIngresado)) {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al verificar codigo: " + e.getMessage());
            return false;
        }

        String update = "UPDATE usuarios SET verificado = TRUE, codigo_verificacion = NULL WHERE correo = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, correo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al marcar verificado: " + e.getMessage());
            return false;
        }
    }
}