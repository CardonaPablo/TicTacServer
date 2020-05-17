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
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        this.username = "";
        try {
            PreparedStatement ps;
            ps = (PreparedStatement) con.prepareStatement("SELECT * FROM usuario WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
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
    

}
