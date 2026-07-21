package dao;

import java.util.List;

/**
 * Contrato generico que deben cumplir todos los DAO del sistema.
 * Pilar de ABSTRACCION: define el "que" (guardar, actualizar, eliminar,
 * listar) sin importar el "como" de cada implementacion concreta.
 *
 * @param <T> tipo de objeto que administra el DAO
 */
public interface ICRUD<T> {
    boolean guardar(T objeto);
    boolean actualizar(T objeto);
    boolean eliminar(int id);
    List<T> listar();
}
