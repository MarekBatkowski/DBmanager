package GUI;

import SQLhandling.QueryHandler;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.FloatBuffer;
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
    private JButton PrintButton;
    private JLabel SumLabel;
    private JFrame ParentFrame;
    Float Sum;

    final Logger logger = Logger.getLogger(MainWindow.class);
    private static final Object[] confirmOptions = {"     Tak     ","     Nie     "};

    public void setPosition(int position)
    {
        this.position = position;
    }

    public ParcelDetails(ArrayList<ArrayList<String>> ParcelDetails, ArrayList<ArrayList<String>> ProductListArray, boolean ResignEnabled, boolean TakeOverEnabled,
                         JFrame Parentframe, MainWindow mainInstance)
    {
        this.ParentFrame = Parentframe;
        ProductListModel.setNumRows(0);

        String Parcel_Id = ParcelDetails.get(0).get(0);
        Sum = 0f;

        for(ArrayList<String> iter : ProductListArray)
        {
            ProductListModel.addRow(new Vector<String>(iter));
            Sum += Integer.parseInt(iter.get(0)) * Float.parseFloat(iter.get(2));
        }

        IDLabel.setText(ParcelDetails.get(0).get(0));
        CourierLabel.setText(ParcelDetails.get(0).get(1));
        ClientLabel.setText(ParcelDetails.get(0).get(2));
        PointLabel.setText(ParcelDetails.get(0).get(3));
        CommentsLabel.setText(ParcelDetails.get(0).get(4));
        SumLabel.setText(String.format("%.2f", Sum));

        if(ResignEnabled)   ResignButton.setEnabled(true);
        if(ResignEnabled)   PrintButton.setEnabled(true);
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
            //    ParentFrame.setEnabled(true);
            //    ParentFrame.setVisible(true);
                mainInstance.UpdateAllParcels();
                mainInstance.UpdateMyParcels();

                ResignButton.setEnabled(true);
                PrintButton.setEnabled(true);
                TakeOverButton.setEnabled(false);
                CourierLabel.setText(CurrentUser.Values.get(1) + " " + CurrentUser.Values.get(2));
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
            //    ParentFrame.setEnabled(true);
            //    ParentFrame.setVisible(true);
                mainInstance.UpdateAllParcels();
                mainInstance.UpdateMyParcels();
                ResignButton.setEnabled(false);
                PrintButton.setEnabled(false);
                TakeOverButton.setEnabled(true);
                CourierLabel.setText(" ");
            }
        });

        PrintButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileChooser = new JFileChooser()
                {
                    @Override
                    public void approveSelection()
                    {
                        File file = getSelectedFile();
                        if(file.exists() && getDialogType() == SAVE_DIALOG)
                        {
                            if(JOptionPane.showOptionDialog(frame, "Plik o tej nazwie już istnieje, nadpisać?", "Istniejący plik",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, confirmOptions, confirmOptions[1])==JOptionPane.YES_OPTION)
                                super.approveSelection();
                            return;
                        }
                        super.approveSelection();
                    }
                };

                String defaultFileName = ParcelDetails.get(0).get(0) + "_" + ParcelDetails.get(0).get(3) + "_" + ParcelDetails.get(0).get(2) + ".pdf";
                fileChooser.setSelectedFile(new File(defaultFileName));
                fileChooser.setFileFilter(new FileNameExtensionFilter("pdf document", "pdf"));
                fileChooser.setAcceptAllFileFilterUsed(false);

                if(fileChooser.showSaveDialog(frame)==JFileChooser.APPROVE_OPTION)
                {
                    String fileName = fileChooser.getSelectedFile().getAbsolutePath();

                    if(!fileChooser.getSelectedFile().getAbsolutePath().endsWith(".pdf"))
                        fileName = fileName + ".pdf";

                    Document document = new Document(PageSize.A4,48,48,48,48);
                    try
                    {
                        PdfWriter.getInstance(document,new FileOutputStream(fileName));

                        document.addTitle(defaultFileName);
                        document.addCreator("DBmanager");

                        // pdf creation
                        document.open();
                        document.add(new Paragraph(""));

                        ArrayList<String> parcelDataValues = new ArrayList<>();
                        parcelDataValues.add("ID:");
                        parcelDataValues.add(ParcelDetails.get(0).get(0));
                        parcelDataValues.add("Dane klienta:");
                        parcelDataValues.add(ParcelDetails.get(0).get(2));
                        parcelDataValues.add("Punkt odbioru:");
                        parcelDataValues.add(ParcelDetails.get(0).get(3));
                        parcelDataValues.add("Uwagi:");
                        parcelDataValues.add(ParcelDetails.get(0).get(4));

                        PdfPTable parcelData = new PdfPTable(2);
                        parcelData.setWidthPercentage(70);
                        parcelData.setHorizontalAlignment(Element.ALIGN_LEFT);
                        PdfPCell temp;

                        for(String iter : parcelDataValues)
                        {
                            temp = new PdfPCell(new Paragraph(iter));
                            temp.setBorder(Rectangle.NO_BORDER);
                            parcelData.addCell(temp);
                        }

                        PdfPTable productList = new PdfPTable(5);
                        parcelData.setWidthPercentage(100);
                        productList.setHorizontalAlignment(Element.ALIGN_LEFT);
                        productList.addCell(new PdfPCell(new Paragraph("Ilosc")));
                        productList.addCell(new PdfPCell(new Paragraph("Nazwa")));
                        productList.addCell(new PdfPCell(new Paragraph("Cena")));
                        productList.addCell(new PdfPCell(new Paragraph("Waga")));
                        productList.addCell(new PdfPCell(new Paragraph("Wymiary")));

                        for(ArrayList<String> iter : ProductListArray)
                        {
                            for(String val : iter)
                                productList.addCell(new PdfPCell(new Paragraph(val)));
                        }

                        document.add(parcelData);
                        document.add(new Paragraph("\nLista produktow:"));
                        document.add(new Paragraph(" "));
                        document.add(productList);
                        document.add(new Paragraph("\nRazem do zaplaty: " + String.format("%.2f", Sum)));
                        document.close();
                    }
                    catch (DocumentException el)
                    {
                        el.printStackTrace();
                    }
                    catch (FileNotFoundException e1)
                    {
                        e1.printStackTrace();
                    }
                }
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
        ParentFrame.setEnabled(false);

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent we)
            {
                frame.dispose();
                ParentFrame.setEnabled(true);
                ParentFrame.setVisible(true);
            }
        });
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
