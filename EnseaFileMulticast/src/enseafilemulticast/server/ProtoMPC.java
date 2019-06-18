
package enseafilemulticast.server;

import enseafilemulticast.graphic.GraphicWindowServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

public class ProtoMPC {

    public final static String adresseMulticast="239.168.28.8";
    public final static int port=60008;
    public final static int tailleDonnee=1024;
    public final static int tailleHeader=19;
    public final static int taillePaquet=tailleDonnee+tailleHeader;
    public final static int tailleFM=272;
    public final static int FMCode=5000;

    private byte[] header=new byte[tailleHeader]; // buffer servant a stocker le header
    private byte[] donnee= new byte[tailleDonnee]; // buffer servant a stocker les donnees brutes du fichier
    private byte[] meta=new byte[tailleFM]; // buffer servant a stocker les metadonees
    private byte[] paquet=new byte[tailleHeader+tailleDonnee]; // buffer servant a stocker les paquets a envoyer (header+donnee)

    private InetAddress adresseIPMulticast;
    private MulticastSocket socketMulticast;
    private DatagramPacket paquetAEnvoyer;

    public File fichierAEnvoyer;
    public int nbTransfert = 0;

    public ProtoMPC(){}

    /*Cette methode sert a creer le header. Elle doit prendre en entree 4 int.
     *Le premier, sommeDeControle correspond au nombre d'octets de donnees transportees.
     *Dans le cas ou l'on est entrain de constituer un header pour metadonnees ce int doit prendre la valeur particuliere FMCode.
     *Les autres parametres d'entree sont suffisament explicite pour ne pas etre detailles*/
    private void creationHeader(int sommeDeControle, int nbPaquetEnvoye, int nbPaquetTotal, int identifiantFichier) throws UnsupportedEncodingException{

        // Identifiant appli
        header[0]="MCP".getBytes("UTF-8")[0];
        header[1]="MCP".getBytes("UTF-8")[1];
        header[2]="MCP".getBytes("UTF-8")[2];

        // Type de paquet
        if(sommeDeControle==FMCode){
            header[3]="FM".getBytes("UTF-8")[0];
            header[4]="FM".getBytes("UTF-8")[1];
        }
        else if(sommeDeControle==tailleDonnee){
            header[3]="FC".getBytes("UTF-8")[0];
            header[4]="FC".getBytes("UTF-8")[1];
        }
        else{
            header[3]="FF".getBytes("UTF-8")[0];
            header[4]="FF".getBytes("UTF-8")[1];
        }

        // Offset
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(ByteOrder.BIG_ENDIAN);
        b.putInt(nbPaquetEnvoye);

        header[5]=b.array()[0];
        header[6]=b.array()[1];
        header[7]=b.array()[2];
        header[8]=b.array()[3];

        // Taille de la donnee transportee
        b=ByteBuffer.allocate(4);
        b.putInt(sommeDeControle);

        header[9]=b.array()[2];
        header[10]=b.array()[3];

        // Nombre de paquets
        b=ByteBuffer.allocate(4);
        b.putInt(nbPaquetTotal);

        header[11]=b.array()[0];
        header[12]=b.array()[1];
        header[13]=b.array()[2];
        header[14]=b.array()[3];

        // identifiant fichier
        b=ByteBuffer.allocate(4);
        b.putInt(identifiantFichier);

        header[15]=b.array()[0];
        header[16]=b.array()[1];
        header[17]=b.array()[2];
        header[18]=b.array()[3];
    }

    /*Cette methode permet de recuperer les metadonnees associer au File d'entree*/
    private void creationFM(File fichier) throws UnsupportedEncodingException{
        // Nom du fichier
        String nom=fichier.getName();
        System.arraycopy(nom.getBytes("UTF-8"), 0, meta, 0, nom.length());

        // Taille du fichier
        long  tailleFichier=fichier.length();
        ByteBuffer b = ByteBuffer.allocate(8);
        b.order(ByteOrder.BIG_ENDIAN);
        b.putLong(tailleFichier);
        System.arraycopy(b.array(), 0, meta, 256, b.array().length);

        // Date du fichier
        long date=fichier.lastModified();
        b=ByteBuffer.allocate(8);
        b.putLong(date);
        System.arraycopy(b.array(), 0, meta, 264, b.array().length);
    }

    /*Cette methode associe les header et les donnees ou metadonnees pour les preparer a l'envoi*/
    private void creationPaquet(String typePaquet){
        if(typePaquet.equals("HFM")){
            System.arraycopy(header, 0, paquet, 0, header.length);
            System.arraycopy(meta, 0, paquet, header.length, meta.length);
            System.arraycopy(new byte[taillePaquet-tailleHeader-tailleFM], 0, paquet, (tailleHeader+tailleFM), taillePaquet-tailleHeader-tailleFM);
        }
        else if(typePaquet.equals("HFC") || typePaquet.equals("HFF")){
            System.arraycopy(header, 0, paquet, 0, header.length);
            System.arraycopy(donnee, 0, paquet, header.length, donnee.length);
        }
    }

