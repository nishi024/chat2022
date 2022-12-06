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

    // componenti GUI
    JFrame frame1;
    JFrame frame2;
    JPanel panel1;
    JPanel panel2;
    JLabel labelNomeUtente;
    JLabel errore;
    JTextField textField;
    JTextField tfMessaggio;
    public static JTextArea textArea;
    JButton buttonInserisci;
    JButton buttonInvia;

    public void inserimentoNomeUtente() {
        frame1 = new JFrame();

        labelNomeUtente = new JLabel("Inserire nome utente");
        labelNomeUtente.setBounds(230, 130, 400, 30);
        labelNomeUtente.setFont(new Font("Itim", Font.BOLD, 18));
        labelNomeUtente.setForeground(Color.decode("#EEEEEE"));

        textField = new JTextField();
        textField.setBounds(240, 170, 160, 25);
        textField.setFont(new Font("Itim", Font.BOLD, 16));
        textField.setBackground(Color.decode("#EEEEEE"));

        buttonInserisci = new JButton("Inserisci");
        buttonInserisci.setBounds(270, 210, 100, 25);
        buttonInserisci.setFont(new Font("Itim", Font.BOLD, 16));
        buttonInserisci.setBackground(Color.decode("#FD7014"));
        buttonInserisci.addActionListener(this);
        buttonInserisci.addKeyListener(this);

        errore = new JLabel();
        errore.setSize(300, 25);
        errore.setForeground(Color.red);

        panel1 = new JPanel();
        panel1.setLayout(null);
        panel1.setPreferredSize(new Dimension(640, 360));
        panel1.setBackground(Color.decode("#222831"));
        panel1.add(labelNomeUtente);
        panel1.add(textField);
        panel1.add(buttonInserisci);
        panel1.add(errore);

        frame1.add(panel1);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setTitle("siChiacchera");
        frame1.setResizable(false);
        frame1.pack();
        frame1.setLocationRelativeTo(null);
        frame1.setVisible(true);
    }

    public void chatGUI() {
        frame2 = new JFrame();

        textArea = new JTextArea();
        textArea.setBounds(10, 10, 834, 425);
        textArea.setFont(new Font("Itim", Font.BOLD, 18));
        textArea.setBackground(Color.decode("#393E46"));
        textArea.setForeground(Color.decode("#EEEEEE"));
        textArea.setEditable(false);
        textArea.append(
                "Ti sei unito alla chat\nComandi:\n$b - Messaggio pubblico\n$v - Messaggio privato\n$e - Abbandona chat\n\n");

        // per rendere la textArea scrollabile (NON FUNZIONA)
        JScrollPane scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        tfMessaggio = new JTextField();
        tfMessaggio.setBounds(10, 445, 744, 25);
        tfMessaggio.setFont(new Font("Itim", Font.BOLD, 16));
        tfMessaggio.setBackground(Color.decode("#EEEEEE"));

        buttonInvia = new JButton("Invia");
        buttonInvia.setBounds(764, 445, 80, 25);
        buttonInvia.setFont(new Font("Itim", Font.BOLD, 16));
        buttonInvia.setBackground(Color.decode("#FD7014"));
        buttonInvia.addActionListener(this);
        buttonInvia.addKeyListener(this);

        panel2 = new JPanel();
        panel2.setLayout(null);
        panel2.setPreferredSize(new Dimension(854, 480));
        panel2.setBackground(Color.decode("#222831"));
        panel2.add(buttonInvia);
        panel2.add(textArea);
        panel2.add(tfMessaggio);
        panel2.add(scroll);

        frame2.add(panel2);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setTitle("siChiacchera | Utente: " + nomeUtente);
        frame2.setResizable(false);
        frame2.pack();
        frame2.setLocationRelativeTo(null);
        frame2.setVisible(true);

        listener.start();

        // non funziona
        // textArea.setText(serverListener.stampaUtentiConnessi());
        // System.out.println(serverListener.stampaUtentiConnessi());
    }

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

        // controllo se viene premuto il pulsante per l'inserimento del nome utente
        if (e.getSource().equals(buttonInserisci)) {
            nomeUtente = textField.getText();

            try {
                // controllo che il nome non sia già stato inserito (NON FUNZIONA)
                if (!serverListener.verify(nomeUtente)) {
                    errore.setText("Nome utente gia' inserito");
                    errore.setLocation(250, 250);
                } else if (nomeUtente.contains(" ")) {
                    errore.setText("Il nome utente non puo' contenere spazi");
                    errore.setLocation(205, 250);
                } else if (nomeUtente.equals("")) {
                    errore.setText("Inserire un nome utente");
                    errore.setLocation(250, 250);
                } else {
                    outVersoServer.writeBytes("/" + nomeUtente + '\n');

                    // Chiudo la finestra per l'inserimento del nome utente
                    frame1.dispose();
                    // apro la finestra della chat
                    chatGUI();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else { // controllo se viene premuto il pulsante per l'invio dei messaggi
            String messaggio = tfMessaggio.getText();
            tfMessaggio.setText("");

            if (messaggio.charAt(0) != '$') {
                textArea.append(nomeUtente + ": " + messaggio + "\n");
            }

            if (!messaggio.isEmpty()) {
                try {
                    outVersoServer.writeBytes(messaggio + '\n');
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            // chiusura della chat se viene mandato il messaggio "$e"
            if (messaggio.equals("$e")) {
                frame2.dispose();
            }
        }

    }

    // metodi sovrascritti dall'implementazione KeyListener
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