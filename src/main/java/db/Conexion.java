package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String url = "jdbc:postgresql://ep-winter-sea-adexnh7y.c-2.us-east-1.aws.neon.tech/neondb?sslmode=require";
    private static final String usuario = "neondb_owner";
    private static final String contrasenia = "npg_XfVE2zWiqCM5";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url,usuario,contrasenia);
            System.out.println("Conexión exitosa a la nube de Neon.");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return connection;
    }
}
