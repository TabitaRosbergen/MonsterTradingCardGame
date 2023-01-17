package app.services;

import app.models.Card;
import app.models.Element;
import app.models.MonsterType;
import app.models.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter

public class BattleService {

    Player player1;
    Player player2;
    StringBuilder battleLog = new StringBuilder();
    int rounds;

    Boolean unoReverse = true;

    Card unoReverseCard = new Card("unoReverse", "unoReverseCard",0 ,  "noBody", false, true, false);

    public BattleService(Player player1, Player player2){
        setPlayer1(player1);
        setPlayer2(player2);

    }

   public void startBattle() {
       Player winner;

       //unique feature
       // one of the two players get a special card in their deck, if the card is drawn
       // the decks are swapped, and the reversecard is deleted so it can only appear once in a game
       if(getUnoReverse()){
           int temp = (Math.random() <= 0.5) ? 1 : 2;
           if(temp == 1){
               getPlayer1().getUser().getDeck().add(getUnoReverseCard());
           }
           else{
               getPlayer2().getUser().getDeck().add(getUnoReverseCard());
           }
       }

       //BattleLog: Start Banner
       getBattleLog().append("\n\n-------------GAME ON-------------\n");

       rounds = 1;
       for (;rounds < 101; rounds++) { //we want 100 rounds max and start counting at 1 =>  i < 101

           //ONE DECK EMPTY
           if (getPlayer1().getUser().getDeck().isEmpty() || getPlayer2().getUser().getDeck().isEmpty()) {

               //Battlelog: which user has empty Deck
               if(getPlayer1().getUser().getDeck().isEmpty()){
                   getBattleLog().append("\n").append(getPlayer1().getUser().getUsername()).append("'s Deck is empty.");
               }
               else{
                   getBattleLog().append("\n").append(getPlayer2().getUser().getUsername()).append("'s Deck is empty.");
               }
               break;


           } else {
               //DECKS NOT EMPTY

               //BattleLog: which round
               getBattleLog().append("\nROUND").append(getRounds());

               //choose Random card, read Type and Element store it in the variables of the Players
               getPlayer1().chooseRandomCard();
               getPlayer2().chooseRandomCard();

               if(getPlayer1().getFightingCard().getName().equals("unoReverseCard") || getPlayer2().getFightingCard().getName().equals("unoReverseCard")){
                   unoReversePlayed();
                   continue;
               }

               //BattleLog: <CardName1> <Damage> VS <CardName2> <Damage>
               getBattleLog().append("\n").append(getPlayer1().getUser().getUsername());
               getBattleLog().append(" Card: ").append(getPlayer1().getFightingCard().getName()).append(" ").append(getPlayer1().getFightingCard().getDamage());
               getBattleLog().append("\nVS");
               getBattleLog().append("\n").append(getPlayer2().getUser().getUsername());
               getBattleLog().append(" Card: ").append(getPlayer2().getFightingCard().getName()).append(" ").append(getPlayer2().getFightingCard().getDamage());

           }

           //determineWinner() returns the player who won or null in case of a draw
           //returns null is handled in reconfigure deck
           //reconfigureDeck gives the lost card in the winners deck and removes it from the losers

           reconfigureDecks(determineWinner());

       }

   //BATTLE IS OVER

       //BattleLog: Game Finished Banner
       getBattleLog().append("\n\n-------------THE GAME IS FINISHED-------------");

       //BattleLog: Draw
       if(getPlayer1().getRoundsWon() == getPlayer2().getRoundsWon()){
           getBattleLog().append("\nThis Battle was a draw.\nthe ELOs stay unchanged");
       }

       //wrapUpBattle() adds to battleLog and changes Elo
       //player1
       else if(getPlayer1().getRoundsWon() > getPlayer2().getRoundsWon()){
           wrapUpBattle(getPlayer1(), getPlayer2());
       }
       //player2
       else{
           wrapUpBattle(getPlayer2(), getPlayer1());
       }

       //just for Testing
       System.out.println(battleLog.toString());

       //set BattleLog in players, signals to waiting thread that the battle is over
       getPlayer1().setBattleLog(battleLog.toString());
       getPlayer2().setBattleLog(battleLog.toString());

   }

    void unoReversePlayed(){
        getBattleLog().append("\n :::::::The UnoReverseCard was drawn so the decks will be swapped.:::::::");

        //swap decks
        ArrayList<Card> temp = new ArrayList<>(player1.getUser().getDeck());
        player1.getUser().getDeck().clear();
        player1.getUser().getDeck().addAll(player2.getUser().getDeck());
        player2.getUser().getDeck().clear();
        player2.getUser().getDeck().addAll(temp);


        //remove unoReverseCard
        if(!getPlayer1().getUser().getDeck().remove(getUnoReverseCard())){
            getPlayer2().getUser().getDeck().remove(getUnoReverseCard());
        }

    }

    //wrapUpBattle() adds infos to battleLog and changes Elo
    void wrapUpBattle(Player winner, Player loser){

       //calculate ELO
       winner.getUser().setElo(winner.getUser().getElo() + 3);
       winner.getUser().setWins(winner.getUser().getWins()+1);
       loser.getUser().setElo(loser.getUser().getElo() - 5);
       loser.getUser().setLosses(loser.getUser().getLosses()+1);

       //BattleLog: announce winner
       getBattleLog().append("\n\n").append(winner.getUser().getUsername()).append(" won the battle with ").append(winner.getRoundsWon()).append(" rounds of ").append(getRounds()-1).append(" won.");

       //BattleLog: new Elo
       getBattleLog().append("\n\nELO:\n").append(winner.getUser().getUsername()).append(": ").append(winner.getUser().getElo());
       getBattleLog().append("\n").append(loser.getUser().getUsername()).append(": ").append(loser.getUser().getElo());

   }


