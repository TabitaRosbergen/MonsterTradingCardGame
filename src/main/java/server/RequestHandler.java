package server;

import app.App;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class RequestHandler implements Runnable{
    private Request request;
    private Response response;
    private PrintWriter outputStream;
    private BufferedReader inputStream;
    private Socket clientSocket;
    private App app;

    public RequestHandler(Socket clientSocket, App app) { //in Server aufgerufen setzt variablen
        this.app = app;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() { //kommen von Server thread gestartet
        System.out.println(Thread.currentThread().getName());
        try {
            setInputStream(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
            setRequest(new Request(getInputStream()));
            setOutputStream(new PrintWriter(clientSocket.getOutputStream(), true));

            if (request.getPathname() == null) {
                setResponse(new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.TEXT,
                        ""
                ));
            } else {
                setResponse(getApp().handleRequest(request));
            }

            getOutputStream().write(getResponse().build());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (getOutputStream() != null) {
                    getOutputStream().close();
                }
                if (getInputStream() != null) {
                    getInputStream().close();
                    getClientSocket().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
