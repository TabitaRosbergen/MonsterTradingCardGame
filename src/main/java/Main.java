import app.App;
import server.Server;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        App app = new App();
        Server server = new Server(app, 10001);

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
