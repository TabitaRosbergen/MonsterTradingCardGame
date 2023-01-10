package app.daos;

import java.sql.SQLException;
//import java.util.HashMap; //probiere ohne HASHMAP
import java.util.HashMap;
import java.util.List;

// Dao needs two Types to work
// first the object and second the ID
public interface Dao<T, ID> {
    // we add the CRUD methods on the interface
    // that needs to be implemented by the DAOs
    String create(T t) throws SQLException;
    T read(String username) throws SQLException;
    // you could also return the newly updated or
    // deleted object, but
    // we already have all the information on the client
    // we want to reduce complexity
    void update(T t) throws SQLException;
    void delete(T t) throws SQLException;
}
