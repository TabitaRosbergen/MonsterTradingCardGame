package app.services;


import app.dtos.UserDTO;
import app.models.Card;
import app.models.Element;
import app.models.Player;
import lombok.Getter;
import lombok.Setter;
import org.mockito.internal.matchers.Equals;
import org.postgresql.gss.GSSOutputStream;

@Getter
@Setter

public class BattleService {

    Player player1;
    Player player2;




    BattleService(UserDTO user1, UserDTO user2){
        player1 = new Player(user1);
        player2 = new Player(user2);

        startBattle();
    }

    private void startBattle() {

        if(!getPlayer1().getUser().getDeck().isEmpty() && !getPlayer2().getUser().getDeck().isEmpty()){
            //TODO: BATTLE SHOULD BE OVER
            return;
        }
        else{
            getPlayer1().chooseRandomCard();
            getPlayer2().chooseRandomCard();
        }

        //determine Winner könnte null zurückgeben TODO: hier weiter machen
        reconfigureDecks(determineWinner());
    }



    Player determineWinner(){

        if(getPlayer1().getMonsterType() == null && getPlayer2().getMonsterType() == null){
            //SPELL VS SPELL
            System.out.println("SPELL VS SPELL");
           calculateEffectiveness();
           if(getPlayer1().getEffectiveness()* getPlayer1().getFightingCard().getDamage() > getPlayer2().getEffectiveness()* getPlayer2().getFightingCard().getDamage()) {
              return getPlayer1();
           }
           else if (getPlayer1().getEffectiveness()* getPlayer1().getFightingCard().getDamage() < getPlayer2().getEffectiveness()* getPlayer2().getFightingCard().getDamage()) {
               return getPlayer2();
           }
           else
               return null;

        }
        else if(getPlayer1().getMonsterType() == null && getPlayer2().getMonsterType() != null){
            // SPELL VS MONSTER
            System.out.println("SPELL VS MONSTER");
            calculateEffectiveness();

        }
        else{
            //MONSTER VS MONSTER
            if(getPlayer1().getFightingCard().getDamage() > getPlayer2().getFightingCard().getDamage()) {
                return getPlayer1();
            }
            if(getPlayer1().getFightingCard().getDamage() < getPlayer2().getFightingCard().getDamage()) {
                return getPlayer2();
            }
            else {
                return null;
            }
        }
        return null;

    }

    void reconfigureDecks(Player winner){
        if(winner == getPlayer1()){
            System.out.println("Round won by" + getPlayer1().getUser().getName());
            getPlayer1().getUser().getDeck().add(getPlayer2().getFightingCard());
            getPlayer2().getUser().getDeck().remove(getPlayer2().getFightingCard());
        }
        else if(winner == getPlayer2()){
            System.out.println("Round won by" + getPlayer2().getUser().getName());
            getPlayer2().getUser().getDeck().add(getPlayer1().getFightingCard());
            getPlayer1().getUser().getDeck().remove(getPlayer1().getFightingCard());
        }
        else{
            System.out.println("This round was a draw!");
        }
    }

    void calculateEffectiveness(){
        if(getPlayer1().getElement() == Element.Water && getPlayer1().getElement() == Element.Fire){
            getPlayer1().setEffectiveness(2);
            getPlayer2().setEffectiveness(0.5);
        }
        else if (getPlayer1().getElement() == Element.Fire && getPlayer1().getElement() == Element.Water) {
            getPlayer1().setEffectiveness(0.5);
            getPlayer2().setEffectiveness(2);
        }
        else if (getPlayer1().getElement() == Element.Fire && getPlayer1().getElement() == Element.Normal) {
            getPlayer1().setEffectiveness(2);
            getPlayer2().setEffectiveness(0.5);
        }
        else if (getPlayer1().getElement() == Element.Normal && getPlayer1().getElement() == Element.Fire) {
            getPlayer2().setEffectiveness(2);
            getPlayer1().setEffectiveness(0.5);
        }
        else if (getPlayer1().getElement() == Element.Normal && getPlayer1().getElement() == Element.Water) {
            getPlayer1().setEffectiveness(2);
            getPlayer2().setEffectiveness(0.5);
        }
        else if (getPlayer1().getElement() == Element.Water && getPlayer1().getElement() == Element.Normal) {
            getPlayer2().setEffectiveness(2);
            getPlayer1().setEffectiveness(0.5);
        }
    }



}
