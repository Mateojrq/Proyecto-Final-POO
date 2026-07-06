package dao;
import java.util.List;

public interface ICRUD<T> {
    boolean crear(T modelo);
    List<T> leerTodos();
    boolean actualizar(T modelo);
    boolean eliminar(int id);
}