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
    private ServerThread rivalActual;
    
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
                System.out.println(this.username + ": recibiendo accion: " + accion);
                switch (accion) {
                    case "login":
                        attemptLogin();
                    break;
                    case "logout":
                        logout();
                    break;
                    case "register":
                        attemptRegister();
                    break;
                    case "sendInvitation":
                        attemptSendInvitation();
                    break;
                    case "move":
                        registerMove();
                    break;
                    case "Respuesta Invitacion": 
                        if(entrada.readBoolean())
                            invitationAccepted();
                        else
                            invitationDeclined();
                    break;
                    case "registerGame": 
                        attemptRegisterGame();
                    break;
                    case "registerTiedGame": 
                        attemptRegisterTiedGame();
                    break;
                    case "registerPcGame": 
                        attemptRegisterPcGame();
                    break;
                    case "getPartidas": 
                        attemptGetPartidas();
                    break;
                    case "users":
                        getUsers();
                    break;
                }
                System.out.println("TERMINADA ACCION");
            } catch (IOException ex) {     
                execute=false;
                TicTacServer.conectedUsers.remove(username);
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
            if(result){
               this.username = user;
               TicTacServer.conectedUsers.add(user);
               
               System.out.println("Usuarios conectados"+TicTacServer.conectedUsers.toString());
                try {
                    salida.writeUTF(TicTacServer.conectedUsers.toString());
                    System.out.println("Enviado la lista de usuarios");
                } catch (IOException ex) {
                    Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getUsers(){
        try {
            System.out.println("GettingUserList");
            salida.writeUTF("usersList");
            salida.writeUTF(TicTacServer.conectedUsers.toString());
            System.out.println("UserList sent");
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void logout(){
        TicTacServer.conectedUsers.remove(username);
       /* try {
            salida.writeUTF(TicTacServer.conectedUsers.toString());
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }*/
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
            System.out.println(this.username + ": Notificando a rival perdedor");
            rivalActual.salida.writeUTF("lostGame");
            System.out.println(this.username + ": Guardando partida en BD");
            connector.registerGame(rivalActual.username);
            rivalActual.rivalActual = null;
            rivalActual = null;
                
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void attemptRegisterTiedGame() {
        try {
            System.out.println(this.username + ": Notificando a rival perdedor");
            rivalActual.salida.writeUTF("tiedGame");
            System.out.println(this.username + ": Guardando partida en BD");
            salida.writeBoolean(connector.registerTiedGame(rivalActual.username));
            rivalActual.rivalActual = null;
            rivalActual = null;
                
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void attemptGetPartidas(){
        try {
            String username=entrada.readUTF();
            System.out.println("LAS PARTIDAS DEL PANA"+connector.getPartidas(username) );
            salida.writeUTF("partidas");
            salida.writeUTF(connector.getPartidas(username));
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void attemptRegisterPcGame() {
        try {
           
           String ganador=entrada.readUTF();
           String perdedor=entrada.readUTF();
           Boolean empate=entrada.readBoolean();
           connector.registerPcGame(ganador,perdedor,empate);
                
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void attemptSendInvitation() {
        System.out.println("In function");
        try {
            String destinatario = entrada.readUTF();
            if(destinatario.startsWith(" ")){
                destinatario = destinatario.substring(1);
            }
            System.out.println("DESTINATARIO FINAL: "+ destinatario);
            //Encontrar el thread que tiene de username
            System.out.println("Recibido destinatario");
            for (int i = 0; i < TicTacServer.conexiones.size(); i++) {
                System.out.println("Buscando en conexiones: " + TicTacServer.conexiones.get(i).username);
                if (TicTacServer.conexiones.get(i).username.equals(destinatario)) {
                    System.out.println("Usuario encontrado: " +TicTacServer.conexiones.get(i).username);
                    //Enviar la invitacion
                    //Enviar a su flujo el receive Invitation
                    ServerThread threadRival = TicTacServer.conexiones.get(i);
                    System.out.println("Enviando a conexion rival");
                    threadRival.salida.writeUTF("recieveInvitation");
                    threadRival.salida.writeUTF(this.username);
                    threadRival.salida.writeUTF(threadRival.username);
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void invitationAccepted() {
        try {
            //Recibe el username que aceptó
            String rivalUser = entrada.readUTF();
            
            for (int i = 0; i < TicTacServer.conexiones.size(); i++) {
                if (TicTacServer.conexiones.get(i).username.equals(rivalUser)) {
                    TicTacServer.conexiones.get(i).rivalActual = this;
                    //Enviar a el que solicitó la invitación la respuesta y el username del rival
                    TicTacServer.conexiones.get(i).salida.writeUTF("onInvitationAccepted");
                    TicTacServer.conexiones.get(i).salida.writeUTF(this.username);
                    TicTacServer.conexiones.get(i).salida.writeUTF(rivalUser);
                    
                    //Crear el juego
                    createGame(TicTacServer.conexiones.get(i));
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void invitationDeclined(){
                System.out.println(this.username + ": Respuesta recibida, declinando invitación de juego");
        try {
            //Recibe el username que aceptó
            String rivalUser = entrada.readUTF();
            for (int i = 0; i < TicTacServer.conexiones.size(); i++) {
                if (TicTacServer.conexiones.get(i).username.equals(rivalUser)) {
                    //Enviar a el que solicitó la invitación la respuesta
                    TicTacServer.conexiones.get(i).salida.writeUTF("onInvitationDeclined");
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createGame(ServerThread rival) {
        try {
            rivalActual = rival;
            //Asignar los signos de cada uno
            rival.salida.writeUTF("O");
            salida.writeUTF("X");
            //Establecer quien va primero
            rival.salida.writeBoolean(false);
            salida.writeBoolean(true);
            
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void registerMove() {
        try {
            System.out.println(this.username + ": Registering Move");
            String slot = entrada.readUTF();
            System.out.println(this.username + ": Slot recieved: " + slot);
            rivalActual.salida.writeUTF("recieveMove");
            rivalActual.salida.writeUTF(slot);
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
