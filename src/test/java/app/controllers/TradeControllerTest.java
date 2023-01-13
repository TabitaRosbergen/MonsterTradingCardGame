package app.controllers;
import app.daos.CardDao;
import app.dtos.UserDTO;
import app.models.Card;
import app.repositories.TradeRepository;
import app.repositories.UserRepository;
import http.ContentType;
import http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Response;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TradeControllerTest {
    UserDTO userDTO;
    TradeRepository tradeRepository;
    UserRepository userRepository;
    CardDao cardDao;
    TradeController tradeController;

    @BeforeEach
    void setUp() {

       userDTO = mock(UserDTO.class);
       tradeRepository = mock(TradeRepository.class);
       userRepository = mock(UserRepository.class);
       cardDao = mock(CardDao.class);
       tradeController = new TradeController(tradeRepository, userRepository, cardDao);

    }


    @Test
    @DisplayName("create Trade -> card does not exist")
    void createTrade_cardNotExist() {

        //arrange

        Response expected_response = new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"data\": null, \"error\": \"The deal contains a card that is not owned by the user or locked in the deck.\" }"
        );


        when(userDTO.findCardInStack(any())).thenReturn(null);
        String body = "{\"Id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"CardToTrade\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"Type\": \"monster\", \"MinimumDamage\": 15}";

        //act
        Response response = tradeController.createTrade(body, userDTO);

        //assert
        assertEquals(expected_response.getStatusCode(), response.getStatusCode());
        assertEquals(expected_response.getStatusMessage(), response.getStatusMessage());
        assertEquals(expected_response.getContentType(), response.getContentType());
        assertEquals(expected_response.getContent(), response.getContent());

    }


    @Test
    @DisplayName("create Trade -> card in trade")
    void createTrade_cardInTrade() {

        //arrange
        Card card = new Card("1", "testCard", 20, "max", true, false, true );
        Response expected_response = new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"data\": null, \"error\": \"The deal contains a card that is not owned by the user or locked in the deck.\" }"
        );


        when(userDTO.findCardInStack(any())).thenReturn(card);
        String body = "{\"Id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"CardToTrade\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"Type\": \"monster\", \"MinimumDamage\": 15}";

        //act
        Response response = tradeController.createTrade(body, userDTO);

        //assert
        assertEquals(expected_response.getStatusCode(), response.getStatusCode());
        assertEquals(expected_response.getStatusMessage(), response.getStatusMessage());
        assertEquals(expected_response.getContentType(), response.getContentType());
        assertEquals(expected_response.getContent(), response.getContent());

    }

    @Test
    @DisplayName("create Trade -> successful")
    void createTrade_successful() {

        //arrange
        Card card = new Card("1", "testCard", 20, "max", true, false, false );

        Response expected_response = new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ \"data\": Trading deal successfully created, \"error\": \"null\" }"
        );


        when(userDTO.findCardInStack(any())).thenReturn(card);
        String body = "{\"Id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"CardToTrade\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"Type\": \"monster\", \"MinimumDamage\": 15}";

        //act
        Response response = tradeController.createTrade(body, userDTO);

        //assert
        assertEquals(expected_response.getStatusCode(), response.getStatusCode());
        assertEquals(expected_response.getStatusMessage(), response.getStatusMessage());
        assertEquals(expected_response.getContentType(), response.getContentType());
        assertEquals(expected_response.getContent(), response.getContent());

    }

    @Test
    @DisplayName("create Trade -> tradeId does exist")
    void createTrade_tradeIdDoesExist() throws SQLException {

        //arrange

        Response expected_response = new Response(
                HttpStatus.CONFLICT,
                ContentType.JSON,
                "{ \"data\": null, \"error\": \"A deal with this deal ID already exists.\" }"
        );

        Card card = new Card("1", "testCard", 20, "max", true, false, false );

        when(userDTO.findCardInStack(any())).thenReturn(card);
        doThrow(SQLException.class).when(tradeRepository).createTrade(any());

        String body = "{\"Id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"CardToTrade\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"Type\": \"monster\", \"MinimumDamage\": 15}";

        //act
        Response response = tradeController.createTrade(body, userDTO);

        //assert
        assertEquals(expected_response.getStatusCode(), response.getStatusCode());
        assertEquals(expected_response.getStatusMessage(), response.getStatusMessage());
        assertEquals(expected_response.getContentType(), response.getContentType());
        assertEquals(expected_response.getContent(), response.getContent());


    }

}


/*
 ArrayList<Card> stack = new ArrayList<>();
        ArrayList<Card> deck = new ArrayList<>();

        UserDTO userDTO = new UserDTO(
                "max",
                "maxi",
                "gamer",
                ":)",
                20,
                23,
                2,
                4,
                stack,
                deck
        );

        stack.add(new Card("1","TestCard", 0, "maxi", true, false, false ));
        stack.add(new Card("2","TestCard",0, "maxi", true, false, false ));
 */