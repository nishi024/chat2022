package it.fi.meucci;

import java.net.*;

public class Server {

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(6789);
            System.out.println("il server è in esecuzione");
            ServerListener hashMap = new ServerListener();

            for (;;) {
                Socket socket = serverSocket.accept();
                ServerThread serverListener = new ServerThread(socket, serverSocket, hashMap);
                serverListener.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Errore durante l'istanza del server!");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
