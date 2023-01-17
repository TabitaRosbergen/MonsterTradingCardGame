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

    StringBuilder battleLog = new StringBuilder();
    int rounds;




    public BattleService(Player player1, Player player2){
        setPlayer1(player1);
        setPlayer2(player2);
    }

   public void startBattle() {

        Player winner;

       rounds = 0;
       for (;rounds < 100; rounds++) {
           getBattleLog().append("\n\nRound").append(rounds);

           if (!getPlayer1().getUser().getDeck().isEmpty() && !getPlayer2().getUser().getDeck().isEmpty()) {
               //One Player has lost all the cards
               break;
           } else {
               getPlayer1().chooseRandomCard();
               getPlayer2().chooseRandomCard();

               //BattleLog
               getBattleLog().append("\n").append(getPlayer1().getUser().getUsername());
               getBattleLog().append(" Card: ").append(getPlayer1().getFightingCard().getName());
               getBattleLog().append("\nVS");
               getBattleLog().append("\n").append(getPlayer2().getUser().getUsername());
               getBattleLog().append(" Card: ").append(getPlayer2().getFightingCard().getName());

           }
           reconfigureDecks(determineWinner()); //null is handled in reconfigure deck

       }

   //BATTLE IS OVER
       //draw
       if(getPlayer1().getRoundsWon() == getPlayer2().getRoundsWon()){
           getBattleLog().append("\nThis Battle was a draw.\nthe ELOs stay unchanged");
       }
       //Player1 won
       else if(getPlayer1().getRoundsWon() > getPlayer2().getRoundsWon()){
           wrapUpBattle(getPlayer1(), getPlayer2());
       }
       //Player2 won
       else{
           wrapUpBattle(getPlayer2(), getPlayer1());
       }
       //TODO: BATTLE SHOULD BE OVER

   }

   void wrapUpBattle(Player winner, Player loser){
       //BattleLog -> who won
       getBattleLog().append("\n").append(winner.getUser().getUsername()).append("won the battle with ").append(winner.getRoundsWon()).append(" rounds of ").append(getRounds()).append("won.");

       //calculate ELO
       winner.getUser().setElo(winner.getUser().getElo() + 3);
       loser.getUser().setElo(loser.getUser().getElo() - 5);

       //BattleLog -> changed Elo
       getBattleLog().append("\nELO:\n").append(winner.getUser().getUsername()).append(": ").append(winner.getUser().getElo());
       getBattleLog().append("\n").append(loser.getUser().getUsername()).append(": ").append(loser.getUser().getElo());

   }

   Player determineWinner(){
       Player winner = null;

       if(getPlayer1().getMonsterType() == null && getPlayer2().getMonsterType() == null){
           calculateEffectiveness();
           winner = elementFight(getPlayer1(), getPlayer2());

       }
       else if(getPlayer1().getMonsterType() != null && getPlayer2().getMonsterType() == null){
           winner = monsterVsSpell(getPlayer1(), getPlayer2());
       }
       else if(getPlayer1().getMonsterType() == null && getPlayer2().getMonsterType() != null) {
           winner = monsterVsSpell(getPlayer2(), getPlayer1());
       }
       else{
           winner = monsterVsMonster();
       }
       if(winner != null){
           winner.setRoundsWon(winner.getRoundsWon()+1);

       }
       return winner;

   }

    Player elementFight(Player playerOne, Player playerTwo){

        if(playerOne.getEffectiveness()* playerOne.getFightingCard().getDamage() > playerTwo.getEffectiveness()* playerTwo.getFightingCard().getDamage()){
            return playerOne;
        }
        else if(playerOne.getEffectiveness()* playerOne.getFightingCard().getDamage() < playerTwo.getEffectiveness()* playerTwo.getFightingCard().getDamage()){
            return playerTwo;
        }
        else return null;

    }

    Player monsterVsSpell(Player monster, Player spell){

       if(monster.getMonsterType() == MonsterType.KRAKEN){
           return monster;
       }
       if(monster.getMonsterType() == MonsterType.KNIGHT && spell.getElement() == Element.Water){
           return spell;
       }
       else{
           calculateEffectiveness();
           return elementFight(monster, spell);
       }

    }

    Player monsterVsMonster(){
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

        //REGULAR //Elements should not have any effect
        getPlayer1().setEffectiveness(1.0);
        getPlayer2().setEffectiveness(1.0);
        return elementFight(getPlayer1(), getPlayer2());
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
        else{ //winner was null
            System.out.println("This round was a draw!");
        }

        getPlayer1().setElement(null);
        getPlayer1().setMonsterType(null);
        getPlayer2().setElement(null);
        getPlayer2().setElement(null);
    }


    void calculateEffectiveness(){
        double[][] effectivenessTable = {
                {1.0, 0.5, 2.0}, // Normal vs Normal, Normal vs Fire, Normal vs Water
                {2.0, 1.0, 0.5}, // Fire vs Normal, Fire vs Fire, Fire vs Water
                {0.5, 2.0, 1.0}  // Water vs Normal, Water vs Fire, Water vs Water
        };

       int row = getPlayer1().getElement().ordinal();
       int col = getPlayer2().getElement().ordinal();

        getPlayer1().setEffectiveness(effectivenessTable[row][col]);
        getPlayer2().setEffectiveness(effectivenessTable[col][row]);
    }
}

 /*
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

     */

/*
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
            if(getPlayer1().getFightingCard().getDamage()* getPlayer1().getEffectiveness() > getPlayer2().getFightingCard().getDamage() * getPlayer2().getEffectiveness()) {
                return getPlayer1();
            }
            if(getPlayer1().getFightingCard().getDamage()* getPlayer1().getEffectiveness() < getPlayer2().getFightingCard().getDamage() * getPlayer2().getEffectiveness()) {
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
            if(getPlayer1().getFightingCard().getDamage()* getPlayer1().getEffectiveness() > getPlayer2().getFightingCard().getDamage() * getPlayer2().getEffectiveness()) {
                return getPlayer1();
            }
            if(getPlayer1().getFightingCard().getDamage()* getPlayer1().getEffectiveness() < getPlayer2().getFightingCard().getDamage() * getPlayer2().getEffectiveness()) {
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
            if(getPlayer1().getFightingCard().getDamage()* getPlayer1().getEffectiveness() > getPlayer2().getFightingCard().getDamage() * getPlayer2().getEffectiveness()) {
                return getPlayer1();
            }
            if(getPlayer1().getFightingCard().getDamage()* getPlayer1().getEffectiveness() < getPlayer2().getFightingCard().getDamage() * getPlayer2().getEffectiveness()) {
                return getPlayer2();
            }
            else {
                return null;
            }
        }

    }
*/


