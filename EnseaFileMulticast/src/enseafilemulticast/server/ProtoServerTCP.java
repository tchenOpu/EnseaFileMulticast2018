
package enseafilemulticast.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ProtoServerTCP {

    public String fichierRecu;
    public int port = 24247;
    private ServerSocket tcp;
    private Socket client;
    public boolean running = true;
    public ProtoServerTCP(int port) {
        this.port = port;
        try {
                initialisation(); // initialisation
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    public ProtoServerTCP(int port, String fichierRecu) {
        this.port = port;
        this.fichierRecu = fichierRecu;//.replace("\0", "");
        try {
                initialisation(); // initialisation
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    public ProtoServerTCP() {
    }

    /// initialisation du serveurUDP ///
    private void initialisation() throws IOException {
        System.out.println("Initialisation...");
        tcp = new ServerSocket(port);
        System.out.println("Initialisation OK ! ");
    }

    public void launch() {
        System.out.println("Initialisation terminée");
        running = true;
        try {
                connexionTCP(); // Attente de connexion
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                arretServer(); // arret serveur
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    // Attends un client et creer une connexion
    private void connexionTCP() throws IOException {
        System.out.println("connexion ...");
        while (running) {
                client = tcp.accept();
                Thread newClient = new Thread(new ThreadClient(client,fichierRecu));
                newClient.start();
        }
    }

    private void arretServer() throws IOException {
        client.close();
        tcp.close();
        System.out.println("Serveur arreté !");
    }
}