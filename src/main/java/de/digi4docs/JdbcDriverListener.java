package de.digi4docs;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ServiceLoader;

public class JdbcDriverListener implements ServletContextListener {

    /**
     * Deregisters the JDBC drivers distributed with the application.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        final ClassLoader cl = event.getServletContext()
                                    .getClassLoader();
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            final Driver driver = drivers.nextElement();
            // We deregister only the classes loaded by this application's classloader
            if (driver.getClass()
                      .getClassLoader() == cl) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    event.getServletContext()
                         .log("JDBC Driver deregistration failure.", e);
                }
            }
        }
    }

    /**
     * Registers the JDBC drivers distributed with the application.
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        Iterator<Driver> driversIterator = ServiceLoader.load(Driver.class)
                                                        .iterator();
        while (driversIterator.hasNext()) {
            try {
                // Instantiates the driver
                driversIterator.next();
            } catch (Throwable t) {
                event.getServletContext()
                     .log("JDBC Driver registration failure.", t);
            }
        }
    }
}