package dao;

import db.Conexion;
import model.Tarea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** CRUD de Tareas (deberes). Implementa ICRUD. */
public class TareaDAO implements ICRUD<Tarea> {

    private static final String SELECT_BASE =
            "SELECT t.*, u.nombres AS profesor_nombres, u.apellidos AS profesor_apellidos " +
                    "FROM tareas t JOIN usuarios u ON t.id_profesor = u.id ";

    @Override
    public boolean guardar(Tarea tarea) {
        String sql = "INSERT INTO tareas (titulo, descripcion, fecha_entrega, id_profesor, archivo, nombre_archivo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tarea.getTitulo());
            ps.setString(2, tarea.getDescripcion());
            ps.setDate(3, java.sql.Date.valueOf(tarea.getFechaEntrega()));
            ps.setInt(4, tarea.getIdProfesor());
            setArchivo(ps, 5, 6, tarea.getArchivo(), tarea.getNombreArchivo());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[TareaDAO] Error al guardar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Tarea tarea) {
        String sql = "UPDATE tareas SET titulo = ?, descripcion = ?, fecha_entrega = ?, archivo = ?, nombre_archivo = ? " +
                "WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tarea.getTitulo());
            ps.setString(2, tarea.getDescripcion());
            ps.setDate(3, java.sql.Date.valueOf(tarea.getFechaEntrega()));
            setArchivo(ps, 4, 5, tarea.getArchivo(), tarea.getNombreArchivo());
            ps.setInt(6, tarea.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[TareaDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM tareas WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[TareaDAO] Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Tarea> listar() {
        return listarConFiltro(null, 0);
    }

    /** Solo las tareas creadas por un profesor especifico (vista del Profesor). */
    public List<Tarea> listarPorProfesor(int idProfesor) {
        return listarConFiltro("WHERE t.id_profesor = ?", idProfesor);
    }

    public Tarea buscarPorId(int id) {
        List<Tarea> resultado = listarConFiltro("WHERE t.id = ?", id);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    private List<Tarea> listarConFiltro(String filtro, int parametro) {
        List<Tarea> lista = new ArrayList<>();
        String sql = SELECT_BASE + (filtro != null ? filtro : "") + " ORDER BY t.fecha_entrega";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (filtro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[TareaDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    private Tarea mapear(ResultSet rs) throws SQLException {
        Tarea tarea = new Tarea();
        tarea.setId(rs.getInt("id"));
        tarea.setTitulo(rs.getString("titulo"));
        tarea.setDescripcion(rs.getString("descripcion"));
        tarea.setFechaEntrega(rs.getDate("fecha_entrega").toLocalDate());
        tarea.setIdProfesor(rs.getInt("id_profesor"));
        tarea.setNombreProfesor(rs.getString("profesor_nombres") + " " + rs.getString("profesor_apellidos"));
        tarea.setArchivo(rs.getBytes("archivo"));
        tarea.setNombreArchivo(rs.getString("nombre_archivo"));
        Timestamp creacion = rs.getTimestamp("fecha_creacion");
        if (creacion != null) {
            tarea.setFechaCreacion(creacion.toLocalDateTime());
        }
        return tarea;
    }

    private void setArchivo(PreparedStatement ps, int posArchivo, int posNombre, byte[] archivo, String nombre) throws SQLException {
        if (archivo != null) {
            ps.setBytes(posArchivo, archivo);
            ps.setString(posNombre, nombre);
        } else {
            ps.setNull(posArchivo, Types.BINARY);
            ps.setNull(posNombre, Types.VARCHAR);
        }
    }
}
