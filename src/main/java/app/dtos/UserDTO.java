package app.dtos;

import app.models.Card;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
@AllArgsConstructor
public class UserDTO {

    String username;
    String name;
    String bio;
    String image;
    int coins;
    int elo;
    int wins;
    int losses;
    ArrayList<Card> stack;
    ArrayList<Card> deck;

    public UserDTO() {}

    public Card findCardInStack(String cardId){
        Card card = null;
        for(Card userCard: getStack()){ //check stack of user who wants to accept trade
            if(userCard.getId().equals(cardId)){
                card = userCard;
            }
        }
        return card;
    }

}
