package app.repositories;

import app.daos.CardDao;
import app.daos.TradeDao;
import app.models.Trade;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.ArrayList;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class TradeRepository {
    UserRepository userRepository;
    TradeDao tradeDao;
    CardDao cardDao;

    public TradeRepository(UserRepository userRepository, CardDao cardDao, TradeDao tradeDao) {
        setUserRepository(userRepository);
        setCardDao(cardDao);
        setTradeDao(tradeDao);
    }

    public Trade getTradeById(String id){
        try {
            return getTradeDao().read(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Trade> getAllTrades(){
        try {
            return new ArrayList<Trade>(getTradeDao().read().values());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTrade(Trade trade) throws SQLException { //throw because we need info if duplicate TradeId
        getTradeDao().create(trade);
    }

    public void deleteTrade(Trade Trade) {
        try {
            getTradeDao().delete(Trade);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}