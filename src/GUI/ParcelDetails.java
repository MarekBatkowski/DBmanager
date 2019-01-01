package GUI;

import SQLhandling.Selector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Vector;

public class ParcelDetails
{
    private Selector selector = new Selector();
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

    public void setPosition(int position)
    {
        this.position = position;
    }

    public ParcelDetails(ArrayList<ArrayList<String>> ParcelDetails, ArrayList<ArrayList<String>> ProductListArray)
    {
        ProductListModel.setNumRows(0);

        IDLabel.setText(ParcelDetails.get(0).get(0));
        CourierLabel.setText(ParcelDetails.get(0).get(1));
        ClientLabel.setText(ParcelDetails.get(0).get(2));
        PointLabel.setText(ParcelDetails.get(0).get(3));
        CommentsLabel.setText(ParcelDetails.get(0).get(4));

        for(ArrayList<String> iter : ProductListArray)
            ProductListModel.addRow(new Vector<String>(iter));
    }

    public void createWindow()
    {
        frame = new JFrame("Szczegóły przesyłki");
        frame.setContentPane(this.MainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(500, 400);
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
