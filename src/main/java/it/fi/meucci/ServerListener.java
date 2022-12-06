package it.fi.meucci;

import java.util.*;

public class ServerListener {
    HashMap<String, ServerThread> handler = new HashMap<String, ServerThread>();//salva i client connessi


    public void stampaUtentiConnessi(String a) throws Exception {//stampa i client connessi 

        if (handler.size() > 0) {
            for (String i : handler.keySet()) {
                handler.get(i).messaggia(handler.keySet() + " ");
            }
        }

    }

    public void aggiungiSocket(String nomeUtente, ServerThread thread) throws Exception {
        handler.put(nomeUtente, thread);

        for (String i : handler.keySet()) {
            if (i != nomeUtente) {
                handler.get(i).messaggia(nomeUtente + " si e' connesso");
            }
        }
    }

    public void sendAll(String messaggio, String mittente) throws Exception {
        for (String i : handler.keySet()) {
            if (i != mittente) {
                handler.get(i).messaggia(mittente + ": " + messaggio);
            }
        }
    }



    public void remove(String nome) throws Exception {
        handler.remove(nome);

        for (ServerThread thread : handler.values()) {
            thread.messaggia(nome + " si e' disconnesso");
        }
    }

    public boolean verify(String nome) throws Exception {

        if (handler.containsKey(nome)) {
            return false;
        }

        return true;
    }

}
