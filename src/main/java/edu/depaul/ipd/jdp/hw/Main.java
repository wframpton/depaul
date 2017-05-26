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

    /** This application populates the HSQL database from the student and 
     *  courses taken input data files, then calculates each student's GPA.
     * @param args
     */
    public static void main(String[] args){
        
        String line;
        
        InputStream stream = null;
                
        try {
            stream = Main.class.getClassLoader().getResourceAsStream("data/student.data");
        
            if(stream==null){
                System.out.println("Student File Not Found");
            }else{
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            
                while((line = br.readLine()) != null) {
                    String[] tokens = line.split(";");

                    //validate id is numeric before creating student object
                    try{
                        int studentId = Integer.valueOf(tokens[0]);
                        Students student = new Students(studentId);

                        student.setLastname(tokens[1]); //is there a way to calc position from table?
                        student.setFirstname(tokens[2]);
                        //student.setGpa(null); //may check to see if student id already exists?
                        student.setGpa(null);
                        student.setPhonenumber(tokens[4]);

                        System.out.println(student);

                        student.add();

                    }catch (NumberFormatException nbr){
                        System.out.println("Invalid Student.Student Id");                        
                    }
                }
                br.close();
                stream.close();
            } 
        }catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        InputStream stream2 = null;
        
        try {
            stream2 = Main.class.getClassLoader().getResourceAsStream("data/coursestaken.data");
        
            if(stream2==null){
                System.out.println("Coursetaken File Not Found");
            }else{
                BufferedReader br2 = new BufferedReader(new InputStreamReader(stream2));
           
                while((line = br2.readLine()) != null) {
                    String[] tokens = line.split(";");

                    try{
                        Integer studentId = Integer.valueOf(tokens[0]);

                        String courseId = tokens[1];

                        //validate id is numeric before creating student object

                        Coursestaken course = new Coursestaken(studentId,courseId);

                        course.setStudentid(studentId);
                        course.setCourseid(courseId); 
                        course.setGrade(tokens[2].charAt(0));

                        System.out.println(course);

                        course.add();
                    }catch(NumberFormatException nbr){
                        System.out.println("Invalid Coursetaken.Student Id");
                        //throw new RuntimeException(nbr);
                    }
                }
                br2.close();
                stream2.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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

                    try {
                        avgGpa = student.calcAvgGpa();
                        student.setGpa(avgGpa);
                        System.out.println(student);
                        student.updateGpa();
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (SQLException sql){
            throw new RuntimeException(sql);
        }
    }
}