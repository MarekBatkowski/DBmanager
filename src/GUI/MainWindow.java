package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import SQLhandling.*;
import org.apache.log4j.Logger;

public class MainWindow
{
    QueryHandler queryHandler = new QueryHandler();
    Selector selector = new Selector();

    static JFrame frame;
    private JButton mojePaczkiButton;
    private JButton wszystkiePaczkiButton;
    private JButton mojeKontoButton;
    private JButton dostawcyButton;
    private JButton wylogujButton;
    private JButton zamknijButton;
    private JPanel MainPanel;
    private JPanel Cards;

    private JPanel MyParcels;
    private JTable MyParcelsTable;
    private DefaultTableModel MyParcelsTableModel;

    private JPanel AllParcels;
    private JTable AllParcelsTable;
    private DefaultTableModel AllParcelsTableModel;

    private JPanel Couriers;
    private JTable CouriersTable;
    private DefaultTableModel CouriersTableModel;

    private JPanel MyAccount;
    private JLabel CurrName;
    private JLabel CurrSurname;
    private JLabel CurrEmail;
    private JTextField ChangeName;
    private JTextField ChangeSurname;
    private JTextField ChangeEmail;
    private JPasswordField ChangePassword;
    private JPasswordField ChangePasswordRepeat;
    private JPasswordField ConfirmPassword;
    private JButton zmieńDaneButton;
    private JCheckBox NameCheckBox;
    private JCheckBox SurnameCheckBox;
    private JCheckBox EmailCheckBox;
    private JCheckBox PasswordCheckBox;
    private JSeparator Separator;
    private JPasswordField ConfirmDelete;
    private JButton usuńKontoButton;

    void ChangeSetEnabled(boolean bool)
    {
        if(bool)
        {
            zmieńDaneButton.setEnabled(true);
            ConfirmPassword.setEnabled(true);
        }
        else
        {
            zmieńDaneButton.setEnabled(false);
            ConfirmPassword.setEnabled(false);
            ConfirmPassword.setText("");
        }
    }

    void changeActiveCard(int card)     //  0-3
    {
        mojePaczkiButton.setEnabled(0!=card);
        wszystkiePaczkiButton.setEnabled(1!=card);
        dostawcyButton.setEnabled(2!=card);
        mojeKontoButton.setEnabled(3!=card);

        mojePaczkiButton.setBackground( 0==card ? Color.WHITE : null );
        wszystkiePaczkiButton.setBackground( 1==card ? Color.WHITE : null );
        dostawcyButton.setBackground( 2==card ? Color.WHITE: null );
        mojeKontoButton.setBackground( 3==card ? Color.WHITE: null );

        if(card==0) frame.setTitle("DBmanager - moje paczki");
        if(card==1) frame.setTitle("DBmanager - wszystkie paczki");
        if(card==2) frame.setTitle("DBmanager - dostawcy");
        if(card==3) frame.setTitle("DBmanager - moje konto");
    }

