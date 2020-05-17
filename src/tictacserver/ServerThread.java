/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictacserver;

import Database.DBConnector;
import java.io.*;
import java.net.*;

/**
 *
 * @author pabca
 */

public class ServerThread extends Thread {
    private Socket socket;
    private DataOutputStream salida;
    private DataInputStream entrada;
    private int sessionId;
    final DBConnector connector = new DBConnector();
    
    public ServerThread(Socket socket, int id) {
        this.socket = socket;
        this.sessionId = id;
        try {
            salida = new DataOutputStream(socket.getOutputStream());
            entrada = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    @Override
    public void run() {
        System.out.println("Hilo iniciado");
        while(true) {
            String accion = "";
            try {
                accion = entrada.readUTF();
                System.out.println("ReadUtf"+ accion);
                switch (accion) {
                    case "login":
                        String user = entrada.readUTF();
                        System.out.println("ReadUtf"+ user);
                        String pass = entrada.readUTF();
                        System.out.println("ReadUtf"+ pass);
                        salida.writeBoolean(connector.login(user, pass));
                    break;
                }
            } catch (IOException ex) {
                System.out.println("ex: " + ex);
            } 
        }
    }
    
    
}