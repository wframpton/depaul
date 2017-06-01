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
    
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    /** This application populates the HSQL database from the student and 
     *  courses taken input data files, then calculates each student's GPA.
     * @param args
     */
    public static void main(String[] args){
        
        String line;
        int recordsAdded=0;
        
        InputStream stream = null;
        stream = Main.class.getClassLoader().getResourceAsStream("data/student.data");
        
        if(stream==null){
            LOGGER.log(Level.SEVERE,"Student File Not Found");
        }else{ 
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
            
                while((line = br.readLine()) != null) {
                    String[] tokens = line.split(";");
                    if (tokens.length!=5){    //Expecting 5 columns
                        LOGGER.log(Level.INFO,"Invalid Number Of Input Fields For Student File");
                    }else{
                    
                        if (isNumeric(tokens[0])){

                            int studentId = Integer.valueOf(tokens[0]);  
                            Student student = new Student(studentId);

                            student.setLastname(tokens[1]); 
                            student.setFirstname(tokens[2]);
                            //Leave gpa null and only let the calcAvg() method populate the field
                            String phoneNumber = tokens[4];
                            phoneNumber=phoneNumber.trim().replaceAll("[^\\d]","");
                            
                            if(phoneNumber.length()==10 && isNumeric(phoneNumber)){

                                phoneNumber=phoneNumber.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "$1-$2-$3");
                                
                                student.setPhonenumber(phoneNumber);
                                
                                try{
                                    student.add();
                                    recordsAdded++;
                                }catch(RuntimeException ex){
                                    System.exit(1);
                                }
                            }else{
                                LOGGER.log(Level.INFO,"Invalid Phone Number, Student Record Not Added");
                            }
                        }else{
                            LOGGER.log(Level.INFO,"Invalid Student Id, Student Record Not Added");
                        }
                    }
                }
            }catch (IOException ex) {
                LOGGER.log(Level.SEVERE,"Unable To Read Student Input File", ex);
            }finally{
                //Make Sure Input Stream is Closed
                try{stream.close();}catch (IOException ex){}
            }
        }
        if(recordsAdded==0){
            LOGGER.log(Level.INFO,"No Records Added For Student Input File");
        }
        
        recordsAdded=0; //Re-initialize records Added Counter
        
        InputStream stream2 = Main.class.getClassLoader().getResourceAsStream("data/coursestaken.data");
        
        if(stream2==null){
            LOGGER.log(Level.SEVERE,"CoursesTaken File Not Found");
        }else{
            try(BufferedReader br2 = new BufferedReader(new InputStreamReader(stream2))){
           
                while((line = br2.readLine()) != null) {
                    String[] tokens = line.split(";");

                    if (tokens.length!=3){    //Expecting 3 tokens/columns
                        LOGGER.log(Level.INFO,"Invalid Number Of Input Fields For CoursesTaken Ffile");
                    }else{
                        if (isNumeric(tokens[0])){
                            int studentId = Integer.valueOf(tokens[0]);
                            String courseId = tokens[1];
                            String grade = tokens[2];
                            Character letterGrade;
                            
                            if(grade.length()!=1){
                                LOGGER.log(Level.INFO,"Invalid Grade, CoursesTaken Record Not Added");
                            }else{
                                letterGrade = grade.charAt(0);
                                
                                if(letterGrade < 'A' || letterGrade > 'F' || letterGrade == 'E'){
                                    LOGGER.log(Level.INFO,"Invalid Grade, CoursesTaken Record Not Added");
                                }else{
                                    CourseTaken course = new CourseTaken(studentId,courseId,letterGrade);
                                    
                                    try{
                                        course.add();
                                    }catch(RuntimeException ex){
                                        System.exit(1);
                                    }
                                }
                            }
                        }else{
                            LOGGER.log(Level.INFO,"Invalid Student Id, CoursesTaken Record Not Added");
                        }
                    }
                }

            }catch (IOException ex) {
                LOGGER.log(Level.SEVERE,"Unable To Read Coursestaken Input File", ex);
            }finally{
                //Make Sure Input Stream is Closed
                try{stream2.close();}catch (IOException ex){}
            } 
        }
        if(recordsAdded==0){
            LOGGER.log(Level.INFO,"No Records Added For Coursestaken Input File");
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
            LOGGER.log(Level.SEVERE,"Database Connection Issue",sql);
            System.exit(1);  //Exit with Error just in case more logic is added
        }
    }
}