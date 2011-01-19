/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpcserver;

import java.sql.SQLException;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author dombesz
 */
public class Gui {
    

    private static JFrame mainFrame = null;
    private JPanel mainPane=null;
    private JPanel windowPane=null;
    static boolean svisrunning = true;
    final private static Dimension tableSize = new Dimension(138, 99);
    ////////////////////////////////////////////////////////////////////////////
    //Menu Components
    private static JMenuBar menuBar;
    private static JMenu menu;
    private static JMenuItem miStart,  miStop,  miRestart,  
                    miExit,  miDBSetting,  miSVSetting;
    private static ActionAdapter menuListener;
    
    ////////////////////////////////////////////////////////////////////////////
    //StatusBar Components
    public static JPanel statusBar = null;
    public static JLabel statusField = null;
    public static JTextField statusColor = null;
    ////////////////////////////////////////////////////////////////////////////
    //Database Panel Components
    private static JPanel dbSettings;
    private static JComboBox dbType;
    private static final String dbTypes[] = {"postgresql", "mysql"};
    private static JTextField dbUsername = new JTextField(10);
    private static JPasswordField dbPassword = new JPasswordField(10);
    private static JTextField dbName = new JTextField(10);
    private static JTextField dbPort = new JTextField(10);
    private static JButton dbOk,dbCancel;
    private static ActionAdapter dbBtnLstnr;
    
    ////////////////////////////////////////////////////////////////////////////
    //Server Panel Components
    private static JPanel svSettings;
    private static JTextField svUsername = new JTextField(10);
    private static JPasswordField svPassword = new JPasswordField(10);
    private static JTextField svName = new JTextField(10);
    private static JTextField acceptClient=new JTextField(12);
    private static JTextField denyClient=new JTextField(12);
    private static String[] tblLabel={"Ip"};
    private static DefaultTableModel accMdl = new DefaultTableModel(new Object [][] {},tblLabel);
    private static DefaultTableModel dndMdl = new DefaultTableModel(new Object [][] {},tblLabel);
    private static JTable acL=new JTable(accMdl);
    private static JTable dnL=new JTable(dndMdl);
    private static JTextField svPort = new JTextField(4);
    private static JCheckBox svParanoid=new JCheckBox();
    private static JButton svOk,svCancel;
    private static ActionAdapter svBtnLstnr;
    private static GridBagConstraints c = new GridBagConstraints();
    private static JScrollPane scrollPane = new JScrollPane();
    private static JScrollPane scrollPane2= new JScrollPane();
    static Vector acceptList=new Vector();
    static Vector denyList=new Vector();
    ////////////////////////////////////////////////////////////////////////////
    //Background Console Panel
    public static JTextArea svConsole = new JTextArea();
    
    
    ////////////////////////////////////////////////////////////////////////////
    //Initialization Methods
    private  JMenuBar initmenuBar() {
        //Setting Up the MenuListener
        //The menuListener handles the menu events
        menuListener = new ActionAdapter() {

            public void actionPerformed(ActionEvent e) {
                System.out.print("\nAction Performed:" + e.getActionCommand()+" ");
                if (e.getActionCommand().equals("start")) {
                    rpcServer.loadSettings();
                    dbconn.loadSettings();
                    if(dbconn.connectionIsSucces()){
                    rpcServer.initRpcServ(rpcServer.port);
                    setServerState(true);
                    } else{JOptionPane.showMessageDialog(mainFrame, "Could not start server! Something is wrong with your database settings!" +
                        "\nPlease check Settings->Database");}
                }
                if (e.getActionCommand().equals("stop")) {
                    try {
                        rpcServer.shutdown();
                        dbconn.conn.close();
                        setServerState(false);
                    } catch (SQLException ex) {
                        
                    }
                }
                
                if (e.getActionCommand().equals("exit")) {
                    System.exit(1);
                }
                if (e.getActionCommand().equals("dbsetting")) {
                    showDBSettings();
                    
                }
                if (e.getActionCommand().equals("svsetting")) {
                    showSVSettings();
                }


            }
        };
        //Setting up the MenuBar
        //and adding elements
        menuBar = new JMenuBar();
        //Creating the System Menu 
        menu = new JMenu("System");
        menu.setMnemonic(KeyEvent.VK_S);
        //MenuItem Start
        miStart = new JMenuItem("Start");
        miStart.setActionCommand("start");
        miStart.setMnemonic(KeyEvent.VK_S);
        miStart.addActionListener(menuListener);

        menu.add(miStart);
        //MenuItem Stop
        miStop = new JMenuItem("Stop");
        miStop.setActionCommand("stop");
        miStop.setMnemonic(KeyEvent.VK_O);
        miStop.addActionListener(menuListener);

        menu.add(miStop);
       
        //MenuItem Exit
        miExit = new JMenuItem("Exit");
        miExit.setActionCommand("exit");
        miExit.setMnemonic(KeyEvent.VK_X);
        miExit.addActionListener(menuListener);

        menu.add(miExit);
        //Adding the System Menu to MenuBar
        menuBar.add(menu);
        //Creating the Settings Menu
        menu = new JMenu("Settings");
        menu.setMnemonic(KeyEvent.VK_E);
        //MenuItem Database Settings
        miDBSetting = new JMenuItem("Database");
        miDBSetting.setActionCommand("dbsetting");
        miDBSetting.setMnemonic(KeyEvent.VK_D);
        miDBSetting.addActionListener(menuListener);

        menu.add(miDBSetting);
        //MenuItem Server Settings
        miSVSetting = new JMenuItem("Server");
        miSVSetting.setActionCommand("svsetting");
        miSVSetting.setMnemonic(KeyEvent.VK_S);
        miSVSetting.addActionListener(menuListener);

        menu.add(miSVSetting);
        //Adding the Settings Menu to MenuBar
        menuBar.add(menu);


        return menuBar;
    }
    
