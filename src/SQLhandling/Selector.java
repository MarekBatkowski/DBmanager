package SQLhandling;

import java.sql.*;
import java.util.ArrayList;

public class Selector
{
    public ArrayList<ArrayList<String>> select(String query)
    {
        ArrayList<ArrayList<String>> returned = new ArrayList<>();

        try
        {
            Connection connection = DBConnection.GetInstance().getConnection();
            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery(query);

            ResultSetMetaData metadata = rs.getMetaData();
            int numcols = metadata.getColumnCount();

            while (rs.next())
            {
                ArrayList<String> row = new ArrayList<>(numcols);
                int i = 1;
                while (i <= numcols)
                {
                    row.add(rs.getString(i++));
                }
                returned.add(row);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            return returned;
        }
    }
}
