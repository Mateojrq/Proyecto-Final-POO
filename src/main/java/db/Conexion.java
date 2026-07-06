package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private final String url = "jdbc:postgresql://localhost:5432/db_centro_nivelacion";
    private final String usuario = "postgres";
    private final String contrasenaia = "1234";

    public Connection conexion() throws  SQLException{
            Connection conexion = null;
            try {
                conexion = DriverManager.getConnection(url, usuario, contrasenaia);
                System.out.println("Conexión exitosa.");
            }catch(SQLException e){
                System.out.println("Error al conectar con la base de datos." + e.getMessage());
            }
        return conexion();
    }
}

