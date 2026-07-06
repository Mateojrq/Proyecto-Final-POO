package dao;
import model.Tarea;
import java.util.List;
import java.util.ArrayList;

public class TareaDAO implements ICRUD<Tarea> {
    @Override
    public boolean crear(Tarea modelo) { return false; /* Lógica SQL aquí en el 2do avance */ }
    @Override
    public List<Tarea> leerTodos() { return new ArrayList<>(); }
    @Override
    public boolean actualizar(Tarea modelo) { return false; }
    @Override
    public boolean eliminar(int id) { return false; }
}