package dao;

import db.Conexion;
import model.Entrega;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** CRUD de Entregas. Implementa ICRUD. */
public class EntregaDAO implements ICRUD<Entrega> {

    private static final String SELECT_BASE =
            "SELECT e.*, t.titulo AS titulo_tarea, u.nombres AS est_nombres, u.apellidos AS est_apellidos " +
                    "FROM entregas e " +
                    "JOIN tareas t ON e.id_tarea = t.id " +
                    "JOIN usuarios u ON e.id_estudiante = u.id ";

    @Override
    public boolean guardar(Entrega entrega) {
        String sql = "INSERT INTO entregas (id_tarea, id_estudiante, archivo, nombre_archivo, comentario, estado) " +
                "VALUES (?, ?, ?, ?, ?, 'PENDIENTE')";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, entrega.getIdTarea());
            ps.setInt(2, entrega.getIdEstudiante());
            ps.setBytes(3, entrega.getArchivo());
            ps.setString(4, entrega.getNombreArchivo());
            ps.setString(5, entrega.getComentario());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[EntregaDAO] Error al guardar: " + e.getMessage());
            return false;
        }
    }

    /** Actualizacion completa (usada por el Estudiante mientras la entrega sigue pendiente). */
    @Override
    public boolean actualizar(Entrega entrega) {
        String sql = "UPDATE entregas SET archivo = ?, nombre_archivo = ? WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (entrega.getArchivo() != null) {
                ps.setBytes(1, entrega.getArchivo());
                ps.setString(2, entrega.getNombreArchivo());
            } else {
                ps.setNull(1, Types.BINARY);
                ps.setNull(2, Types.VARCHAR);
            }
            ps.setInt(3, entrega.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[EntregaDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    /** Usado por el Profesor para calificar una entrega. */
    public boolean calificar(int idEntrega, double calificacion, String comentario) {
        String sql = "UPDATE entregas SET calificacion = ?, comentario = ?, estado = 'CALIFICADO' WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, calificacion);
            ps.setString(2, comentario);
            ps.setInt(3, idEntrega);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[EntregaDAO] Error al calificar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM entregas WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[EntregaDAO] Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Entrega> listar() {
        return listarConFiltro(null, 0);
    }

    /** Entregas propias de un estudiante. */
    public List<Entrega> listarPorEstudiante(int idEstudiante) {
        return listarConFiltro("WHERE e.id_estudiante = ?", idEstudiante);
    }

    /** Entregas de las tareas creadas por un profesor (para calificar). */
    public List<Entrega> listarPorProfesor(int idProfesor) {
        return listarConFiltro("WHERE t.id_profesor = ?", idProfesor);
    }

    public boolean existeEntrega(int idTarea, int idEstudiante) {
        String sql = "SELECT 1 FROM entregas WHERE id_tarea = ? AND id_estudiante = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTarea);
            ps.setInt(2, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[EntregaDAO] Error al validar duplicado: " + e.getMessage());
            return true; // ante la duda, no dejar continuar
        }
    }

    private List<Entrega> listarConFiltro(String filtro, int parametro) {
        List<Entrega> lista = new ArrayList<>();
        String sql = SELECT_BASE + (filtro != null ? filtro : "") + " ORDER BY e.fecha_entrega DESC";
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
            System.err.println("[EntregaDAO] Error al listar: " + e.getMessage());
        }
        return lista;
    }

    private Entrega mapear(ResultSet rs) throws SQLException {
        Entrega entrega = new Entrega();
        entrega.setId(rs.getInt("id"));
        entrega.setIdTarea(rs.getInt("id_tarea"));
        entrega.setTituloTarea(rs.getString("titulo_tarea"));
        entrega.setIdEstudiante(rs.getInt("id_estudiante"));
        entrega.setNombreEstudiante(rs.getString("est_nombres") + " " + rs.getString("est_apellidos"));
        entrega.setArchivo(rs.getBytes("archivo"));
        entrega.setNombreArchivo(rs.getString("nombre_archivo"));
        if (rs.getTimestamp("fecha_entrega") != null) {
            entrega.setFechaEntrega(rs.getTimestamp("fecha_entrega").toLocalDateTime());
        }
        double calificacion = rs.getDouble("calificacion");
        entrega.setCalificacion(rs.wasNull() ? null : calificacion);
        entrega.setComentario(rs.getString("comentario"));
        entrega.setEstado(rs.getString("estado"));
        return entrega;
    }
}
