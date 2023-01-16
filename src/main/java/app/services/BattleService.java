package app.services;


import app.dtos.UserDTO;
import app.models.Card;
import app.models.Element;
import app.models.MonsterType;
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




    public BattleService(Player player1, Player player2){
        setPlayer1(player1);
        setPlayer2(player2);
    }

   public void startBattle() {

       player1.setBattleLog("Battle over1");
       player2.setBattleLog("Battle over2");
       return;

     /*  for (int i = 0; i < 100; i++) {

           if (!getPlayer1().getUser().getDeck().isEmpty() && !getPlayer2().getUser().getDeck().isEmpty()) {
               //TODO: BATTLE SHOULD BE OVER
               player1.setBattleLog("Battle over1");
               player2.setBattleLog("Battle over2");
               return;
           } else {
               getPlayer1().chooseRandomCard();
               getPlayer2().chooseRandomCard();
           }

           //determine Winner könnte null zurückgeben TODO: hier weiter machen
           reconfigureDecks(determineWinner());
       }*/
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
        else if(getPlayer1().getMonsterType() != null && getPlayer2().getMonsterType() == null){

        //MONSTER VS SPELL

            System.out.println("SPELL VS MONSTER");

            //KRAKEN IS IMMUNE AGAINST SPELLS
            if(getPlayer1().getMonsterType() == MonsterType.KRAKEN){
                System.out.println("Kraken is immune to spells!");
                return getPlayer1();
            }

            //WATERSPELL ALWAYS WINS AGAINST KNIGHT
            if(getPlayer1().getMonsterType() == MonsterType.KNIGHT && getPlayer2().getElement() == Element.Water){
                return getPlayer2();
            }

            //REGULAR
            calculateEffectiveness();
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
        else if(getPlayer1().getMonsterType() == null && getPlayer2().getMonsterType() != null) {

        //SPELL VS MONSTER

            //KRAKEN IS IMMUNE AGAINST SPELLS

            if(getPlayer2().getMonsterType() == MonsterType.KRAKEN){
                System.out.println("Kraken is immune to spells!");
                return getPlayer2();
            }

            //WATERSPELL ALWAYS WINS AGAINST KNIGHT

            if(getPlayer1().getElement() == Element.Water && getPlayer2().getMonsterType() == MonsterType.KNIGHT){
                return getPlayer2();
            }

            //REGULAR
            calculateEffectiveness();
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
        else{

        //MONSTER VS MONSTER

            System.out.println("MONSTER VS MONSTER");

            //GOBLIN VS DRAGON
            if(getPlayer1().getMonsterType() == MonsterType.GOBLIN && getPlayer2().getMonsterType() == MonsterType.DRAGON){
                return getPlayer1();
            }
            if(getPlayer1().getMonsterType() == MonsterType.DRAGON && getPlayer2().getMonsterType() == MonsterType.GOBLIN){
                return getPlayer2();
            }

            //WIZZARD VS ORK
            if(getPlayer1().getMonsterType() == MonsterType.WIZZARD && getPlayer2().getMonsterType() == MonsterType.ORK){
                return getPlayer1();
            }
            if(getPlayer1().getMonsterType() == MonsterType.ORK && getPlayer2().getMonsterType() == MonsterType.WIZZARD){
                return getPlayer2();
            }

            //FIREELVE VS DRAGON
            if(getPlayer1().getMonsterType() == MonsterType.ELVE && getPlayer1().getElement() == Element.Fire && getPlayer2().getMonsterType() == MonsterType.DRAGON){
                return getPlayer1();
            }
            if(getPlayer1().getMonsterType() == MonsterType.DRAGON && getPlayer2().getElement() == Element.Fire && getPlayer2().getMonsterType() == MonsterType.ELVE){
                return getPlayer2();
            }

            //REGULAR
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
        if(getPlayer1().getElement() == Element.Water && getPlayer2().getElement() == Element.Fire){
            getPlayer1().setEffectiveness(2.0);
            getPlayer2().setEffectiveness(0.5);
        }
        else if (getPlayer1().getElement() == Element.Fire && getPlayer2().getElement() == Element.Water) {
            getPlayer1().setEffectiveness(0.5);
            getPlayer2().setEffectiveness(2.0);
        }
        else if (getPlayer1().getElement() == Element.Fire && getPlayer2().getElement() == Element.Normal) {
            getPlayer1().setEffectiveness(2.0);
            getPlayer2().setEffectiveness(0.5);
        }
        else if (getPlayer1().getElement() == Element.Normal && getPlayer2().getElement() == Element.Fire) {
            getPlayer2().setEffectiveness(2.0);
            getPlayer1().setEffectiveness(0.5);
        }
        else if (getPlayer1().getElement() == Element.Normal && getPlayer2().getElement() == Element.Water) {
            getPlayer1().setEffectiveness(2.0);
            getPlayer2().setEffectiveness(0.5);
        }
        else if (getPlayer1().getElement() == Element.Water && getPlayer2().getElement() == Element.Normal) {
            getPlayer1().setEffectiveness(0.5);
            getPlayer2().setEffectiveness(2.0);
        }
        else{
            getPlayer2().setEffectiveness(1.0);
            getPlayer1().setEffectiveness(1.0);
        }
    }



}
