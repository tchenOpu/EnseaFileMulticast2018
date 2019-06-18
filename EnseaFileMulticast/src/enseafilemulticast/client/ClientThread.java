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

package enseafilemulticast.client;

import java.io.IOException;

/**
 *
 * @author Stephen TCHEN (tchenste@gmail.com)
 */
public class ClientThread extends Thread {
    String path;
    public ClientMulticast clientMulticast = new ClientMulticast();
    public boolean modeTCP = false;
    public String serverAddress = "localhost";
    public String serverPort = "24247";
    
    public ClientThread(String path, String serverAddress, String serverPort) {
        this.path = path;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        clientMulticast = new ClientMulticast(path);
    }
    
    @Override
    public void run() {
        try {
            clientMulticast.launch();
        } catch (IOException ex) {
            System.out.println("Erreur : " + ex);
        }
        modeTCP = true;
        System.out.println("Passage en mode TCP");
    }
}
