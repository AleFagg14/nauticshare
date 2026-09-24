//Properties props = new Properties();
//InputStream input = getClass().getClassLoader()
//        .getResourceAsStream("db.properties");
//props.load(input);
//String url      = props.getProperty("db.url");
//String username = props.getProperty("db.username");
//String password = props.getProperty("db.password");

package it.unifi.nauticshare.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream input = ConnectionManager.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                throw new RuntimeException("Impossibile trovare il file db.properties");
            }
            prop.load(input);
            url = prop.getProperty("db.url");
            user = prop.getProperty("db.username");
            password = prop.getProperty("db.password");

            // Carica esplicitamente il driver PostgreSQL
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il caricamento della configurazione DB", e);
        }
    }

    private ConnectionManager() {
        // Costruttore privato per prevenire l'istanziamento (Pattern Singleton)
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}