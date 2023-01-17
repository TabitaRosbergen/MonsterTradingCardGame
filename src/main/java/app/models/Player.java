package app.models;

import app.dtos.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter

public class Player {
    UserDTO user;

    int roundsWon = 0;
    Card fightingCard;

    Element element;

    MonsterType monsterType;
    double effectiveness;

    public volatile String battleLog = ""; //to tell compiler that it could be changed by another thread

    public Player(UserDTO user){
        setUser(user);
        effectiveness = 1.00;
    }


    public void chooseRandomCard(){
            int index = (int)(Math.random() * getUser().getDeck().size());
            fightingCard = getUser().getDeck().get(index);


            readTypeAndElement(fightingCard);

    }

    void readTypeAndElement(Card card){
        String name = fightingCard.getName();
        if(name.contains("Fire")){
            setElement(Element.Fire);
            name = name.replace("Fire", "");
        }
        else if (name.contains("Water")) {
            setElement(Element.Water);
            name = name.replace("Water", "");
        }
        else if (!name.contains("Fire") && !name.contains("Water")) {
            setElement(Element.Normal);
        }

        if(name.contains("Spell")){
            setMonsterType(null);
        }
        else{
            switch (name){
                case "Goblin" -> setMonsterType(MonsterType.GOBLIN);
                case "Dragon" -> setMonsterType(MonsterType.DRAGON);
                case "Ork" -> setMonsterType(MonsterType.ORK);
                case "Kraken" -> setMonsterType(MonsterType.KRAKEN);
                case "Wizzard" -> setMonsterType(MonsterType.WIZZARD);
                case "Knight" -> setMonsterType(MonsterType.KNIGHT);
                case "Elve" -> setMonsterType(MonsterType.ELVE);
            }

        }
    }

}
