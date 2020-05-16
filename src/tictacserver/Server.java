/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictacserver;
import java.net.*;
import java.io.*;

/**
 *
 * @author pabca
 */

public class Server {
    ServerSocket server;
    Socket socket;
    int puerto=9000;
    DataOutputStream salida;
    BufferedReader entrada;
    
    public void iniciar()
    {
     try {
        server= new ServerSocket(puerto);
        while(true){
            socket=new Socket();
            socket=server.accept();
         
            //cuando entra algo
            entrada= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensaje=entrada.readLine();
            System.out.println("Entrada: "+mensaje+" de "+socket.toString());
         
            /* 
            RECIBIR OTRO MENSAJE DEL MISMO CLIENTE
            mensaje=entrada.readLine();
            System.out.println("Entrada2: "+mensaje);*/
         
            salida=new DataOutputStream(socket.getOutputStream());//respuesta
            salida.writeUTF("pong");//respuesta
            socket.close();
         }
     } catch(Exception e){
         System.out.println("Error de conexion"+e.getMessage());
     }
    }
}