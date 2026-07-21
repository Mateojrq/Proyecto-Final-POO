package dao;

import db.Conexion;
import model.Estudiante;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Administra las cuentas con rol ESTUDIANTE. Implementa ICRUD. */
public class EstudianteDAO implements ICRUD<Estudiante> {

    @Override
    public boolean guardar(Estudiante estudiante) {
        String sql = "INSERT INTO usuarios (nombres, apellidos, correo, contrasena, rol, curso) " +
                "VALUES (?, ?, ?, ?, 'ESTUDIANTE', ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombres());
            ps.setString(2, estudiante.getApellidos());
            ps.setString(3, estudiante.getCorreo());
            ps.setString(4, PasswordUtil.hash(estudiante.getContrasena()));
            ps.setString(5, estudiante.getCurso());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[EstudianteDAO] Error al guardar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Estudiante estudiante) {
        String sql = "UPDATE usuarios SET nombres = ?, apellidos = ?, correo = ?, curso = ? " +
                "WHERE id = ? AND rol = 'ESTUDIANTE'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estudiante.getNombres());
            ps.setString(2, estudiante.getApellidos());
            ps.setString(3, estudiante.getCorreo());
            ps.setString(4, estudiante.getCurso());
            ps.setInt(5, estudiante.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[EstudianteDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    /** Actualiza unicamente la contrasena (usado desde "Mi Perfil"). */
    public boolean actualizarContrasena(int id, String nuevaContrasenaPlano) {
        String sql = "UPDATE usuarios SET contrasena = ? WHERE id = ? AND rol = 'ESTUDIANTE'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, PasswordUtil.hash(nuevaContrasenaPlano));
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[EstudianteDAO] Error al actualizar contrasena: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ? AND rol = 'ESTUDIANTE'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[EstudianteDAO] Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Estudiante> listar() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = 'ESTUDIANTE' ORDER BY id";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Estudiante(
                        rs.getInt("id"), rs.getString("nombres"), rs.getString("apellidos"),
                        rs.getString("correo"), rs.getString("contrasena"), rs.getString("curso")));
            }
        } catch (SQLException e) {
            System.err.println("[EstudianteDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }
}
