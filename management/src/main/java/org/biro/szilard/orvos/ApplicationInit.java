package org.biro.szilard.orvos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationInit implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void contextInitialized(ServletContextEvent event) {

        Properties appProperties = loadAppProperties();

        event.getServletContext().setAttribute("INIT_STATUS_SUCCESS", true);
        if (appProperties == null) {
            event.getServletContext().setAttribute("INIT_STATUS_SUCCESS", false);
        }
        else
        {
            event.getServletContext().setAttribute("APP_PROPERTIES", appProperties);
            String SQL_SELECT = "SELECT * FROM PATIENTS";
            try (Connection conn = DriverManager
                    .getConnection(
                    appProperties.getProperty("doctor.database.url"), appProperties.getProperty("doctor.database.user"), appProperties.getProperty("doctor.database.pass"));
                PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

                ResultSet resultSet = preparedStatement.executeQuery();

                System.out.println("Az alkalmazashoz szukseges adatbazis kapcsolatnal minden rendben van.");

            } catch (SQLException e) {
                event.getServletContext().setAttribute("INIT_STATUS_SUCCESS", false);
                System.err.println("Az alkalmazashoz szukseges adatbazis kapcsolatnal valami nincs rendbe.");
                System.err.println("Kerem ellenorizze, hogy az adatbazis letezik es valoban a megadott adatokkal elerheto-e:");
                System.err.format("*** SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            } catch (Exception e) {
                event.getServletContext().setAttribute("INIT_STATUS_SUCCESS", false);
                e.printStackTrace();
            }
        }
    }

    private Properties loadAppProperties() {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "application.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            String dburl = prop.getProperty("doctor.database.url");
            String dbuser = prop.getProperty("doctor.database.user");
            String dbpass = prop.getProperty("doctor.database.pass");
            System.out.format("Application configuration: %s : %s : %s", dburl, dbuser, dbpass);
            return prop;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
		}
    }
    
}