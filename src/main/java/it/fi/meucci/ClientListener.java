package it.fi.meucci;

import java.io.*;
import java.net.*;

public class ClientListener extends Thread {

    Client client = new Client();
    BufferedReader inDalServer;
    String stringaRicevutaDalServer;
    Socket mioSocket;

    // costruttore
    public ClientListener(Socket socket) throws IOException {
        mioSocket = socket;
        inDalServer = new BufferedReader(new InputStreamReader(mioSocket.getInputStream()));
    }

    public void ascolta() throws IOException, InterruptedException {
        for (;;) {
            stringaRicevutaDalServer = inDalServer.readLine();
            
            Thread.sleep(500);

            System.out.println("Stringa ricevuta: " + stringaRicevutaDalServer);

            Client.textArea.append(stringaRicevutaDalServer + "\n");

            if (stringaRicevutaDalServer.charAt(0) == '$' && stringaRicevutaDalServer.charAt(1) == 'e') {
                System.out.println("Disconnessione...");
                break;
            }
        }
    }

    public void run() {
        try {
            ascolta();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
