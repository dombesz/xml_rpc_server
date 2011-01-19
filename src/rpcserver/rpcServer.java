/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpcserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.WebServer;

/**
 *
 * @author dombesz
 */
public class rpcServer{
    public static Integer port;
    public static Boolean paranoid=false;
    public static WebServer server=null;
    public static void initRpcServ(int port){
       



            server = new WebServer(port);

            server.addHandler("request", new requestHandler());
            server.setParanoid(paranoid);
            server.start();
        
        
        
    }
    
    public static void saveSettings(){
    try {  
            Properties properties = new Properties();
            properties.setProperty("port", port.toString());
            properties.setProperty("paranoid", paranoid.toString());
           
            File fs = new File("rpcserver-configuration.xml");
            fs.createNewFile();
            FileOutputStream fos = new FileOutputStream(fs);
            properties.storeToXML(fos, "RPC Server Configuration", "UTF-8");
        } catch (IOException ex) {
            System.out.print("File save exception while saving server settings!");
        }
    }
    public static void loadSettings(){
    try {

            Properties properties = new Properties();
            FileInputStream fis = null;
           
            fis = new FileInputStream("rpcserver-configuration.xml");
            try{
            properties.loadFromXML(fis);
            port = Integer.parseInt(properties.getProperty("port"));
            paranoid=Boolean.parseBoolean(properties.getProperty("paranoid"));
            
            
            fis.close();
            }catch(IOException e){
            paranoid=false;
            port = 9988;
            }

        } catch (FileNotFoundException ex) {
            paranoid=false;
            port = 9988;
        }
    
    }
    public static void setParanoid(boolean paranoid){
        if(server!=null){
        server.setParanoid(paranoid);}
        rpcServer.paranoid=paranoid;
    }
    public static String acceptClient(String ip){
        try{
        server.acceptClient(ip);
        }catch(IllegalArgumentException e){
        return e.getMessage();
        }
    return(ip+" Added to accepted List");}
    public static String denyClient(String ip){
        try{
        server.denyClient(ip);
        }catch(IllegalArgumentException e){
        return e.getMessage();
        }
    return(ip+" Added to denyed List");}
    public static void start() throws InterruptedException{
 
        server.start();    
    }
    public static void shutdown(){
        server.shutdown();
   
    }
}
