package app.repositories;

import app.daos.CardDao;
import app.dtos.CardInfo;
import app.dtos.UserDTO;
import app.models.Card;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import java.sql.SQLException;
import java.util.ArrayList;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class PackageRepository {
    UserRepository userRepository;
    CardDao cardDao;

    public PackageRepository(UserRepository userRepository, CardDao cardDao) {
        setUserRepository(userRepository);
        setCardDao(cardDao);
    }

    public boolean createPackage(ArrayList<CardInfo> cardInfos) { //throw because we need info if duplicate Card
        try {
            //check that no card already exists
            for (CardInfo cardInfo : cardInfos) {
                Card card = getCardDao().read(cardInfo.getId());
                if(card != null){
                    return false; //return false if duplicate card was found
                }
            }

            for (CardInfo cardInfo : cardInfos) { //check if duplicates in Request
                for (CardInfo cardInfo2 : cardInfos) {
                    if(cardInfo != cardInfo2 && cardInfo.getId().equals(cardInfo2.getId())){
                        return false;
                    }
                }
            }

            for (CardInfo cardInfo : cardInfos) {
                Card card = new Card(
                        cardInfo.getId(),
                        cardInfo.getName(),
                        cardInfo.getDamage(),
                        null,
                        true,
                        false,
                        false
                );
                getCardDao().create(card);
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Card> openPackage(UserDTO user) { //throw
        try {
            ArrayList<Card> pack = getCardDao().readPackCards();
            if(pack.size() < 5){
                return pack; //so controller knows why it did not work
            }

            for(Card card : pack){
                card.setInPack(false);
            }

            user.getStack().addAll(pack);
            user.setCoins(user.getCoins() - 5);
            getUserRepository().updateUser(user);
            return pack;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}