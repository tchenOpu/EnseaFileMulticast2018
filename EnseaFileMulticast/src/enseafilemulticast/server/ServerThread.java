/*
                                        ##############
                                    ####              ##
                                  ##                    ##                  ############
                                ##                      ##              ######          ####
                              ###                       ##            ####                  ##
                            ###                         ##          ##                        ##
                            ##                          ##        ##                           ###
                           ###                        ##         ###                            ##
                          ###                      ###          ##                              ##
                          ###                   ###            ###                              ##
                          ###                 ##              ##                              ##
                          ##                 ##               ##                            ####
                          ##                ##               ##                  ###########
                          ##                ##              ##                ####
                         ###               ##               ##              ###
                         ###               ##              ##              ##
                         ###               ##              ##             ##
                          ##               ##             ##             ##
                          ###        ###    ##            ##    ###     ##
                           ###     ##   ##  ##            ##  ##  ##   ##
                            ##     ##   ##  ##            ##  ##  ##   ##
                            ##      ##  ############################   ##
                              ##    ####                            ## ##
                                ####                                  ####
                              ##                                        ####
                            ##              ######                        ####
                          ##              ###   ####            ########    ##
             ###        ##              #####   ####           ###   ####    ##
           ##///###    ##              ############          #####   #####   ##
        ###////////## ##               ############          #############   ##
       #/////////////##                  ########              ###########    ##
      #//////////////##                                           ####        ##
     #//////////////##                                                        ##
     #/////////////##                                  /\                     ##
      #///////////##                                                        ##
      #//////////##                                                         ##
        ###////## ##                                                       ##
           ####     ####                                                  ##
                      ######                                            ###
                          ########      ##    ##  ##          ##########
                                  ######  ####  ##  ############
*/

package enseafilemulticast.server;

import java.io.IOException;

/**
 *
 * @author Stephen TCHEN (tchenste@gmail.com)
 */
public class ServerThread extends Thread {
    String path;
    int bitRLimit;
    int port = 24247;
    int numberSend = 5;
    ProtoMPC serverMulticast = new ProtoMPC();
    
    public ServerThread(String path, int bitRLimit, String textPort, int numberSend) {
        this.path = path;
        this.numberSend = numberSend;
        try {
            this.port = Integer.parseInt(textPort);
        } catch (NumberFormatException e) {
            System.out.println("Numero de port non reconnu : port par défaut 24247 : " + e);
        }
        this.bitRLimit = bitRLimit;
    }
    
    @Override
    public void run() {
        try {				
            serverMulticast.launch(path, bitRLimit, numberSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Fin de transfert");
        ProtoServerTCP transfertTCP = new ProtoServerTCP(port, serverMulticast.fichierAEnvoyer.getPath());
        transfertTCP.launch();	
    }
}
