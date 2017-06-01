/*
 * Coursestaken Class
 * Author-Will Frampton
 * 
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

/**
 * Coursestaken Class
 * Author-Will Frampton
 * @author wfram
 */

public class Coursestaken implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private int studentid;
    private String courseid;
    private Character grade;

    /** Coursestaken Constructor
     *
     * @param studentid
     * @param courseid
     * @param grade
     */
    public Coursestaken(int studentid, String courseid,Character grade) {
        this.studentid = studentid;
        this.courseid = courseid;
        this.grade = grade;
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
    public void add() throws RuntimeException{
        
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
            throw new RuntimeException(sql);
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
