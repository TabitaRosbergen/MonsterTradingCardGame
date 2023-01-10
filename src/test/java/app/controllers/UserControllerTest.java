package app.controllers;

import app.dtos.UserDTO;
import app.dtos.UserData;
import app.models.Card;
import app.models.User;
import app.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.ContentType;
import http.HttpStatus;
import org.junit.jupiter.api.*;
import server.Response;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserControllerTest {

    //dependencies
    User user;
    UserRepository userRepository;

    UserController userController;

    @BeforeEach
    void setUp(){
        user = mock(User.class);
        userRepository = mock(UserRepository.class);

        userController = new UserController(userRepository);
    }

    @Test
    @DisplayName("createUser -> fails because user already exists")
    void testCreateUser() throws JsonProcessingException {
        //arrange
        Response expected_response = new Response(
                HttpStatus.CONFLICT,
                ContentType.JSON,
                "{ \"data\": null, \"error\": \"Username already exists!\" }"
                );

        String body = "{\"Username\": \"test\", \"Password\": \"test\"}";

        when(userRepository.createUser(user)).thenReturn(null);

        //act
        Response response = userController.createUser(body);

        //assert
        assertEquals(expected_response.getStatusCode(), response.getStatusCode());
        assertEquals(expected_response.getStatusMessage(), response.getStatusMessage());
        assertEquals(expected_response.getContentType(), response.getContentType());
        assertEquals(expected_response.getContent(), response.getContent());

    }

    @Test
    @DisplayName("editUser user not authenticated")
    void testEditUser_notAuthenticated(){

        //arrange
        String body = "{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}";
        String username = "uwe";
        String authUsername = "lars";


        Response expected_response = new Response(
                HttpStatus.UNAUTHORIZED,
                ContentType.JSON,
                "{ \"data\": null, \"error\": \"Access token is missing or invalid\" }"
        );

        //act
        Response response = userController.editUser(body, username, authUsername);

        //assert
        assertEquals(expected_response.getStatusCode(), response.getStatusCode());
        assertEquals(expected_response.getStatusMessage(), response.getStatusMessage());
        assertEquals(expected_response.getContentType(), response.getContentType());
        assertEquals(expected_response.getContent(), response.getContent());


    }
    @Test
    @DisplayName("editUser admin")
    void testEditUser_admin() throws SQLException {
        //arrange
        UserDTO userDTO = mock(UserDTO.class);
        UserData userData = mock(UserData.class);

        String body = "{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}";
        String username = "admin";
        String authUsername = "admin";
        when(userRepository.getById(username)).thenReturn(userDTO);

        //act
        userController.editUser(body, username, authUsername);

        //assert
        verify(userRepository).editUser(any(String.class), any()); //argument matcher -> looks at type not at instance

    }

    @Test
    @DisplayName("edit User -> user was not found")
    void testEditUser_userNotFound(){

        //arrange
        String body = "{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}";
        String username = "uwe";
        String authUsername = "uwe";

        Response expected_response = new Response(
                HttpStatus.NOT_FOUND,
                ContentType.JSON,
                "{ \"data\": null, \"error\": \"User was not found\" }"
        );

        when(userRepository.getById(username)).thenReturn(null);

        //act
        Response response = userController.editUser(body, username, authUsername);

        //assert
        assertEquals(expected_response.getStatusCode(), response.getStatusCode());
        assertEquals(expected_response.getStatusMessage(), response.getStatusMessage());
        assertEquals(expected_response.getContentType(), response.getContentType());
        assertEquals(expected_response.getContent(), response.getContent());


    }

    @Test
    @DisplayName("edit User -> user authenticated")
    void testEditUser_userAuthenticated() throws SQLException {

        //arrange
        UserDTO userDTO = mock(UserDTO.class);
        UserData userData = mock(UserData.class);

        String body = "{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\", \"Image\": \":-)\"}";
        String username = "uwe";
        String authUsername = "uwe";
        when(userRepository.getById(username)).thenReturn(userDTO);

        //act
        userController.editUser(body, username, authUsername);

        //assert
        verify(userRepository).editUser(any(String.class), any()); //argument matcher -> looks at type not at instance
    }

    @Test
    @DisplayName("getUserCards -> no cards")
    void testGetUserCards(){
        //arrange
        String authUsername = "uwe";
        ArrayList<Card> userCards;
        when(userRepository.getUserCards(authUsername)).thenReturn(null);

        Response expected_response = new Response(
                HttpStatus.NO_CONTENT,
                ContentType.JSON,
                "{ \"data\": null, \"error\": \"The request was fine, but the user doesn't have any cards\" }"
        );

        //act
        Response response = userController.getUserCards(authUsername);

        //assert
        assertEquals(expected_response.getStatusCode(), response.getStatusCode());
        assertEquals(expected_response.getStatusMessage(), response.getStatusMessage());
        assertEquals(expected_response.getContentType(), response.getContentType());
        assertEquals(expected_response.getContent(), response.getContent());


    }



}