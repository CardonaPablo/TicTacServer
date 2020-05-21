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
    final DBConnector connector = new DBConnector();
    String username;
    
    public ServerThread(Socket socket) {
        this.socket = socket;
        try {
            salida = new DataOutputStream(socket.getOutputStream());
            entrada = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    @Override
    public void run() {
        boolean execute=true;
        System.out.println("Hilo iniciado");
        while(execute) {
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
                    case "sendInvitation":
                        attemptSendInvitation();
                    break;
                    case "recieveInvitation":
                        
                    break;
                }
            } catch (IOException ex) {     
                execute=false;
                System.out.println("Deteniendo hilo: " + ex);
            } 
        }
    }
    
    public void attemptLogin() {
        try {
            String user = entrada.readUTF();
            String pass = entrada.readUTF();
            boolean result = connector.login(user, pass);
            salida.writeBoolean(result);
            if(result)
               this.username = user;
            
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
    
    public void attemptSendInvitation() {
        try {
            String destinatario = entrada.readUTF();
            //Encontrar el thread que tiene de username
            for (int i = 0; i < TicTacServer.conexiones.size(); i++) {
                if (TicTacServer.conexiones.get(i).username.equals(destinatario)) {
                    //Enviar la invitacion
                    if(TicTacServer.conexiones.get(i).recieveInvitation(this.username)){
                        //Iniciar el jeugo
                        startGame();
                    } else {
                        //Rechazar la invitacion
                    }
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean recieveInvitation(String username) {
        try {
            // Mandar a Menu la solicitud de juego
            salida.writeUTF("recieveInvitation");
            salida.writeUTF(username);
            //Recibir la respuesta
            return entrada.readBoolean();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void startGame() {
        
    }
}
