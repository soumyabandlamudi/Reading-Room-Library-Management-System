package com.library.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Loads db.properties once and hands out JDBC connections. */
public final class DB {
    private static final Properties P = new Properties();
    private static volatile boolean driverLoaded = false;

    static {
        try (InputStream in = DB.class.getClassLoader().getResourceAsStream("db.properties")) {
            P.load(in);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DB() {}

    public static Connection get() throws SQLException {
        if (!driverLoaded) {
            String driver = P.getProperty("db.driver");
            try {
                Class.forName(driver);
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new SQLException("JDBC driver " + driver + " not found. Copy the ojdbc jar into "
                        + "src/main/webapp/WEB-INF/lib, then clean and restart the server.", e);
            }
        }
        return DriverManager.getConnection(
                P.getProperty("db.url"), P.getProperty("db.user"), P.getProperty("db.password"));
    }

    public static int loanDays()   { return Integer.parseInt(P.getProperty("loan.days", "14")); }
    public static int finePerDay() { return Integer.parseInt(P.getProperty("fine.per.day", "5")); }
    public static int maxLoans()   { return Integer.parseInt(P.getProperty("max.loans", "5")); }
    /** true = error page shows the technical cause (handy while developing). */
    public static boolean showErrors() { return Boolean.parseBoolean(P.getProperty("show.errors", "false")); }
}
