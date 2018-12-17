package SQLhandling;

import java.sql.*;

public class QueryHandler
{
    public void insert(String query)
    {
        Statement stmt = null;
        try
        {
            Connection connection = DBConnection.GetInstance().getConnection();
            stmt = connection.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
        }
        catch (SQLException e)
        {
            System.out.println("Incorrect SQLhandling query!");
            e.printStackTrace();
        }
    }
}

