package br.com.gaboso.photo;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UiScreen {

    private JFrame frmSearchphotos;
    private JTextField urlField;
    private JTextField destinyFolderField;
    private JLabel labelDestinyFolder;
    private JLabel labelUrl;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UiScreen window = new UiScreen();
                window.frmSearchphotos.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public UiScreen() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmSearchphotos = new JFrame();
        frmSearchphotos.setResizable(false);
        frmSearchphotos.setTitle("SearchPhotos");
        frmSearchphotos.setBounds(100, 100, 450, 129);
        frmSearchphotos.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frmSearchphotos.getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 444, 106);
        frmSearchphotos.getContentPane().add(panel);

        labelUrl = new JLabel("URL:");
        labelUrl.setVisible(true);
        panel.setLayout(new MigLayout("", "[100px][314px]", "[20px][20px][23px]"));
        panel.add(labelUrl, "cell 0 0,growx,aligny center");

        labelDestinyFolder = new JLabel("Pasta de destino:");
        labelDestinyFolder.setVisible(true);
        panel.add(labelDestinyFolder, "cell 0 1,growx,aligny center");

        urlField = new JTextField();
        panel.add(urlField, "cell 1 0,grow");
        urlField.setColumns(10);

        destinyFolderField = new JTextField();
        panel.add(destinyFolderField, "cell 1 1,grow");
        destinyFolderField.setColumns(10);

        JButton buttonStart = new JButton("Iniciar");
        buttonStart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent arg0) {
                checkFields();
            }
        });
        panel.add(buttonStart, "cell 1 2,grow");
    }

    private void checkFields() {
        String url = urlField.getText();
        String folderPath = destinyFolderField.getText();

        if (url != null && url.trim().length() > 0) {
            labelUrl.setForeground(Color.GREEN);
        } else {
            labelUrl.setForeground(Color.RED);
        }

        if (folderPath != null && folderPath.trim().length() > 0) {
            labelDestinyFolder.setForeground(Color.GREEN);
        } else {
            labelDestinyFolder.setForeground(Color.RED);
        }
    }

}