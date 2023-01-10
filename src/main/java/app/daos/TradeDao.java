//sql statements sind hier,
//

package app.daos;

import app.models.Trade;
import app.models.Type;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

// The User Data Access Object implements the DAO interface
// we tell the interface that our Type (T) will be a User
// and our Type (ID) will be an Integer
// See City Dao for details
public class TradeDao implements Dao<Trade, String> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection; //kommt von Databaseservice (in App instanziert)

    public TradeDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public String create(Trade trade) throws SQLException {
        String query = "INSERT INTO trades(id, card_id, type, min_damage) " +
                "VALUES(?, ?, ?, ?)" ;
        PreparedStatement stmt = getConnection().prepareStatement(query);

        stmt.setString(1, trade.getId());
        stmt.setString(2, trade.getCardId());
        stmt.setInt(3, Type.valueOf(trade.getType()).ordinal()); //ordinal => zahl von enum
        stmt.setInt(4, trade.getMinDamage());

        stmt.executeUpdate();

        return trade.getId();
    }

    @Override
    public Trade read(String id) throws SQLException {

        String query = "SELECT * FROM trades WHERE id = ?";
        PreparedStatement stmt = getConnection().prepareStatement(query);

        stmt.setString(1, id);

        ResultSet result = stmt.executeQuery();
        Trade trade = null;
        if(result.next()){
            trade = new Trade(
                    result.getString(1),
                    result.getString(2),
                    Type.values()[result.getInt(3)].name(),
                    result.getInt(4)
            );
        }
        return trade;
    }

    public LinkedHashMap<String, Trade> read() throws SQLException {

        String query = "SELECT * FROM trades";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        ResultSet result = stmt.executeQuery();

        LinkedHashMap<String, Trade> trades = new LinkedHashMap<>();

        while(result.next()){
            Trade trade = new Trade(
                    result.getString(1),
                    result.getString(2),
                    Type.values()[result.getInt(3)].name(),
                    result.getInt(4)
            );
            trades.put(trade.getId(), trade);
        }
        return trades;
    }

    @Override
    public void update(Trade trade) throws SQLException {
    }

    @Override
    public void delete(Trade trade) throws SQLException {
        String query = "DELETE FROM trades WHERE id = ?";
        PreparedStatement stmt = getConnection().prepareStatement(query);

        stmt.setString(1, trade.getId());

        stmt.executeUpdate();
    }
}
