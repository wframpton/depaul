/*
 * Student Class
 * Author-Will Frampton
 * 
 */
package edu.depaul.ipd.jdp.hw;

import java.sql.*;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wfram
 */


public class Student implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer studentid;
    private String lastname;
    private String firstname;
    private Double gpa;
    private String phonenumber;

    /** Constructor for Students
     *
     * @param studentid
     */
    public Student(Integer studentid) {
        this.studentid = studentid;
    }
    
    /** Constructor for Students
     *
     * @param studentid
     * @param lastname
     * @param firstname
     * @param gpa
     * @param phonenumber
     */
    public Student(Integer studentid,String lastname,String firstname,
            Double gpa,String phonenumber) {
        
        this.studentid=studentid;
        this.lastname=lastname;
        this.firstname=firstname;
        this.gpa=gpa;
        this.phonenumber=phonenumber;
    }
    
    public Integer getStudentid() {
        return studentid;
    }

    public void setStudentid(Integer studentid) {
        this.studentid = studentid;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
    
    /** add() - adds student to HSQL Database
     *
     */
    public void add() throws RuntimeException{
        
        try (Connection con = DbConnection.getConnection()){
            
            if(con!=null){
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM PUBLIC.STUDENTS WHERE STUDENTID = " + this.studentid);

                if(rs.next()){
                    System.out.println("StudentId "+this.studentid+" Not Added - Already Exists");
                }else{                  

                    String sqlAddStudent = "INSERT INTO PUBLIC.STUDENTS " +
                            "(STUDENTID,LASTNAME,FIRSTNAME,GPA,PHONENUMBER) VALUES" +
                            " ("+this.studentid+",\'"+this.lastname+"\',\'"+this.firstname+
                            "\',"+this.gpa+",\'"+this.phonenumber+"\')";

                    stmt.executeUpdate(sqlAddStudent);
                    System.out.println("StudentId "+this.studentid+" Added");
                }
            }
        } catch (SQLException sql){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                                "Database Connection Issue");
            throw new RuntimeException(sql);
        }
    }
    
    /** updateGpa() - updates student item's GPA field on the HSQL Database
     *
     */
    public void updateGpa(){ 
        
        this.gpa = calcAvgGpa();
               
        try (Connection con = DbConnection.getConnection()){

            if(con!=null){
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM PUBLIC.STUDENTS WHERE STUDENTID = " + this.studentid);

                if (rs.next()){

                    if(gpa.equals(rs.getDouble(4))){
                        System.out.println("StudentId "+this.studentid+" Not Updated - GPA Value Did Not Change");
                    }else{

                        String x = "UPDATE PUBLIC.STUDENTS SET GPA="+
                                this.gpa+" WHERE STUDENTID="+this.studentid;

                        stmt.executeUpdate(x);

                        System.out.println("StudentId "+this.studentid+" GPA Updated: " + this.gpa);

                    }
                }else{
                    System.out.println("StudentId "+this.studentid+" Not Updated - Record Not Found");
                }
            }
        } catch (SQLException sql){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                                "Database Connection Issue");
        }
    }
    
    /** calcAvgGpa() - calculates student's GPA from cousestaken table 
     *                 on HSQL Database
     * @return returns calculated GPA value
     */
    private Double calcAvgGpa(){ 
        Double results = 0.0;
        
        try (Connection con = DbConnection.getConnection()){

            if(con!=null){
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT GRADE FROM PUBLIC.COUSESTAKEN" +
                            " WHERE STUDENTID =" + this.studentid);

                double gpaSum = 0.0;
                int nbrOfClasses = 0;

                while(rs.next()){

                    double multiplier = 0.0;

                    Character letterGrade = rs.getString(1).charAt(0);

                    switch (letterGrade)
                    {
                        case 'A':    
                            multiplier = 4.0;
                            break;
                        case 'B':
                            multiplier = 3.0;
                            break;
                        case 'C':
                            multiplier = 2.0;
                            break;
                        case 'D':
                            multiplier = 1.0;
                            break; 
                    }

                    gpaSum += multiplier;
                    ++nbrOfClasses;
                }  // end of coursesTaken Loop

                if(nbrOfClasses>0){
                    results=gpaSum/nbrOfClasses;
                }
            }
        } catch (SQLException sql){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                                "Database Connection Issue");
        }
        
        return results;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studentid != null ? studentid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Student)) {
            return false;
        }
        Student other = (Student) object;
        if ((this.studentid == null && other.studentid != null) || (this.studentid != null && !this.studentid.equals(other.studentid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        
        return "edu.depaul.ipd.jdp.hw.Students[ studentid=" + studentid + 
                " - " + firstname + " " + lastname +
               "\t\tgpa=" + gpa + " ]";
    }
    
}