    public MainWindow()
    {
        System.out.println("Creating MainForm");

        final Logger logger = Logger.getLogger(MainWindow.class);

        Cards.add(MyParcels, "MyParcels");
        Cards.add(AllParcels, "AllParcels");
        Cards.add(Couriers, "Couriers");
        Cards.add(MyAccount, "MyAccount");

        CardLayout cards = (CardLayout) (Cards.getLayout());

        // initial card
        cards.show(Cards, "MyParcels");
        mojePaczkiButton.setEnabled(false);
        mojePaczkiButton.setBackground(Color.WHITE);

        // changeActiveCard(0);

        MyParcelsTableModel.setNumRows(0);

        ArrayList<ArrayList<String>> MyParcelsList = selector.select("SELECT CONCAT(d.imie, ' ', d.nazwisko), " +
                "CONCAT(k.imie, ' ', k.nazwisko), p.oznaczenie, z.uwagi FROM dostawca d, zamowienie z, klient k, punkt_odbioru p " +
                "WHERE d.id_dostawcy = z.id_dostawcy and k.id_klienta = z.id_klienta and z.id_punktu = p.id_punktu and d.email = '"
                +CurrentUser.Values.get(2)+"';");

        for(ArrayList<String> iter : MyParcelsList)
            MyParcelsTableModel.addRow(new Vector<String>(iter));

        // Components

        mojePaczkiButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cards.show(Cards, "MyParcels");
                changeActiveCard(0);

                MyParcelsTableModel.setNumRows(0);

                ArrayList<ArrayList<String>> MyParcelsList = selector.select("SELECT CONCAT(d.imie, ' ', d.nazwisko), " +
                        "CONCAT(k.imie, ' ', k.nazwisko), p.oznaczenie, z.uwagi FROM dostawca d, zamowienie z, klient k, punkt_odbioru p " +
                        "WHERE d.id_dostawcy = z.id_dostawcy and k.id_klienta = z.id_klienta and z.id_punktu = p.id_punktu and d.email = '"
                        +CurrentUser.Values.get(2)+"';");

                for(ArrayList<String> iter : MyParcelsList)
                    MyParcelsTableModel.addRow(new Vector<String>(iter));
            }
        });

        wszystkiePaczkiButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cards.show(Cards, "AllParcels");
                changeActiveCard(1);

                AllParcelsTableModel.setNumRows(0);

                ArrayList<ArrayList<String>> AllParcelsList = selector.select("SELECT CONCAT(d.imie, ' ', d.nazwisko), " +
                        "CONCAT(k.imie, ' ', k.nazwisko), p.oznaczenie, z.uwagi FROM dostawca d, zamowienie z, klient k, punkt_odbioru p " +
                        "WHERE d.id_dostawcy = z.id_dostawcy and k.id_klienta = z.id_klienta and z.id_punktu = p.id_punktu;");

                for(ArrayList<String> iter : AllParcelsList)
                    AllParcelsTableModel.addRow(new Vector<String>(iter));
            }
        });

        dostawcyButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cards.show(Cards, "Couriers");
                changeActiveCard(2);

                CouriersTableModel.setNumRows(0);

                ArrayList<ArrayList<String>> CouriersList = selector.select("SELECT d.imie, d.nazwisko, d.email, COUNT(z.id_zamowienia) FROM dostawca d, zamowienie z " +
                        "WHERE z.id_dostawcy = d.id_dostawcy GROUP BY d.id_dostawcy;");

                for(ArrayList<String> iter : CouriersList)
                    CouriersTableModel.addRow(new Vector<String>(iter));
            }
        });

        mojeKontoButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cards.show(Cards, "MyAccount");
                changeActiveCard(3);

                CurrName.setText(CurrentUser.Values.get(0));
                CurrSurname.setText(CurrentUser.Values.get(1));
                CurrEmail.setText(CurrentUser.Values.get(2));
            }
        });

        wylogujButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JOptionPane.showConfirmDialog(frame, "Czy chcesz się wylogować?", "Potwierdź operację", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                {
                    logger.trace(CurrentUser.Values.get(2) + "logged out");
                    CurrentUser.Values = null;
                    new LoginForm().createWindow();
                    frame.dispose();
                }
            }
        });

        zamknijButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JOptionPane.showConfirmDialog(frame, "Czy chcesz wyjść?", "Potwierdź operację", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                {
                    logger.trace("Application closed with exit code 0");
                    System.exit(0);
                }
            }
        });

        //// My Parcels ////

        MyParcelsTable.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                int row = MyParcelsTable.rowAtPoint(evt.getPoint());
                int col = MyParcelsTable.columnAtPoint(evt.getPoint());

                ArrayList<ArrayList<String>> ParcelDetails = selector.select("SELECT z.id_zamowienia, CONCAT(d.imie, ' ', d.nazwisko), " +
                        "CONCAT(k.imie, ' ', k.nazwisko), p.oznaczenie, z.uwagi FROM dostawca d, zamowienie z, klient k, punkt_odbioru p WHERE" +
                        " d.id_dostawcy = z.id_dostawcy and k.id_klienta = z.id_klienta and z.id_punktu = p.id_punktu and d.email = '" +
                        CurrentUser.Values.get(2)+ "' LIMIT " + row +  ", 1;");

                ArrayList<ArrayList<String>> ProductListArray = selector.select("SELECT zp.ilosc, p.nazwa, p.cena, p.waga, p.wymiary" +
                        " FROM produkt p, zamowiony_produkt zp WHERE zp.id_produktu = p.id_produktu AND zp.id_zamowienia = " + ParcelDetails.get(0).get(0) + " ORDER BY zp.id_zamowienia;");

                ParcelDetails parcelDetails = new ParcelDetails(ParcelDetails, ProductListArray);
                parcelDetails.createWindow();
                parcelDetails.setPosition(row);

            //   JOptionPane.showMessageDialog(frame, "Kliknąłeś na "+ (row+1) + "pozycję", "Info", JOptionPane.PLAIN_MESSAGE);
            }
        });

        //// All Parcels ////

        AllParcelsTable.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                int row = AllParcelsTable.rowAtPoint(evt.getPoint());
                int col = AllParcelsTable.columnAtPoint(evt.getPoint());

                ArrayList<ArrayList<String>> ParcelDetails = selector.select("SELECT z.id_zamowienia, CONCAT(d.imie, ' ', d.nazwisko), " +
                        "CONCAT(k.imie, ' ', k.nazwisko), p.oznaczenie, z.uwagi FROM dostawca d, zamowienie z, klient k, punkt_odbioru p WHERE" +
                        " d.id_dostawcy = z.id_dostawcy and k.id_klienta = z.id_klienta and z.id_punktu = p.id_punktu LIMIT " + row +  ", 1;");

                ArrayList<ArrayList<String>> ProductListArray = selector.select("SELECT zp.ilosc, p.nazwa, p.cena, p.waga, p.wymiary" +
                        " FROM produkt p, zamowiony_produkt zp WHERE zp.id_produktu = p.id_produktu AND zp.id_zamowienia = " + (row+1) + " ORDER BY zp.id_zamowienia;");

                ParcelDetails parcelDetails = new ParcelDetails(ParcelDetails, ProductListArray);
                parcelDetails.createWindow();
                parcelDetails.setPosition(row);

            //    JOptionPane.showMessageDialog(frame, "Kliknąłeś na "+ (row+1) + " pozycję", "Info", JOptionPane.PLAIN_MESSAGE);
            }
        });

        //// All Users ////

        CouriersTable.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                int row = CouriersTable.rowAtPoint(evt.getPoint());
                int col = CouriersTable.columnAtPoint(evt.getPoint());

            //    JOptionPane.showMessageDialog(frame, "Kliknąłeś na "+ row + "pozycję", "Info", JOptionPane.PLAIN_MESSAGE);
            }
        });

        //// My Account ////

        NameCheckBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(NameCheckBox.isSelected())
                {
                    ChangeName.setEnabled(true);
                    ChangeSetEnabled(true);
                }
                else
                {
                    ChangeName.setEnabled(false);
                    ChangeName.setText("");
                    if (!NameCheckBox.isSelected() && !SurnameCheckBox.isSelected() && !EmailCheckBox.isSelected() && !PasswordCheckBox.isSelected())
                        ChangeSetEnabled(false);
                }
            }
        });

        SurnameCheckBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(SurnameCheckBox.isSelected())
                {
                    ChangeSurname.setEnabled(true);
                    ChangeSetEnabled(true);
                }
                else
                {
                    ChangeSurname.setEnabled(false);
                    ChangeSurname.setText("");
                    if (!NameCheckBox.isSelected() && !SurnameCheckBox.isSelected() && !EmailCheckBox.isSelected() && !PasswordCheckBox.isSelected())
                        ChangeSetEnabled(false);
                }
            }
        });

        EmailCheckBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(EmailCheckBox.isSelected())
                {
                    ChangeEmail.setEnabled(true);
                    ChangeSetEnabled(true);
                }
                else
                {
                    ChangeEmail.setEnabled(false);
                    ChangeEmail.setText("");
                    if (!NameCheckBox.isSelected() && !SurnameCheckBox.isSelected() && !EmailCheckBox.isSelected() && !PasswordCheckBox.isSelected())
                        ChangeSetEnabled(false);
                }
            }
        });

        PasswordCheckBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(PasswordCheckBox.isSelected())
                {
                    ChangePassword.setEnabled(true);
                    ChangePasswordRepeat.setEnabled(true);
                    ChangeSetEnabled(true);
                }
                else
                {
                    ChangePassword.setEnabled(false);
                    ChangePasswordRepeat.setEnabled(false);
                    ChangePassword.setText("");
                    ChangePasswordRepeat.setText("");
                    if(!NameCheckBox.isSelected() && !SurnameCheckBox.isSelected() && !EmailCheckBox.isSelected() && !PasswordCheckBox.isSelected())
                        ChangeSetEnabled(false);
                }
            }
        });

        zmieńDaneButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(String.valueOf(ConfirmPassword.getPassword()).equals(CurrentUser.Values.get(3)))    //  check password
                {
                    if(String.valueOf(ChangePassword.getPassword()).equals(String.valueOf(ChangePasswordRepeat.getPassword())))    // check new password
                    {
                        if(EmailCheckBox.isSelected() && ChangeEmail.getText().length()<10)
                        {
                            JOptionPane.showMessageDialog(frame, "Email jest za krótki!", "Błąd", JOptionPane.PLAIN_MESSAGE);
                            return;
                        }

                        if(PasswordCheckBox.isSelected() && String.valueOf(ChangePassword.getPassword()).length()<8)
                        {
                            JOptionPane.showMessageDialog(frame, "Nowe hasło jest za krótkie!", "Błąd", JOptionPane.PLAIN_MESSAGE);
                            return;
                        }

                        // data can be updated
                        if(NameCheckBox.isSelected())
                        {
                            queryHandler.insert("UPDATE Dostawca SET imie = '" + ChangeName.getText() + "' where email = '" + CurrentUser.Values.get(2) + "';");
                            CurrentUser.Values.set(0, ChangeName.getText());
                            logger.trace(CurrentUser.Values.get(2) + "changed name");
                        }

                        if(SurnameCheckBox.isSelected())
                        {
                            queryHandler.insert("UPDATE Dostawca SET nazwisko = '" + ChangeSurname.getText() + "' where email = '" + CurrentUser.Values.get(2) + "';");
                            CurrentUser.Values.set(1, ChangeSurname.getText());
                            logger.trace(CurrentUser.Values.get(2) + "changed surname");
                        }

                        if(EmailCheckBox.isSelected())
                        {
                            String oldMail = CurrentUser.Values.get(2);
                            queryHandler.insert("UPDATE Dostawca SET email = '" + ChangeEmail.getText() + "' where email = '" + CurrentUser.Values.get(2) + "';");
                            CurrentUser.Values.set(2, ChangeEmail.getText());
                            logger.trace(oldMail + "changed email to " + CurrentUser.Values.get(2));
                        }

                        if(PasswordCheckBox.isSelected())
                        {
                            queryHandler.insert("UPDATE Dostawca SET haslo = '" + String.valueOf(ChangePassword.getPassword()) + "' where email = '" + CurrentUser.Values.get(2) + "';");
                            CurrentUser.Values.set(3, String.valueOf(ChangePassword.getPassword()));
                            logger.trace(CurrentUser.Values.get(2) + "changed email");
                        }

                        JOptionPane.showMessageDialog(frame, "Dane zaktualizowane", "Operacja przebiegła pomyślnie", JOptionPane.PLAIN_MESSAGE);
                        ChangeName.setText("");
                        ChangeSurname.setText("");
                        ChangeEmail.setText("");
                        ChangePassword.setText("");
                        ChangePasswordRepeat.setText("");
                        ConfirmPassword.setText("");

                        CurrName.setText(CurrentUser.Values.get(0));
                        CurrSurname.setText(CurrentUser.Values.get(1));
                        CurrEmail.setText(CurrentUser.Values.get(2));
                    }
                    else JOptionPane.showMessageDialog(frame, "Hasła się nie zgadzają!", "Błąd", JOptionPane.PLAIN_MESSAGE);
                }
                else JOptionPane.showMessageDialog(frame, "Niepoprawne hasło!", "Błąd", JOptionPane.PLAIN_MESSAGE);

            }
        });

        usuńKontoButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(String.valueOf(ConfirmDelete.getPassword()).equals(CurrentUser.Values.get(3)))    //  check password
                {
                    if(JOptionPane.showConfirmDialog(frame, "Usunąć konto? Tej operacji NIE MOŻNA COFNĄĆ!", "Potwierdź operację", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
                    {
                        queryHandler.insert("DELETE FROM Dostawca where email='"+ CurrentUser.Values.get(2) +"';");
                        logger.trace(CurrentUser.Values.get(2) + " removed his account");
                        CurrentUser.Values = null;
                        new LoginForm().createWindow();
                        frame.dispose();

                        JOptionPane.showMessageDialog(frame, "Twoje konto zostało usunięte.", "Operacja przebiegła pomyślnie", JOptionPane.PLAIN_MESSAGE);
                    }
                    else ConfirmDelete.setText("");
                }
                else JOptionPane.showMessageDialog(frame, "Niepoprawne hasło!", "Błąd", JOptionPane.PLAIN_MESSAGE);
            }
        });
    }

    public void createWindow()
    {
        frame = new JFrame("DBmanager - moje paczki");
        frame.setContentPane(this.MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(720, 500);
        frame.setLocationRelativeTo(null);
    }

    private void createUIComponents()
    {
        CouriersTable = new JTable();
        String[] CouriersColumns = {"Imię", "Nazwisko", "Email", "Przesyłki"};
        CouriersTable.setModel(new DefaultTableModel(CouriersColumns, 0));
        CouriersTableModel = (DefaultTableModel) CouriersTable.getModel();
        CouriersTable.setDefaultEditor(Object.class, null);

        MyParcelsTable = new JTable();
        String[] MyParcelsColumns = {"Dostawca", "Klient", "Punkt", "Uwagi"};
        MyParcelsTable.setModel(new DefaultTableModel(MyParcelsColumns, 0));
        MyParcelsTableModel = (DefaultTableModel) MyParcelsTable.getModel();
        MyParcelsTable.setDefaultEditor(Object.class, null);

        AllParcelsTable = new JTable();
        String[] AllParcelsColumns = {"Dostawca", "Klient", "Punkt", "Uwagi"};
        AllParcelsTable.setModel(new DefaultTableModel(AllParcelsColumns, 0));
        AllParcelsTableModel = (DefaultTableModel) AllParcelsTable.getModel();
        AllParcelsTable.setDefaultEditor(Object.class, null);
    }
}