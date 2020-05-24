/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pabca
 */
public class DBConnector {
    
    Connection con;
    String username;
    
    /**
     * Se activa el conector y se instancia la conexión con la base de datos
     */
    public DBConnector() {
        try { 
            Class.forName("com.mysql.jdbc.Driver"); 
            con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/miniproyecto", "root", "");
        } catch (ClassNotFoundException ex) { 
            System.out.println("Error al cargar el driver " + ex.getMessage()); 
        } catch (SQLException e){
            System.out.println("Error al cargar la base de datos" + e.getMessage());
        }
    }
    
    public boolean login (String username, String password) {
        this.username = "";
        try {
            PreparedStatement ps;
            ps = (PreparedStatement) con.prepareStatement("SELECT * FROM usuario WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                System.out.println("Iniciando sesión");
                this.username = username;
                return true;
            } else {
                return false;
            }
        }
        catch(Exception e){
            System.err.println(e);
        }
        return false;
    }
    
    public boolean register (String username, String password) {
        this.username = "";
        try {
            PreparedStatement ps;
            ps = (PreparedStatement) con.prepareStatement("INSERT INTO usuario (username,password) VALUES (?,?)");
            ps.setString(1, username);
            ps.setString(2, password);

            ps.executeUpdate();
            return true;
        }
        catch(Exception e){
            System.err.println(e);
            return false;
        }
    }
    
    public boolean registerGame(String rival){
        try {
            PreparedStatement ps;
            ps = (PreparedStatement) con.prepareStatement("INSERT INTO Partida (ganador,perdedor,empate) VALUES (?,?,?)");
                ps.setString(1, username);
                ps.setString(2, rival);
                ps.setBoolean(3, false);//CAMBIAR ESO
            ps.executeUpdate();
            System.out.println("Juego registrado");
            return true;
            
        } catch (SQLException ex) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean registerTiedGame(String rival){
        try {
            PreparedStatement ps;
            ps = (PreparedStatement) con.prepareStatement("INSERT INTO Partida (ganador,perdedor,empate) VALUES (?,?,?)");
                ps.setString(1, username);
                ps.setString(2, rival);
                ps.setBoolean(3, true);//CAMBIAR ESO
            ps.executeUpdate();
            System.out.println("Juego registrado");
            return true;
            
        } catch (SQLException ex) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void registerPcGame(String ganador, String perdedor, Boolean empate){
        try {
            PreparedStatement ps;
            ps = (PreparedStatement) con.prepareStatement("INSERT INTO Partida (ganador,perdedor,empate) VALUES (?,?,?)");
            ps.setString(1, ganador);
            ps.setString(2, perdedor);
            ps.setBoolean(3, empate);
            ps.executeUpdate();
            System.out.println("Juego registrado");  
        } catch (SQLException ex) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getPartidas(String username){
        ArrayList<String> partidas = new ArrayList<>();
        
        try {
            PreparedStatement ps;
            ps = (PreparedStatement) con.prepareStatement("SELECT * FROM Partida WHERE ganador=? OR perdedor=? ORDER BY id DESC");
            ps.setString(1, username);
            ps.setString(2, username);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                String resultado="";
                if(rs.getBoolean("empate")){
                    resultado="Hubo un empate entre " + rs.getString("ganador") + " y " + rs.getString("perdedor");
                }
                else{
                    resultado=rs.getString("ganador")+" ganó ante "+rs.getString("perdedor");
                }
                partidas.add(resultado);
            }

            return partidas.toString();
            
        } catch (SQLException ex) {
            Logger.getLogger(DBConnector.class.getName()).log(Level.SEVERE, null, ex);
            return partidas.toString();
        }
    }
    

}
