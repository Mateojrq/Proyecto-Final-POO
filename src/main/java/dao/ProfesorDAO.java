package dao;

import db.Conexion;
import model.Profesor;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Administra las cuentas con rol PROFESOR. Implementa ICRUD. */
public class ProfesorDAO implements ICRUD<Profesor> {

    @Override
    public boolean guardar(Profesor profesor) {
        String sql = "INSERT INTO usuarios (nombres, apellidos, correo, contrasena, rol, especialidad) " +
                "VALUES (?, ?, ?, ?, 'PROFESOR', ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, profesor.getNombres());
            ps.setString(2, profesor.getApellidos());
            ps.setString(3, profesor.getCorreo());
            ps.setString(4, PasswordUtil.hash(profesor.getContrasena()));
            ps.setString(5, profesor.getEspecialidad());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProfesorDAO] Error al guardar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Profesor profesor) {
        String sql = "UPDATE usuarios SET nombres = ?, apellidos = ?, correo = ?, especialidad = ? " +
                "WHERE id = ? AND rol = 'PROFESOR'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, profesor.getNombres());
            ps.setString(2, profesor.getApellidos());
            ps.setString(3, profesor.getCorreo());
            ps.setString(4, profesor.getEspecialidad());
            ps.setInt(5, profesor.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProfesorDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    /** Actualiza unicamente la contrasena (usado desde "Mi Perfil"). */
    public boolean actualizarContrasena(int id, String nuevaContrasenaPlano) {
        String sql = "UPDATE usuarios SET contrasena = ? WHERE id = ? AND rol = 'PROFESOR'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, PasswordUtil.hash(nuevaContrasenaPlano));
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProfesorDAO] Error al actualizar contrasena: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ? AND rol = 'PROFESOR'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProfesorDAO] Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Profesor> listar() {
        List<Profesor> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = 'PROFESOR' ORDER BY id";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Profesor(
                        rs.getInt("id"), rs.getString("nombres"), rs.getString("apellidos"),
                        rs.getString("correo"), rs.getString("contrasena"), rs.getString("especialidad")));
            }
        } catch (SQLException e) {
            System.err.println("[ProfesorDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }
}
