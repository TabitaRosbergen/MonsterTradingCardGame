package app.services;

import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
@Setter
public class DatabaseService {
    // The service used to communicate to the database
    private Connection connection;

    public DatabaseService() throws SQLException {
        setConnection(
            DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/mtcg_db",
                "swe1user",
                "swe1pw"
            )
        );
    }
}