    private static JPanel dbSettings() {
    
        dbSettings = new JPanel(new GridLayout(0, 1, 0, 10));
        //Buttonlistener is Handling the actions
        //for Ok and Cancel buttons in dbSettings panel
        dbBtnLstnr = new ActionAdapter() {

            public void actionPerformed(ActionEvent e) {
                System.out.print("\nAction Performed:" + e.getActionCommand()+" ");
                if (e.getActionCommand().equals("Ok")) {
                    
                        
                        saveDBProperties();
                        
 
                    
                }
                if (e.getActionCommand().equals("Cancel")) {
                    dbSettings.setVisible(false);
                    scrollPane.setVisible(true);
                }

            }};
        
        //Type Input
        dbType = new JComboBox(dbTypes);
        JPanel pane = new JPanel(new GridLayout(0, 2));
        pane.add(new JLabel("Database Type:"));
        pane.add(dbType);
        dbSettings.add(pane);

        //Username Input
        pane = new JPanel(new GridLayout(0, 2));
        pane.add(new JLabel("Username:"));
        pane.add(dbUsername);
        dbSettings.add(pane);

        //Password Input
        pane = new JPanel(new GridLayout(0, 2));
        pane.add(new JLabel("Password:"));
        pane.add(dbPassword);
        dbSettings.add(pane);

        //Database Name Input
        pane = new JPanel(new GridLayout(0, 2));
        pane.add(new JLabel("DBName:"));
        pane.add(dbName);
        dbSettings.add(pane);

        //Port Input
        pane = new JPanel(new GridLayout(0, 2));
        pane.add(new JLabel("Port:"));
        pane.add(dbPort);
        dbSettings.add(pane);
        
        //The Ok and Cancel Buttons
        pane = new JPanel(new GridLayout(0, 2,10,5));
        dbOk=new JButton("Ok");
        dbOk.addActionListener(dbBtnLstnr);
        dbCancel= new JButton("Cancel");
        dbCancel.addActionListener(dbBtnLstnr);
        pane.add(dbOk);
        pane.add(dbCancel);
        dbSettings.add(pane);
        
        //Setting The TitleBorder
        Border titledBdr = BorderFactory.createTitledBorder("Database Settings");
        Border emptyBdr = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border compoundBdr = BorderFactory.createCompoundBorder(titledBdr, emptyBdr);
        dbSettings.setBorder(compoundBdr);
        //dbSettings.setMaximumSize(paneSize);
        //dbSettings.setVisible(false);
        return dbSettings;
    }
    
