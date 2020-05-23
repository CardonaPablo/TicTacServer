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
                    case "users":
                        getUsers();
                    break;
                }
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
            System.out.println("Usuarios conectados"+TicTacServer.conectedUsers.toString());
            salida.writeUTF(TicTacServer.conectedUsers.toString());
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
                    System.out.println(this.username + ": 2. Usuario encontrado");
                    if(TicTacServer.conexiones.get(i).recieveInvitation(this.username)){
                        //Iniciar el juego
                        System.out.println(this.username + ": Respuesta recibida, aceptando invitación de juego");
                        salida.writeBoolean(true);
                        salida.writeUTF(username);
                        createGame(TicTacServer.conexiones.get(i));
                    } else {
                        //Rechazar la invitacion
                        System.out.println(this.username + ": respuesta recibida, invitacion declinada");
                        salida.writeBoolean(false);
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
            salida.writeUTF(this.username);
            
            //Recibir la respuesta
            System.out.println(this.username + ": Esperando respuesta del usuario");
            boolean response = entrada.readBoolean();
            System.out.println(this.username + ": After Recibido: " + response);
            return response;
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void createGame(ServerThread rival) {
        System.out.println(this.username + ": 9. Creando juego desde server");
        try {
            rivalActual = rival;
            System.out.println(this.username + ":Rival obtenido");
            //Asignar los signos de cada uno
            System.out.println(this.username + ":10. Enviando signos");
            rival.salida.writeUTF("O");
            salida.writeUTF("X");
            System.out.println(this.username + ":12. Enviando ismyTurn");
            //Establecer quien va primero
            rival.salida.writeBoolean(false);
            salida.writeBoolean(true);
            
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void registerMove() {
        try {
            rivalActual.salida.writeUTF(entrada.readUTF());
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
