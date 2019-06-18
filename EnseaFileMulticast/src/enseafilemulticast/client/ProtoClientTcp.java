package enseafilemulticast.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.Socket;

public class ProtoClientTcp {
    
    public final static int tailleDonnee = 1024;
    public final static int tailleHeader = 19;
    public final static int taillePaquet = tailleDonnee + tailleHeader;
    public int destPort = 24247;
    public String destAddr = "192.168.1.1";
    public String fichierRecu;
    public boolean running = true;

    private Socket client;
    private boolean[] paquetRecu;

    public ProtoClientTcp(String destAddr, String destPort,String fichierRecu, boolean[] paquetRecu) {
        try {
            this.destPort = Integer.parseInt(destPort);
        }catch (NumberFormatException e) {}
        this.destAddr = destAddr;
        this.fichierRecu = fichierRecu;
        this.paquetRecu = paquetRecu;

        try {
            initialisation();
        } catch (IOException e) {
            System.out.println("erreur init tcp : " +e);
        }
    }

    public ProtoClientTcp() {}

    /// initialisation du client ///
    private void initialisation() throws IOException {
        client = new Socket(destAddr, destPort);
        System.out.println("Connexion établie");
    }

    public void launch() {
        System.out.println("Démarrage du transfert TCP");
        running = true;
        try {
            connexionTCP(); // Attente de connexion
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            arretClient(); // arret client
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Attends un client et creer une connexion
    private void connexionTCP() throws IOException {
        while (running) {
            transfertTrameManquante();
        }
        System.out.println("Fin du transfert TCP");
    }

    private void emission(String msg) throws IOException {
        PrintStream comOut = new PrintStream(client.getOutputStream());	
        System.out.println("envoi TCP : "+msg);
        comOut.println(msg);
    }

    private byte[] reception() throws IOException {
        BufferedReader comIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String data = comIn.readLine();
        return(data.getBytes());
    }

    private void arretClient() throws IOException {
        client.close();
        System.out.println("client arreté !");
    }

    private void transfertTrameManquante() throws IOException{
        RandomAccessFile out = new RandomAccessFile(fichierRecu.replace("\0", ""), "rw");
        int offset = 0;
        int k = 0;
        while(offset < paquetRecu.length) {
            if(!paquetRecu[offset]){
                emission(""+offset);
                System.out.println(k+" : Demande de la donnée au serveur: " +offset);
                byte[] buffer = reception();
                System.out.println("Donnée "+offset+" recue");
                out.seek((long) offset * tailleDonnee);
                out.write(buffer);
                System.out.println("Donnée "+offset+" écrite dans le fichier "+fichierRecu);
                paquetRecu[offset]=true;
                k+=1;
            }
            offset +=1;
        }
        out.close();
        running = false;
    }
}