    private void initProto(String cheminFichier)throws IOException{
        // Definition du groupe multicast
        adresseIPMulticast= InetAddress.getByName(adresseMulticast);
        socketMulticast = new MulticastSocket(port);
        socketMulticast.joinGroup(adresseIPMulticast);
        fichierAEnvoyer=new File(cheminFichier);				
        creationFM(fichierAEnvoyer);
        formationTrameFM();
        sendFM();
    }

    private void formationTrameFM() throws IOException{
        // Creation et envoi des metadonnees
        creationHeader(FMCode,0,(int)(fichierAEnvoyer.length()/1024),fichierAEnvoyer.hashCode());
        creationPaquet("HFM");
    }

    private void sendFM() throws IOException{
        paquetAEnvoyer= new DatagramPacket(paquet,paquet.length,adresseIPMulticast,port);
        socketMulticast.send(paquetAEnvoyer);	
    }

    private void formationTrameFC(int sommeDeControle, int nbPaquetEnvoye) throws IOException{
        creationHeader(sommeDeControle,nbPaquetEnvoye,(int)(fichierAEnvoyer.length()/1024),fichierAEnvoyer.hashCode());
        creationPaquet("HFC");
    }

    private void sendFC() throws IOException{
        paquetAEnvoyer= new DatagramPacket(paquet,paquet.length,adresseIPMulticast,port);
        socketMulticast.send(paquetAEnvoyer);
    }

    private void formationTrameFF(int sommeDeControle, int nbPaquetEnvoye) throws IOException{
        creationHeader(sommeDeControle,nbPaquetEnvoye,(int)(fichierAEnvoyer.length()/1024),fichierAEnvoyer.hashCode());
        creationPaquet("HFF");
    }

    private void sendFF() throws IOException{
        paquetAEnvoyer= new DatagramPacket(paquet,paquet.length,adresseIPMulticast,port);
        socketMulticast.send(paquetAEnvoyer);
    }

    private void transfertFichier(int bitRLimit)throws IOException{
        int sommeDeControle=0;
        int nbPaquetEnvoye=0;
        int compteurFM = 0;

        // Envoi donnee
        FileInputStream fluxEntrant=new FileInputStream(fichierAEnvoyer);

        sommeDeControle=fluxEntrant.read(donnee,0,donnee.length);

        while(sommeDeControle!=-1){
            compteurFM++;
            // si dernière trame du fichier
            if(sommeDeControle>0 && sommeDeControle<tailleDonnee){
                formationTrameFF(sommeDeControle, nbPaquetEnvoye);
                sendFF();
                nbPaquetEnvoye+=1;
                //System.out.println("Paquet numero "+nbPaquetEnvoye+" sur "+(int)(1+(fichierAEnvoyer.length()/1024))+"envoye");
                GraphicWindowServer.progressBar.setValue(100);
                sommeDeControle=fluxEntrant.read(donnee,0,donnee.length);
            }

            // si trame trame du autre que la dernière
            else{
                formationTrameFC(sommeDeControle, nbPaquetEnvoye);
                sendFC();
                nbPaquetEnvoye+=1;
                //System.out.println("Paquet numero "+nbPaquetEnvoye+" envoye sur "+(int)(1+(fichierAEnvoyer.length()/1024)));
                GraphicWindowServer.progressBar.setValue((int)(100*nbPaquetEnvoye/(int)(1+(fichierAEnvoyer.length()/1024))));
                sommeDeControle=fluxEntrant.read(donnee,0,donnee.length);
                if (bitRLimit > 0 && bitRLimit<250) {
                    try {
                        TimeUnit.MILLISECONDS.sleep((int)1000/bitRLimit);
                    }
                    catch (InterruptedException e) {}
                }
            }
            // réenvoie l'entête pour les utilisateurs arrivant en cours de transfert.
            // trame FM toutes les 500 trames. 
            if(compteurFM == 500){ 
                formationTrameFM();
                sendFM();
                if (nbPaquetEnvoye+499 <= 1+(fichierAEnvoyer.length()/1024)) {
                    System.out.println("Envoi des paquets "+nbPaquetEnvoye+" a "+(nbPaquetEnvoye+499)+" sur "+(int)(1+(fichierAEnvoyer.length()/1024)));
                }
                else {
                    System.out.println("Envoi des paquets "+nbPaquetEnvoye+" a "+(int)(1+(fichierAEnvoyer.length()/1024))+" sur "+(int)(1+(fichierAEnvoyer.length()/1024)));
                }
                if (bitRLimit > 250 && bitRLimit<500000) {
                    try {
                        TimeUnit.MILLISECONDS.sleep((int)500000/bitRLimit);
                    }
                    catch (InterruptedException e) {}
                }
                compteurFM = 0;
            }		
        }
        fluxEntrant.close();
        socketMulticast.close();		
    }

    /*Cette methode permet de rejoindre le groupe multicast puis de transferer le fichier.
     * Pour cela elle fait notament appel aux methodes vues precedemment*/
    public void launch(String cheminFichier, int bitRLimit, int numberSend) throws IOException{
        // Envoie le fichier "numberSend" fois
        while(nbTransfert < numberSend) {
            initProto(cheminFichier);
            transfertFichier(bitRLimit);
            nbTransfert+=1;
        }
        nbTransfert=0;
    }
}