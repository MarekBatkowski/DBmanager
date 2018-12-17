package GUI;

import SQLhandling.DBConnection;
import SQLhandling.QueryHandler;
import SQLhandling.Selector;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginForm
{
    private static JFrame frame;
    private JPanel MainPanel;
    private JPanel Cards;

    private JPanel StartScreen;
    private JLabel ImageTruck;
    private JButton logInButton;
    private JButton signInButton;
    private JButton WyjdźButton;

    private JPanel LogIn;
    private JLabel ImageKey;
    private JTextField LogInEmail;
    private JPasswordField LogInPassword;
    private JButton WsteczButton;
    private JButton ZalogujSieButton;

    private JPanel SignIn;
    private JLabel ImageAdd;
    private JTextField SignUpName;
    private JTextField SignUpSurname;
    private JTextField SignUpEmail;
    private JPasswordField SignUpPassword;
    private JButton WsteczButton1;
    private JButton ZałóżKontoButton;

    public LoginForm()
    {
        Cards.add(StartScreen, "StartScreen");
        Cards.add(LogIn, "LogIn");
        Cards.add(SignIn, "SignIn");

        CardLayout cards = (CardLayout) (Cards.getLayout());
        cards.show(Cards, "StartScreen");

        Selector selector = new Selector();
        QueryHandler queryHandler = new QueryHandler();
        final Logger logger = Logger.getLogger(LoginForm.class);
        CurrentUser.Values = null;

        try
        {
            DBConnection.GetInstance().getConnection();
        }
        catch (SQLException e)
        {
            logger.error("Couldn't get connection to database");
            logger.trace("Application closed with exit code 1");

            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Nie można połaczyć z bazą danych", "Błąd", JOptionPane.PLAIN_MESSAGE);
            System.exit(1);
        }

        logger.trace("Connected to Database");


        //// StartScreen ////

        logInButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.setSize(340, 350);
                frame.setTitle("Zaloguj się");
                cards.show(Cards, "LogIn");
                LogInEmail.requestFocus();
            }
        });

        signInButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.setSize(340, 400);
                frame.setTitle("Zarejestruj się");
                cards.show(Cards, "SignIn");
                SignUpEmail.requestFocus();
            }
        });

        WyjdźButton.addActionListener(new ActionListener()
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


        //// LogIn ////

        ZalogujSieButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String email = LogInEmail.getText();
                String haslo = String.valueOf(LogInPassword.getPassword());

                ArrayList<ArrayList<String>> result = selector.select("SELECT imie, nazwisko, email, haslo FROM Dostawca " +
                                                                        "WHERE email = '" + email + "' and haslo = '" + haslo + "'");

                if (result.size() == 1)
                {
                    CurrentUser.Values = result.get(0);
                    new MainWindow().createWindow();
                    frame.dispose();

                    logger.trace("User " + CurrentUser.Values.get(2) + " logged");
                }
                else
                {
                    logger.warn("Incorrect user data provided using email: " + email);

                    JOptionPane.showMessageDialog(frame, "Niepoprawne dane!", "Błąd", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        WsteczButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.setSize(340, 380);
                frame.setTitle("Zaloguj lub zarejestruj się");
                cards.show(Cards, "StartScreen");
                logInButton.requestFocus();
            }
        });


        // SignIn

        ZałóżKontoButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(SignUpEmail.getText().length()<10)
                {
                    JOptionPane.showMessageDialog(frame, "Email jest za krótki!", "Błąd", JOptionPane.PLAIN_MESSAGE);
                    return;
                }

                if(String.valueOf(SignUpPassword.getPassword()).length()<8)
                {
                    JOptionPane.showMessageDialog(frame, "Hasło jest za krótkie!", "Błąd", JOptionPane.PLAIN_MESSAGE);
                    return;
                }

                String imie = SignUpName.getText();
                String nazwisko = SignUpSurname.getText();
                String email = SignUpEmail.getText();
                String haslo = String.valueOf(SignUpPassword.getPassword());

                queryHandler.insert("INSERT INTO dostawca(imie, nazwisko, email, haslo) VALUES ('" + imie + "', '" + nazwisko + "', '" + email + "', '" + haslo + "');");
                JOptionPane.showMessageDialog(frame, "Dodano nowego użytownika!", "Operacja Pomyślna", JOptionPane.PLAIN_MESSAGE);
                cards.show(Cards, "StartScreen");

                logger.trace("Created new user " + email);
            }
        });

        WsteczButton1.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.setSize(340, 380);
                frame.setTitle("Zaloguj lub zarejestruj się");
                cards.show(Cards, "StartScreen");
                logInButton.requestFocus();
            }
        });
    }

    void createWindow()
    {
        frame = new JFrame("Zaloguj lub zarejestruj się");
        frame.setContentPane(new LoginForm().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(340, 380);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    private void createUIComponents()
    {
        ImageTruck = new JLabel(new ImageIcon("TruckDelivery.png"));
        ImageKey = new JLabel(new ImageIcon("Key.png"));
        ImageAdd = new JLabel(new ImageIcon("Add.png"));
    }
}
