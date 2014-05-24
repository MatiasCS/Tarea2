/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TCPClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Matias
 */
public class ClienteTCP {
    
    Socket conexionCliente;
    final static int puerto = 6789;
    InetAddress IP;
    
    public ClienteTCP() throws UnknownHostException, IOException{
        this.IP = InetAddress.getByName("192.168.0.4"); 
        this.conexionCliente = new Socket(IP, puerto);
    }
    
    public void enviarMensaje(String mensaje) throws IOException{
        DataOutputStream outServer = new DataOutputStream(this.conexionCliente.getOutputStream());
        outServer.writeBytes(mensaje + '\n');
        outServer.flush();
    }
}
