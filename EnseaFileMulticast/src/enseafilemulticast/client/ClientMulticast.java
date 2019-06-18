
package enseafilemulticast.client;

import enseafilemulticast.graphic.GraphicWindow;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class ClientMulticast {

    public final static String adresseMulticast = "239.168.28.8";
    public final static int port = 60008;

    public final static int tailleDonnee = 1024;
    public final static int tailleHeader = 19;
    public final static int taillePaquet = tailleDonnee + tailleHeader;
    public final static int tailleFM = 272;


    public boolean[] paquetRecu; // tableau permettant de repertorier les paquets recus
    public boolean fichierComplet = false;
    public boolean finTransfert = false;
    public String idAppli;
    public String typeFichier;
    public int offset;
    public int nbPaquet;
    public int idFichier;
    public String nomFichier;
    public String cheminTelechargement = "";
    public long dateFichier;


    private int tailleDonneeTrans;
//	private int paquetPrec = 0;
    private boolean init = true;	
    private File fichierRecu;
    private long tailleFichier = 1;
    private final byte[] buffer = new byte[taillePaquet]; // buffer permettant de recevoir les
//	private long tailleActuelleFichier = 0;
                                                    // ou non.

                                                                                    // paquets du groupe multicast.


    public ClientMulticast() {
    }

    public ClientMulticast(String cheminTelechargement) {
        this.cheminTelechargement = cheminTelechargement;
    }


    /*
     * Cette methodes permet de recuperer les differentes informations contenues
     * dans le header et de les convertir en des formats plus coherents.
     */
    private void lectureHeader() throws UnsupportedEncodingException {
        // Identifiant appli
        idAppli = new String(buffer, 0, 3, "UTF-8");

        // Type de paquet
        typeFichier = new String(buffer, 3, 2, "UTF-8");

        // Offset
        ByteBuffer b = ByteBuffer.wrap(buffer, 5, 4);
        b.order(ByteOrder.BIG_ENDIAN);
        offset = b.getInt();

        // Taille donnee
        b = ByteBuffer.wrap(buffer, 9, 2);
        b.order(ByteOrder.BIG_ENDIAN);
        tailleDonneeTrans = b.getShort();

        // Nombre de paquets
        b = ByteBuffer.wrap(buffer, 11, 4);
        b.order(ByteOrder.BIG_ENDIAN);
        nbPaquet = b.getInt();

        // Identifiant du fichier
        b = ByteBuffer.wrap(buffer, 15, 4);
        b.order(ByteOrder.BIG_ENDIAN);
        idFichier = b.getInt();
    }

    /*
     * Cette methodes permet de recuperer les differentes informations contenues
     * dans les metadonnees et de les convertir en des formats plus coherents.
     */
    private void lectureFM() throws UnsupportedEncodingException {
        // Nom du Fichier
        nomFichier = new String(buffer, tailleHeader, 256, "UTF-8");

        // Taille du fichier
        ByteBuffer b = ByteBuffer.wrap(buffer, tailleHeader + 256, 8);
        b.order(ByteOrder.BIG_ENDIAN);
        tailleFichier = b.getLong();

        // Date du fichier
        b = ByteBuffer.wrap(buffer, tailleHeader + 256 + 8, 8);
        b.order(ByteOrder.BIG_ENDIAN);
        dateFichier = b.getLong();
    }

// calcule le taux de transfert. Et permet de finir la com.
// fonctionne bien si on modifie l'entete FM.
    private void statsTransfert() {
        float couverture; 
        int nbPaquetManquant = 0;
        int i = 0;

        while(i < nbPaquet + 1){
            if(!paquetRecu[i]){
                nbPaquetManquant+=1;
            }
            i+=1;
        }

        if(nbPaquetManquant == 0){
            finTransfert = true;
            fichierComplet = true;
        }

        if(finTransfert){
            couverture = (nbPaquet - nbPaquetManquant);
            couverture = couverture / nbPaquet;
            couverture = couverture * 100;		
            System.out.println("nbPaquet : " + nbPaquet + " nbManquant : " + nbPaquetManquant);
            System.out.println("Le transfert a été effectué avec " + couverture + "% de réussite");			
        }
    }


    private void attenteReceptionTrame(MulticastSocket socketMulticast, DatagramPacket paquetARecevoir){
        try {
            socketMulticast.setSoTimeout(10000);
            socketMulticast.receive(paquetARecevoir);
        } catch(IOException e) {
            System.out.println("Fin du transfert : " + e);
            finTransfert = true;
        }
    }

    private void traitementMPC() throws IOException{
        //si initialisation pas faite et la trame est une FM alors ok 
        if (init == true && typeFichier.equals("FM")) {
            lectureFM();
            fichierRecu = new File(cheminTelechargement + java.io.File.separator + nomFichier.replace("\0", ""));
            RandomAccessFile out = new RandomAccessFile(fichierRecu, "rw");
            out.setLength(tailleFichier);
            out.close();
            init = false;
            System.out.println("Telechargement du fichier " + nomFichier.replace("\0",""));

        } else if (init == false) { // si init = false alors initialisation à été faite

            // vérifie que le paquet n'a pas déjà été traité
            if (!paquetRecu[offset] ) {
                //System.out.println("trame " + offset + " sur " + nbPaquet);
                RandomAccessFile out = new RandomAccessFile(fichierRecu, "rw");
                // si FC alors ok
                if (typeFichier.equals("FC")) {
                    out.seek((long) offset * tailleDonnee);
                    out.write(buffer, tailleHeader, tailleDonneeTrans);
                    paquetRecu[offset] = true;

                //si FF alors ok
                } else if (typeFichier.equals("FF")) {
                    out.seek((long) offset * tailleDonnee);
                    out.write(buffer, tailleHeader, tailleDonneeTrans);
                    paquetRecu[offset] = true;
                }
                out.close();
            }
            //deja une trame reçus c'est juste une trame récurante. elle permet de vérifier si fichier complet
            //Pour utiliser cette partie modifier la trame FM pour quelle contienne le nombre de trames de données émises
            if (typeFichier.equals("FM")){
                statsTransfert();
            }
        }
    }

    /*
     * Cette methode permet de rejoindre le groupe multicast et de recevoir
     * le(s) fichier(s) diffusé(s). Pour cela elle fait appel au methodes vues
     * precedemment.
     */
    public void launch() throws IOException {
        boolean initTabFC = false;		

        // Definition du groupe multicast
        InetAddress adresseIPMulticast = InetAddress.getByName(adresseMulticast);
        MulticastSocket socketMulticast = new MulticastSocket(port);
        socketMulticast.joinGroup(adresseIPMulticast);

        DatagramPacket paquetARecevoir;
        System.out.println("En écoute..");

        // on reste en ecoute tant que le fichier n'est pas au complet.
        //while (tailleActuelleFichier != tailleFichier && !finTransfert) {
        while (!finTransfert) {
            paquetARecevoir = new DatagramPacket(buffer, buffer.length);

            attenteReceptionTrame(socketMulticast, paquetARecevoir);
            lectureHeader();

            //si init pas encore validé j'initialise mon tableau sinon je fait rien
            if(initTabFC == false && typeFichier.equals("FC")){
                    paquetRecu = new boolean[nbPaquet+1]; // nbPaquet + 1 = nbFC + nbFF mais l'indice commence à 0
                    initTabFC = true;
            }

            // la trame reçu est pour notre appli (mpc)
            if (idAppli.equals("MCP")) {
                    traitementMPC();
            }
            GraphicWindow.progressBar.setValue((int)(100*offset/nbPaquet));
        }

        statsTransfert();
        socketMulticast.close();
    }
}