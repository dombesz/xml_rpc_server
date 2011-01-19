/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rpcserver;

import java.sql.SQLException;

/**
 *
 * @author dombesz
 */
public class requestHandler {
public String dbQuery(String ip,String query) throws Exception{
        
            String response = null;
            response = dbconn.getQueryasXml(query);
            
            System.out.print("\nGot Request from: "+ip);
            System.out.print("\nThe query is: "+query+"\n");
            

            return response;
        
                
       
    }
public String getTableNames(String ip)throws Exception {
    String response;
   
            try {
                response = dbconn.getTableNames();
               
              
                } catch (Exception ex) {
                throw ex;
            } 

     System.out.print("\nGot Request from: "+ip);
     System.out.print("\nThe query is: GetTableNames \n");
   
    return response;
    
    
    }
public String dbExec(String ip,String exec)throws Exception{
String response="";
try{
int i=dbconn.setUpdate(exec);
response="Update executed succesfully!\n "+i+" rows affected!";

System.out.print("\nGot Request from: "+ip);
System.out.print("\nUpdate executed: "+exec+"\n");
return response;
}catch(Exception ex){throw ex;}
}
public int getTableRowCount(String ip, String table) throws SQLException{
    int response;
    System.out.print("\nGot Request from: "+ip+"tablename= "+table);
    System.out.print("\nThe query is: GetTableRowCount \n");
    response=dbconn.getTableRowCount(table);
    System.out.print("response is: "+response);
return response;
}
}
