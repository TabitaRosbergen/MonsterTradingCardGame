package app.services;

import app.dtos.UserDTO;
import app.models.Card;
import app.models.Element;
import app.models.MonsterType;
import app.models.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BattleServiceTest {
    UserDTO user1;
    UserDTO user2;

    Player player1;
    Player player2;
    BattleService battleService;
    ArrayList<Card> deckPlayer1 = new ArrayList<>();
    ArrayList<Card> deckPlayer2 = new ArrayList<>();
    Card card1;
    Card card2;


    @BeforeEach
    void setUp()
    {
        user1 = mock(UserDTO.class);
        user2 = mock(UserDTO.class);

        player1 = new Player(user1);
        player2 = new Player(user2);

        battleService = new BattleService(player1, player2);

        card1 = new Card("1","Testcard1", 10, "player1", false, true, false);
        card2 = new Card("2","Testcard2", 10, "player1", false, true, false);

        player1.setFightingCard(card1);
        player2.setFightingCard(card2);

        // deckPlayer1.add(new Card("1","WaterSpell", 20, "player1", false, true, false));
        //deckPlayer1.add(new Card("2","WaterSpell", 20, "player1", false, true, false));

        //deckPlayer1.add(new Card("3","FireSpell", 20, "player2", false, true, false));
        //deckPlayer1.add(new Card("4","FireSpell", 20, "player2", false, true, false));



    }

    @Test
    @DisplayName("effectiveness: Water vs Fire")
    void testCalculateEffectiveness_WaterVSFire(){
        //arrange
        player1.setElement(Element.Water);
        player2.setElement(Element.Fire);

        //act
        battleService.calculateEffectiveness();


        //assert
        assertEquals(2.0, battleService.player1.getEffectiveness());
        assertEquals( 0.5, battleService.player2.getEffectiveness());

    }

    @Test
    @DisplayName("effectiveness: Fire vs Water")
    void testCalculateEffectiveness_FireVSWater(){
        //arrange
        player1.setElement(Element.Fire);
        player2.setElement(Element.Water);

        //act
        battleService.calculateEffectiveness();


        //assert
        assertEquals(0.5, battleService.player1.getEffectiveness());
        assertEquals(2.0 , battleService.player2.getEffectiveness());

    }

    @Test
    @DisplayName("effectiveness: Fire vs Normal")
    void testCalculateEffectiveness_FireVSNormal(){
        //arrange
        player1.setElement(Element.Fire);
        player2.setElement(Element.Normal);

        //act
        battleService.calculateEffectiveness();


        //assert
        assertEquals(2.0 , battleService.player1.getEffectiveness());
        assertEquals(0.5, battleService.player2.getEffectiveness());

    }

    @Test
    @DisplayName("effectiveness: Normal vs Fire")
    void testCalculateEffectiveness_NormalVSFire(){
        //arrange
        player1.setElement(Element.Normal);
        player2.setElement(Element.Fire);

        //act
        battleService.calculateEffectiveness();


        //assert
        assertEquals(0.5, battleService.player1.getEffectiveness());
        assertEquals(2.0 , battleService.player2.getEffectiveness());

    }

    @Test
    @DisplayName("effectiveness: Normal vs Water")
    void testCalculateEffectiveness_NormalVSWater(){
        //arrange
        player1.setElement(Element.Normal);
        player2.setElement(Element.Water);

        //act
        battleService.calculateEffectiveness();

        //assert
        assertEquals(2.0 , battleService.player1.getEffectiveness());
        assertEquals(0.5, battleService.player2.getEffectiveness());

    }

    @Test
    @DisplayName("effectiveness: Water vs Normal")
    void testCalculateEffectiveness_WaterVSNormal(){
        //arrange
        player1.setElement(Element.Water);
        player2.setElement(Element.Normal);

        //act
        battleService.calculateEffectiveness();

        //assert
        assertEquals(0.5, battleService.player1.getEffectiveness());
        assertEquals(2.0 , battleService.player2.getEffectiveness());


    }

    @Test
    @DisplayName("effectiveness: Water vs Water")
    void testCalculateEffectiveness_WaterVSWater(){
        //arrange
        player1.setElement(Element.Water);
        player2.setElement(Element.Water);

        //act
        battleService.calculateEffectiveness();

        //assert
        assertEquals(1.0, battleService.player1.getEffectiveness());
        assertEquals(1.0 , battleService.player2.getEffectiveness());
    }

    @Test
    @DisplayName("effectiveness: Fire vs Fire")
    void testCalculateEffectiveness_FireVSFire(){
        //arrange
        player1.setElement(Element.Fire);
        player2.setElement(Element.Fire);

        //act
        battleService.calculateEffectiveness();

        //assert
        assertEquals(1.0, battleService.player1.getEffectiveness());
        assertEquals(1.0 , battleService.player2.getEffectiveness());
    }

    @Test
    @DisplayName("effectiveness: Normal vs Normal")
    void testCalculateEffectiveness_NormalVSNormal(){
        //arrange
        player1.setElement(Element.Normal);
        player2.setElement(Element.Normal);

        //act
        battleService.calculateEffectiveness();

        //assert
        assertEquals(1.0, battleService.player1.getEffectiveness());
        assertEquals(1.0 , battleService.player2.getEffectiveness());
    }

    @Test
    @DisplayName("effectiveness back to 1.0")
    void testCalculateEffectiveness_backToDefault(){
        testCalculateEffectiveness_NormalVSFire();
        testCalculateEffectiveness_FireVSFire();

        assertEquals(1.0, battleService.player1.getEffectiveness());
        assertEquals(1.0 , battleService.player2.getEffectiveness());

    }

    @Test
    @DisplayName("reconfiguiure decks")
    void testReconfigureDecks(){
        //arrange



    }


    @Test
    @DisplayName("determineWinner WaterSpellVSFireSpell")
    void testDetermineWinner_WaterSpell_VS_FireSpell(){
        //arrange
        player1.setMonsterType(null);
        player1.setElement(Element.Water);

        player2.setMonsterType(null);
        player2.setElement(Element.Fire);

        //act
        Player winner = battleService.determineWinner();

        //assert
        assertEquals(player1, winner);

    }
    @Test
    @DisplayName("determineWinner FireSpell VS WaterSpell")
    void testDetermineWinner_FireSpell_VS_FireSpell(){
        //arrange
        player1.setMonsterType(null);
        player1.setElement(Element.Fire);

        player2.setMonsterType(null);
        player2.setElement(Element.Water);

        //act
        Player winner = battleService.determineWinner();

        //assert
        assertEquals(player2, winner);
        assertEquals(0, player1.getRoundsWon());
        assertEquals(1, player2.getRoundsWon());


    }

    @Test
    @DisplayName("determineWinner draw")
    void testDetermineWinner_Spell_VS_Spell_draw(){
        //arrange
        player1.setMonsterType(null);
        player1.setElement(Element.Water);

        player2.setMonsterType(null);
        player2.setElement(Element.Water);

        //act
        Player winner = battleService.determineWinner();

        //assert
        assertNull(winner);
        assertEquals(0, player1.getRoundsWon());
        assertEquals(0, player2.getRoundsWon());

    }

    @Test
    @DisplayName("determineWinner -> Kraken(player1) is immune to Spell")
    void testDetermineWinner_Kraken_VS_Spell(){
        //arrange
        player1.setMonsterType(MonsterType.KRAKEN);
        player1.setElement(Element.Water);

        player2.setMonsterType(null);
        player2.setElement(Element.Water);

        //act
        Player winner = battleService.determineWinner();

        //assert
        assertEquals(player1, winner);
        assertEquals(1, player1.getRoundsWon());
        assertEquals(0, player2.getRoundsWon());
    }
    @Test
    @DisplayName("determineWinner -> Kraken(player2) is immune to Spell")
    void testDetermineWinner_Spell_VS_Kraken(){
        //arrange
        player1.setMonsterType(null);
        player1.setElement(Element.Water);

        player2.setMonsterType(MonsterType.KRAKEN);
        player2.setElement(Element.Water);

        //act
        Player winner = battleService.determineWinner();

        //assert
        assertEquals(player2, winner);
        assertEquals(0, player1.getRoundsWon());
        assertEquals(1, player2.getRoundsWon());
    }

    @Test
    @DisplayName("determineWinner -> Knight(player1) looses against water")
    void testDetermineWinner_Knight_VS_Water(){
        //arrange
        player1.setMonsterType(MonsterType.KNIGHT);
        player1.setElement(null);

        player2.setMonsterType(null);
        player2.setElement(Element.Water);

        //act
        Player winner = battleService.determineWinner();

        //assert
        assertEquals(player2, winner);
        assertEquals(0, player1.getRoundsWon());
        assertEquals(1, player2.getRoundsWon());
    }

}