//sql statements sind hier,
//

package app.daos;

import app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

// The User Data Access Object implements the DAO interface
// we tell the interface that our Type (T) will be a User
// and our Type (ID) will be an Integer
// See City Dao for details
public class UserDao implements Dao<User, String> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection; //kommt von Databaseservice (in App instanziert)

    public UserDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public String create(User user) throws SQLException {
        String query = "INSERT INTO users(username, password, name, bio, image, coins, elo, wins, losses) " +
                "VALUES(?, ? , ? , ?, ? , ? , ?, ?, ?)" ;
        PreparedStatement stmt = getConnection().prepareStatement(query);
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword()); //TODO: hash???
        stmt.setString(3, user.getUsername()); //same name until edited
        stmt.setString(4, "");
        stmt.setString(5, ":-)");
        stmt.setInt(6, 20);
        stmt.setInt(7, 100);
        stmt.setInt(8, 0);
        stmt.setInt(9, 0);

        stmt.executeUpdate();

        return user.getUsername();

    }

    @Override
    public User read(String username) throws SQLException {

        String query = "SELECT * FROM users WHERE username = ?";
        PreparedStatement stmt = getConnection().prepareStatement(query);

        stmt.setString(1, username);

        ResultSet result = stmt.executeQuery();
        User user = null;
        if(result.next()){
            user = new User(
                    result.getString(1), //username
                    result.getString(2), // password
                    result.getString(3), //name
                    result.getString(4), //bio
                    result.getString(5), //image
                    result.getInt(6), //coins
                    result.getInt(7), //elo
                    result.getInt(8), //wins
                    result.getInt(9) //losses
            );
        }
        return user;
    }

    public LinkedHashMap<String, User> read() throws SQLException {

        String query = "SELECT * FROM users ORDER BY elo DESC";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        ResultSet result = stmt.executeQuery();

        LinkedHashMap<String, User> users = new LinkedHashMap<>();

        while(result.next()){
            User user = new User(
                    result.getString(1), //username
                    result.getString(2), // password
                    result.getString(3), //name
                    result.getString(4), //bio
                    result.getString(5), //image
                    result.getInt(6), //coins
                    result.getInt(7), //elo
                    result.getInt(8), //wins
                    result.getInt(9) //losses
            );
            users.put(user.getUsername(), user);
        }
        return users;
    }

    @Override
    public void update(User user) throws SQLException {
       String query = "UPDATE users SET  name = ?, bio = ?, image = ?, coins = ?, elo = ?, wins = ?, losses = ? WHERE username = ?";
       PreparedStatement stmt = getConnection().prepareStatement(query);

       stmt.setString(1, user.getName());
       stmt.setString(2, user.getBio());
       stmt.setString(3, user.getImage());
       stmt.setInt(4, user.getCoins());
       stmt.setInt(5, user.getElo());
       stmt.setInt(6, user.getWins());
       stmt.setInt(7, user.getLosses());
       stmt.setString(8, user.getUsername());

       stmt.executeUpdate();
    }

    @Override
    public void delete(User user) throws SQLException {

    }
}
