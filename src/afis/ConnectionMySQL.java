package afis;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Stefanos Tzortzoglou
 */

public class ConnectionMySQL {
    public String db;
    public String url;
    public String user;
    public String pass;

    public ConnectionMySQL() {
        this.db = "Attendance";
        this.url = "jdbc:mysql://localhost/"+db;
        this.user = "root";
        this.pass = "";
    }   //Enter the information to connect to the database.
    
    public Connection Connect(){
        
        Connection link = null;
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            
            link = DriverManager.getConnection(this.url, this.user,this.pass);
            
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Error!, " + ex);
        }
        
        return link;
    }
        
}