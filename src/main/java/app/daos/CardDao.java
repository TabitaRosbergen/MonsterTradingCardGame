//sql statements sind hier,
//

package app.daos;

import app.models.Card;
import app.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

// The User Data Access Object implements the DAO interface
// we tell the interface that our Type (T) will be a User
// and our Type (ID) will be an Integer
// See City Dao for details
public class CardDao implements Dao<Card, String> {
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Connection connection; //kommt von Databaseservice (in App instanziert)

    public CardDao(Connection connection) {
        setConnection(connection);
    }

    @Override
    public String create(Card card) throws SQLException {
        String query = "INSERT INTO cards(id, name, damage, owner, in_pack, in_deck, in_trade) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)" ;
        PreparedStatement stmt = getConnection().prepareStatement(query);

        stmt.setString(1, card.getId());
        stmt.setString(2, card.getName()); //TODO: hash???
        stmt.setInt(3, card.getDamage());
        stmt.setString(4, null); //owner
        stmt.setBoolean(5, true); //in_pack
        stmt.setBoolean(6, false); //in_deck
        stmt.setBoolean(7, false); //in_trade

        stmt.executeUpdate();

        return card.getId();
    }

    @Override
    public Card read(String id) throws SQLException {

        String query = "SELECT * FROM cards WHERE id = ?";
        PreparedStatement stmt = getConnection().prepareStatement(query);

        stmt.setString(1, id);

        ResultSet result = stmt.executeQuery();
        Card card = null;
        if(result.next()){
            card = new Card(
                    result.getString(1),
                    result.getString(2),
                    result.getInt(3),
                    result.getString(4),//owner
                    result.getBoolean(5), //in_pack
                    result.getBoolean(6), //in_deck
                    result.getBoolean(7) //in_trade
            );
        }
        return card;
    }

    public ArrayList<Card> readByUsername(String username) throws SQLException {

        String query = "SELECT * FROM cards WHERE owner = ?";
        PreparedStatement stmt = getConnection().prepareStatement(query);

        stmt.setString(1, username);

        ResultSet result = stmt.executeQuery();

        ArrayList<Card> cards = new ArrayList<>();

        while(result.next()){
            Card card = new Card(
                    result.getString(1),
                    result.getString(2),
                    result.getInt(3),
                    result.getString(4),//owner
                    result.getBoolean(5), //in_pack
                    result.getBoolean(6), //in_deck
                    result.getBoolean(7) //in_trade
            );
            cards.add(card);
        }
        return cards;
    }

    public ArrayList<Card> readPackCards() throws SQLException {

        String query = "SELECT * FROM cards WHERE in_pack = TRUE LIMIT 5";
        PreparedStatement stmt = getConnection().prepareStatement(query);

        ResultSet result = stmt.executeQuery();

        ArrayList<Card> cards = new ArrayList<>();

        while(result.next()){
            Card card = new Card(
                    result.getString(1),
                    result.getString(2),
                    result.getInt(3),
                    result.getString(4),//owner
                    result.getBoolean(5), //in_pack
                    result.getBoolean(6), //in_deck
                    result.getBoolean(7) //in_trade
            );
            cards.add(card);
        }
        return cards;
    }

    public LinkedHashMap<String, Card> read() throws SQLException {

        String query = "SELECT * FROM cards";
        PreparedStatement stmt = getConnection().prepareStatement(query);
        ResultSet result = stmt.executeQuery();

        LinkedHashMap<String, Card> cards = new LinkedHashMap<>();

        while(result.next()){
            Card card = new Card(
                    result.getString(1),
                    result.getString(2),
                    result.getInt(3),
                    result.getString(4),//owner
                    result.getBoolean(5), //in_pack
                    result.getBoolean(6), //in_deck
                    result.getBoolean(7) //in_trade
            );
            cards.put(card.getId(), card);
        }
        return cards;
    }

    @Override
    public void update(Card card) throws SQLException {
        String query = "UPDATE cards SET name = ?, damage = ?, owner = ?, in_pack = ?, in_deck = ?, in_trade = ? WHERE id = ?";
        PreparedStatement stmt = getConnection().prepareStatement(query);

        stmt.setString(1, card.getName()); //TODO: hash???
        stmt.setInt(2, card.getDamage());
        stmt.setString(3, card.getOwner()); //owner
        stmt.setBoolean(4, card.isInPack()); //in_pack
        stmt.setBoolean(5, card.isInDeck()); //in_deck
        stmt.setBoolean(6, card.isInTrade()); //in_trade
        stmt.setString(7, card.getId());

        stmt.executeUpdate();
    }

    @Override
    public void delete(Card card) throws SQLException {

    }
}
