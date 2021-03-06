package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Pattern;

import SQLhandling.*;
//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private JButton zmienDaneButton;
    private JCheckBox NameCheckBox;
    private JCheckBox SurnameCheckBox;
    private JCheckBox EmailCheckBox;
    private JCheckBox PasswordCheckBox;
    private JSeparator Separator;
    private JPasswordField ConfirmDelete;
    private JButton usunKontoButton;
    private JTextField MyParcelsFilter;
    private JTextField AllParcelsFilter;
    private JTextField CouriersFilter;

    private static final Pattern emailRegEx = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
    private static final Pattern passwordRegEx = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");
    private static final Pattern nameRegEx = Pattern.compile("^[A-Z]+[a-z]{2,}");
    private static final Object[] confirmOptions = {"     Tak     ","     Nie     "};

    private MessageDigest digest = null;
    private final Logger logger = LogManager.getLogger(MainWindow.class);
    private MainWindow mainInstance = this;

    void ChangeSetEnabled(boolean bool)
    {
        if(bool)
        {
            zmienDaneButton.setEnabled(true);
            ConfirmPassword.setEnabled(true);
        }
        else
        {
            zmienDaneButton.setEnabled(false);
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

    void UpdateMyParcels()
    {
        MyParcelsTableModel.setNumRows(0);

        ArrayList<ArrayList<String>> MyParcelsList = selector.select("SELECT z.id_zamowienia, CONCAT(d.imie, ' ', d.nazwisko), " +
                "CONCAT(k.imie, ' ', k.nazwisko), p.oznaczenie, z.uwagi FROM dostawca d, zamowienie z, klient k, punkt_odbioru p " +
                "WHERE d.id_dostawcy = z.id_dostawcy and k.id_klienta = z.id_klienta and z.id_punktu = p.id_punktu and d.email = '"
                +CurrentUser.Values.get(3)+"';");

        for(ArrayList<String> iter : MyParcelsList)
            MyParcelsTableModel.addRow(new Vector<String>(iter));
    }

    void UpdateAllParcels()
    {
        AllParcelsTableModel.setNumRows(0);

        ArrayList<ArrayList<String>> AllParcelsList = selector.select("SELECT z.id_zamowienia, CONCAT(d.imie, ' ', d.nazwisko), " +
                "CONCAT(k.imie, ' ', k.nazwisko), p.oznaczenie, z.uwagi FROM zamowienie z LEFT JOIN dostawca d ON z.id_dostawcy = d.id_dostawcy, " +
                "klient k, punkt_odbioru p WHERE z.id_klienta = k.id_klienta and z.id_punktu = p.id_punktu ORDER BY z.id_zamowienia;");

        for(ArrayList<String> iter : AllParcelsList)
            AllParcelsTableModel.addRow(new Vector<String>(iter));
    }

    void UpdateAllCouriers()
    {
        CouriersTableModel.setNumRows(0);

        ArrayList<ArrayList<String>> CouriersList = selector.select("SELECT d.imie, d.nazwisko, d.email, COUNT(z.id_zamowienia) FROM" +
                " dostawca d LEFT JOIN zamowienie z ON d.id_dostawcy = z.id_dostawcy GROUP BY d.id_dostawcy;");

        for(ArrayList<String> iter : CouriersList)
            CouriersTableModel.addRow(new Vector<String>(iter));
    }

    public MainWindow()
    {
        try
        {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

    //    System.out.println("Creating MainForm");

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
        UpdateMyParcels();

        // Components

        mojePaczkiButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cards.show(Cards, "MyParcels");
                changeActiveCard(0);
                UpdateMyParcels();
            }
        });

        wszystkiePaczkiButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cards.show(Cards, "AllParcels");
                changeActiveCard(1);
                UpdateAllParcels();
            }
        });

        dostawcyButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cards.show(Cards, "Couriers");
                changeActiveCard(2);
                UpdateAllCouriers();
            }
        });

        mojeKontoButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cards.show(Cards, "MyAccount");
                changeActiveCard(3);

                CurrName.setText(CurrentUser.Values.get(1));
                CurrSurname.setText(CurrentUser.Values.get(2));
                CurrEmail.setText(CurrentUser.Values.get(3));
            }
        });

        wylogujButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(JOptionPane.showOptionDialog(frame, "Czy chcesz się wylogować?", "Potwierdź operację",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, confirmOptions, confirmOptions[1])==JOptionPane.YES_OPTION)
                {
                    logger.trace(CurrentUser.Values.get(3) + "logged out");
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
                if(JOptionPane.showOptionDialog(frame, "Czy chcesz wyjść?", "Potwierdź operację",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, confirmOptions, confirmOptions[1])==JOptionPane.YES_OPTION)
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
                int row = MyParcelsTable.convertRowIndexToModel(MyParcelsTable.rowAtPoint(evt.getPoint()));

                ArrayList<ArrayList<String>> ParcelDetails = selector.select("SELECT z.id_zamowienia, CONCAT(d.imie, ' ', d.nazwisko), " +
                        "CONCAT(k.imie, ' ', k.nazwisko), p.oznaczenie, z.uwagi FROM dostawca d, zamowienie z, klient k, punkt_odbioru p WHERE" +
                        " d.id_dostawcy = z.id_dostawcy and k.id_klienta = z.id_klienta and z.id_punktu = p.id_punktu and d.email = '" +
                        CurrentUser.Values.get(3)+ "' LIMIT " + row +  ", 1;");

                ArrayList<ArrayList<String>> ProductListArray = selector.select("SELECT zp.ilosc, p.nazwa, p.cena, p.waga, p.wymiary" +
                        " FROM produkt p, zamowiony_produkt zp WHERE zp.id_produktu = p.id_produktu AND zp.id_zamowienia = " + ParcelDetails.get(0).get(0) + " ORDER BY zp.id_zamowienia;");

                ParcelDetails parcelDetails = new ParcelDetails(ParcelDetails, ProductListArray, true, false, frame, mainInstance);
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
                int row = AllParcelsTable.convertRowIndexToModel(AllParcelsTable.rowAtPoint(evt.getPoint()));

                ArrayList<ArrayList<String>> ParcelDetails = selector.select("SELECT z.id_zamowienia, CONCAT(d.imie, ' ', d.nazwisko), " +
                        "CONCAT(k.imie, ' ', k.nazwisko), p.oznaczenie, z.uwagi FROM zamowienie z LEFT JOIN dostawca d ON z.id_dostawcy = d.id_dostawcy, " +
                        "klient k, punkt_odbioru p WHERE z.id_klienta = k.id_klienta and z.id_punktu = p.id_punktu ORDER BY z.id_zamowienia LIMIT " + row + ", 1;");

                ArrayList<ArrayList<String>> ProductListArray = selector.select("SELECT zp.ilosc, p.nazwa, p.cena, p.waga, p.wymiary" +
                        " FROM produkt p, zamowiony_produkt zp WHERE zp.id_produktu = p.id_produktu AND zp.id_zamowienia = " + (row+1) + " ORDER BY zp.id_zamowienia;");

                ArrayList<ArrayList<String>> temp = selector.select("SELECT id_dostawcy FROM zamowienie WHERE id_zamowienia = " + (row+1) + ";");
                String id_dostawcy = temp.get(0).get(0);

                boolean ResignEnable = false;
                boolean TakeOverEnable = (id_dostawcy == null);
                if(!TakeOverEnable)
                    ResignEnable = (id_dostawcy.equals(CurrentUser.Values.get(0)));

                ParcelDetails parcelDetails = new ParcelDetails(ParcelDetails, ProductListArray, ResignEnable, TakeOverEnable, frame, mainInstance);
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
                int row = CouriersTable.convertRowIndexToModel(CouriersTable.rowAtPoint(evt.getPoint()));

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

        zmienDaneButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String password = String.valueOf(ConfirmPassword.getPassword());
                String SHApassword = String.format("%032x", new BigInteger(1, digest.digest(password.getBytes(StandardCharsets.UTF_8))));

                if(SHApassword.equalsIgnoreCase(CurrentUser.Values.get(4)))    //  check password
                {
                    if(String.valueOf(ChangePassword.getPassword()).equals(String.valueOf(ChangePasswordRepeat.getPassword())))    // check new password
                    {
                        if(NameCheckBox.isSelected() && !nameRegEx.matcher(ChangeName.getText()).matches())
                        {
                            JOptionPane.showMessageDialog(frame, "Imię jest nieprawidłowe!", "Błąd", JOptionPane.PLAIN_MESSAGE);
                            return;
                        }

                        if(SurnameCheckBox.isSelected() && !nameRegEx.matcher(ChangeSurname.getText()).matches())
                        {
                            JOptionPane.showMessageDialog(frame, "Nazwisko jest nieprawidłowe!", "Błąd", JOptionPane.PLAIN_MESSAGE);
                            return;
                        }

                        if(EmailCheckBox.isSelected() && !emailRegEx.matcher(ChangeEmail.getText()).matches())
                        {
                            JOptionPane.showMessageDialog(frame, "Email jest nieprawidłowy!", "Błąd", JOptionPane.PLAIN_MESSAGE);
                            return;
                        }

                        if(PasswordCheckBox.isSelected() && !passwordRegEx.matcher(String.valueOf(ChangePassword.getPassword())).matches())
                        {
                            JOptionPane.showMessageDialog(frame,
                                    "Hasło nieprawidłowe!\n\n" +
                                             "Hasło musi:\n" +
                                             "-zawierać co najmniej jedną cyfrę, jedną wielką i jedną małą literę\n" +
                                             "-mieć co najmniej 8 znaków.", "Błąd", JOptionPane.PLAIN_MESSAGE);
                            return;
                        }

                        // data can be updated
                        if(NameCheckBox.isSelected())
                        {
                            queryHandler.execute("UPDATE Dostawca SET imie = '" + ChangeName.getText() + "' WHERE email = '" + CurrentUser.Values.get(3) + "';");
                            CurrentUser.Values.set(1, ChangeName.getText());
                            logger.trace(CurrentUser.Values.get(3) + "changed name");
                        }

                        if(SurnameCheckBox.isSelected())
                        {
                            queryHandler.execute("UPDATE Dostawca SET nazwisko = '" + ChangeSurname.getText() + "' WHERE email = '" + CurrentUser.Values.get(3) + "';");
                            CurrentUser.Values.set(2, ChangeSurname.getText());
                            logger.trace(CurrentUser.Values.get(3) + "changed surname");
                        }

                        if(EmailCheckBox.isSelected())
                        {
                            String oldMail = CurrentUser.Values.get(2);
                            queryHandler.execute("UPDATE Dostawca SET email = '" + ChangeEmail.getText() + "' WHERE email = '" + CurrentUser.Values.get(3) + "';");
                            CurrentUser.Values.set(3, ChangeEmail.getText());
                            logger.trace(oldMail + "changed email to " + CurrentUser.Values.get(3));
                        }

                        if(PasswordCheckBox.isSelected())
                        {
                            String newPassword = String.valueOf(ChangePassword.getPassword());
                            String SHAnewPassword = String.format("%032x", new BigInteger(1, digest.digest(newPassword.getBytes(StandardCharsets.UTF_8))));

                            queryHandler.execute("UPDATE Dostawca SET haslo = '" + SHAnewPassword + "' WHERE email = '" + CurrentUser.Values.get(3) + "';");
                            CurrentUser.Values.set(4, SHAnewPassword);
                            logger.trace(CurrentUser.Values.get(3) + "changed email");
                        }

                        JOptionPane.showMessageDialog(frame, "Dane zaktualizowane", "Operacja przebiegła pomyślnie", JOptionPane.PLAIN_MESSAGE);
                        ChangeName.setText("");
                        ChangeSurname.setText("");
                        ChangeEmail.setText("");
                        ChangePassword.setText("");
                        ChangePasswordRepeat.setText("");
                        ConfirmPassword.setText("");

                        CurrName.setText(CurrentUser.Values.get(1));
                        CurrSurname.setText(CurrentUser.Values.get(2));
                        CurrEmail.setText(CurrentUser.Values.get(3));
                    }
                    else JOptionPane.showMessageDialog(frame, "Hasła się nie zgadzają!", "Błąd", JOptionPane.PLAIN_MESSAGE);
                }
                else JOptionPane.showMessageDialog(frame, "Niepoprawne hasło!", "Błąd", JOptionPane.PLAIN_MESSAGE);
            }
        });

        usunKontoButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ArrayList<ArrayList<String>> temp = selector.select("SELECT COUNT(z.id_zamowienia) FROM zamowienie z WHERE id_dostawcy = " + CurrentUser.Values.get(0) + ";");
                String LinkedParcels = temp.get(0).get(0);

                if(!LinkedParcels.equals("0"))
                {
                    JOptionPane.showMessageDialog(frame, "Nie możesz usunąć konta powiązanego z jakąkolwiek ilością paczek!\n" +
                            "Ilość powiązanych paczek: " + LinkedParcels, "Operacja nieudana", JOptionPane.PLAIN_MESSAGE);
                }
                else
                {
                    String password = String.valueOf(ConfirmDelete.getPassword());
                    String SHApassword = String.format("%032x", new BigInteger(1, digest.digest(password.getBytes(StandardCharsets.UTF_8))));

                    if(SHApassword.equalsIgnoreCase(CurrentUser.Values.get(4)))    //  check password
                    {
                        if(JOptionPane.showOptionDialog(frame, "Usunąć konto? Tej operacji NIE MOŻNA COFNĄĆ!", "Potwierdź operację",
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[1])==JOptionPane.YES_OPTION)
                        {
                            queryHandler.execute("DELETE FROM Dostawca where email='"+ CurrentUser.Values.get(3) +"';");
                            logger.trace(CurrentUser.Values.get(3) + " removed his account");
                            CurrentUser.Values = null;
                            new LoginForm().createWindow();
                            frame.dispose();

                            JOptionPane.showMessageDialog(frame, "Twoje konto zostało usunięte.", "Operacja przebiegła pomyślnie", JOptionPane.PLAIN_MESSAGE);
                        }
                        else ConfirmDelete.setText("");
                    }
                    else JOptionPane.showMessageDialog(frame, "Niepoprawne hasło!", "Błąd", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        MyParcelsFilter.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>((DefaultTableModel) MyParcelsTable.getModel());
                sorter.setRowFilter(RowFilter.regexFilter(MyParcelsFilter.getText()));
                MyParcelsTable.setRowSorter(sorter);
            }

            @Override
            public void keyPressed(KeyEvent e) {            }

            @Override
            public void keyReleased(KeyEvent e) {            }
        });

        AllParcelsFilter.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>((DefaultTableModel) AllParcelsTable.getModel());
                sorter.setRowFilter(RowFilter.regexFilter(AllParcelsFilter.getText()));
                AllParcelsTable.setRowSorter(sorter);
            }

            @Override
            public void keyPressed(KeyEvent e){            }

            @Override
            public void keyReleased(KeyEvent e) {            }
        });

        CouriersFilter.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>((DefaultTableModel) CouriersTable.getModel());
                sorter.setRowFilter(RowFilter.regexFilter(CouriersFilter.getText()));
                CouriersTable.setRowSorter(sorter);
            }

            @Override
            public void keyPressed(KeyEvent e) {            }

            @Override
            public void keyReleased(KeyEvent e) {            }
        });
    }

    public void createWindow()
    {
        frame = new JFrame("DBmanager - moje paczki");
        frame.setContentPane(this.MainPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(720, 500);
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent we)
            {
                if(JOptionPane.showOptionDialog(frame, "Czy chcesz wyjść?", "Potwierdź operację",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, confirmOptions, confirmOptions[1])==JOptionPane.YES_OPTION)
                {
                    logger.trace("Application closed with exit code 0");
                    System.exit(0);
                }
            }
        });
    }

    private void createUIComponents()
    {
        MyParcelsTable = new JTable();
        String[] MyParcelsColumns = {"ID", "Dostawca", "Klient", "Punkt", "Uwagi"};
        MyParcelsTable.setModel(new DefaultTableModel(MyParcelsColumns, 0));
        MyParcelsTableModel = (DefaultTableModel) MyParcelsTable.getModel();
        MyParcelsTable.setDefaultEditor(Object.class, null);
        MyParcelsTable.setAutoCreateRowSorter(true);

        AllParcelsTable = new JTable();
        String[] AllParcelsColumns = {"ID", "Dostawca", "Klient", "Punkt", "Uwagi"};
        AllParcelsTable.setModel(new DefaultTableModel(AllParcelsColumns, 0));
        AllParcelsTableModel = (DefaultTableModel) AllParcelsTable.getModel();
        AllParcelsTable.setDefaultEditor(Object.class, null);
        AllParcelsTable.setAutoCreateRowSorter(true);

        CouriersTable = new JTable();
        String[] CouriersColumns = {"Imię", "Nazwisko", "Email", "Przesyłki"};
        CouriersTable.setModel(new DefaultTableModel(CouriersColumns, 0));
        CouriersTableModel = (DefaultTableModel) CouriersTable.getModel();
        CouriersTable.setDefaultEditor(Object.class, null);
        CouriersTable.setAutoCreateRowSorter(true);
    }
}