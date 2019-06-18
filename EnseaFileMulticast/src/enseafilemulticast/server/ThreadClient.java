
package enseafilemulticast.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.Socket;


public class ThreadClient implements Runnable {

    private Socket client;
    private boolean finCom = true;
    final static int tailleDonnee=1024;
    public String fichierRecu;

    public ThreadClient() {}

    public ThreadClient(Socket client, String fichierRecu) {
        this.client = client;
        this.fichierRecu = fichierRecu;
    }

    public void run() {
        try {
            communicationWithClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void communicationWithClient() throws IOException {
        BufferedReader comIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintStream comOut = new PrintStream(client.getOutputStream());
        RandomAccessFile out = new RandomAccessFile(fichierRecu.replace("\0", ""), "rw");
        System.out.println("fichier : "+ fichierRecu.replace("\0", ""));
        byte[] buffer = new byte[tailleDonnee];
        int k =0;
        while (finCom) {
            String data = comIn.readLine();
            System.out.println(k +" : Le client demande la trame : " + data);
            out.seek((long) Integer.parseInt(data) * tailleDonnee);
            out.read(buffer, 0,tailleDonnee); //Bug de la partie TCP, parfois _buffer_ est un vecteur zeros, on peut l'observer avec les 2 lignes commentées suivantes
                  //System.out.println("data : "+(long) Integer.parseInt(data));
                  //System.out.println(Arrays.toString(buffer));
            comOut.write(buffer);
            System.out.println("Envoi de la trame : "+ data);
            conditionFinCom(data);
            k+=1;
        }
        out.close();
    }

    // pas vraiment utilis�. � modifier pour le rendre utile.
    private boolean conditionFinCom(String data) {
        if (data.contains("$STOP$") == true || data.contains("$STOPCOM$") == true) {
            finCom = false;
        }
        return finCom;
    }
}