   Player determineWinner(){
       Player winner = null;

       //effectiveness is calculated by comparing the elements
       calculateEffectiveness();
       //calculates effectiveness based on special rules has to come after calculateEffectiveness()
       //because effectiveness could be overwritten
       calculateSpeciality();

       double player1FinalDamage = getPlayer1().getEffectiveness()* getPlayer1().getFightingCard().getDamage();
       double player2FinalDamage = getPlayer2().getEffectiveness()* getPlayer2().getFightingCard().getDamage();

       //BattleLog: <finalDamage> VS <finalDamage>
       getBattleLog().append("\n").append(player1FinalDamage).append(" VS ").append(player2FinalDamage);

       //Player1 wins
       if(player1FinalDamage > player2FinalDamage){
           winner = getPlayer1();
       }
       //Player2 wins
       else if(player1FinalDamage < player2FinalDamage){
           winner = getPlayer2();
       }
       //Draw
       else return null; //when draw

       //No Draw: increment roundsWon and return winner
       winner.setRoundsWon(winner.getRoundsWon()+1);
       return winner;

   }

    void calculateSpeciality(){
        //System.out.println("MONSTER VS MONSTER");

        //GOBLIN VS DRAGON
        if(getPlayer1().getMonsterType() == MonsterType.GOBLIN && getPlayer2().getMonsterType() == MonsterType.DRAGON){
            getPlayer1().setEffectiveness(0);
            getBattleLog().append("\n**Goblins are too afraid of Dragons to attack**");
            return;
        }
        if(getPlayer1().getMonsterType() == MonsterType.DRAGON && getPlayer2().getMonsterType() == MonsterType.GOBLIN){
            getPlayer2().setEffectiveness(0);
            getBattleLog().append("\n**Goblins are too afraid of Dragons to attack**");
            return;
        }

        //WIZZARD VS ORK
        if(getPlayer1().getMonsterType() == MonsterType.WIZZARD && getPlayer2().getMonsterType() == MonsterType.ORK){
            getPlayer2().setEffectiveness(0);
            getBattleLog().append("\n**Wizzards can't be damaged by Orks**");
            return;
        }
        if(getPlayer1().getMonsterType() == MonsterType.ORK && getPlayer2().getMonsterType() == MonsterType.WIZZARD){
            getPlayer1().setEffectiveness(0);
            getBattleLog().append("\n**Wizzards can't be damaged by Orks**");
            return;
        }

        //FIREELVE VS DRAGON
        if(getPlayer1().getMonsterType() == MonsterType.ELVE && getPlayer1().getElement() == Element.Fire && getPlayer2().getMonsterType() == MonsterType.DRAGON){
            getPlayer2().setEffectiveness(0);
            getBattleLog().append("\n**FireElves can evade the Dragon's attack**");
            return;
        }
        if(getPlayer1().getMonsterType() == MonsterType.DRAGON && getPlayer2().getElement() == Element.Fire && getPlayer2().getMonsterType() == MonsterType.ELVE){
            getPlayer1().setEffectiveness(0);
            getBattleLog().append("\n**FireElves can evade the Dragon's attack**");
            return;
        }


        if(getPlayer1().getMonsterType() == MonsterType.KRAKEN && getPlayer2().getMonsterType() == null){
            getPlayer2().setEffectiveness(0);
            getBattleLog().append("\n**The Kraken is immune against Spells**");
            return;
        }
        if(getPlayer2().getMonsterType() == MonsterType.KRAKEN && getPlayer1().getMonsterType() == null){
            getPlayer1().setEffectiveness(0);
            getBattleLog().append("\n**The Kraken is immune against Spells**");
            return;
        }

        if(getPlayer1().getMonsterType() == MonsterType.KNIGHT && getPlayer2().getMonsterType() == null && getPlayer2().getElement() == Element.Water){
            getPlayer1().setEffectiveness(0);
            getBattleLog().append("\n**WaterSpells make Knights drown instantly**");
            return;
        }
        if(getPlayer2().getMonsterType() == MonsterType.KNIGHT && getPlayer1().getMonsterType() == null && getPlayer1().getElement() == Element.Water){
            getPlayer2().setEffectiveness(0);
            getBattleLog().append("\n**WaterSpells make Knights drown instantly**");
            return;
        }

        return;
    }


    void reconfigureDecks(Player winner){

        //getPlayer1().setElement(null);
        getPlayer1().setMonsterType(null);
        getPlayer2().setMonsterType(null);


        if(winner == null){
            getBattleLog().append("\n\nThis round was a draw!\n\n");
            return;
        }

        getBattleLog().append("\n\n").append(winner.getUser().getUsername()).append(" won this round!\n\n");

        if(winner == getPlayer1()){
            getPlayer1().getUser().getDeck().add(getPlayer2().getFightingCard());
            getPlayer2().getUser().getDeck().remove(getPlayer2().getFightingCard());

        }
        else if(winner == getPlayer2()){
            getPlayer2().getUser().getDeck().add(getPlayer1().getFightingCard());
            getPlayer1().getUser().getDeck().remove(getPlayer1().getFightingCard());
        }
    }


    void calculateEffectiveness(){
        if(getPlayer1().getMonsterType() != null && getPlayer2().getMonsterType() != null){
            getPlayer1().setEffectiveness(1);
            getPlayer2().setEffectiveness(1);
        }

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

