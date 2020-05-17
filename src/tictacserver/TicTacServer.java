/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictacserver;

import java.net.*;

/**
 *
 * @author pabca
 */
public class TicTacServer {

    ServerSocket server;
    final int puerto = 9000;
    
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
        int sessionId = 0;
        
        while(true){
            //Crear el socket y aceptar la conexión 
            Socket socket;
            socket = server.accept();
            System.out.println("Cliente conectado");
            ((ServerThread) new ServerThread(socket, sessionId)).start();
            sessionId++;
         }
     } catch (Exception e){
         System.out.println("Error de conexion"+e.getMessage());
     }
    }
    
    
    
}
