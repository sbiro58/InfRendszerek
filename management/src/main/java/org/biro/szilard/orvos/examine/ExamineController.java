package org.biro.szilard.orvos.examine;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.biro.szilard.orvos.ApplicationException;
import org.biro.szilard.orvos.BaseController;
import org.biro.szilard.orvos.api.Examine;
import org.biro.szilard.orvos.api.Patient;

@ManagedBean(name = "examine", eager = true)
@RequestScoped
public class ExamineController extends BaseController implements Serializable {

    private Properties prop;
    @ManagedProperty(value = "#{tajCode}")
    private String tajCode;

    private List<Examine> patientExamines;

    public ExamineController() throws ApplicationException {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        prop = (Properties)servletContext.getAttribute("APP_PROPERTIES");
        this.setPatient(null);
    }

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{patient}")
    private Patient patient;

    @ManagedProperty(value = "#{newdiagnose}")
    private String newdiagnose;
    @ManagedProperty(value = "#{newmedicines}")
    private String newmedicines;
    @ManagedProperty(value = "#{newtreatments}")
    private String newtreatments;
    @ManagedProperty(value = "#{newinsurancecode}")
    private String newinsurancecode;

    public void addnewexamine() {
        System.out.format("*****UJ vizit: %s %s %s %s", this.newdiagnose, this.newmedicines, this.newtreatments, this.newinsurancecode);
        if (this.newinsurancecode == null || "".equalsIgnoreCase(this.newinsurancecode.trim()))
        {
            return;
        }
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());
        String SQL_INSERT = "INSERT INTO EXAMINE (`socialinsurancecode`, `diagnose`, `medicines`, `treatments`, `date`)" +
        " VALUES ('"+this.newinsurancecode+"','"+this.getNewdiagnose()+"','"+this.getNewmedicines()+"','"+this.getNewtreatments()+"','"+date+"')";

        System.out.println("SQL INSERT: ");
        System.out.println(SQL_INSERT);

        try (Connection conn = DriverManager
                .getConnection(
                    prop.getProperty("doctor.database.url"), prop.getProperty("doctor.database.user"), prop.getProperty("doctor.database.pass"));
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT)) {

            int rowCount = preparedStatement.executeUpdate();

            System.out.println("Database has been updated with row: " + rowCount);

            if (rowCount == 1)
            {
                this.setNewdiagnose("");
                this.setNewtreatments("");
                this.setNewmedicines("");
            }      
            else
            {
                return;
            }
        } catch (SQLException e) {
            System.err.format("*****************SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String SQL_SELECT_EXAMINES = "SELECT * FROM EXAMINE WHERE SOCIALINSURANCECODE = '"+ this.newinsurancecode +"'";
        try (Connection conn = DriverManager
                .getConnection(
                    prop.getProperty("doctor.database.url"), prop.getProperty("doctor.database.user"), prop.getProperty("doctor.database.pass"));
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT_EXAMINES)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Examine> foundExamine = new ArrayList<Examine>();
            while (resultSet.next()) {
                Examine found = new Examine(resultSet.getInt("ID"), resultSet.getString("SOCIALINSURANCECODE"), resultSet.getString("DIAGNOSE"),resultSet.getString("MEDICINES"),resultSet.getString("TREATMENTS"), resultSet.getDate("DATE"));
                foundExamine.add(found);
            }
            this.setPatientExamines(foundExamine);

        } catch (SQLException e) {
            System.err.format("*** SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void search() {
        this.setPatient(null);
        this.setPatientExamines(null);
        this.setNewinsurancecode(null);
        System.out.println("*** Tajcode received: " + this.tajCode);

        String SQL_SELECT = "SELECT * FROM PATIENTS WHERE SOCIALINSURANCECODE = '"+ this.tajCode +"'";
        String SQL_SELECT_EXAMINES = "SELECT * FROM EXAMINE WHERE SOCIALINSURANCECODE = '"+ this.tajCode +"'";
        try (Connection conn = DriverManager
                .getConnection(
                    prop.getProperty("doctor.database.url"), prop.getProperty("doctor.database.user"), prop.getProperty("doctor.database.pass"));
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Patient found = new Patient(resultSet.getInt("ID"), resultSet.getString("NAME"), resultSet.getDate("BIRTHDATE"), resultSet.getString("SOCIALINSURANCECODE"), resultSet.getString("GENDER"));
                this.setPatient(found);
            }
            this.setNewinsurancecode(this.tajCode);
        } catch (SQLException e) {
            System.err.format("*** SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try (Connection conn = DriverManager
                .getConnection(
                    prop.getProperty("doctor.database.url"), prop.getProperty("doctor.database.user"), prop.getProperty("doctor.database.pass"));
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT_EXAMINES)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Examine> foundExamine = new ArrayList<Examine>();
            while (resultSet.next()) {
                Examine found = new Examine(resultSet.getInt("ID"), resultSet.getString("SOCIALINSURANCECODE"), resultSet.getString("DIAGNOSE"),resultSet.getString("MEDICINES"),resultSet.getString("TREATMENTS"), resultSet.getDate("DATE"));
                foundExamine.add(found);
            }
            this.setPatientExamines(foundExamine);

        } catch (SQLException e) {
            System.err.format("*** SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setTajCode("");
    }

    public void setPatientExamines(List<Examine> examines) {
        this.patientExamines = new ArrayList<Examine>();
        if (examines != null && examines.size() > 0)
        {
            this.patientExamines.addAll(examines);
        }
    }

    public List<Examine> getPatientExamines() {
        if (this.patientExamines == null)
        {
            this.patientExamines = new ArrayList<Examine>();
        }
        return this.patientExamines;
    }

    public void setTajCode(String value) {
        this.tajCode = value;
    }

    public String getTajCode() {
        return this.tajCode;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Patient getPatient() {
        if (this.patient == null || this.patient.getId() == 0)
        {
            return null;
        }
        return this.patient;
    }

    public String getNewdiagnose() {
        if (this.newdiagnose == null)
        {
            return "";
        }
        return this.newdiagnose;
    }

    public void setNewdiagnose(String newdiagnose) {
        this.newdiagnose = newdiagnose;
    }

    public String getNewmedicines() {
        if (this.newmedicines == null)
        {
            return "";
        }
        return this.newmedicines;
    }

    public void setNewmedicines(String newmedicines) {
        this.newmedicines = newmedicines;
    }

    public String getNewtreatments() {
        if (this.newtreatments == null)
        {
            return "";
        }
        return this.newtreatments;
    }

    public void setNewtreatments(String newtreatments) {
        this.newtreatments = newtreatments;
    }

    public String getNewinsurancecode() {
        return this.newinsurancecode;
    }

    public void setNewinsurancecode(String newinsurancecode) {
        this.newinsurancecode = newinsurancecode;
    }

}