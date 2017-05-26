/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.depaul.ipd.jdp.hw;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author wfram
 */
public class DbConnection {
    
    /** Connects to database that is specified in the properties file.
     *
     * @return returns Connection
     */
    public static Connection getConnection(){
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
                System.out.println("Database Not Connected");
            }
                
        } catch (IOException | ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("Database Not Connected");
        }
        return con;
    }
    
}
