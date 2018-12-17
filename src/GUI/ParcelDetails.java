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
    private JLabel ClientLabel;
    private JLabel PointLabel;
    private JLabel Comments;
    private JLabel Courier;
    private JPanel MainPanel;

    public void setPosition(int position)
    {
        this.position = position;
    }

    public void createWindow()
    {
        frame = new JFrame("Szczegóły przesyłki");
        frame.setContentPane(new ParcelDetails().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(720, 500);
        frame.setLocationRelativeTo(null);

        ProductListModel.setNumRows(0);

        ArrayList<ArrayList<String>> ProductList = selector.select("SELECT zp.ilosc, p.nazwa, p.cena, p.waga, p.wymiary FROM zamowienie z," +
                " zamowiony_produkt zp, produkt p WHERE zp.id_produktu = p.id_produktu AND z.id_zamowienia = zp.id_zamowienia AND z.id_zamowienia = " + position + ";");

        for(ArrayList<String> iter : ProductList)
        {
            ProductListModel.addRow(new Vector<String>(iter));
        }
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
