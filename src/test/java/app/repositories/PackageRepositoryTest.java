package app.repositories;

import app.daos.CardDao;
import app.dtos.CardInfo;
import app.dtos.UserDTO;
import app.models.Card;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PackageRepositoryTest {

    @Test
    @DisplayName("create package -> card exists")
    void testCreatePackage_cardExists() throws SQLException {
        CardDao cardDao = mock(CardDao.class);
        UserRepository userRepository = mock(UserRepository.class);
        PackageRepository packageRepository = new PackageRepository(userRepository, cardDao);

        Card card = new Card("1", "testcard", 20, "uwe", true, false, false);

        ArrayList<CardInfo> cardInfos = new ArrayList<>();
        cardInfos.add(new CardInfo("1", "test", 20));

        when(cardDao.read(any())).thenReturn(card);

        //act
        boolean result = packageRepository.createPackage(cardInfos);

        //assert
        assertFalse(result);

    }

    @Test
    @DisplayName("create Package -> duplicates")
    void testCreatePackage_duplicate() throws SQLException {
        CardDao cardDao = mock(CardDao.class);
        UserRepository userRepository = mock(UserRepository.class);

        PackageRepository packageRepository = new PackageRepository(userRepository, cardDao);

        ArrayList<CardInfo> cardInfos = new ArrayList<>();
        cardInfos.add(new CardInfo("1", "testcard", 20));
        cardInfos.add(new CardInfo("1", "testcard", 20));

        //act
        boolean result = packageRepository.createPackage(cardInfos);

        //assert
        assertFalse(result);

    }

    @Test
    @DisplayName("create Package -> valid")
    void testCreatePackage_valid() throws SQLException {
        CardDao cardDao = mock(CardDao.class);
        UserRepository userRepository = mock(UserRepository.class);
        ArgumentCaptor<Card> argumentCaptor = ArgumentCaptor.forClass(Card.class);
        Card expected_card1 = new Card("1",
                "testcard1",
                20,
                null,
                true,
                false,
                false);

        Card expected_card2 = new Card("2",
                "testcard2",
                20,
                null,
                true,
                false,
                false);

        PackageRepository packageRepository = new PackageRepository(userRepository, cardDao);

        ArrayList<CardInfo> cardInfos = new ArrayList<>();
        cardInfos.add(new CardInfo("1", "testcard1", 20));
        cardInfos.add(new CardInfo("2", "testcard2", 20));

        //act
        boolean result = packageRepository.createPackage(cardInfos);

        //assert
        verify(cardDao, times(2)).create(argumentCaptor.capture()); //capture the cards that were used in method
        ArrayList<Card>  capturedCards = (ArrayList<Card>) argumentCaptor.getAllValues();
        assertEquals(expected_card1.getId(), capturedCards.get(0).getId());
        assertEquals(expected_card2.getId(), capturedCards.get(1).getId());
        assertTrue(capturedCards.get(0).isInPack());
        assertTrue(capturedCards.get(1).isInPack());

    }

    @Test
    @DisplayName("open Package -> not enough")
    void testOpenPackage_notEnough() throws SQLException {
        //arrange
        UserDTO user = mock(UserDTO.class);
        CardDao cardDao = mock(CardDao.class);
        UserRepository userRepository = mock(UserRepository.class);
        PackageRepository packageRepository = new PackageRepository(userRepository, cardDao);
        ArrayList<Card> pack = new ArrayList<>();
        pack.add(new Card(
                "1",
                "testcard1",
                20,
                null,
                true,
                false,
                false)
        );

        when(cardDao.readPackCards()).thenReturn(pack);

        //act
        ArrayList<Card> result = packageRepository.openPackage(user);

        //assert
        assertEquals(pack, result);
    }

    @Test
    @DisplayName("open Package -> check inPack")
    void testOpenPackage_checkInPack() throws SQLException {
        //arrange
        UserDTO user = mock(UserDTO.class);
        CardDao cardDao = mock(CardDao.class);
        Card card = mock(Card.class);
        UserRepository userRepository = mock(UserRepository.class);
        PackageRepository packageRepository = new PackageRepository(userRepository, cardDao);
        ArrayList<Card> pack = new ArrayList<>();
        Card card1 = new Card("1","testcard1", 20, null, true, false, false);
        Card card2 = new Card("2","testcard2", 20, null, true, false, false);
        Card card3 = new Card("3","testcard3", 20, null, true, false, false);
        Card card4 = new Card("4","testcard4", 20, null, true, false, false);
        Card card5 = new Card("5","testcard5", 20, null, true, false, false);

        pack.add(card1);
        pack.add(card2);
        pack.add(card3);
        pack.add(card4);
        pack.add(card5);

        when(cardDao.readPackCards()).thenReturn(pack);

        //act
        ArrayList<Card> result = packageRepository.openPackage(user);

        //assert
        assertFalse(result.get(0).isInPack());
        assertFalse(result.get(1).isInPack());
        assertFalse(result.get(2).isInPack());
        assertFalse(result.get(3).isInPack());
        assertFalse(result.get(4).isInPack());



    }
}