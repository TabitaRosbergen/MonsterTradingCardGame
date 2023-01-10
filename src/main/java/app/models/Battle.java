package app.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Battle {
    User playerOne;
    User playerTwo;

    public Battle(User playerOne, User playerTwo){
        setPlayerOne(playerOne);
        setPlayerTwo(playerTwo);
    }





}
