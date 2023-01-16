package app.controllers;

import app.dtos.UserDTO;
import app.models.Player;
import app.repositories.UserRepository;
import app.services.BattleService;
import http.HttpStatus;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.util.concurrent.Exchanger;
@Setter
@Getter
public class BattleController { //request kommt rein und queue -> warteschlange ist drinnen
    public static Exchanger<Player> userExchanger = new Exchanger<>();
    private UserRepository userRepository;

    public BattleController(UserRepository userRepository){
        setUserRepository(userRepository);
    }

    public Response battleRequest(UserDTO user1){
        if(user1.getDeck().size() != 5){
            return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "Deck needs 5 cards!");
        }
        try {
            Player player1 = new Player(user1);
            Player player2 = userExchanger.exchange(player1); //wartet bis anderer Thread exchange

            if(player1.getUser().getUsername().compareTo(player2.getUser().getUsername()) > 0){ //if swapped (other thread) result of compare to os negative
                //Here this thread completes the battle
                BattleService battleService = new BattleService(player1, player2);
                battleService.startBattle();
                userRepository.updateUser(player1.getUser());
                userRepository.updateUser(player2.getUser());

            }
            else{ //here the thread waits for the other thr. to complete the battle
                while(player1.getBattleLog().isEmpty()){
                    continue;
                }
            }
            return Response.getPlaintextResponse(HttpStatus.OK, player1.getBattleLog());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
