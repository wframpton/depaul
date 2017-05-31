/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.depaul.ipd.jdp.hw;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@Table(name = "COUSESTAKEN")
@NamedQueries({
    @NamedQuery(name = "Cousestaken.findAll", query = "SELECT c FROM Cousestaken c")
    , @NamedQuery(name = "Cousestaken.findById", query = "SELECT c FROM Cousestaken c WHERE c.id = :id")
    , @NamedQuery(name = "Cousestaken.findByStudentid", query = "SELECT c FROM Cousestaken c WHERE c.studentid = :studentid")
    , @NamedQuery(name = "Cousestaken.findByCourseid", query = "SELECT c FROM Cousestaken c WHERE c.courseid = :courseid")
    , @NamedQuery(name = "Cousestaken.findByGrade", query = "SELECT c FROM Cousestaken c WHERE c.grade = :grade")})

public class Coursestaken implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "STUDENTID")
    private int studentid;
    @Basic(optional = false)
    @Column(name = "COURSEID")
    private String courseid;
    @Column(name = "GRADE")
    private Character grade;

    /**
     *
     */
    public Coursestaken() {
    }

    /**
     *
     * @param studentid
     * @param courseid
     */
    public Coursestaken(int studentid, String courseid) {
        this.studentid = studentid;
        this.courseid = courseid;
        this.id = this.hashCode();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getStudentid() {
        return studentid;
    }

    public void setStudentid(int studentid) {
        this.studentid = studentid;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public Character getGrade() {
        return grade;
    }

    public void setGrade(Character grade) {
        this.grade = grade;
    }

    /** add() - adds course taken item to COUSESTAKEN table on the HSQL database 
     *
     */
    public void add(){
        
        try (Connection con = DbConnection.getConnection()){
            
            if(con!=null){
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM PUBLIC.COUSESTAKEN WHERE STUDENTID="+
                        this.studentid+" AND COURSEID=\'"+this.courseid+"\'");

                if (rs.next()){
                    System.out.println("Course "+this.courseid+" for StudentId "+this.studentid+" Not Added - Already Exists");
                }else{

                    String sqlAddCourseTaken = "INSERT INTO PUBLIC.COUSESTAKEN " +
                            "(ID,STUDENTID,COURSEID,GRADE) VALUES" +
                            " ("+this.id+","+this.studentid+",\'"+this.courseid+"\',\'"+this.grade+"\')";

                    stmt.executeUpdate(sqlAddCourseTaken);
                    System.out.println("Course "+this.courseid+" for StudentId "+this.studentid+" Added");
                }
            }
        } catch (SQLException sql){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
                                "Database Connection Issue");
        }
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.studentid;
        hash = 67 * hash + Objects.hashCode(this.courseid);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coursestaken other = (Coursestaken) obj;
        if (this.studentid != other.studentid) {
            return false;
        }
        if (!Objects.equals(this.courseid, other.courseid)) {
            return false;
        }
        return true;
    }
   
    @Override
    public String toString() {
        return "edu.depaul.ipd.jdp.hw.Cousestaken[ Id="+id+
                " - StudentId=" + studentid +
                " - CourseId="+courseid+
                " - Grade="+grade+" ]";
    }
    
}
