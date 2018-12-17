package Console;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import SQLhandling.QueryHandler;
import org.apache.log4j.Logger;

import SQLhandling.*;

public class Main
{
    static Scanner scanner   = new Scanner(System.in);
    static Selector selector = new Selector();
    static QueryHandler queryHandler = new QueryHandler();

    static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws SQLException, IOException
    {
        ArrayList<String> currentUser = null;
        int option;
        System.out.println("Press enter to connect to to database");
        scanner.nextLine();
        DBConnection.GetInstance().getConnection(); // check if config file exists
/*
        SQLhandling.DBConnection SQLhandling.DBConnection = new SQLhandling.DBConnection();
        Connection connection = SQLhandling.DBConnection.getConnection();
*/

        while(true)
        {
            if(currentUser == null) // before login
            {
                System.out.println("\n1 - zaloguj\n2 - zarejestruj");
                option = scanner.nextInt();
                scanner.nextLine();

                if(option==1)
                {
                    System.out.println("email: ");
                    String email = scanner.nextLine();
                    System.out.println("haslo: ");
                    String haslo = scanner.nextLine();

                    ArrayList<ArrayList<String>> result = selector.select("SELECT imie, nazwisko, email, haslo FROM Dostawca WHERE email = '"
                            + email + "' and haslo = '" + haslo + "'");

                    if (result.size() == 1)
                    {
                        currentUser = result.get(0);
                        System.out.println("Zalogowano jako: " + currentUser.get(0) + " " + currentUser.get(1));

                        logger.trace("User " + currentUser.get(2) + " logged");
                    }
                    else
                    {
                        System.out.println("Niepoprawne dane");

                        logger.warn("Incorrect user data provided using email " + email);
                    }
                }
                else if(option==2)
                {
                    System.out.println("imie: ");
                    String imie = scanner.nextLine();
                    System.out.println("nazwisko: ");
                    String nazwisko = scanner.nextLine();
                    System.out.println("email: ");
                    String email = scanner.nextLine();
                    System.out.println("haslo: ");
                    String haslo = scanner.nextLine();

                    queryHandler.insert("INSERT INTO dostawca(imie, nazwisko, email, haslo) VALUES ('" + imie + "', '" + nazwisko + "', '" + email +"', '" + haslo + "');");
                    System.out.println("Poprawnie dodano uzytkownika!");

                    logger.trace("Created new user " + email);
                }
                else    System.out.println("incorrect option");
            }
            else    // after login
            {
                System.out.println("\n1 - zmodyfikuj dane\n2 - pokaz innych uzytkownikow systemu\n3 - usun konto\n4 - wyloguj");
                option = scanner.nextInt();
                scanner.nextLine();

                if(option==1)
                {
                    System.out.println("Obecne dane:\nimie: " + currentUser.get(0) + "\nnazwisko: " + currentUser.get(1) +
                            "\nemail: " + currentUser.get(2) + "\nhaslo: " + currentUser.get(3));

                    System.out.println("Zmien:\n1 - imie\n2 - nazwisko\n3 - email\n4 - haslo");
                    option = scanner.nextInt();
                    scanner.nextLine();
                    String temp;

                    if(option==1)
                    {
                        System.out.println("Nowe imie: ");
                        temp = scanner.nextLine();
                        queryHandler.insert("UPDATE Dostawca SET imie = '" + temp + "' where email = '" + currentUser.get(2)+ "';");

                        logger.trace(currentUser.get(2) + "changed name");
                    }
                    else if(option==2)
                    {
                        System.out.println("Nowe nazwisko: ");
                        temp = scanner.nextLine();
                        queryHandler.insert("UPDATE Dostawca SET nazwisko = '" + temp + "' where email = '" + currentUser.get(2)+ "';");

                        logger.trace(currentUser.get(2) + "changed surname");
                    }
                    else if(option==3)
                    {
                        System.out.println("Nowy email: ");
                        temp = scanner.nextLine();

                        logger.trace(currentUser.get(2) + "changed email to " + temp);

                        queryHandler.insert("UPDATE Dostawca SET email = '" + temp + "' where email = '" + currentUser.get(2)+ "';");
                        currentUser = selector.select("SELECT imie, nazwisko, email, haslo FROM Dostawca WHERE email = '" + temp + "'").get(0);
                    }
                    else if(option==4)
                    {
                        System.out.println("Nowe haslo: ");
                        temp = scanner.nextLine();
                        queryHandler.insert("UPDATE Dostawca SET haslo = '" + temp + "' where email = '" + currentUser.get(2)+ "';");

                        logger.trace(currentUser.get(2) + "changed password");
                    }
                    else    System.out.println("incorrect option");

                    if(option!=3)
                        currentUser = selector.select("SELECT imie, nazwisko, email, haslo FROM Dostawca WHERE email = '" + currentUser.get(2) + "'").get(0);
                    System.out.println("Dane zmodyfikowane");

                    System.out.println("Nowe dane:\nimie: " + currentUser.get(0) + "\nnazwisko: " + currentUser.get(1) +
                            "\nemail: " + currentUser.get(2) + "\nhaslo: " + currentUser.get(3));

                    /*
                    System.out.println("\nNowe dane:\nimie: ");
                    String imie = scanner.nextLine();
                    System.out.println("nazwisko: ");
                    String nazwisko = scanner.nextLine();
                    System.out.println("email: ");
                    String email = scanner.nextLine();
                    System.out.println("haslo: ");
                    String haslo = scanner.nextLine();

                    queryHandler.insert("UPDATE Dostawca SET imie = '" + imie + "', Nazwisko = '" + nazwisko + "', email = '" + email + "', haslo = '" + haslo + "' where email = '" + currentUser.get(2)+ "';", connection);
                    System.out.println("Dane zmodyfikowane");

                    currentUser = selector.select("SELECT imie, nazwisko, email, haslo FROM Dostawca WHERE email = '" + email + "'", connection).get(0);
                    */
                }
                else if(option==2)
                {
                    ArrayList<ArrayList<String>> lista = selector.select("SELECT * FROM Dostawca");
                    for(int i=0; i<lista.size(); i++)
                    {
                        System.out.println("\nimie: " + lista.get(i).get(0));
                        System.out.println("nazwisko: " + lista.get(i).get(1));
                        System.out.println("email: " + lista.get(i).get(2));
                    }

                    logger.trace(currentUser.get(2) + "vieved all users");
                }
                else if(option==3)
                {
                    queryHandler.insert("DELETE FROM Dostawca where email='"+ currentUser.get(2) +"';");

                    logger.trace(currentUser.get(2) + "removed his account");

                    currentUser = null;
                    System.out.println("Twoje konto zostalo usuniete");
                }
                else
                {
                    logger.trace(currentUser.get(2) + "logged out");

                    currentUser = null;
                    System.out.println("Wylogowano pomyslnie");
                }
            }
        }
    }
}

