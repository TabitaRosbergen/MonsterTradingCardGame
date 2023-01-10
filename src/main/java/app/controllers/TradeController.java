package app.controllers;

import app.daos.CardDao;
import app.dtos.UserDTO;
import app.models.Card;
import app.models.Trade;
import app.models.Type;
import app.repositories.TradeRepository;
import app.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.sql.SQLException;
import java.util.ArrayList;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class TradeController extends Controller {
    TradeRepository tradeRepository;
    UserRepository userRepository;
    CardDao cardDao;

    public TradeController(TradeRepository tradeRepository, UserRepository userRepository, CardDao cardDao){
        setTradeRepository(tradeRepository);
        setUserRepository(userRepository);
        setCardDao(cardDao);
    }

    public Response createTrade(String body,UserDTO user){
        try {
            Trade trade = getObjectMapper().readValue(body, Trade.class);

            Card card = user.findCardInStack(trade.getCardId());

            if(card == null || card.isInTrade()){
                return Response.getErrorResponse(HttpStatus.FORBIDDEN, "The deal contains a card that is not owned by the user or locked in the deck.");
            }

            getTradeRepository().createTrade(trade);
            card.setInTrade(true);
            getUserRepository().updateUser(user);

            return Response.getNormalResponse(HttpStatus.CREATED, "Trading deal successfully created");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "Request was malformed");
        } catch (SQLException e) {
            return Response.getErrorResponse(HttpStatus.CONFLICT, "A deal with this deal ID already exists.");
        }
    }

    public Response getTrades(){
        try {
            ArrayList<Trade> trades = getTradeRepository().getAllTrades();
            if (trades.isEmpty()) {
                return Response.getErrorResponse(HttpStatus.NO_CONTENT, "The request was fine, but there are no trading deals available");
            }
            String tradesJSON = getObjectMapper().writeValueAsString(trades);
            return Response.getNormalResponse(HttpStatus.OK, tradesJSON);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Response deleteTrade(String tradeId, UserDTO user){
            Trade trade = getTradeRepository().getTradeById(tradeId);

            if(trade == null){
                return Response.getErrorResponse(HttpStatus.NOT_FOUND, "The provided deal ID was not found.");
            }

            Card card = user.findCardInStack(trade.getCardId());

            if(card == null){
                return Response.getErrorResponse(HttpStatus.FORBIDDEN, "The deal contains a card that is not owned by the user.");
            }

            getTradeRepository().deleteTrade(trade);
            card.setInTrade(false);
            getUserRepository().updateUser(user);

            return Response.getNormalResponse(HttpStatus.OK, "Trading deal successfully deleted");
    }

    public Response acceptTrade(String cardId, String tradeId, UserDTO authUser){ //cardId -> card to accept trade

        cardId = cardId.replace("\"", ""); //Trim " from request body

        try {
            Trade trade = getTradeRepository().getTradeById(tradeId); //

            if(trade == null){
                return Response.getErrorResponse(HttpStatus.NOT_FOUND, "The provided deal ID was not found.");
            }

            Card card = authUser.findCardInStack(cardId); //find card in stack of user who wants to accept

            if(card == null || card.isInTrade()){
                return Response.getErrorResponse(HttpStatus.FORBIDDEN, "The deal contains a card that is not owned by the user, or the offered card is locked in the deck or trade.");
            }

            Type cardType = Type.monster;
            if (card.getName().contains("Spell")) {
                cardType = Type.spell;
            }

            //check for trade card requirements
            if(!trade.getType().equals(cardType.name()) || trade.getMinDamage() > card.getDamage()){
                return Response.getErrorResponse(HttpStatus.FORBIDDEN, "The offered card does not meet requirements (Type, MinimumDamage).");
            }

            //check if user is trying to trade with self
            String tradeCardOwnerUsername = getCardDao().read(trade.getCardId()).getOwner(); //get card and then owner name
            UserDTO tradeCardOwner = getUserRepository().getById(tradeCardOwnerUsername); //get owner with name
            if(tradeCardOwner.getUsername().equals(authUser.getUsername())){ //check for trade with self
                return Response.getErrorResponse(HttpStatus.FORBIDDEN, "User tried to trade with self");
            }

            //---Trade conditions cleared----

            Card tradeCard = tradeCardOwner.findCardInStack(trade.getCardId());

            card.setInTrade(false);
            tradeCard.setInTrade(false);

            tradeCardOwner.getStack().remove(tradeCard);
            tradeCardOwner.getStack().add(card);

            authUser.getStack().remove(card);
            authUser.getStack().add(tradeCard);

            getUserRepository().updateUser(authUser);
            getUserRepository().updateUser(tradeCardOwner);

            getTradeRepository().deleteTrade(trade);

            return Response.getNormalResponse(HttpStatus.OK, "Trading deal successfully executed.");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


