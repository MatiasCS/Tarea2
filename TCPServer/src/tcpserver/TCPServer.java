/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tcpserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matias
 */
public class TCPServer implements Runnable{

    static final int puerto = 8082;
    Socket conexion;
    DataOutputStream outCliente;
    String comandos[] = {"GREET","SENDOK"};
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            // TODO code application logic here
            //Creacion del servidor y espera de clientes            
            ServerSocket servidor = new ServerSocket(puerto);
            while(true){
                TCPServer cliente = new TCPServer(servidor.accept());
                Thread hebra = new Thread(cliente);
                hebra.start();  
              }
            
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public TCPServer(Socket conexion) throws IOException{
        this.conexion = conexion;
        this.outCliente = new DataOutputStream(this.conexion.getOutputStream());
    }
    
    //FUNCIONES SERVIDOR
    //SE CONSIDERO ESTA ESTRUCTURA SEND_M##IP_DESTINO##IP_FUENTE##MENSAJE
    //Las funciones no necesariamente tienen que tener esos argumentos, tambien se le puede pasar el mensaje entero y ir revisandolo
    //dentro de la función.
    
    public void GREET() throws IOException{
        String mensajeTotal = comandos[0] + "Greetings! Bienvenido al Servidor de Avioncito de Papel";
        outCliente.writeBytes(mensajeTotal + "\n");
        outCliente.flush();
        
        //implementacion
    }
    
    //Funcion para revisar y enviar mensajes desde el servidor al cliente
    public void SENDOK(String mensaje, String IPDestino) throws IOException{
        //Implementacion
        
        /*File fichero;
        fichero = new File("Chat.txt");
            
        FileWriter escritor=new FileWriter(fichero,true);
        BufferedWriter buffescritor=new BufferedWriter(escritor);
        PrintWriter escritor_final= new PrintWriter(buffescritor);
                    
        escritor_final.append(mensaje+"\r\n");
        escritor_final.close();
        buffescritor.close();*/
    }
    
    //Funcion para analizar y ver que tipo de mensaje es el que se manda (para enviar mensaje archivo...)
    public void revisar_tipo_msg_cliente(){}
    
    public void servidor_envia_msg_cliente(String IP_fuente, String IP_destino, String mensaje){
        System.out.println(IP_fuente);
        System.out.println(IP_destino);
        System.out.println(mensaje);
        }
    
    public void servidor_revisa_msg_cliente(String IP_solicitante, int puerto) throws IOException{
        try {
            //Variables de validación
            int validar=0;
            //Variables utilizadas para leer
            FileReader fr = new FileReader ("Chat.txt");
            BufferedReader br = new BufferedReader(fr);
            String linea;
            //Variables utilizadas para la separación del String
            StringTokenizer aux1;
            String aux2;
            //Variables utilizadas para guardar campos.
            String IP_d;            //IP destino
            String IP_f;            //IP fuente
            String mensaje;
            
            //Variables para poder escribir los mensajes que no pertenecen a la IP
            File fichero;
            fichero = new File("temporal.txt");
            FileWriter escritor=new FileWriter(fichero,true);
            BufferedWriter buffescritor=new BufferedWriter(escritor);
            PrintWriter escritor_final= new PrintWriter(buffescritor);

            //En este caso se hace la suposición que el string guardado será
            //SEND_M##IP_DESTINO##IP_FUENTE##MENSAJE
            while ((linea=br.readLine())!=null){
                System.out.println(linea);
                validar=0;
                aux1=new StringTokenizer(linea,"##");
                while(aux1.hasMoreTokens()){
                    aux2= aux1.nextToken();         //SEND
                    if (!aux1.hasMoreTokens()){
                        break;
                    }
                    aux2= aux1.nextToken();         //IP_DESTINO
                    
                    if (aux2.equals(IP_solicitante)){
                        validar=1;
                        IP_d=aux2;
                        IP_f=aux1.nextToken();
                        mensaje=aux1.nextToken();
                        servidor_envia_msg_cliente(IP_d,IP_f,mensaje);     
                    }
                    else {
                        break;
                    }
                }
                if (validar==0){
                escritor_final.append(linea+"\r\n");    
                }
            }
                   
            fr.close();
            buffescritor.close();
            
            
        } catch (FileNotFoundException ex) {
            System.out.println("No se ha escrito ningun mensaje aún");
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    //Funcion para recibir archivo del cliente.
    public void servidor_recibe_archivo_cliente(){
    }
    
    //Funcion para enviar archivos al cliente.
    public void servidor_envia_archivo_cliente(){
    }
    
    
    
    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        BufferedReader inClienteTCP;
        System.out.println("Hola acepte conexion");
        try {
            
            inClienteTCP = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String mensaje = inClienteTCP.readLine();
            
            //servidor_recibe_msg_cliente(mensaje);
           
            
            
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
}
