package app.controllers;

import app.dtos.*;
import app.models.Card;
import app.models.User;
import app.repositories.PackageRepository;
import app.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.sql.SQLException;
import java.util.ArrayList;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class PackageController extends Controller {
    PackageRepository packageRepository;
    UserRepository userRepository;

    public PackageController(PackageRepository packageRepository, UserRepository userRepository){
        setPackageRepository(packageRepository);
        setUserRepository(userRepository);
    }

    public Response createPackage(String body, String authUsername){  //check if admin
        try {
            if(!authUsername.equals("admin")){
                return Response.getErrorResponse(HttpStatus.FORBIDDEN, "Provided user is not \"admin\"");
            }

            TypeReference<ArrayList<CardInfo>> cardInfoArray = new TypeReference<>() {}; //makes array from string
            ArrayList<CardInfo> cardInfos = getObjectMapper().readValue(body, cardInfoArray);

            if(!getPackageRepository().createPackage(cardInfos)){
                return Response.getNormalResponse(HttpStatus.CONFLICT, "At least one card in the packages already exists");
            }

            return Response.getNormalResponse(HttpStatus.CREATED, "Package and cards successfully created");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "Request was malformed");
        }
    }

    public Response openPackage(UserDTO user){
        try {

            if(user.getCoins() < 5){
                return Response.getErrorResponse(HttpStatus.FORBIDDEN, "Not enough money for buying a card package");
            }

            ArrayList<Card> openedPack = getPackageRepository().openPackage(user);

            if(openedPack.size() < 5){
                return Response.getErrorResponse(HttpStatus.NOT_FOUND, "No card package available for buying");
            }

            String openedPackJSON = getObjectMapper().writeValueAsString(openedPack);
            return Response.getNormalResponse(HttpStatus.OK, openedPackJSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "Request was malformed");
        }
    }
}


