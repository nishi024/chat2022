package it.fi.meucci;

import java.io.*;
import java.net.*;

public class ServerThread extends Thread {
    ServerSocket server = null;
    ServerListener serverListener;
    Socket client = null;
    String stringaRicevuta = null;
    BufferedReader inDalClient;
    DataOutputStream outVersoClient;
    String nomeUtente = null;
    String destinatario;
    Client chat = new Client();

    public ServerThread(Socket socket, ServerSocket server, ServerListener writer1) throws Exception{
        this.client = socket;
        this.server = server;
        this.serverListener = writer1;
        inDalClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        outVersoClient = new DataOutputStream(client.getOutputStream());
    }

    public void run() {
        try {
            comunica();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void comunica() throws Exception{

        for (;;) {
            //leggo la stringa inviata dal client
            stringaRicevuta = inDalClient.readLine(); 

            // controllo che sia un nome utente (viene aggiunto un '/' all'inizio del nomeUtente)
            if (stringaRicevuta.charAt(0) == '/') { 

                // rimuovo il primo carattere della stringa
                stringaRicevuta = stringaRicevuta.substring(1);

                //se il controllo va a buon fine allora il client entra nella chat, senno darà errore e richiederà l'inserimento dei dati
                if(serverListener.verify(stringaRicevuta)){ 
                    nomeUtente = stringaRicevuta;
                    serverListener.aggiungiSocket(nomeUtente, this); // this fa passare il ServerThread corrente
                    System.out.println("Aggiunto utente: " + nomeUtente);
                }

            } else if(stringaRicevuta.charAt(0) == '#' && stringaRicevuta.charAt(1) == 'p'){ //TIPS: LE "text" VENGONO USATE PER LA STRINGA MENTRE 'text' PER LE CHAR
                //confermo la selezione del PUBBLIC e chiedo al client il messaggio da inviare a tutti
                outVersoClient.writeBytes("Selezionato messaggio Pubblico, inserire messaggio" + '\n'); 
                stringaRicevuta = inDalClient.readLine(); //aspetto l'invio del messaggio
                //funzione del thread writer che esegue l'invio del messaggio a tutti i client connessi
                serverListener.sendAll(stringaRicevuta, nomeUtente); 
                // outVersoClient.writeBytes("Messaggio inviato correttamente." + '\n');
                System.out.println("SERVER DICE: HO APPENA INVIATO A TUTTI UN MESSAGGIO");

            } else if(stringaRicevuta.charAt(0) == '#' && stringaRicevuta.charAt(1) == 'c'){ //faccio uscire dalla chat l'utente
                serverListener.remove(nomeUtente);
                break;
            } else {
                outVersoClient.writeBytes("Comando non valido" + '\n');
            }
        }
        outVersoClient.close();
        inDalClient.close();
        System.out.println("Chiusura socket: " + client);
        client.close();
    }

    public void messaggia(String messaggio) throws Exception{
        outVersoClient.writeBytes(messaggio + "\n");
    }
}
