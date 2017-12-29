package com.github.gaboso.photo;


import com.github.gaboso.photo.util.SearchPhoto;
import com.github.gaboso.photo.util.Validate;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UiScreen {

    private static final Logger LOGGER = Logger.getLogger(UiScreen.class);

    // Colors
    private static final Color BUTTON_FONT_COLOR = Color.decode("#EEEEEE");
    private static final Color BUTTON_BACK_COLOR = Color.decode("#01579B");

    private JFrame frmSearchphotos;
    private JTextField urlField;
    private JTextField destinyFolderField;

    /**
     * Create the application.
     */
    private UiScreen() {
        initialize();
    }

    /**
     * * Launch the application.
     *
     * @param args - param args
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UiScreen window = new UiScreen();
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                window.frmSearchphotos.setVisible(true);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
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
        frmSearchphotos.setIconImage(new ImageIcon(getClass().getResource("/img/camera.png")).getImage());

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 444, 106);
        frmSearchphotos.getContentPane().add(panel);

        JLabel labelUrl = new JLabel("URL:");
        labelUrl.setVisible(true);
        panel.setLayout(new MigLayout("", "[100px][314px]", "[20px][20px][23px]"));
        panel.add(labelUrl, "cell 0 0,growx,aligny center");

        JLabel labelDestinyFolder = new JLabel("Pasta de destino:");
        labelDestinyFolder.setVisible(true);
        panel.add(labelDestinyFolder, "cell 0 1,growx,aligny center");

        urlField = new JTextField();
        panel.add(urlField, "cell 1 0,grow");
        urlField.setColumns(10);

        destinyFolderField = new JTextField();
        panel.add(destinyFolderField, "cell 1 1,grow");
        destinyFolderField.setColumns(10);

        JButton buttonStart = new JButton("Iniciar");
        buttonStart.setForeground(BUTTON_FONT_COLOR);
        buttonStart.setBackground(BUTTON_BACK_COLOR);
        buttonStart.setBorder(new EmptyBorder(0, 0, 0, 0));
        buttonStart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent arg0) {
                validateFieldsAndOpenConnection();
            }
        });
        panel.add(buttonStart, "cell 1 2,grow");
    }

    /**
     * Metodo qual checa se os campos foram preenchidos corretamente e sinaliza de forma visual.
     * Se tudo estiver preenchido é chamado a conexao com a url que foi passada
     */
    private void validateFieldsAndOpenConnection() {
        String url = urlField.getText();
        String folderPath = destinyFolderField.getText();

        if (Validate.isNullOrEmpty(url))
            urlField.setBorder(new LineBorder(Color.RED, 1));
        else
            urlField.setBorder(new LineBorder(Color.GREEN, 1));

        if (Validate.isNullOrEmpty(folderPath))
            destinyFolderField.setBorder(new LineBorder(Color.RED, 1));
        else
            destinyFolderField.setBorder(new LineBorder(Color.GREEN, 1));

        if (!Validate.isNullOrEmpty(url) && !Validate.isNullOrEmpty(folderPath)) {
            SearchPhoto.connection(url, folderPath);
        }
    }

}