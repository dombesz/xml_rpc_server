/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpcserver;



/**
 *
 * @author dombesz
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        // TODO code application logic here
      
    

    dbconn.loadSettings();
    Gui gui=new Gui();
    gui.initGui();
    if(dbconn.connectionIsSucces()){
    rpcServer.loadSettings();
    rpcServer.initRpcServ(rpcServer.port);
    gui.setServerState(true);
    }else{gui.setServerState(false);}
    
    }
    

}
