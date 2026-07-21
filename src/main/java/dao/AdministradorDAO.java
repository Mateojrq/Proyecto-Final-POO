package dao;

import db.Conexion;
import model.Administrador;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Administra las cuentas con rol ADMIN. Implementa ICRUD (ABSTRACCION):
 * este DAO se compromete a cumplir el mismo contrato que ProfesorDAO,
 * EstudianteDAO, TareaDAO y EntregaDAO, aunque cada uno lo resuelva
 * de forma distinta (POLIMORFISMO en tiempo de compilacion / sobrecarga
 * de responsabilidades por tipo).
 */
public class AdministradorDAO implements ICRUD<Administrador> {

    @Override
    public boolean guardar(Administrador admin) {
        String sql = "INSERT INTO usuarios (nombres, apellidos, correo, contrasena, rol) VALUES (?, ?, ?, ?, 'ADMIN')";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, admin.getNombres());
            ps.setString(2, admin.getApellidos());
            ps.setString(3, admin.getCorreo());
            ps.setString(4, PasswordUtil.hash(admin.getContrasena()));
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[AdministradorDAO] Error al guardar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Administrador admin) {
        String sql = "UPDATE usuarios SET nombres = ?, apellidos = ?, correo = ? WHERE id = ? AND rol = 'ADMIN'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, admin.getNombres());
            ps.setString(2, admin.getApellidos());
            ps.setString(3, admin.getCorreo());
            ps.setInt(4, admin.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[AdministradorDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ? AND rol = 'ADMIN'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[AdministradorDAO] Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Administrador> listar() {
        List<Administrador> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = 'ADMIN' ORDER BY id";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Administrador(
                        rs.getInt("id"), rs.getString("nombres"), rs.getString("apellidos"),
                        rs.getString("correo"), rs.getString("contrasena")));
            }
        } catch (SQLException e) {
            System.err.println("[AdministradorDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }
}
