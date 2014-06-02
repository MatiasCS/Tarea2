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
        BufferedReader entrada = new BufferedReader(new FileReader(conversacion));
        String linea = "";
        DataOutputStream outCliente;
        outCliente = new DataOutputStream(this.conexion.getOutputStream());
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
    
    //Funcion que sirve para recoger los datos del archivo Nombre y Tamaño
    //recibe como parametros la IP_Fuente y la IP_Destino
    public String servidor_recibe_archivo_datos_cliente(String IP_Fuente, String IP_Destino) throws IOException{
        File archivos_compartidos = new File(IP_Fuente+"_Archivos.txt");
        FileWriter escritor = new FileWriter(archivos_compartidos, true);
        BufferedWriter buffescritor = new BufferedWriter(escritor);
        PrintWriter escritor_final = new PrintWriter(buffescritor);                    
        
        DataInputStream dis = new DataInputStream( this.conexion.getInputStream() );
        String nombreArchivo = dis.readUTF().toString(); 
        int tam = dis.readInt(); 
        escritor_final.append(nombreArchivo + "##" +tam+ "##" +IP_Destino +"\r\n");
        escritor_final.close();
        buffescritor.close();
        return (nombreArchivo+"##"+tam);
    }
    
    //Funcion que sirve para recoger el flujo de datos del archivo enviado por el cliente y reescribirlos en un
    //archivo en el sector del servidor
    //Como parametros recibe nombre y largo que pueden ser obtenidos con la funcion servidor_recibe_archivo_datos_cliente
    public void servidor_recibe_archivo_cliente(String nombre, int largo) throws FileNotFoundException, IOException{
        String nombreArchivo=nombre;
        int tam=largo;
        System.out.println( "Recibiendo archivo "+nombreArchivo );
        FileOutputStream fos = new FileOutputStream( nombreArchivo );
        BufferedOutputStream out = new BufferedOutputStream( fos );
        BufferedInputStream in = new BufferedInputStream( conexion.getInputStream() );
        byte[] buffer = new byte[ tam ];
        for( int i = 0; i < buffer.length; i++ ){            
              buffer[ i ] = ( byte )in.read( ); 
        }
        out.write( buffer ); 
        out.flush(); 
        in.close();
        out.close(); 
        //conexion.close();
        System.out.println( "Archivo Recibido "+nombreArchivo );
    }
    /*
    //Funcion para enviar por una conexion los datos Nombre y Tamaño del archivo que se mandará
    //Como parametro recibe un directorio que será entregado desde la página WEB semantica
    public void servidor_envia_nombre_y_largo() throws IOException{
        DataOutputStream dos = new DataOutputStream( this.conexion.getOutputStream());
        dos.writeUTF("");
        dos.writeInt(717263);
        dos.flush();
    }
    
    //Funcion para enviar por una conexion el Flujo de datos del archivo.
    //Como parametros recibe el directorio del archivo
    public void servidor_envia_archivo_cliente() throws FileNotFoundException, IOException{
        String nombreArchivo="Documento.pdf";
        File archivo = new File( nombreArchivo );
        int largo_archivo = 717263;
        FileInputStream fis = new FileInputStream( nombreArchivo );
        BufferedInputStream bis = new BufferedInputStream( fis );
        BufferedOutputStream bos = new BufferedOutputStream( conexion.getOutputStream());
        byte[] buffer = new byte[ largo_archivo ];
        bis.read( buffer ); 
        for( int i = 0; i < buffer.length; i++ )
        {
            bos.write( buffer[ i ] ); 
        } 
        bis.close();
        bos.close();
        conexion.close(); 
        
    }*/
    
    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        BufferedReader inClienteTCP;
        System.out.println("Hola acepte conexion");
       
        /*
        Acá esta lo que responde el servidor en este caso, falta colocarlo por caso como esta abajo
        acá necesitare ayuda por que trate de ponerlo pero no pude xd pero iwal deberia funcionar.
        
        try {    
        servidor_recibe_archivo_datos_cliente("18083782-5","18083782-4");
        servidor_recibe_archivo_cliente("Documento.pdf",717263);
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        try {
            servidor_envia_archivo_cliente();
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        
        
        
        
        
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
                    SENDOK("Anyone is there","192.168.0.4","192.168.0.4");
                    mensaje = inClienteTCP.readLine();
                    System.out.println("SendMsg:"+mensaje);
                    //Quebrar la cadena;
                    //SENDOK(parametros);
                    break;
                case("GOTMSG"):
                    SENDMSGS("192.168.0.4",0);
                    System.out.println("GotMsg:"+mensaje);
                    mensaje = null;
                    break;
            }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
}
