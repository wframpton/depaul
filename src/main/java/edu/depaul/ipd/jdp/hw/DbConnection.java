package edu.depaul.ipd.jdp.hw;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/** DbConnection Class
 *
 * @author wfram Will Frampton
 */
public class DbConnection {
    
    private static final Logger LOGGER = Logger.getLogger(DbConnection.class.getName());
    
    /** Connects to database that is specified in the properties file.
     *
     * @return returns Connection
     * @throws java.sql.SQLException
     */
    public static Connection getConnection() throws SQLException{
        Properties props = new Properties();
        InputStream stream = null;
        Connection con = null;
        
        try {
            stream = Main.class.getClassLoader().getResourceAsStream("db-config.properties");
            props.load(stream);

            Class.forName(props.getProperty("db.driver.class"));

            con = DriverManager.getConnection(props.getProperty("db.url"),
                            props.getProperty("userid"),
                            props.getProperty("password"));
            
            if(con==null){
                LOGGER.log(Level.SEVERE,"Database Connection Error");
                throw new SQLException();
            }
                
        } catch (IOException | ClassNotFoundException | SQLException e) {

            throw new SQLException(e);
        }
        return con;
    }
    
}
