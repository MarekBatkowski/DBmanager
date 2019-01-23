package SQLhandling;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class DBConnection
{
    static final Logger logger = LogManager.getLogger(DBConnection.class);

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
    }

    private DBConnection() throws SQLException
    {
        Properties properties=new Properties();

        try // try to read from file
        {
            properties.load(new FileInputStream("dbconnection.properties"));
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
                properties.setProperty("DBURL", "jdbc:mysql://127.0.0.1:55555/Paczkomat?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift" +
                        "=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
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

        logger.trace("Connection parameters loaded properly");
    //    System.out.println("Connected to database");
    }
}