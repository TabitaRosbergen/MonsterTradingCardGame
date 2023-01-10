package app.controllers;

import app.dtos.UserDTO;
import app.dtos.UserData;
import app.dtos.UserLogin;
import app.dtos.UserStats;
import app.models.Card;
import app.models.User;
import app.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Request;
import server.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class UserController extends Controller{
    UserRepository userRepository;

    public UserController(UserRepository userRepository){

        setUserRepository(userRepository);
    }

    public Response createUser(String body){
        try {
            //create a userObject
            User userData = getObjectMapper().readValue(body, User.class);
            if(getUserRepository().createUser(userData) == null){ //user schon in db
                return Response.getErrorResponse(HttpStatus.CONFLICT, "Username already exists!");
            }

            return Response.getNormalResponse(HttpStatus.CREATED, body);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "request was malformed");
        }
    }

    /*
    public void getUsers(){ //TODO: RETURNTYPE muss respose sein + implementieren
        try {
            ArrayList<UserDTO> users = getUserRepository().getAll();
            String userJSON = getObjectMapper().writeValueAsString(users);
            System.out.println(userJSON); //TEST //////////////////////////////////////////////////

            //TODO return RESPONSE


        } catch (JsonProcessingException e) {
            e.printStackTrace();

            //TODO return RESPONSE
        }
    }
*/

    public Response editUser(String body, String username, String authUsername) {

        if(!username.equals(authUsername) && !authUsername.equals("admin")){
            return Response.getErrorResponse(HttpStatus.UNAUTHORIZED, "Access token is missing or invalid");
        }

        if(getUserRepository().getById(username) == null){
            return Response.getErrorResponse(HttpStatus.NOT_FOUND, "User was not found");
        }

        try {

            UserData userData = getObjectMapper().readValue(body, UserData.class);
            getUserRepository().editUser(username, userData);

            return Response.getNormalResponse(HttpStatus.OK);
        } catch (JsonProcessingException e) {
            return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "request was malformed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Response getUser(String username, String authUsername){

        if(!username.equals(authUsername) && !authUsername.equals("admin")){
            return Response.getErrorResponse(HttpStatus.UNAUTHORIZED, "Access token is missing or invalid");
        }

        try {
            UserData userData = getUserRepository().getUserData(username);
            String userDataJSON = getObjectMapper().writeValueAsString(userData);
            if (userData == null) {
                return Response.getErrorResponse(HttpStatus.NOT_FOUND, "User does not exist");
            }
            return Response.getNormalResponse(HttpStatus.OK, userDataJSON);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "Request was malformed");
        }
    }

    public Response getUserCards(String authUsername){ //user exists because authenticated -> no need to check null
        try {
            ArrayList<Card> userCards = getUserRepository().getUserCards(authUsername);

            if(userCards==null || userCards.isEmpty()){
                return Response.getErrorResponse(HttpStatus.NO_CONTENT, "The request was fine, but the user doesn't have any cards");
            }


            String userCardsJSON = getObjectMapper().writeValueAsString(userCards);

            return Response.getNormalResponse(HttpStatus.OK, userCardsJSON);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Response getUserDeck(String authUsername){ //user exists because authenticated -> no need to check null

        try {
            ArrayList<Card> userDeck = getUserRepository().getUserDeck(authUsername);

            if(userDeck.isEmpty()){
                return Response.getErrorResponse(HttpStatus.NO_CONTENT, "The request was fine, but the deck doesn't have any cards");
            }

            String userDeckJSON = getObjectMapper().writeValueAsString(userDeck);

            return Response.getNormalResponse(HttpStatus.OK, userDeckJSON);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Response createUserDeck(String body, String authUsername){

        try {
            TypeReference<ArrayList<String>> cardIdsType = new TypeReference<ArrayList<String>>() {};
            ArrayList<String> cardIds = getObjectMapper().readValue(body, cardIdsType);

            if(cardIds.size() != 4){
                return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "The provided deck did not include the required amount of cards");
            }

            if(!getUserRepository().setUserDeck(cardIds, authUsername)) { //user doesn't have all the cards or card is in trade
                return Response.getErrorResponse(HttpStatus.FORBIDDEN, "At least one of the provided cards does not belong to the user or is not available.");
            }

            return Response.getNormalResponse(HttpStatus.OK, "The deck has been successfully configured");

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "request was malformed");
        }
    }

    public Response getUserStats(String authUsername){

        try {
            UserStats userStats = getUserRepository().getUserStats(authUsername);
            String userDataJSON = getObjectMapper().writeValueAsString(userStats);
            if (userStats == null) {
                return Response.getErrorResponse(HttpStatus.NOT_FOUND, "User does not exist");
            }
            return Response.getNormalResponse(HttpStatus.OK, userDataJSON);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Response getScores(){

        try {
            ArrayList<UserStats> scores = getUserRepository().getScores();
            if (scores.isEmpty()) {
                return Response.getErrorResponse(HttpStatus.NOT_FOUND, "There are no users");
            }

            String userDataJSON = getObjectMapper().writeValueAsString(scores);
            return Response.getNormalResponse(HttpStatus.OK, userDataJSON);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Response login(String body){
        try {
            UserLogin loginData = getObjectMapper().readValue(body, UserLogin.class);

            if(getUserRepository().checkUserLogin(loginData.getUsername(), loginData.getPassword())){
                return Response.getNormalResponse(HttpStatus.OK, "\"" + loginData.getUsername() + "-mtcgToken\"");
            }
            return Response.getErrorResponse(HttpStatus.UNAUTHORIZED, "Wrong username or password!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "request was malformed");
        }
    }
}