    private static JPanel svSettings() {
    
        svSettings = new JPanel(new GridBagLayout());
        
        //Buttonlistener is Handling the actions
        //for Ok and Cancel buttons in svSettings panel
        svBtnLstnr = new ActionAdapter() {

            public void actionPerformed(ActionEvent e) {
                System.out.print("\nAction Performed:" + e.getActionCommand()+" ");
                if (e.getActionCommand().equals("Ok")) {
                            
                            saveSVProperties();
                    
                }
                if (e.getActionCommand().equals("Cancel")) {
                    loadSVProperties();
                    svSettings.setVisible(false);
                    scrollPane.setVisible(true);
                    
                }

            }};
       
        //Port Input
        
        c.gridx=GridBagConstraints.PAGE_START;
        c.gridwidth=0;
        c.gridy=0;
        
        svSettings.add(new JLabel("Port:"));
        svSettings.add(svPort);
        //svSettings.add(pane);
        
        //Paranoid Mode
        c=new GridBagConstraints();
        c.gridx=0;
        c.gridy=1;
        svSettings.add(new JLabel("Paranoid Mode? "),c);
        svParanoid.setSelected(false);
        c.gridx=1;
        svSettings.add(svParanoid,c);
       
        
        //Setting Up the acceptClient and denyClient interface
        c=new GridBagConstraints();
        c.gridy=2;
        c.gridx=0;
        JPanel pane = new JPanel(new GridLayout(2, 2));
        pane.add(new JLabel("Accepted Clients"));
        pane.add(new JLabel("Denyed Clients"));
        
        acceptClient.addActionListener(new ActionAdapter() {
            public void actionPerformed(ActionEvent e) {
               String s = acceptClient.getText();
               if (!s.equals("") && svisrunning) {
                  String response=rpcServer.acceptClient(s);
                  JOptionPane.showMessageDialog(mainFrame, response);
                  acceptClient.setText("");

               }
               if(!svisrunning)JOptionPane.showMessageDialog(mainFrame, "You cannot add this ip beacuse the server is not running");
            }
         });
        
         denyClient.addActionListener(new ActionAdapter() {
            public void actionPerformed(ActionEvent e) {
               String s = denyClient.getText();
               if (!s.equals("")&& svisrunning) {
                   
                  String response=rpcServer.denyClient(s);
                  JOptionPane.showMessageDialog(mainFrame, response);
                  acceptClient.setText("");

               }
               if(!svisrunning)JOptionPane.showMessageDialog(mainFrame, "You cannot add this ip beacuse the server is not running");
            }
         });
        pane.add(acceptClient);
        pane.add(denyClient);
        
        
        pane.setBorder(BorderFactory.createEtchedBorder());
        JPanel temp = new JPanel(new GridBagLayout());
        temp.add(pane);
        
        pane=new JPanel(new GridBagLayout());
       
        c.gridwidth=2;
        
        temp.add(pane,c);
        svSettings.add(temp,c);
        
        
        
        //The Ok and Cancel Buttons
        
       
        pane = new JPanel(new GridLayout(0, 2,10,5));
        svOk=new JButton("Ok");
        svOk.addActionListener(svBtnLstnr);
        svCancel= new JButton("Cancel");
        svOk.setPreferredSize(svCancel.getPreferredSize());
        svCancel.addActionListener(svBtnLstnr);
        pane.add(svOk);
        pane.add(svCancel);
        
        c=new GridBagConstraints(); 
        c.gridy=4;
        c.gridx=0;
        c.gridwidth=2;
        svSettings.add(pane,c);
        //Setting The TitleBorder
        Border titledBdr = BorderFactory.createTitledBorder("Server Settings");
        Border emptyBdr = BorderFactory.createEmptyBorder(0, 10, 0, 10);
        Border compoundBdr = BorderFactory.createCompoundBorder(titledBdr, emptyBdr);
        svSettings.setBorder(compoundBdr);
        
        return svSettings;
    }
    private static JTextArea svConsole()
    {   svConsole=new JTextArea();
         //Set Up the background console area
        PrintStream out = new PrintStream( new console( Gui.svConsole ) );

         // redirect standard output stream to the custom console
        System.setOut( out );

        // redirect standard error stream to the custom console
        System.setErr( out );
        svConsole.setBackground(svSettings.getBackground());
        svConsole.setEnabled(false);
        svConsole.setDisabledTextColor(Color.BLACK);
        return svConsole;
    }
    public void initGui() {
        //Set Up the Menu
        menuBar = initmenuBar();
        
        //Set Up The StatusBar
        
        statusField = new JLabel();
        statusField.setText("Stopped");
        statusColor = new JTextField(1);
        statusColor.setBackground(Color.red);
        statusColor.setEditable(false);
        statusBar = new JPanel(new BorderLayout());
        statusBar.add(statusColor, BorderLayout.WEST);
        statusBar.add(statusField, BorderLayout.CENTER);
        statusBar.setVisible(true);
        statusBar.setBorder(BorderFactory.createTitledBorder(""));
        
        //dbSettings Pane Initialisation
        dbSettings = dbSettings();
        //svSettings Pane Initialisation
        svSettings = svSettings();
        svSettings.setVisible(true);
        //svConsole Pane Initialisation
        svConsole=svConsole();
        scrollPane = new JScrollPane(svConsole);
        //scrollPane.setBounds(r);
        scrollPane.setVisible(true);
        scrollPane.setPreferredSize(new Dimension(700,410));
        //scrollPane.setOpaque(true);
        //Set Up the Window Pane
        windowPane=new JPanel(new GridBagLayout());
        windowPane.add(dbSettings);
        windowPane.add(svSettings);
       
        
        // Set up the main pane
        
        mainPane = new JPanel(new BorderLayout());
        mainPane.setBorder(BorderFactory.createBevelBorder(1));
        mainPane.add(statusBar,BorderLayout.SOUTH);
        
        mainPane.add(windowPane,BorderLayout.NORTH);
        mainPane.add(scrollPane,BorderLayout.CENTER);
        //mainPane.setMaximumSize(paneSize);
        // Set up the main frame
        mainFrame = new JFrame("Rpc-Server");
        mainFrame.setJMenuBar(menuBar);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setSize(700, 500);
        mainFrame.setContentPane(mainPane);
        mainFrame.setLocation(350, 150);
        //mainFrame.pack();
        setInitState();
        mainFrame.setVisible(true);
    }

