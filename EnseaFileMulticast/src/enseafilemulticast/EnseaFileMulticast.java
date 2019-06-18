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

package enseafilemulticast;

import enseafilemulticast.graphic.GraphicWindow;
import enseafilemulticast.graphic.GraphicWindowServer;

/**
 *
 * @author Stephen TCHEN (tchenste@gmail.com)
 */
public class EnseaFileMulticast {
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].substring(0,1).equalsIgnoreCase("s")) {
                GraphicWindowServer win = new GraphicWindowServer("Ensea File Multicast Server");
            }
        }
        else {
            GraphicWindow win = new GraphicWindow("Ensea File Multicast");
        }
    }
}
