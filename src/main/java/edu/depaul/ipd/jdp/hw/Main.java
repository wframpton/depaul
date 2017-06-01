package edu.depaul.ipd.jdp.hw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Will Frampton
 * @author wfram
 */
public class Main {

    /** Checks to see if string is a numeric value.
     *
     * @param str
     * @return True - If string is numeric.
     *         False - If string is not numeric.
     */
    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }
    
    /** This application populates the HSQL database from the student and 
     *  courses taken input data files, then calculates each student's GPA.
     * @param args
     */
    public static void main(String[] args){
        
        String line;
        
        InputStream stream = null;
        stream = Main.class.getClassLoader().getResourceAsStream("data/student.data");
        
        if(stream==null){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,"Student File Not Found");
        }else{ 
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
            
                while((line = br.readLine()) != null) {
                    String[] tokens = line.split(";");
                    if (tokens.length!=5){    //Expecting 5 columns
                        Logger.getLogger(Main.class.getName()).log(Level.INFO,
                                "Invalid Number of Input Fields");
                    }else{
                    
                        if (isNumeric(tokens[0])){

                            int studentId = Integer.valueOf(tokens[0]);  
                            Student student = new Student(studentId);

                            student.setLastname(tokens[1]); 
                            student.setFirstname(tokens[2]);
                            
                            String phoneNumber = tokens[4];
                       
                            phoneNumber=phoneNumber.trim().replaceAll("[^\\d]","");
                            
                            if(phoneNumber.length()==10 && isNumeric(phoneNumber)){

                                phoneNumber=phoneNumber.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "$1-$2-$3");
                                
                                student.setPhonenumber(phoneNumber);
                                
                                try{
                                    student.add();
                                }catch(RuntimeException ex){
                                    System.exit(1);
                                }
                            }else{
                                Logger.getLogger(Main.class.getName()).log(Level.INFO,
                                    "Invalid Phone Number, Student Record Not Added");
                            }
                        }else{
                            Logger.getLogger(Main.class.getName()).log(Level.INFO,
                                    "Invalid Student Id, Student Record Not Added");
                        }
                    }
                }
            }catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                        "Unable to read student input file", ex);
            }finally{
                //Make Sure Input Stream is Closed
                try{stream.close();}catch (IOException ex){}
            }

        }
        
        InputStream stream2 = Main.class.getClassLoader().getResourceAsStream("data/coursestaken.data");
        
        if(stream2==null){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,"Coursetaken File Not Found");
        }else{
            try(BufferedReader br2 = new BufferedReader(new InputStreamReader(stream2))){
           
                while((line = br2.readLine()) != null) {
                    String[] tokens = line.split(";");

                    if (tokens.length!=3){    //Expecting 3 tokens/columns
                        Logger.getLogger(Main.class.getName()).log(Level.INFO,
                                "Invalid Number of Input Fields");
                    }else{
                        if (isNumeric(tokens[0])){
                            int studentId = Integer.valueOf(tokens[0]);
                            String courseId = tokens[1];
                            String grade = tokens[2];
                            Character letterGrade;
                            
                            if(grade.length()!=1){
                                Logger.getLogger(Main.class.getName()).log(Level.INFO,
                                    "Invalid Grade, CourseTaken Record Not Added");
                            }else{
                                letterGrade = grade.charAt(0);
                                
                                if(letterGrade < 'A' || letterGrade > 'F' || letterGrade == 'E'){
                                    Logger.getLogger(Main.class.getName()).log(Level.INFO,
                                    "Invalid Grade, CourseTaken Record Not Added");
                                }else{
                                    Coursestaken course = new Coursestaken(studentId,courseId,letterGrade);
                                    
                                    try{
                                        course.add();
                                    }catch(RuntimeException ex){
                                        System.exit(1);
                                    }
                                }
                            }
                        }else{
                            Logger.getLogger(Main.class.getName()).log(Level.INFO,
                                    "Invalid Student Id, CourseTaken Record Not Added");
                        }
                    }
                }

            }catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                        "Unable to read cousetaken input file", ex);
            }finally{
                //Make Sure Input Stream is Closed
                try{stream2.close();}catch (IOException ex){}
            } 
        }
        
        try(Connection con = DbConnection.getConnection()){
            
            if(con!=null){

                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM PUBLIC.STUDENTS");

                while (rs.next()){

                    Student student = new Student(rs.getInt(1));
                    student.setLastname(rs.getString(2));
                    student.setFirstname(rs.getString(3));
                    student.setPhonenumber(rs.getString(5));

                    student.updateGpa();

                }
            }
        } catch (SQLException sql){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                                "Database Connection Issue",sql);
            System.exit(1);
        }
    }
}