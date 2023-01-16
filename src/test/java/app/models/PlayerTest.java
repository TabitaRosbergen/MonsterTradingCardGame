package app.models;

import app.dtos.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {

    UserDTO user;
    Player player;
    Card card;

    @BeforeEach
    void setUp(){
        user = mock(UserDTO.class);
        ArrayList<Card> deck = new ArrayList<>();
        player = new Player(user);
        when(user.getDeck()).thenReturn(deck);



    }

    @Test
    @DisplayName("getRandomCard -> empty Deck")
    void testGetRandomCard_emptyDeck(){
        //arrange
        if(!user.getDeck().isEmpty()){
            user.getDeck().clear();
        }
        //act
        player.chooseRandomCard();

        //assert
        assertNull(player.getFightingCard());

    }

    @Test
    @DisplayName("getRandomCard -> not empty")

    void testGetRandomCard_notEmpty(){
        //arrange
        user.getDeck().add(new Card("1","FireGoblin", 0, "testuser", true, true, false));

        //act
        player.chooseRandomCard();

        //assert
        assertNotNull(player.getFightingCard());
        assertEquals(user.getDeck().get(0), player.getFightingCard() );

    }

    @Test
    @DisplayName("readType&Element -> FireSpell")
    void testReadTypeAndElement_FireSpell(){
        //arrange
        card = new Card("1","FireSpell", 20, "testuser", false, true, false);
        player.fightingCard = card;

        //act
        player.readTypeAndElement(card);

        //assert
        assertEquals(player.element, Element.Fire );
        assertNull(player.monsterType);

    }

    @Test
    @DisplayName("readType&Element -> WaterSpell")
    void testReadTypeAndElement_WaterSpell(){
        //arrange
        card = new Card("1","WaterSpell", 20, "testuser", false, true, false);
        player.fightingCard = card;

        //act
        player.readTypeAndElement(card);

        //assert
        assertEquals(player.element, Element.Water );
        assertNull(player.monsterType);

    }

    @Test
    @DisplayName("readType&Element -> NormalSpell")
    void testReadTypeAndElement_NormalSpell(){
        //arrange
        card = new Card("1","NormalSpell", 20, "testuser", false, true, false);
        player.fightingCard = card;

        //act
        player.readTypeAndElement(card);

        //assert
        assertEquals(player.element, Element.Normal );
        assertNull(player.monsterType);

    }

    @Test
    @DisplayName("readType&Element -> FireGoblin")
    void testReadTypeAndElement_FireGoblin(){
        //arrange
        card = new Card("1","FireGoblin", 20, "testuser", false, true, false);
        player.fightingCard = card;

        //act
        player.readTypeAndElement(card);

        //assert
        assertEquals(player.element, Element.Fire );
        assertEquals(player.monsterType, MonsterType.GOBLIN);

    }

    @Test
    @DisplayName("readType&Element -> WaterDragon")
    void testReadTypeAndElement_WaterDragon(){
        //arrange
        card = new Card("1","WaterDragon", 20, "testuser", false, true, false);
        player.fightingCard = card;

        //act
        player.readTypeAndElement(card);

        //assert
        assertEquals(player.element, Element.Water );
        assertEquals(player.monsterType, MonsterType.DRAGON);

    }

}