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

package enseafilemulticast.graphic;

import enseafilemulticast.client.ClientThread;
import enseafilemulticast.client.ProtoClientTcp;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import static java.lang.Thread.sleep;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

public class GraphicWindow extends JFrame implements ActionListener
{
    private final JTextArea consoleTextArea = new JTextArea();
    private final TextAreaOutputStream taOutputStream = new TextAreaOutputStream(consoleTextArea, "Console");
    private static JTextArea textPath;
    private static JTextArea textTCPServerAddress;
    private static JTextArea textTCPServerPort;
    private JFileChooser chooser;
    public static JProgressBar progressBar;
    String chooserTitle;

    public GraphicWindow(String titre)
    {
        super(titre);
        setLocation(100,100);
        setSize(800,600);

        //Barre de menus
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("Fichier");
            menuBar.add(menuFile);
            JMenuItem close = new JMenuItem("Quitter");
            close.addActionListener(this);
            close.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
            menuFile.add(close);
        JMenu menuEdit = new JMenu("Edition");
            menuBar.add(menuEdit);
            JMenuItem editPath = new JMenuItem("Dossier de téléchargement");
            editPath.addActionListener(this);
            menuEdit.add(editPath);
        JMenu menuHelp = new JMenu("Aide");
            menuBar.add(menuHelp);
            JMenuItem about = new JMenuItem("À propos");
            menuHelp.add(about);
            about.addActionListener(this);
        setJMenuBar(menuBar); //Marchepa en D55(Ubuntu 16.04LTS)

        setLayout(new GridLayout(1,2)); //Mise en page 2 colonnes (console | panneau de controle)

        //Console
        consoleTextArea.setLineWrap(true);
        add(new JScrollPane(consoleTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        System.setOut(new PrintStream(taOutputStream));

        //Panneau de controle
        JPanel controlPan = new JPanel();
        SpringLayout layout = new SpringLayout();
        controlPan.setLayout(layout);
        controlPan.setBackground(Color.GRAY);
        add(controlPan);

        //Elements du panneau de controle
        JLabel labelPath = new JLabel("Les fichiers seront téléchargés dans ce dossier:"); 
        controlPan.add(labelPath);
        textPath = new JTextArea(System.getProperty("user.dir"));
        JScrollPane textPathPane = new JScrollPane(textPath, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        controlPan.add(textPathPane);
        Button buttonPath = new Button("Parcourir...");
        buttonPath.addActionListener(this);
        controlPan.add(buttonPath);
        JLabel labelTCPServerAddress = new JLabel("Adresse du serveur (optionnel)");
        controlPan.add(labelTCPServerAddress);
        textTCPServerAddress = new JTextArea("192.168.1.1");
        controlPan.add(textTCPServerAddress);
        JLabel labelTCPServerPort = new JLabel("Port TCP du serveur (optionnel)");
        controlPan.add(labelTCPServerPort);
        textTCPServerPort = new JTextArea("24247");
        controlPan.add(textTCPServerPort);
        Button buttonStart = new Button("Commencer le téléchargement");
        buttonStart.addActionListener(this);
        controlPan.add(buttonStart);
        progressBar = new JProgressBar();
        controlPan.add(progressBar);
        
        //Mise en page
        layout.putConstraint(SpringLayout.NORTH, labelPath, 5, SpringLayout.NORTH, controlPan);
        layout.putConstraint(SpringLayout.NORTH, textPathPane, 5, SpringLayout.SOUTH, labelPath);
        layout.putConstraint(SpringLayout.NORTH, buttonPath, 5, SpringLayout.SOUTH, textPathPane);
        layout.putConstraint(SpringLayout.NORTH, labelTCPServerAddress, 5, SpringLayout.SOUTH, buttonPath);
        layout.putConstraint(SpringLayout.NORTH, textTCPServerAddress, 5, SpringLayout.SOUTH, buttonPath);
        layout.putConstraint(SpringLayout.NORTH, labelTCPServerPort, 5, SpringLayout.SOUTH, textTCPServerAddress);
        layout.putConstraint(SpringLayout.NORTH, textTCPServerPort, 5, SpringLayout.SOUTH, textTCPServerAddress);
        layout.putConstraint(SpringLayout.NORTH, buttonStart, 15, SpringLayout.SOUTH, textTCPServerPort);
        layout.putConstraint(SpringLayout.WEST, labelPath, 5, SpringLayout.WEST, controlPan);
        layout.putConstraint(SpringLayout.WEST, textPathPane, 5, SpringLayout.WEST, controlPan);
        layout.putConstraint(SpringLayout.WEST, labelTCPServerAddress, 5, SpringLayout.WEST, controlPan);
        layout.putConstraint(SpringLayout.WEST, textTCPServerAddress, 5, SpringLayout.EAST, labelTCPServerAddress);
        layout.putConstraint(SpringLayout.WEST, labelTCPServerPort, 5, SpringLayout.WEST, controlPan);
        layout.putConstraint(SpringLayout.WEST, textTCPServerPort, 5, SpringLayout.EAST, labelTCPServerPort);
        layout.putConstraint(SpringLayout.EAST, textTCPServerAddress, -5, SpringLayout.EAST, controlPan);
        layout.putConstraint(SpringLayout.EAST, textTCPServerPort, -5, SpringLayout.EAST, controlPan);
        layout.putConstraint(SpringLayout.EAST, textPathPane, -5, SpringLayout.EAST, controlPan);
        layout.putConstraint(SpringLayout.EAST, buttonPath, -5, SpringLayout.EAST, controlPan);
        layout.putConstraint(SpringLayout.EAST, buttonStart, -5, SpringLayout.EAST, controlPan);
        layout.putConstraint(SpringLayout.NORTH, progressBar, 5, SpringLayout.SOUTH, buttonStart);
        layout.putConstraint(SpringLayout.EAST, progressBar, -5, SpringLayout.EAST, controlPan);
        layout.putConstraint(SpringLayout.WEST, progressBar, 5, SpringLayout.WEST, controlPan);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String input = e.getActionCommand();
        switch(input)
        {
            case "Quitter" :
                System.exit(0);
                break;
            case "À propos" :
                JOptionPane.showMessageDialog(new JFrame(), "Ensea File Multicast\n\nHermes Maniatis\nWilliam Legoff\nMehdi Maarouf\nStephen Tchen\n", "À propos", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Parcourir..." :
            case "Dossier de téléchargement" :
                chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle(chooserTitle);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                {
                    textPath.setText(chooser.getSelectedFile().toString());
                    System.out.println(chooser.getSelectedFile().toString());
                }
                break;
            case "Commencer le téléchargement":
                String path = textPath.getText();
                ClientThread thread = new ClientThread(path, textTCPServerAddress.getText(), textTCPServerPort.getText());
                thread.start();
                new Thread() { //Nettoyage de la console toutes les 1 secondes
                    public void run() {
                        try {
                            while(true) {
                                int len = consoleTextArea.getText().length();
                                if (len > 6000) {
                                    consoleTextArea.setText(consoleTextArea.getText().substring(0, 400)+"(...)\n"+consoleTextArea.getText().substring(len - 2000, len));
                                    consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
                                }
                                sleep(1000);
                            }
                        }
                        catch (InterruptedException e) {}
                    }
                }.start();
                
                new Thread() { //lancement du mode TCP
                    public void run() {
                        try {
                            while(!thread.clientMulticast.fichierComplet) {
                                if (thread.modeTCP) {
                                    ProtoClientTcp receptionTCP = new ProtoClientTcp(thread.serverAddress,thread.serverPort,path+"/"+thread.clientMulticast.nomFichier,thread.clientMulticast.paquetRecu);
                                    receptionTCP.launch();
                                }
                                sleep(10000);
                            }
                        }
                        catch (InterruptedException e) {}
                    }
                }.start();
                break;
        }
    }
}
