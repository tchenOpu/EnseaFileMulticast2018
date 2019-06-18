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

import enseafilemulticast.server.ServerThread;
import java.awt.Button;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

public class GraphicWindowServer extends JFrame implements ActionListener
{
    private final JTextArea consoleTextArea = new JTextArea();
    private final TextAreaOutputStream taOutputStream = new TextAreaOutputStream(consoleTextArea, "Console");
    private static JTextArea textPath;
    private static JTextArea textPort;
    private static JTextArea textNumberSend;
    private JFileChooser chooser;
    public static JProgressBar progressBar;
    private static JTextField textLimit;
    String chooserTitle;

    public GraphicWindowServer(String titre)
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
            JMenuItem editPath = new JMenuItem("Fichier à transférer");
            editPath.addActionListener(this);
            menuEdit.add(editPath);
        JMenu menuHelp = new JMenu("Aide");
            menuBar.add(menuHelp);
            JMenuItem about = new JMenuItem("À propos");
            menuHelp.add(about);
            about.addActionListener(this);
        setJMenuBar(menuBar); //Marche pas en D55(Ubuntu 16.04LTS) (java 9+)

        setLayout(new GridLayout(1,2)); //Mise en page 2 colonnes (console | panneau de controle)

        //Console
        consoleTextArea.setLineWrap(true);
        add(new JScrollPane(consoleTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        System.setOut(new PrintStream(taOutputStream));

        //Panneau de controle
        JPanel controlPan = new JPanel();
        SpringLayout layout = new SpringLayout();
        controlPan.setLayout(layout);
        controlPan.setBackground(Color.DARK_GRAY);
        add(controlPan);

        //DElements du panneau de controle
        JLabel labelPath = new JLabel("Le fichier à transférer :"); 
        labelPath.setForeground(Color.orange);
        controlPan.add(labelPath);
        textPath = new JTextArea();
        JScrollPane textPathPane = new JScrollPane(textPath, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        controlPan.add(textPathPane);
        Button buttonPath = new Button("Parcourir...");
        buttonPath.addActionListener(this);
        controlPan.add(buttonPath);
        
        JLabel labelNumberSend = new JLabel("Nombre d'envois consécutifs"); 
        labelNumberSend.setForeground(Color.orange);
        controlPan.add(labelNumberSend);
        textNumberSend = new JTextArea("5");
        controlPan.add(textNumberSend);
        JLabel labelLimit = new JLabel("Limiter le taux de transfert (kbit/s)"); 
        labelLimit.setForeground(Color.orange);
        controlPan.add(labelLimit);
        textLimit = new JTextField("32000");
        controlPan.add(textLimit);
        JLabel labelPort = new JLabel("Port TCP (optionnel)");
        labelPort.setForeground(Color.orange);
        controlPan.add(labelPort);
        textPort = new JTextArea("24247");
        controlPan.add(textPort);
        Button buttonStart = new Button("Envoyer le fichier en multicast");
        buttonStart.addActionListener(this);
        controlPan.add(buttonStart);
        progressBar = new JProgressBar();
        controlPan.add(progressBar);
        
        //Mise en page
        layout.putConstraint(SpringLayout.NORTH, labelPath, 5, SpringLayout.NORTH, controlPan);
        layout.putConstraint(SpringLayout.NORTH, textPathPane, 5, SpringLayout.SOUTH, labelPath);
        layout.putConstraint(SpringLayout.NORTH, buttonPath, 5, SpringLayout.SOUTH, textPathPane);
        layout.putConstraint(SpringLayout.NORTH, labelNumberSend, 5, SpringLayout.SOUTH, buttonPath);
        layout.putConstraint(SpringLayout.NORTH, textNumberSend, 5, SpringLayout.SOUTH, buttonPath);
        layout.putConstraint(SpringLayout.NORTH, labelLimit, 5, SpringLayout.SOUTH, labelNumberSend);
        layout.putConstraint(SpringLayout.NORTH, textLimit, 5, SpringLayout.SOUTH, labelNumberSend);
        layout.putConstraint(SpringLayout.NORTH, labelPort, 5, SpringLayout.SOUTH, labelLimit);
        layout.putConstraint(SpringLayout.NORTH, textPort, 5, SpringLayout.SOUTH, labelLimit);
        layout.putConstraint(SpringLayout.NORTH, buttonStart, 15, SpringLayout.SOUTH, textPort);
        layout.putConstraint(SpringLayout.WEST, labelPath, 5, SpringLayout.WEST, controlPan);
        layout.putConstraint(SpringLayout.WEST, labelLimit, 5, SpringLayout.WEST, controlPan);
        layout.putConstraint(SpringLayout.WEST, textPathPane, 5, SpringLayout.WEST, controlPan);
        layout.putConstraint(SpringLayout.WEST, textLimit, 5, SpringLayout.EAST, labelLimit);
        layout.putConstraint(SpringLayout.WEST, labelPort, 5, SpringLayout.WEST, controlPan);
        layout.putConstraint(SpringLayout.WEST, textPort, 5, SpringLayout.EAST, labelPort);
        layout.putConstraint(SpringLayout.WEST, labelNumberSend, 5, SpringLayout.WEST, controlPan);
        layout.putConstraint(SpringLayout.WEST, textNumberSend, 5, SpringLayout.EAST, labelNumberSend);
        layout.putConstraint(SpringLayout.EAST, textNumberSend, 5, SpringLayout.EAST, controlPan);
        layout.putConstraint(SpringLayout.EAST, textPort, -5, SpringLayout.EAST, controlPan);
        layout.putConstraint(SpringLayout.EAST, textPathPane, -5, SpringLayout.EAST, controlPan);
        layout.putConstraint(SpringLayout.EAST, buttonPath, -5, SpringLayout.EAST, controlPan);
        layout.putConstraint(SpringLayout.EAST, buttonStart, -5, SpringLayout.EAST, controlPan);
        layout.putConstraint(SpringLayout.EAST, textLimit, -5, SpringLayout.EAST, controlPan);
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
                JOptionPane.showMessageDialog(new JFrame(), "Ensea File Multicast\n\nHermes Maniatis\nWilliam Legoff\nMehdi Maarouf\nStephen Tchen\n\nEn cours de développement", "À propos", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Parcourir..." :
            case "Fichier à transférer" :
                chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle(chooserTitle);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(true);
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                {
                    textPath.setText(chooser.getSelectedFile().toString());
                    System.out.println(chooser.getSelectedFile().toString());
                }
                break;
            case "Envoyer le fichier en multicast":
                String path = textPath.getText();
                File f = new File(path);
                if(f.exists() && !f.isDirectory()) { 
                    System.out.println("Demarrage de l'envoi du fichier");
                    int bitRLimit = 0;
                    try {
                        bitRLimit = Integer.parseInt(textLimit.getText());
                    } catch (NumberFormatException ex) {
                        bitRLimit = 0;
                        System.out.println("Limite du taux de transfert non reconnu : " + ex);
                    }
                    int numberSend = 5;
                    try {
                        numberSend = Integer.parseInt(textNumberSend.getText());
                    } catch (NumberFormatException e1) {
                        numberSend = 5;
                        System.out.println("Nombre d'envois non reconnu, valeur par défaut 5 : " + e1);
                    }                   
                    if (bitRLimit == 0) {
                        System.out.println("Limite du taux de transfert non utilisée");
                    }
                    else {
                        System.out.println("Limite du taux de transfert utilisée : " + bitRLimit + "kbit/s");
                    }
                    ServerThread thread = new ServerThread(path, bitRLimit, textPort.getText(), numberSend);
                    thread.start();
                    new Thread() { //Nettoyage de la console toutes les 10 secondes
                        public void run() {
                            try {
                                while(true) {
                                    int len = consoleTextArea.getText().length();
                                    if (len > 5000) {
                                        consoleTextArea.setText(consoleTextArea.getText().substring(0, 200)+"(...)\n"+consoleTextArea.getText().substring(len - 2000, len));
                                        consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());
                                    }
                                    sleep(5000);
                                }
                            }
                            catch (InterruptedException e) {}
                        }
                    }.start();
                }
                else {
                    System.out.println("Fichier introuvable\nEchec du transfert");
                }
                break;
        }
    }
}
