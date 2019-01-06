package SQLhandling;

import java.sql.*;

public class QueryHandler
{
    public void execute(String query)
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
            System.out.println("Incorrect query!");
            e.printStackTrace();
        }
    }
}

