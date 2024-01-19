package lk.ijse.liveChatRoom.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private final ArrayList<ClientHandler> clients = new ArrayList<>();

    public void initialize() {
        try {
            ServerSocket serverSocket = new ServerSocket(3400);
            System.out.println("Server Initialized!!!");

            while (true) {
                Socket localSocket = serverSocket.accept();
                System.out.println("New Client Connected!!!");

                ClientHandler clientThread = new ClientHandler(localSocket,clients);
                clients.add(clientThread);
                clientThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
