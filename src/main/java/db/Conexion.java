package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase Singleton encargada de gestionar la conexión a la base de datos en la nube.
 * Mantiene una única instancia activa durante la ejecución.
 */
public class Conexion {

    private static Connection instancia;

    // Credenciales de conexión
    private static final String URL = "jdbc:postgresql://ep-winter-sea-adexnh7y.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require";
    private static final String USUARIO = "neondb_owner";
    private static final String CONTRASENIA = "npg_XfVE2zWiqCM5";

    // Constructor privado para evitar que se instancie directamente con 'new'
    private Conexion() {
    }

    /**
     * Devuelve la instancia activa de la conexión a la base de datos.
     * Si no existe o está cerrada, crea una nueva.
     */
    public static Connection getConexion() {
        try {
            if (instancia == null || instancia.isClosed()) {
                instancia = DriverManager.getConnection(URL, USUARIO, CONTRASENIA);
                System.out.println("[Conexion] Conexión exitosa a la nube de Neon.");
            }
        } catch (SQLException e) {
            System.err.println("[Conexion] Error al conectar a la base de datos: " + e.getMessage());
        }
        return instancia;
    }

    /**
     * Cierra la conexión activa si está abierta.
     */
    public static void cerrarConexion() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
                System.out.println("[Conexion] Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("[Conexion] Error al cerrar la conexión: " + e.getMessage());
        }
    }
}