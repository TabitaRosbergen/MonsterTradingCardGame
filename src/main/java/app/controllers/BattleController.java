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
public class BattleController {
    public static Exchanger<Player> userExchanger = new Exchanger<>();
    private UserRepository userRepository;
    public BattleController(UserRepository userRepository){

        setUserRepository(userRepository);
    }

    public Response battleRequest(UserDTO user1){
        //Check if right number of Cards in deck

        if(user1.getDeck().size() != 4){
            return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "Deck needs 4 cards!");
        }
        try {

            Player player1 = new Player(user1);
            Player player2 = userExchanger.exchange(player1); //waits until other thread calls exchange

            //So now in every thread user1 is the user who sent the request and user 2 is the opponent
            //to determine which thread should make the battle, the two usernames are compared with compareTo() which
            //returns an int. only if string1 == string2 it would return 0, which can't be because username is unique
            //if user1 and user2 swapped (other thread) result of compareTo() is negative
            // => one of the two has a negative number for sure

            if(player1.getUser().getUsername().compareTo(player2.getUser().getUsername()) > 0){

                //Here this thread completes the battle

                BattleService battleService = new BattleService(player1, player2);
                battleService.startBattle();

                //Write everything to DB
                userRepository.updateUser(player1.getUser());
                userRepository.updateUser(player2.getUser());

            }
            else{
                //here the thread waits for the other thr. to complete the battle
                //at the end of the battle the battleLog is set => needs volatile so compiler leaves it alone
                while(player1.getBattleLog().isEmpty()){
                    continue;
                }
            }
            //return in Plaintext
            return Response.getPlaintextResponse(HttpStatus.OK, player1.getBattleLog());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
