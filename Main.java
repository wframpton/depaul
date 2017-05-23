package edu.depaul.ipd.jdp.hw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        InputStream stream = Main.class.getClassLoader().getResourceAsStream("data/student.data");
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        
        String line;
        while((line = br.readLine()) != null) {
            String[] tokens = line.split(";");
            
            //validate id is numeric before creating student object
            int studentId = Integer.valueOf(tokens[0]);
            Students student = new Students(studentId);
            
            student.setLastname(tokens[1]); //is there a way to calc position from table?
            student.setFirstname(tokens[2]);
            //student.setGpa(null); //may check to see if student id already exists?
            student.setGpa(null);
            student.setPhonenumber(tokens[4]);
            
            System.out.println(student);
            
            student.add();
        }  
        
        InputStream stream2 = Main.class.getClassLoader().getResourceAsStream("data/coursestaken.data");
        BufferedReader br2 = new BufferedReader(new InputStreamReader(stream2));
        
        String line2;
        int i = 1;
        
        while((line2 = br2.readLine()) != null) {
            String[] tokens = line2.split(";");
            
            int studentId = Integer.valueOf(tokens[0]);
            String courseId = tokens[1];
            
            //validate id is numeric before creating student object
            
            Coursestaken course = new Coursestaken(studentId,courseId);
            
            course.setStudentid(studentId);
            course.setCourseid(courseId); //is there a way to calc position from table?
            course.setGrade(tokens[2].charAt(0));
            
            System.out.println(course);
            
            course.add();
            i++;
        } 
        
        try(Connection con = DbConnection.getConnection()){
            
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
               
                System.out.println(student);
                student.updateGpa();
            }
        } catch (SQLException sql){
            throw new RuntimeException(sql);
        }
    }
}