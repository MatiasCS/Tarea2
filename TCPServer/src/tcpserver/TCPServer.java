/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tcpserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    String comandos[] = {"GREET","SENDOK","SENDMSGS"};
    
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
    }
    
    //FUNCIONES SERVIDOR
    //SE CONSIDERO ESTA ESTRUCTURA SEND_M##IP_DESTINO##IP_FUENTE##MENSAJE
    //Las funciones no necesariamente tienen que tener esos argumentos, tambien se le puede pasar el mensaje entero y ir revisandolo
    //dentro de la función.
    
    public void GREET() throws IOException{
        DataOutputStream outCliente;
        outCliente = new DataOutputStream(this.conexion.getOutputStream());
        String mensajeTotal = comandos[0] + "##Greetings! Bienvenido al Servidor de Avioncito de Papel";
        outCliente.writeBytes(mensajeTotal + '\n');
        outCliente.flush();
    }
    
    //Funcion para recibir mensajes desde el cliente y guardarlos en el servidor
    public void SENDOK(String mensaje, String IPDestino, String IPOrigen) throws IOException{    
        //Variables para el envio de datos hacia el clienteTCP
        DataOutputStream outCliente;
        outCliente = new DataOutputStream(this.conexion.getOutputStream());
        String mensajeTotal = comandos[1];
        
        //Traspaso del mensaje al archivo txt que sera guardado en el servidorTCP
        File conversacion = new File(IPDestino+".txt");
        FileWriter escritor = new FileWriter(conversacion, true);
        BufferedWriter buffescritor = new BufferedWriter(escritor);
        PrintWriter escritor_final = new PrintWriter(buffescritor);                    
        escritor_final.append(mensaje + "##" +IPOrigen+"\r\n");
        //SE GUARDA DE LA FORMA MENSAJE##IP_ORIGEN
        escritor_final.close();
        buffescritor.close();
        
        //Se envia el mensaje SENDOK al ClienteTCP indicando que el mensaje fue recibido correctamente
        outCliente.writeBytes(mensajeTotal + "\n");
        outCliente.flush();
    }
    
    public void SENDMSGS(String IPOrigen, int nSequencia) throws FileNotFoundException, IOException{
        File conversacion = new File(IPOrigen+".txt");
        DataOutputStream outCliente;
        outCliente = new DataOutputStream(this.conexion.getOutputStream());
        String linea = "";
        
        if(!conversacion.exists()){
            outCliente.writeBytes("FIN\n");
            outCliente.flush();
        }
        else{
            BufferedReader entrada = new BufferedReader(new FileReader(conversacion));
            while(nSequencia>0){
                linea = entrada.readLine();
                nSequencia -= 1;
            }
            while(entrada.ready()){            
                linea = entrada.readLine();
                String mensajeTotal = comandos[2]+"##"+linea+"\n";
                System.out.println(mensajeTotal);
                outCliente.writeBytes(mensajeTotal);
                outCliente.flush();
            }
            outCliente.writeBytes("FIN\n");
            outCliente.flush();
        }
    }
    
    //Funcion que sirve para recoger los datos del archivo Nombre y Tamaño
    //recibe como parametros la IP_Fuente y la IP_Destino
    public void servidor_recibe_archivo_datos_cliente(String IP_Fuente, String IP_Destino,String nombreArchivo1, int tamannoArchivo) throws IOException{
        File archivos_compartidos = new File(IP_Destino+"_Archivos.txt");
        FileWriter escritor = new FileWriter(archivos_compartidos, true);
        BufferedWriter buffescritor = new BufferedWriter(escritor);
        PrintWriter escritor_final = new PrintWriter(buffescritor);                    
        
        String nombreArchivo = nombreArchivo1; 
        int tam = tamannoArchivo;
        escritor_final.append(nombreArchivo + "##" +tam+ "##" +IP_Fuente +"\r\n");
        escritor_final.close();
        buffescritor.close();
       
    }
    
    //Funcion que sirve para recoger el flujo de datos del archivo enviado por el cliente y reescribirlos en un
    //archivo en el sector del servidor
    //Como parametros recibe nombre y largo que pueden ser obtenidos con la funcion servidor_recibe_archivo_datos_cliente
    public void servidor_recibe_archivo_cliente(String nombre, int largo) throws FileNotFoundException, IOException{
        ServerSocket serverSocket = new ServerSocket(15123);
        Socket socket = serverSocket.accept();
        byte [] bytearray  = new byte [largo];
        InputStream is = socket.getInputStream();
	FileOutputStream fos = new FileOutputStream(nombre);
	BufferedOutputStream bos = new BufferedOutputStream(fos);
        is.read( bytearray );
        for ( int i = 0; i < bytearray.length; i++ ) {
            bos.write( bytearray[ i ] );
        }
	bos.flush();
	bos.close();
        fos.close();
	socket.close();
    }
    
    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        BufferedReader inClienteTCP;
        System.out.println("Hola acepte conexion");
        try {          
            inClienteTCP = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String mensaje = inClienteTCP.readLine();
            
            while(mensaje != null){
            StringTokenizer token = new StringTokenizer(mensaje, "##");
            String metodo = token.nextToken();
            switch(metodo){                
                case("MEET"):
                    GREET();
                    mensaje = inClienteTCP.readLine();
                    System.out.println(mensaje);
                    break;
                case("SENDMSG"):
                    String IPDestino = token.nextToken();
                    String IPOrigen = token.nextToken();
                    String message = token.nextToken();
                    SENDOK(message,IPDestino,IPOrigen);
                    mensaje = inClienteTCP.readLine();
                    System.out.println("SendMsg:"+mensaje);
                    break;
                case("GOTMSG"):
                    String IPOrigen1 = token.nextToken();
                    int secuencia = Integer.parseInt(token.nextToken());
                    SENDMSGS(IPOrigen1,secuencia);
                    System.out.println("GotMsg:"+mensaje);
                    mensaje = null;
                    break;
                case("SENDFILE"):
                    String IPOrigen_Archivo= token.nextToken();
                    String IPDestino_Archivo=token.nextToken();
                    String nombreArchivo = token.nextToken();
                    int tamannoArchivo = Integer.parseInt(token.nextToken());
                    servidor_recibe_archivo_datos_cliente(IPOrigen_Archivo,IPDestino_Archivo,nombreArchivo,tamannoArchivo);
                    servidor_recibe_archivo_cliente(nombreArchivo,tamannoArchivo);
                    mensaje=null;
                    break;
                case("GOTFILE"):
                    break;
            }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
}
