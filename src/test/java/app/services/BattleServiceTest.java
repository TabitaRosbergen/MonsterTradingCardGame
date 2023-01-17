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
    Card card1;
    Card card2;
    ArrayList<Card> deckPlayer1;
    ArrayList<Card> deckPlayer2;

    @BeforeEach
    void setUp()
    {
        user1 = mock(UserDTO.class);
        user2 = mock(UserDTO.class);

        player1 = new Player(user1);
        player2 = new Player(user2);
        deckPlayer1 = new ArrayList<>();
        deckPlayer2 = new ArrayList<>();


        battleService = new BattleService(player1, player2);

        card1 = new Card("1","WaterSpell", 10, "player1", false, true, false);
        card2 = new Card("2","FireSpell", 10, "player1", false, true, false);

        player1.setFightingCard(card1);
        player2.setFightingCard(card2);

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
        player1.setElement(Element.Normal);

        player2.setMonsterType(null);
        player2.setElement(Element.Water);

        //act
        Player winner = battleService.determineWinner();

        //assert
        assertEquals(player2, winner);
        assertEquals(0, player1.getRoundsWon());
        assertEquals(1, player2.getRoundsWon());
    }


    @Test()
    @DisplayName("testBattle SpellFight -> draw")
    void testStartBattle_SpellFight_draw(){

        //arrange
        deckPlayer1.add(new Card("1","FireSpell", 10, "player1", false, true, false));
        deckPlayer2.add(new Card("2","FireSpell", 10, "player1", false, true, false));
        deckPlayer1.add(new Card("3","FireSpell", 10, "player1", false, true, false));
        deckPlayer2.add(new Card("4","FireSpell", 10, "player1", false, true, false));

        UserDTO userOne = new UserDTO("Si Si", "pl1", "...", "", 5, 100, 0, 0, null, deckPlayer1);
        UserDTO userTwo = new UserDTO("Jassi", "pl2", "...", "", 5, 100, 0, 0, null, deckPlayer2);
        Player player1 = new  Player(userOne);
        player1.setFightingCard(null);
        Player player2 = new  Player(userTwo);
        player1.setFightingCard(null);

        battleService = new BattleService(player1, player2);

        //act
        battleService.startBattle();

        //assert
        assertEquals(100 , player1.getUser().getElo());
        assertEquals(100 , player2.getUser().getElo());
        assertEquals(2, deckPlayer1.size());


    }

    @Test()
    @DisplayName("testBattle -> Kraken VS Spell")
    void testBattle_Kraken_VS_Spell(){

        //arrange
        deckPlayer1.add(new Card("1","Kraken", 10, "player1", false, true, false));
        deckPlayer2.add(new Card("1","FireSpell", 10, "player1", false, true, false));

        UserDTO userOne = new UserDTO("Si Si", "pl1", "...", "", 5, 100, 0, 0, null, deckPlayer1);
        UserDTO userTwo = new UserDTO("Jassi", "pl2", "...", "", 5, 100, 0, 0, null, deckPlayer2);

        Player player1 = new  Player(userOne);
        player1.setFightingCard(null);
        Player player2 = new  Player(userTwo);
        player1.setFightingCard(null);

        battleService = new BattleService(player1, player2);

        //act
        battleService.startBattle();

        //assert
        assertEquals(103 , player1.getUser().getElo());
        assertEquals(95 , player2.getUser().getElo());
        assertEquals(2, deckPlayer1.size());
        assertTrue(deckPlayer2.isEmpty());
    }

    @Test()
    @DisplayName("testBattle -> Knight VS WaterSpell ->KNIGHT")
    void testBattle_Knight_VS_WaterSpell(){

        //arrange
        deckPlayer1.add(new Card("1","WaterSpell", 10, "player1", false, true, false));
        deckPlayer2.add(new Card("1","Knight", 10, "player1", false, true, false));

        UserDTO userOne = new UserDTO("Si Si", "pl1", "...", "", 5, 100, 0, 0, null, deckPlayer1);
        UserDTO userTwo = new UserDTO("Jassi", "pl2", "...", "", 5, 100, 0, 0, null, deckPlayer2);
        Player player1 = new  Player(userOne);
        player1.setFightingCard(null);
        Player player2 = new  Player(userTwo);
        player1.setFightingCard(null);

        battleService = new BattleService(player1, player2);

        //act
        battleService.startBattle();

        //assert
        assertEquals(103 , player1.getUser().getElo());
        assertEquals(95 , player2.getUser().getElo());
        assertEquals(2, deckPlayer1.size());
        assertTrue(deckPlayer2.isEmpty());


    }

    @Test()
    @DisplayName("testBattle -> Monster VS Spell")
    void testBattle_Monster_VS_Sell(){

        //arrange
        deckPlayer1.add(new Card("1","WaterGoblin", 10, "player1", false, true, false));
        deckPlayer2.add(new Card("1","FireSpell", 10, "player1", false, true, false));

        UserDTO userOne = new UserDTO("Si Si", "pl1", "...", "", 5, 100, 0, 0, null, deckPlayer1);
        UserDTO userTwo = new UserDTO("Jassi", "pl2", "...", "", 5, 100, 0, 0, null, deckPlayer2);
        Player player1 = new  Player(userOne);
        player1.setFightingCard(null);
        Player player2 = new  Player(userTwo);
        player1.setFightingCard(null);

        battleService = new BattleService(player1, player2);

        //act
        battleService.startBattle();

        //assert
        assertEquals(103 , player1.getUser().getElo());
        assertEquals(95 , player2.getUser().getElo());
        assertEquals(2, deckPlayer1.size());
        assertTrue(deckPlayer2.isEmpty());


    }

    @Test()
    @DisplayName("testBattle -> WaterWizzard VS NormalOrk ->WIZZARD")
    void testBattle_WaterWizzard_VS_NormalOrk(){

        //arrange
        deckPlayer1.add(new Card("1","WaterWizzard", 10, "player1", false, true, false));
        deckPlayer2.add(new Card("2","NormalOrk", 10, "player1", false, true, false));

        UserDTO userOne = new UserDTO("Si Si", "pl1", "...", "", 5, 100, 0, 0, null, deckPlayer1);
        UserDTO userTwo = new UserDTO("Jassi", "pl2", "...", "", 5, 100, 0, 0, null, deckPlayer2);
        Player player1 = new  Player(userOne);
        player1.setFightingCard(null);
        Player player2 = new  Player(userTwo);
        player1.setFightingCard(null);

        battleService = new BattleService(player1, player2);

        //act
        battleService.startBattle();

        //assert
        assertEquals(95 , player1.getUser().getElo());
        assertEquals( 103, player2.getUser().getElo());
        assertEquals(2, deckPlayer2.size());
        assertTrue(deckPlayer1.isEmpty());


    }

    @Test()
    @DisplayName("testBattle -> FireElve_VS_WaterDragon ->ELVE")
    void testBattle_FireElve_VS_WaterDragon(){

        //arrange
        deckPlayer1.add(new Card("1","FireElve", 10, "player1", false, true, false));
        deckPlayer2.add(new Card("1","WaterDragon", 10, "player1", false, true, false));

        UserDTO userOne = new UserDTO("Si Si", "pl1", "...", "", 5, 100, 0, 0, null, deckPlayer1);
        UserDTO userTwo = new UserDTO("Jassi", "pl2", "...", "", 5, 100, 0, 0, null, deckPlayer2);
        Player player1 = new  Player(userOne);
        player1.setFightingCard(null);
        Player player2 = new  Player(userTwo);
        player1.setFightingCard(null);

        battleService = new BattleService(player1, player2);

        //act
        battleService.startBattle();

        //assert
        assertEquals(103 , player1.getUser().getElo());
        assertEquals(95 , player2.getUser().getElo());
        assertEquals(2, deckPlayer1.size());
        assertTrue(deckPlayer2.isEmpty());


    }

    @Test()
    @DisplayName("testBattle -> WaterOrk VS FireElve ->WaterOrk")
    void testBattle_WaterOrk_VS_FireElve(){

        //arrange
        deckPlayer1.add(new Card("1","WaterOrk", 10, "player1", false, true, false));
        deckPlayer2.add(new Card("1","FireElve", 10, "player1", false, true, false));

        UserDTO userOne = new UserDTO("Si Si", "pl1", "...", "", 5, 100, 0, 0, null, deckPlayer1);
        UserDTO userTwo = new UserDTO("Jassi", "pl2", "...", "", 5, 100, 0, 0, null, deckPlayer2);
        Player player1 = new  Player(userOne);
        player1.setFightingCard(null);
        Player player2 = new  Player(userTwo);
        player1.setFightingCard(null);

        battleService = new BattleService(player1, player2);

        //act
        battleService.startBattle();

        //assert
        assertEquals(103 , player1.getUser().getElo());
        assertEquals(95 , player2.getUser().getElo());
        assertEquals(2, deckPlayer1.size());
        assertEquals(0, deckPlayer2.size());



    }

    @Test()
    @DisplayName("testBattle -> WaterOrk VS FireElve ->Draw")
    void testBattle_WaterGoblin_VS_RegularSpell(){

        //arrange
        deckPlayer1.add(new Card("1","RegularGoblin", 10, "player1", false, true, false));
        deckPlayer2.add(new Card("1","WaterGoblin", 45, "player1", false, true, false));

        UserDTO userOne = new UserDTO("Si Si", "pl1", "...", "", 5, 100, 0, 0, null, deckPlayer1);
        UserDTO userTwo = new UserDTO("Jassi", "pl2", "...", "", 5, 100, 0, 0, null, deckPlayer2);
        Player player1 = new  Player(userOne);
        player1.setFightingCard(null);
        Player player2 = new  Player(userTwo);
        player1.setFightingCard(null);

        battleService = new BattleService(player1, player2);

        //act
        battleService.startBattle();

        //assert
        assertEquals(20.0, player1.getEffectiveness()*player1.getFightingCard().getDamage());
        assertEquals(22.5, player2.getEffectiveness()*player2.getFightingCard().getDamage());

        //assertEquals(1, deckPlayer1.size());
        //assertEquals(1, deckPlayer2.size());



    }







}