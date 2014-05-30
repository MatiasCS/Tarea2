/*lo tuyo no
 Probando!
 */

package HttpServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import TCPClient.ClienteTCP;
import com.sun.org.apache.xerces.internal.xs.StringList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Matias
 */
public class ServidorHttp implements Runnable{
    
    //Variables Estaticas
    static final int puerto = 8080;
    static final File directorio_raiz = new File(".");
    static final String inicio = "index.html";
    
    
    Socket conexion;
    
    public ServidorHttp(Socket conexion) throws IOException{
        this.conexion = conexion;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  {
        try {
            // TODO code application logic here
            //Creacion del servidor y espera de clientes
            System.out.println(InetAddress.getLocalHost());
            ServerSocket servidor = new ServerSocket(puerto);
            while(true){
                ServidorHttp cliente = new ServidorHttp(servidor.accept());
                Thread hebra = new Thread(cliente);
                hebra.start();  
              }
            
        } catch (IOException ex) {
            Logger.getLogger(ServidorHttp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        
        // Mensaje cliente, archivo que se pide, metodo POST o GET
        BufferedReader formulario;      //Variable que guarda la entrada.
        BufferedReader entradacliente;
        String archivoPedido;
        String metodo;
        BufferedOutputStream salidaArchivo;
        PrintWriter output = null;
        try {
        
            //Lectura mensaje enviado por el cliente
            //crearHtml();
            entradacliente = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            String entrada = entradacliente.readLine();
            System.out.println("Entrada:"+ entrada + "\n");//nuevo
            StringTokenizer token = new StringTokenizer(entrada);
            metodo = token.nextToken();
            archivoPedido = token.nextToken();
            salidaArchivo = new BufferedOutputStream(conexion.getOutputStream());
            output = new PrintWriter(conexion.getOutputStream());
            
            
            formulario =entradacliente; //Se le da el valor a la variable de lo que envia el Usuario

            if(archivoPedido.equals("/"))
                archivoPedido += inicio;
        
            //Implementacion GET
            if(metodo.equals("GET")){
                FileInputStream stream;               
                File archivo = new File(directorio_raiz,archivoPedido);                    
                int pesoArchivo = (int) archivo.length();
                if(!archivoPedido.startsWith("/?")){
                   
                    byte[] buffer = new byte[pesoArchivo];                   
                    List <String> lista = obtenerContactos();
                    
                    if(archivoPedido.startsWith("/index")){
                        BufferedReader index = new BufferedReader(new FileReader(archivo));
                        String linea;
                        String temp = "";
                        Iterator iterador2 = lista.iterator();
                        boolean flag = true;                        
                        String ultimoNombre = "";
                        boolean escribo = true;                          
                        while(index.ready()){
                            linea = index.readLine();
                            if(linea.indexOf("<!--Contactos-->")>0){                                  
                                Iterator iterador = lista.iterator();
                                while(iterador.hasNext()){                                    
                                    temp += (String) iterador.next();                                  
                                }
                            }
                            else
                                temp = temp + linea;
                        }
                    output.println("HTTP/1.0 200 OK");
                    output.println("Server: Java HTTP Server 1.0");
                    output.println("Date: " + new Date());
                    output.println("Content-length: " + temp.length());
                    output.println("Content-type: text/html");
                    output.println("");
                    output.println(temp);
                    output.flush();                                               
                    }
                    else{
                        stream = new FileInputStream(archivo);
                        stream.read(buffer);                
                        String contenido = tipoDeContenido(archivoPedido);

                        output.println("HTTP/1.0 200 OK");
                        output.println("Server: Java HTTP Server 1.0");
                        output.println("Date: " + new Date());
                        output.println("Content-length: " + pesoArchivo);
                        output.println("Content-type: " + contenido);
                        output.println();
                        output.flush();

                        salidaArchivo.write(buffer,0,pesoArchivo);
                        salidaArchivo.flush();

                        entradacliente.close();
                        salidaArchivo.close();
                    }
                }
                else{
                    String instruccion = archivoPedido;
                    StringTokenizer t = new StringTokenizer(instruccion,"/?");
                    String mensaje = t.nextToken();
                    
                    if(mensaje.startsWith("mensaje")){
                        System.out.println("HolaHolaHola");
                        ClienteTCP TCPClient;
                        TCPClient = new ClienteTCP();
                        TCPClient.enviarMensaje(mensaje);
                    }
                }
                conexion.close();
                output.close();
            }
            //Implementacion POST
            else if(metodo.equals("POST")){
                //--------------------------------------
                //Parte de identificar el contenido.
                //--------------------------------------
                int Largo=-1;                   //Variable que indica largo de la palabra.
                String delimitadores="[& =]";   //String que contiene los delimitadores de las palabras
                String datos1;                  //Variable auxiliar para guardar los datos que se tomaran con el metodo post
                String[] datos2;                //Varible auxiliar para guardar los datos finales.
                File archivo = new File(directorio_raiz,archivoPedido);
                int pesoArchivo = (int) archivo.length();
                while(true){
                    final String linea=formulario.readLine();           
                    final String iniciador = "Content-Length: ";            //Variable para indicar que se debe leer la palabra que comienze con esas palabras
                    if(linea.startsWith(iniciador)){
                        Largo=Integer.parseInt(linea.substring(iniciador.length()));
                    }
                    if (linea.length()==0){
                        break;
                    }
                }
                final char[] contenido=new char[Largo];
                formulario.read(contenido);
                       
                datos1 = new String(contenido);
                System.out.println(datos1);    
                datos2=datos1.split(delimitadores);
                System.out.println(datos2[1]);    
                System.out.println(datos2[3]);    
                System.out.println(datos2[5]);    
                
                
                //-----------------------------------------
                //Parte de ingresar los datos a un archivo.txt
                //-----------------------------------------
                File fichero;
                fichero = new File("Contactos.txt");
                
                try{
                    FileWriter escritor=new FileWriter(fichero,true);
                    BufferedWriter buffescritor=new BufferedWriter(escritor);
                    PrintWriter escritor_final= new PrintWriter(buffescritor);
                    
                    escritor_final.append(datos2[1]+" "+datos2[3]+" "+datos2[5]+"\r\n");
                    escritor_final.close();
                    buffescritor.close();
                
                    FileInputStream stream;
                    byte[] buffer = new byte[pesoArchivo];

                    stream = new FileInputStream(archivo);
                    stream.read(buffer);

                    output.println("HTTP/1.0 200 OK");
                    output.println("Server: Java HTTP Server 1.0");
                    output.println("Date: " + new Date());
                    output.println("Content-length: " + pesoArchivo);
                    output.println("Content-type: " + contenido);
                    output.println();
                    output.flush();

                    salidaArchivo.write(buffer,0,pesoArchivo);
                    salidaArchivo.flush();

                }
                catch(IOException e){}
                //-----------------------------------------
                //FIN ESCRIBIR FICHERO
                //-----------------------------------------

            }
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        } catch (IOException ex) {
            Logger.getLogger(ServidorHttp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<String> obtenerContactos(){
        File f = new File( "Contactos.txt" );
        List<String> Contactos = new ArrayList<>();
        //File html = new File("contacto.html");
        BufferedReader entrada;
        try {
            //FileWriter escrito = new FileWriter(html);
            //BufferedWriter bw = new BufferedWriter(escrito);
            //PrintWriter wr = new PrintWriter(bw);  

            entrada = new BufferedReader( new FileReader( f ) );
            String linea;
            //wr.append("<HTML>");
            //wr.append("<BODY>");
            System.out.println(entrada.ready());
            while(entrada.ready()){
                linea = entrada.readLine();
                StringTokenizer nombre = new StringTokenizer(linea);
                Contactos.add(leerNombre(nombre.nextToken()));
                //wr.append("<FONT FACE = 'calibri' >" + leerNombre(nombre.nextToken()) + "</FONT><BR>");
            }
            //wr.append("</BODY></HTML>");
            //wr.close();
            //bw.close();
            entrada.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return Contactos;
    }
    
    public String leerNombre(String nombre){
        StringTokenizer token = new StringTokenizer(nombre, "+");
        String nombreCompleto = "";
        while(token.hasMoreElements()){
            nombreCompleto += token.nextToken() + " ";
        }
        return(nombreCompleto);
    }
    
     private String tipoDeContenido(String archivo)
  {
    if (archivo.endsWith(".htm") ||
      archivo.endsWith(".html"))
    {
      return "text/html";
    }
    else if (archivo.endsWith(".png"))
    {
      return "image/png";
    }
    else if (archivo.endsWith(".jpg") ||
      archivo.endsWith(".jpeg"))
    {
      return "image/jpeg";
    }
    else
    {
      return "text/plain";
    }
  }
    
}