     void setInitState() {
        dbSettings.setVisible(false);
        svSettings.setVisible(false);
        scrollPane.setVisible(true);
        setServerState(false);

    }

     void setServerState(boolean svisrunning) {
        if (svisrunning) {
            
            
            miStart.setEnabled(false);
            miStop.setEnabled(true);
            //miRestart.setEnabled(true);
            statusField.setText("Started");
            statusColor.setBackground(Color.green);
        } else {
            
            miStart.setEnabled(true);
            miStop.setEnabled(false);
            //miRestart.setEnabled(false);
            statusField.setText("Stopped");
            statusColor.setBackground(Color.red);
        }
        Gui.svisrunning = svisrunning;
    }
    

    static void showDBSettings(){
        loadDBProperties();
        svSettings.setVisible(false);
        scrollPane.setVisible(false);
        dbSettings.setVisible(true);
        
    }
    static void showSVSettings(){
        loadSVProperties();
        scrollPane.setVisible(false);
        dbSettings.setVisible(false);
        svSettings.setVisible(true);
    }
    static void loadDBProperties(){
            
            dbconn.loadSettings();
            dbUsername.setText(dbconn.username);
            dbName.setText(dbconn.dbname);
            dbPassword.setText(dbconn.password);
            dbType.setSelectedItem(dbconn.dbtype);
            dbPort.setText(dbconn.port);
        
    }
    static void loadSVProperties(){
            rpcServer.loadSettings();
            svPort.setText(rpcServer.port.toString());
            svParanoid.setSelected(rpcServer.paranoid);
            
          
            
            
    }
    static void saveDBProperties(){
    
    dbconn.dbname=dbName.getText();
    dbconn.username=dbUsername.getText();
    dbconn.password=dbPassword.getText();
    dbconn.dbtype=dbType.getSelectedItem().toString();
    dbconn.port=dbPort.getText();
    if(dbconn.connectionIsSucces()){
    JOptionPane.showMessageDialog(mainFrame, "The connection with the database is valid!");
    dbconn.saveSettings();
    dbSettings.setVisible(false);
    scrollPane.setVisible(true);
    }
    else{
    JOptionPane.showMessageDialog(mainFrame, "The connection with the database is not valid!");
    dbconn.loadSettings();
    }
    }
    public static void saveSVProperties(){
    try{
    if(svPort.getText().length()>4)throw new NumberFormatException();    
    rpcServer.port=Integer.parseInt(svPort.getText());
    rpcServer.setParanoid(false);
    
    rpcServer.setParanoid(svParanoid.isSelected());
    
    rpcServer.saveSettings();
    JOptionPane.showMessageDialog(mainFrame, "Save Succesfull!");
    svSettings.setVisible(false);
    scrollPane.setVisible(true);
    }catch(NumberFormatException e){
    JOptionPane.showMessageDialog(mainFrame, svPort.getText()+" is not a valid port number!");
    }
    
    }
    
}


class ActionAdapter implements ActionListener {

    public void actionPerformed(ActionEvent e) {
    }
}
