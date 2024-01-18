package lk.ijse.liveChatRoom.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    ServerSocket serverSocket;
    Socket localSocket;
    DataInputStream dataInputStream;
    String message = "";

    public void initialize() {

    }
}
