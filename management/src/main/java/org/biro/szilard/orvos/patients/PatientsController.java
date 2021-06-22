package org.biro.szilard.orvos.patients;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.biro.szilard.orvos.ApplicationException;
import org.biro.szilard.orvos.BaseController;
import org.biro.szilard.orvos.api.Patient;

@ManagedBean(name = "patients", eager = true)
@RequestScoped
public class PatientsController extends BaseController implements Serializable {

    private static final long serialVersionUID = 373484663922000559L;

    private Properties prop;

    public PatientsController() throws ApplicationException {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        prop = (Properties)servletContext.getAttribute("APP_PROPERTIES");
        this.setNewpatient(new Patient());
    }

    @ManagedProperty(value = "#{newpatient}")
    private Patient newpatient;

    private List<Patient> existpatients;

    public Patient getNewpatient() {
        if (this.newpatient == null)
        {
            this.newpatient = new Patient();
        }
        return this.newpatient;
    }

    public void setNewpatient(Patient patient) {
        if (patient == null)
        {
            this.newpatient = new Patient();
        }
        else
        {
            this.newpatient = patient;
        }
    }

    public List<Patient> getExistpatients() {
        return loadPatients();
    }

    public void setExistpatients(List<Patient> existpatients) {
        this.existpatients = existpatients;
    }

    public List<Patient> loadPatients() {
        List<Patient> patients = new ArrayList<Patient>();
        this.setExistpatients(patients);
        String SQL_SELECT = "SELECT * FROM PATIENTS";
        try (Connection conn = DriverManager
                .getConnection(
                    prop.getProperty("doctor.database.url"), prop.getProperty("doctor.database.user"), prop.getProperty("doctor.database.pass"));
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Patient found = new Patient(resultSet.getInt("ID"), resultSet.getString("NAME"), resultSet.getDate("BIRTHDATE"), resultSet.getString("SOCIALINSURANCECODE"), resultSet.getString("GENDER"));
                patients.add(found);
            }
            this.setExistpatients(patients);
        } catch (SQLException e) {
            System.err.format("*** SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            e.printStackTrace();
            return new ArrayList<Patient>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Patient>();
        }
        return patients;
    }

    public void addnewpatient() {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(this.newpatient.getBirthDate());
        String SQL_INSERT = "INSERT INTO PATIENTS (`name`,`socialinsurancecode`,`birthdate`,`gender`)" +
        " VALUES ('"+this.newpatient.getName()+"','"+this.newpatient.getSocialInsuranceCode().replace(" ", "")+"','"+date+"','"+this.newpatient.getGender()+"')";

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
                this.setNewpatient(new Patient());
            }      
            else
            {
                return;
            }
        } catch (SQLException e) {
            System.err.format("*** SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}