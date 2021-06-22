package org.biro.szilard.orvos.screeningtest;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.biro.szilard.orvos.ApplicationException;
import org.biro.szilard.orvos.BaseController;
import org.biro.szilard.orvos.api.Patient;
import org.biro.szilard.orvos.api.ScreeningTest;

@ManagedBean(name = "screeningtest", eager = true)
@RequestScoped
public class ScreeningTestController extends BaseController implements Serializable {

    public ScreeningTestController() throws ApplicationException {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        prop = (Properties)servletContext.getAttribute("APP_PROPERTIES");
        this.setNewtest(new ScreeningTest());
        this.getExisttests();
    }

    private static final long serialVersionUID = 1L;

    private Properties prop;

    @ManagedProperty(value = "#{newtest}")
    private ScreeningTest newtest;

    private Map<String,String> existtests = new HashMap<String, String>();

    private List<Patient> existpatients;

    

    public ScreeningTest getNewtest() {
        return newtest;
    }

    public void setNewtest(ScreeningTest newtest) {
        this.newtest = newtest;
    }

    public Map<String, String> getExisttests() {
        return loadExistTestNames();
    }

    public void setExisttests(Map<String, String> existtests) {
        this.existtests = existtests;
    }
    
    public Map<String, String> loadExistTestNames() {
       Map<String, String> testNames = new HashMap<String, String>();
        String SQL_SELECT = "SELECT DISTINCT SCREENINGTEST FROM SCREENINGTESTS";
        try (Connection conn = DriverManager
                .getConnection(
                    prop.getProperty("doctor.database.url"), prop.getProperty("doctor.database.user"), prop.getProperty("doctor.database.pass"));
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                testNames.put(resultSet.getString("SCREENINGTEST"),resultSet.getString("SCREENINGTEST"));
            }
            System.out.println("Testnames: "+ testNames.size());
            this.setExisttests(testNames);
        } catch (SQLException e) {
            System.err.format("*****************SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            e.printStackTrace();
            return new HashMap<String, String>();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<String, String>();
        }
        return testNames;
    }
 
    public void search() {
        if (this.newtest.getName() == null || this.newtest.getName().trim().equalsIgnoreCase(""))
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba!", "Szűrővizsgálat neve nem lett megadva!"));
            return;
        }
        this.setExistpatients(null);
        String genderStatement = "";
        if (this.newtest.getGender().equalsIgnoreCase("A"))
        {
            genderStatement = "'M','F'";
        }
        else
        {
            genderStatement = "'"+this.newtest.getGender().toUpperCase()+"'";
        }

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String minAge = simpleDateFormat.format(new Date());
        if (this.newtest.getMin() != 0)
        {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, this.newtest.getMin() * -1);
            minAge = simpleDateFormat.format(cal.getTime());
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1800);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date dateRepresentation = cal.getTime();
        String maxAge = simpleDateFormat.format(cal.getTime());
        if (this.newtest.getMax() != 0)
        {
            cal = new GregorianCalendar();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, this.newtest.getMax() * -1);
            maxAge = simpleDateFormat.format(cal.getTime());
        }

        String SQL_SELECT_POSSIBLE_PATIENTS = "SELECT * FROM PATIENTS WHERE `gender` IN ("+genderStatement+") AND (`birthdate` BETWEEN DATE('"+maxAge+"') AND DATE('"+minAge+"'))";
        System.out.println("SQL : " + SQL_SELECT_POSSIBLE_PATIENTS);
        List<Patient> possiblePatients = new ArrayList<Patient>();
        List<String> incusranceCodes = new ArrayList<String>();
        try (Connection conn = DriverManager
                .getConnection(
                    prop.getProperty("doctor.database.url"), prop.getProperty("doctor.database.user"), prop.getProperty("doctor.database.pass"));
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT_POSSIBLE_PATIENTS)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Patient found = new Patient(resultSet.getInt("ID"), resultSet.getString("NAME"), resultSet.getDate("BIRTHDATE"), resultSet.getString("SOCIALINSURANCECODE"), resultSet.getString("GENDER"));
                possiblePatients.add(found);
                incusranceCodes.add(resultSet.getString("SOCIALINSURANCECODE"));
            }
        } catch (SQLException e) {
            System.err.format("*** SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba!", "Nem végrehajtható keresés!"));
            return;
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba!", "Nem végrehajtható keresés!"));
            return;
        }

        if (incusranceCodes.size() == 0)
        {
            System.out.println("No possible");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Fél siker!", "Nincs a keresésnek megfelelő páciens!"));
            return;
        }
        String insucranceCodeStatement = "";
        if (incusranceCodes.size() == 1)
        {
            insucranceCodeStatement = "'"+incusranceCodes.get(0)+"'";
        }
        else
        {
            int index = 0;
            for (String code : incusranceCodes)
            {
                insucranceCodeStatement = insucranceCodeStatement + "'"+code+"'";
                if (index != incusranceCodes.size()-1)
                {
                    insucranceCodeStatement = insucranceCodeStatement + ",";
                }
                index++;
            }
        }

        String lastPeriod = simpleDateFormat.format(new Date());
        if (this.newtest.getPeriod() != 0)
        {
            cal = new GregorianCalendar();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, this.newtest.getPeriod() * -1);
            lastPeriod = simpleDateFormat.format(cal.getTime());
        }

        String SQL_SELECT_BY_INSURANCE_PATIENT_HAVE_ACTIVE_SCREENINGTEST = "SELECT `socialinsurancecode` FROM SCREENINGTESTS WHERE `screeningtest` = '"+this.newtest.getName()+"' AND `date` >= DATE('"+lastPeriod+"') AND `socialinsurancecode` IN ("+insucranceCodeStatement+")";
        System.out.println("SQL : " + SQL_SELECT_BY_INSURANCE_PATIENT_HAVE_ACTIVE_SCREENINGTEST);
        try (Connection conn = DriverManager
                .getConnection(
                    prop.getProperty("doctor.database.url"), prop.getProperty("doctor.database.user"), prop.getProperty("doctor.database.pass"));
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT_BY_INSURANCE_PATIENT_HAVE_ACTIVE_SCREENINGTEST)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                incusranceCodes.remove(resultSet.getString("SOCIALINSURANCECODE"));
            }
        } catch (SQLException e) {
            System.err.format("*** SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba!", "Nem végrehajtható keresés!"));

            return;
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba!", "Nem végrehajtható keresés!"));

            return;
        }

        List<Patient> needTest = new ArrayList<Patient>();
        for (Patient patient : possiblePatients)
        {
            if (incusranceCodes.contains(patient.getSocialInsuranceCode()))
            {
                needTest.add(patient);
            }
        }
        if (needTest.size() == 0)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Fél siker!", "Nincs olyan páciens akinek ilyen teszten ezekkel a beállításokkal részt kellene vennie!"));

        }
        System.out.println("Possible: " + possiblePatients.size());
        System.out.println("Need Test: " + needTest.size());
        this.setExistpatients(needTest);
        this.setNewtest(new ScreeningTest());
    }

    public List<Patient> getExistpatients() {
        return existpatients;
    }

    public void setExistpatients(List<Patient> existpatients) {
        if (existpatients == null)
        {
            this.existpatients = new ArrayList<Patient>();
        }
        else
        {
            this.existpatients = existpatients;
        }
    }
}