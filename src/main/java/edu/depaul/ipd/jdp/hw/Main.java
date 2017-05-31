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
        if (!Character.isDigit(c))
            return false;
    }
    return true;
}
    
    /** This application populates the HSQL database from the student and 
     *  courses taken input data files, then calculates each student's GPA.
     * @param args
     */
    public static void main(String[] args){
        
        try(Connection con = DbConnection.getConnection()){
            
        }catch (SQLException sql){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,"Database Not Connected",sql);
            System.exit(0);
        }
        
        String line;
        
        InputStream stream = null;
        stream = Main.class.getClassLoader().getResourceAsStream("data/student.data");
        
        if(stream==null){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,"Student File Not Found");
        }else{ 
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
            
                while((line = br.readLine()) != null) {
                    String[] tokens = line.split(";");
                    
                    if (isNumeric(tokens[0])){
                        
                        int studentId = Integer.valueOf(tokens[0]);  
                        Students student = new Students(studentId);

                        student.setLastname(tokens[1]); 
                        student.setFirstname(tokens[2]);
                        //student.setGpa(null); //may check to see if student id already exists?
                        student.setGpa(null);
                        student.setPhonenumber(tokens[4]);

                        //System.out.println(student);

                        student.add();
                    }else{
                        Logger.getLogger(Main.class.getName()).log(Level.INFO,
                                "Invalid Student Id, Student Record Not Added");
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

                    if (isNumeric(tokens[0])){
                        int studentId = Integer.valueOf(tokens[0]);

                        String courseId = tokens[1];

                        Coursestaken course = new Coursestaken(studentId,courseId);

                        course.setStudentid(studentId);
                        course.setCourseid(courseId); 
                        course.setGrade(tokens[2].charAt(0));

                        //System.out.println(course);

                        course.add();
                    }else{
                        Logger.getLogger(Main.class.getName()).log(Level.INFO,
                                "Invalid Student Id, CourseTaken Record Not Added");
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
                Double avgGpa = 0.0;

                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM PUBLIC.STUDENTS");

                while (rs.next()){

                    Students student = new Students(rs.getInt(1));
                    student.setLastname(rs.getString(2));
                    student.setFirstname(rs.getString(3));
                    student.setPhonenumber(rs.getString(5));

                    avgGpa = student.calcAvgGpa();
                    student.setGpa(avgGpa);
                    //System.out.println(student);
                    student.updateGpa();

                }
            }
        } catch (SQLException sql){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                                "Database Connection Issue",sql);
            //throw new RuntimeException(sql);
        }
    }
}