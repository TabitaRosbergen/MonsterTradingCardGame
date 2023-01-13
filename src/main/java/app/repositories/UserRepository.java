package app.repositories;

import app.daos.CardDao;
import app.dtos.UserDTO;
import app.dtos.UserData;
import app.dtos.UserStats;
import app.models.Card;
import app.models.User;

import app.daos.UserDao;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class UserRepository implements Repository<UserDTO, String> {
    UserDao userDao;
    CardDao cardDao;

    public UserRepository(UserDao userDao, CardDao cardDao) {
        setUserDao(userDao);
        setCardDao(cardDao);
    }

    @Override
    public UserDTO getById(String username) {
        try {
            User user = getUserDao().read(username);
            if (user == null) {
                return null;
            }

            //get the Cards
            ArrayList<Card> userCards = getCardDao().readByUsername(username);
            ArrayList<Card> userStack = new ArrayList<>();
            ArrayList<Card> userDeck = new ArrayList<>();

            for (Card card : userCards) {
                if (card.isInDeck()) {
                    userDeck.add(card);
                } else {
                    userStack.add(card);
                }
            }

            return new UserDTO(
                    user.getUsername(),
                    user.getName(),
                    user.getBio(),
                    user.getImage(),
                    user.getCoins(),
                    user.getElo(),
                    user.getWins(),
                    user.getLosses(),
                    userStack,
                    userDeck
            );

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public ArrayList<Card> getUserCards(String username) {
        UserDTO user = this.getById(username);
        if (user == null) {
            return null;
        }

        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(user.getStack());
        cards.addAll(user.getDeck());
        return cards;
    }


    public ArrayList<Card> getUserDeck(String username) {
        UserDTO user = this.getById(username);
        if (user == null) {
            return null;
        }

        return user.getDeck();
    }

    public boolean setUserDeck(ArrayList<String> cardIds, String username) {
        try {

            //check if cards belong to user
            for (String cardId : cardIds) {
                Card card = getCardDao().read(cardId);
                if (card == null || !card.getOwner().equals(username) || card.isInTrade()) {
                    return false;
                }
            }

            //load user
            UserDTO user = this.getById(username);
            if (user == null) {
                return false;
            }

            user.getStack().addAll(user.getDeck());
            user.getDeck().clear();

            for (String cardId : cardIds) {
                for (Card card : user.getStack()) {
                    if (card.getId().equals(cardId)) {
                        user.getDeck().add(card);
                    }
                }
            }

            for (String cardId : cardIds) {
                for (Card card : user.getDeck()) {
                    if (card.getId().equals(cardId)) {
                        user.getStack().remove(card);
                    }
                }
            }

            this.updateUser(user);

            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUser(UserDTO userDTO) {
        try {
            //first update user
            User user = new User(
                    userDTO.getUsername(),
                    null,
                    userDTO.getName(),
                    userDTO.getBio(),
                    userDTO.getImage(),
                    userDTO.getCoins(),
                    userDTO.getElo(),
                    userDTO.getWins(),
                    userDTO.getLosses()
            );
            getUserDao().update(user);

            //update Cards
            for (Card card : userDTO.getStack()) {
                card.setInDeck(false);
            }

            for (Card card : userDTO.getDeck()) {
                card.setInDeck(true);
            }

            ArrayList<Card> allUserCards = new ArrayList<>();
            allUserCards.addAll(userDTO.getStack());
            allUserCards.addAll(userDTO.getDeck());

            for (Card card : allUserCards) {
                card.setOwner(userDTO.getUsername());
                getCardDao().update(card);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public UserData getUserData(String username) {
        try {
            User user = getUserDao().read(username);
            if (user == null) {
                return null;
            }
            return new UserData(
                    user.getName(),
                    user.getBio(),
                    user.getImage()
            );

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public UserStats getUserStats(String username) {
        try {
            User user = getUserDao().read(username);
            if (user == null) {
                return null;
            }
            return new UserStats(
                    user.getName(),
                    user.getElo(),
                    user.getWins(),
                    user.getLosses()
            );

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public ArrayList<UserStats> getScores() {
        try {

            LinkedHashMap<String, User> users = getUserDao().read();
            ArrayList<UserStats> userStats = new ArrayList<>();

            for (User user : users.values()) {
                userStats.add(this.getUserStats(user.getUsername()));
            }

            return userStats;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /*
        //TODO: Überprüfen ob user schon vorhanden -> getById -> was gibt das zurück wenn nicht gefunden? kann man das vernünftig überprüfen?
        public UserDTO addUser(User userData) {
            try {

                getUserDao().create(userData); //write to DB

                return getById(userData.getName());

            } catch (SQLException e){
                e.printStackTrace();
                return null;
            }

        }
    */
    public String createUser(User user) {
        try {
            if (getById(user.getUsername()) != null) {
                return null;
            }
            return getUserDao().create(user); //returns username

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void editUser(String username, UserData changes) throws SQLException {

        User user = getUserDao().read(username);

        if (changes.getName() != null) {
            user.setName(changes.getName());
        }
        if (changes.getBio() != null) {
            user.setBio(changes.getBio());
        }
        if (changes.getImage() != null) {
            user.setImage(changes.getImage());
        }

        getUserDao().update(user);
    }

    public Boolean checkUserLogin(String username, String password) throws SQLException {
        User user = getUserDao().read(username);

        if (user == null) {
            return false;
        }

        return user.getPassword().matches(password);
    }

}