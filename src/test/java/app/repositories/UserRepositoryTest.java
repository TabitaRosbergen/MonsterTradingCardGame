package app.repositories;

import app.daos.CardDao;
import app.daos.UserDao;
import app.dtos.UserDTO;
import app.dtos.UserData;
import app.models.Card;
import app.models.Element;
import app.models.User;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;




import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryTest {
//dependencies
    UserDao userDao;
    CardDao cardDao;
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void setUp(){
        user = new User("max", "passwort123", "maxi", "gamer", ":)", 20, 23,2,4 );

        userDao = mock(UserDao.class);
        cardDao = mock(CardDao.class);
        userRepository =  new UserRepository(userDao, cardDao);
    }

    @Test
    void testGetById() throws SQLException {
    ///arrange

        ArrayList<Card> userCards = new ArrayList<>();

        //in stack
        ArrayList<Card> expect_inStack = new ArrayList<>();
        expect_inStack.add(new Card("1","TestCard", 0, user.getUsername(), true, false, false ));
        expect_inStack.add(new Card("2","TestCard",0, user.getUsername(), true, false, false ));

        //in deck
        ArrayList<Card> expect_inDeck = new ArrayList<>();
        expect_inDeck.add(new Card("3","TestCard",0, user.getUsername(), false, true, false ));
        expect_inDeck.add(new Card("4","TestCard",0, user.getUsername(), false, true, false ));

        //array to start
        userCards.addAll(expect_inStack);
        userCards.addAll(expect_inDeck);

        //stub userDao.read
        when(userDao.read(user.getUsername())).thenReturn(user);
        when(cardDao.readByUsername(user.getUsername())).thenReturn(userCards);

        //act
        UserDTO userDTO = userRepository.getById(user.getUsername());

        //assert
        assertEquals(user.getUsername(), userDTO.getUsername());
        assertEquals(user.getName(), userDTO.getName());
        assertEquals(user.getBio(), userDTO.getBio());
        assertEquals(user.getImage(), userDTO.getImage());
        assertEquals(user.getCoins(), userDTO.getCoins());
        assertEquals(user.getElo(), userDTO.getElo());
        assertEquals(user.getWins(), userDTO.getWins());
        assertEquals(user.getLosses(), userDTO.getLosses());


        assertEquals(expect_inDeck.get(0), userDTO.getDeck().get(0));
        assertEquals(expect_inDeck.get(1), userDTO.getDeck().get(1));
        assertEquals(expect_inStack.get(0), userDTO.getStack().get(0));
        assertEquals(expect_inStack.get(1), userDTO.getStack().get(1));


    }



    @Test
   @DisplayName("Dao returns null -> expect null")
    void testGetUserData_expectNull() throws SQLException {
        //arrange

        when(userDao.read(user.getUsername())).thenReturn(null);

        //act
        UserData userdata = userRepository.getUserData(user.getUsername());

        //assert
        assertNull(userdata);
    }

    @Test
    @DisplayName("Dao returns valid user -> expect valid userData object")
    void testGetUserData_expectValidUsarData() throws SQLException {
        //arrange
        when(userDao.read(user.getUsername())).thenReturn(user);

        //act
        UserData userdata = userRepository.getUserData(user.getUsername());

        //assert
        assertEquals(user.getName(), userdata.getName());
        assertEquals(user.getBio(), userdata.getBio());
        assertEquals(user.getImage(), userdata.getImage());

    }

    @Test
    @DisplayName("createUser() with already existing name -> return null ")
    void testCreateUser_expectNull(){
        //arrange
        user.setUsername(null);
        //act
        String username = userRepository.createUser(user);
        //assert
        assertNull(username);

        //clean up
        user.setUsername("max");
    }


    @Test
    @DisplayName("createUser() with Valid username -> expect username")
    void testCreateUser_expectUsername() throws SQLException {

        //arrange
        when(userDao.create(user)).thenReturn(user.getUsername());
        //act
        String username = userRepository.createUser(user);
        //assert
        assertEquals(user.getUsername(), username);

    }


}