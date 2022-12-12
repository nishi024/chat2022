package it.fi.meucci;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client implements ActionListener, KeyListener {
    String nomeServer = "localhost";
    int portaServer = 6789;
    Socket miosocket;
    BufferedReader tastiera;
    String stringaUtente;
    String stringaRicevutaDalServer;
    String nomeUtente;
    DataOutputStream outVersoServer;
    ClientListener listener;
    ServerListener serverListener = new ServerListener();



    public Socket connetti() {
        System.out.println("Ingresso nella chat");
        try {

            tastiera = new BufferedReader(new InputStreamReader(System.in));

            miosocket = new Socket(nomeServer, portaServer);

            outVersoServer = new DataOutputStream(miosocket.getOutputStream());

            listener = new ClientListener(miosocket);

        } catch (UnknownHostException e) {
            System.err.println("Host sconosciuto");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Errore durante la connessione");
            System.exit(1);
        }
        return miosocket;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

       
        if (e.getSource().equals(buttonInserisci)) { // controllo se viene premuto il pulsante per l'inserimento del nome utente
            nomeUtente = textField.getText();

 else { 
            String messaggio = tfMessaggio.getText();//bottone invio messaggi
            tfMessaggio.setText("");

            if (messaggio.charAt(0) != '#') {
                textArea.append(nomeUtente + ": " + messaggio + "\n");
            }

            if (!messaggio.isEmpty()) {
                try {
                    outVersoServer.writeBytes(messaggio + '\n');
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            
            if (messaggio.equals("#chiudi")) {// chat viene chiusa con il comando"#chiudi"
                frame2.dispose();
            }
        }

    }

    // metodi  dall'implementazione KeyListener
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.connetti();
        client.inserimentoNomeUtente();
    }

}
