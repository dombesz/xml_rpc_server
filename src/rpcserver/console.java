package rpcserver;



import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

/*This class is an extension of OutputStream  class
 *Contains a JTextArea object, this will be the output
 * of the OutputStream
 */

public class console extends OutputStream {
    private JTextArea textControl;
    
  //The Constructor   
    public console( JTextArea control ) {
        textControl = control;
    }
    
    /*
     *Overriding the write method of the OutputStream
     * to write the output to the JTextArea object
     */
    public void write( int b ) throws IOException {
        // append the data as characters to the JTextArea control
        textControl.append( String.valueOf( ( char )b ) );
        
    }  
}