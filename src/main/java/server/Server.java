package server;

import app.App;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.*;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private App app;
    private int port;

    //Constructor
    public Server(App app, int port){
        setApp(app);
        setPort(port);
    }

    public void start() throws IOException{
        setServerSocket(new ServerSocket(getPort()));
        run();

    }

    private void run(){
        int counter =0;
        while(true){
            try {
                setClientSocket(getServerSocket().accept());
                //server makes thread for client -> waits for more incoming requests
                RequestHandler task = new RequestHandler(getClientSocket(), getApp());
                Thread thread = new Thread(task);
                thread.setName(String.valueOf(counter));
                System.out.println(thread.getName() + "working");
                ++counter;
                thread.start();

            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }
}
