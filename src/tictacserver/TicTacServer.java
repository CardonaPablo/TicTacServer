/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictacserver;

import java.net.*;
import java.util.ArrayList;

/**
 *
 * @author pabca
 */
public class TicTacServer {

    ServerSocket server;
    final int puerto = 9000;
    static ArrayList<ServerThread> conexiones = new ArrayList<>();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         new TicTacServer().iniciar();
    }
    
    public void iniciar() {
     try {
        //Iniciar el Server en el puerto correspondiente
        server= new ServerSocket(puerto);
        System.out.println("Iniciando servidor...");
        
        while(true){
            //Crear el socket y aceptar la conexión 
            Socket socket;
            socket = server.accept();
            System.out.println("Cliente conectado");
            ServerThread a = new ServerThread(socket);
            a.start();
            conexiones.add(a);
         }
     } catch (Exception e){
         System.out.println("Error de conexion"+e.getMessage());
     }
    }
    
    
    
}
