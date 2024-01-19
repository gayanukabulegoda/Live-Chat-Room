package lk.ijse.liveChatRoom;

import lk.ijse.liveChatRoom.server.Server;

public class ServerInitializer {
    public static void main(String[] args) {
        Server server = new Server();
        server.initialize();
    }
}
