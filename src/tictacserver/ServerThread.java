/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictacserver;

import Database.DBConnector;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                switch (accion) {
                    case "login":
                        attemptLogin();
                    break;
                    case "register":
                        attemptRegister();
                    break;
                    case "game":
                    break;
                }
            } catch (IOException ex) {
                System.out.println("ex: " + ex);
            } 
        }
    }
    
    public void attemptLogin() {
        try {
            String user = entrada.readUTF();
            String pass = entrada.readUTF();
            salida.writeBoolean(connector.login(user, pass));
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void attemptRegister() {
        try {
            String user = entrada.readUTF();
            String pass = entrada.readUTF();
            salida.writeBoolean(connector.register(user, pass));
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void attemptRegisterGame() {
        try {
            String rival = entrada.readUTF();
            boolean isWinner = entrada.readBoolean();
            salida.writeBoolean(connector.registerGame(isWinner, rival));
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}