package GUI;

import SQLhandling.QueryHandler;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;

public class ParcelDetails
{
    QueryHandler queryHandler = new QueryHandler();
    private int position;

    static JFrame frame;
    private JTable ProductList;
    private DefaultTableModel ProductListModel;
    private JLabel IDLabel;
    private JLabel CourierLabel;
    private JLabel ClientLabel;
    private JLabel PointLabel;
    private JLabel CommentsLabel;
    private JPanel MainPanel;
    private JButton ResignButton;
    private JButton TakeOverButton;

    final Logger logger = Logger.getLogger(MainWindow.class);

    public void setPosition(int position)
    {
        this.position = position;
    }

    public ParcelDetails(ArrayList<ArrayList<String>> ParcelDetails, ArrayList<ArrayList<String>> ProductListArray, boolean ResignEnabled, boolean TakeOverEnabled)
    {
        ProductListModel.setNumRows(0);

        IDLabel.setText(ParcelDetails.get(0).get(0));
        CourierLabel.setText(ParcelDetails.get(0).get(1));
        ClientLabel.setText(ParcelDetails.get(0).get(2));
        PointLabel.setText(ParcelDetails.get(0).get(3));
        CommentsLabel.setText(ParcelDetails.get(0).get(4));

        String Parcel_Id = ParcelDetails.get(0).get(0);

        for(ArrayList<String> iter : ProductListArray)
            ProductListModel.addRow(new Vector<String>(iter));

        if(ResignEnabled)   ResignButton.setEnabled(true);
        if(TakeOverEnabled) TakeOverButton.setEnabled(true);

        ProductList.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {

            }
        });

        TakeOverButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                queryHandler.execute("UPDATE Zamowienie SET id_dostawcy = '" + CurrentUser.Values.get(0) + "' WHERE id_zamowienia = '" + Parcel_Id + "';");
                JOptionPane.showMessageDialog(frame, "Przesyłka została przypisana do Twojego konta.", "Operacja powiodła się", JOptionPane.PLAIN_MESSAGE);
                logger.trace(CurrentUser.Values.get(3) + " linked parcel of ID: " + Parcel_Id + "to account");
                frame.dispose();
            }
        });
        ResignButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                queryHandler.execute("UPDATE Zamowienie SET id_dostawcy = NULL WHERE id_zamowienia = '" + Parcel_Id + "';");
                JOptionPane.showMessageDialog(frame, "Przypisanie paczki do Twojego konta zostało usunięte.", "Operacja powiodła się", JOptionPane.PLAIN_MESSAGE);
                logger.trace(CurrentUser.Values.get(3) + " detatched parcel of ID: " + Parcel_Id + "from account");
                frame.dispose();
            }
        });
    }

    public void createWindow()
    {
        frame = new JFrame("Szczegóły przesyłki");
        frame.setContentPane(this.MainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
    }

    private void createUIComponents()
    {
        ProductList = new JTable();
        String[] CouriersColumns = {"Ilość", "Nazwa", "Cena", "Waga", "Wymiary"};
        ProductList.setModel(new DefaultTableModel(CouriersColumns, 0));
        ProductListModel = (DefaultTableModel) ProductList.getModel();
        ProductList.setDefaultEditor(Object.class, null);
    }
}
