import app.App;
import server.Server;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        App app = new App();
        Server server = new Server(app, 10001);

        //TEST userControllerMethods////////////////////////////////////////////
        //GET-----------------------------------------------------------------
        //app.userController.getUsers();
        //CREATE--------------------------------------------------------------
        //app.userController.createUser("{\"Username\":\"rudi\", \"Password\":\"bla\"}");
        //EDIT----------------------------------------------------------------
        //app.userController.editUser("{\"Name\": \"Kienboeck\",  \"Bio\": \"me playin...\",\"Image\": \":-)\"}");
        //app.userController.editUser("{\"Name\": \"Kienboeck\"}");

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
