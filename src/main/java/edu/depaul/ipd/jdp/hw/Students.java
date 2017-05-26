/*
 * Students Class
 * Author-Will Frampton
 * 
 */
package edu.depaul.ipd.jdp.hw;

import java.sql.*;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author wfram
 */
@Entity
@Table(name = "STUDENTS")
@NamedQueries({
    @NamedQuery(name = "Students.findAll", query = "SELECT s FROM Students s")
    , @NamedQuery(name = "Students.findByStudentid", query = "SELECT s FROM Students s WHERE s.studentid = :studentid")
    , @NamedQuery(name = "Students.findByLastname", query = "SELECT s FROM Students s WHERE s.lastname = :lastname")
    , @NamedQuery(name = "Students.findByFirstname", query = "SELECT s FROM Students s WHERE s.firstname = :firstname")
    , @NamedQuery(name = "Students.findByGpa", query = "SELECT s FROM Students s WHERE s.gpa = :gpa")
    , @NamedQuery(name = "Students.findByPhonenumber", query = "SELECT s FROM Students s WHERE s.phonenumber = :phonenumber")})

public class Students implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "STUDENTID")
    private Integer studentid;
    @Column(name = "LASTNAME")
    private String lastname;
    @Column(name = "FIRSTNAME")
    private String firstname;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "GPA")
    private Double gpa;
    @Column(name = "PHONENUMBER")
    private String phonenumber;

    /**
     *
     */
    public Students() {
    }

    /**
     *
     * @param studentid
     */
    public Students(Integer studentid) {
        this.studentid = studentid;
    }
    
    /**
     *
     * @param studentid
     * @param lastname
     * @param firstname
     * @param gpa
     * @param phonenumber
     */
    public Students(Integer studentid,String lastname,String firstname,
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
    public void add(){
        
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
                            "\',"+this.gpa+",\'"+this.phonenumber.substring(1).replace(") ","-")+"\')";

                    stmt.executeUpdate(sqlAddStudent);
                    System.out.println("StudentId "+this.studentid+" Added");
                }
            }
        } catch (SQLException sql){
            throw new RuntimeException(sql);
        }
    }
    
    /** updateGpa() - updates student item's GPA field on the HSQL Database
     *
     *
     */
    public void updateGpa(){
        
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

                        System.out.println("StudentId "+this.studentid+" Updated");

                    }
                }else{
                    System.out.println("StudentId "+this.studentid+" Not Updated - Record Not Found");
                }
            }
        } catch (SQLException sql){
            throw new RuntimeException(sql);
        }
    }
    
    /** calcAvgGpa() - calculates student's GPA from cousestaken table 
     *                 on HSQL Database
     * @return returns calculated GPA value
     * @throws ClassNotFoundException
     */
    public Double calcAvgGpa() throws ClassNotFoundException {
      
        Double results = 0.0;
        
        try (Connection con = DbConnection.getConnection()){

            if(con!=null){
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT GRADE FROM PUBLIC.COUSESTAKEN" +
                            " WHERE STUDENTID =" + this.studentid);

                double gpaSum = 0;
                int nbrOfClasses = 0;

                while(rs.next()){

                    double multiplier = 0;

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
            throw new RuntimeException(sql);
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
        if (!(object instanceof Students)) {
            return false;
        }
        Students other = (Students) object;
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
