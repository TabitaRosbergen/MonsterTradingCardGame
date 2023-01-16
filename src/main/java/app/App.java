package app;

import app.controllers.BattleController;
import app.controllers.PackageController;
import app.controllers.TradeController;
import app.controllers.UserController;
import app.daos.CardDao;
import app.daos.TradeDao;
import app.daos.UserDao;
import app.dtos.UserDTO;
import app.models.User;
import app.repositories.PackageRepository;
import app.repositories.TradeRepository;
import app.repositories.UserRepository;
import app.services.DatabaseService;
import http.HttpStatus;
import http.Method;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Request;
import server.Response;

import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.Integer.parseInt;


@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class App {
    private UserController userController;
    private PackageController packageController;
    private TradeController tradeController;
    private BattleController battleController;

    private UserRepository userRepository;

    private Connection connection;

    public App() {
        try {
            setConnection(new DatabaseService().getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    //DAOS
        UserDao userDao = new UserDao(getConnection());
        CardDao cardDao = new CardDao(getConnection());
        TradeDao tradeDao = new TradeDao(getConnection());

    //REPOSITORIES
        UserRepository userRepository = new UserRepository(userDao, cardDao);
        PackageRepository packageRepository = new PackageRepository(userRepository, cardDao);
        TradeRepository tradeRepository = new TradeRepository(userRepository, cardDao, tradeDao);

        setUserRepository(userRepository);

    //CONTROLLERS
        UserController userController = new UserController(userRepository);
        PackageController packageController = new PackageController(packageRepository, userRepository);
        TradeController tradeController = new TradeController(tradeRepository, userRepository, cardDao);
        BattleController battleController = new BattleController(userRepository);

        setBattleController(battleController);
        setUserController(userController);
        setPackageController(packageController);
        setTradeController(tradeController);
    }

    public Response handleRequest(Request request) {
        //Auth. -> switch case mit auth in eigenen Block (wenn nicht auth dann nur createn oder login

        if (request.getPathname().equals("/sessions") && request.getMethod() == Method.POST) {
            return getUserController().login(request.getBody());
        }

        if (request.getPathname().equals("/users") && request.getMethod() == Method.POST) {
            return getUserController().createUser(request.getBody());
        }

        String authUsername = request.getToken();
        UserDTO authUser = getUserRepository().getById(authUsername);
        if(authUser == null){ //no token provided or token is invalid
            return Response.getErrorResponse(HttpStatus.UNAUTHORIZED, "Access token is missing or invalid");
        }

        //check method
        switch (request.getMethod()) {
            case GET: {
                if (request.getPathname().matches("/users/[A-Za-z]+")) {
                    String username = request.getPathname().split("/")[2];
                    return getUserController().getUser(username, authUsername);
                }
                if (request.getPathname().equals("/stats")) {
                    return getUserController().getUserStats(authUsername);
                }
                if (request.getPathname().equals("/scores")) {
                    return getUserController().getScores();
                }
                if (request.getPathname().equals("/tradings")) {
                    return getTradeController().getTrades();
                }
                if (request.getPathname().equals("/cards")) {
                    return getUserController().getUserCards(authUsername);
                }
                if (request.getPathname().equals("/decks")) {
                    return getUserController().getUserDeck(authUsername);
                }
                break;
            }
            case POST: {
                if (request.getPathname().equals("/packages")) {
                    return getPackageController().createPackage(request.getBody(), authUsername);
                }
                if(request.getPathname().equals("/transactions/packages")){
                    return getPackageController().openPackage(authUser);
                }
                if (request.getPathname().equals("/tradings")) {
                    return getTradeController().createTrade(request.getBody(), authUser);
                }
                if (request.getPathname().matches("/tradings/.+")) {
                    String tradeId = request.getPathname().split("/")[2];
                    return getTradeController().acceptTrade(request.getBody(), tradeId, authUser);
                }
                if(request.getPathname().matches("/battles")){
                    return getBattleController().battleRequest(authUser);
                }
                break;
            }
            case PUT: {
                if (request.getPathname().matches("/users/[A-Za-z]+")) {
                    String username = request.getPathname().split("/")[2];
                    return getUserController().editUser(request.getBody(), username, authUsername);
                }
                if (request.getPathname().matches("/decks")) {
                    return getUserController().createUserDeck(request.getBody(), authUsername);
                }
                break;
            }
            case DELETE: {
                if (request.getPathname().matches("/tradings/.+")) {
                    String tradeId = request.getPathname().split("/")[2];
                    return getTradeController().deleteTrade(tradeId, authUser);
                }
                break;
            }
        }
        return Response.getErrorResponse(HttpStatus.BAD_REQUEST, "The requested path does not exist");
    }
}


