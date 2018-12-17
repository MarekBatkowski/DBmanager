package SQLhandling;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class DBConnection
{
    static final Logger logger = Logger.getLogger(DBConnection.class);

    private Connection connection;

    public Connection getConnection()
    {
        return connection;
    }

    private static DBConnection instance;

    public static DBConnection GetInstance() throws SQLException
    {
        if(instance == null || instance.getConnection().isClosed())
            instance = new DBConnection();
        return instance;

        /*
        try
        {
            if(instance == null || instance.getConnection().isClosed())
                instance = new DBConnection();
        }
        catch (SQLException e)
        {
            logger.error("Couldn't get connection to database");
            e.printStackTrace();
        }
        return instance;
        */
    }

    private DBConnection() throws SQLException
    {
        Properties properties=new Properties();

        try // try to read from file
        {
            properties.load(new FileInputStream("dbconnection.properties"));
            logger.trace("Connection parameters loaded properly");
        }
        catch (IOException e)   // no file exists
        {
            // log error
            System.out.println("No configuration file found! Loading default configuration.");
            logger.warn("No configuration file found. Loading default configuration");

            File f = new File("dbconnection.properties");   // create new file and load default configuration
            try
            {
                FileOutputStream output = new FileOutputStream(f);
                properties.setProperty("userName", "root");
                properties.setProperty("password", "");
                properties.setProperty("DBURL", "jdbc:mysql://127.0.0.1:55555/Paczkomat?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
                properties.store(output, "Database connection paramaters");
            }
            catch (IOException el)
            {
                el.printStackTrace();
            }
        }

        String DBURL    = (String) properties.get("DBURL");
        String userName = (String) properties.get("userName");
        String password = (String) properties.get("password");

        this.connection = DriverManager.getConnection(DBURL, userName, password);

        System.out.println("Connected to database");
        logger.trace("Connected to database");
    }
}