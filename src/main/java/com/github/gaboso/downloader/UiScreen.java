package com.github.gaboso.downloader;

import com.github.gaboso.downloader.ui.BaseUI;
import com.github.gaboso.downloader.util.SearchPhoto;
import net.miginfocom.swing.MigLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class UiScreen {

    private static final Logger LOGGER = LogManager.getLogger(UiScreen.class);

    private JFrame frmSearchphotos;
    private JTextField urlField;
    private JTextField destinyFolderField;

    private String url;
    private String folderPath;
    private boolean isValid;

    /**
     * Create the application.
     */
    private UiScreen() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmSearchphotos = new JFrame();
        frmSearchphotos.setResizable(false);
        frmSearchphotos.setTitle("Downloader");
        frmSearchphotos.setBounds(100, 100, 460, 129);
        frmSearchphotos.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frmSearchphotos.getContentPane().setLayout(null);
        frmSearchphotos.setIconImage(new ImageIcon(getClass().getResource("/img/camera.png")).getImage());

        JPanel panel = BaseUI.newPanel();
        panel.setBounds(0, 0, 444, 106);
        frmSearchphotos.getContentPane().add(panel);

        JLabel labelUrl = BaseUI.newLabel("URL:");
        panel.setLayout(new MigLayout("", "[100px][314px]", "[20px][20px][23px]"));
        panel.add(labelUrl, "cell 0 0,growx,aligny center");

        JLabel labelDestinyFolder = BaseUI.newLabel("Destination folder:");
        panel.add(labelDestinyFolder, "cell 0 1,growx,aligny center");

        urlField = BaseUI.newField();
        panel.add(urlField, "cell 1 0,grow");

        destinyFolderField = BaseUI.newField();
        panel.add(destinyFolderField, "cell 1 1,grow");

        JButton buttonStart = BaseUI.newButton();
        buttonStart.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                setLoading(buttonStart);

                url = urlField.getText();
                folderPath = destinyFolderField.getText();

                validateFields();
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
                if (isValid) {
                    try {
                        openConnection(url, folderPath);

                        setDefault(buttonStart);
                    } catch (Exception e) {
                        LOGGER.error(e);
                        setError(buttonStart, "Try download again");
                    }

                } else {
                    setError(buttonStart, "Check fields & try download again");
                }

            }
        });
        panel.add(buttonStart, "cell 1 2,grow");
    }

    private void setLoading(JButton button) {
        final Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);

        button.setText("Downloading ...");
        button.setForeground(BaseUI.BUTTON_FONT_LOADING_COLOR);
        button.setBackground(BaseUI.LOADING_COLOR);
        button.setCursor(waitCursor);
    }

    private void validateFields() {
        final boolean isValidURL = Strings.isNotBlank(url);
        final boolean isValidFolder = Strings.isNotBlank(folderPath);

        urlField.setBorder(isValidURL ? BaseUI.SUCCESS_BORDER : BaseUI.ERROR_BORDER);
        destinyFolderField.setBorder(isValidFolder ? BaseUI.SUCCESS_BORDER : BaseUI.ERROR_BORDER);

        isValid = isValidURL && isValidFolder;
    }

    private void openConnection(String url, String folderPath) {
        SearchPhoto searchPhoto = new SearchPhoto();
        Document page = searchPhoto.getPage(url, folderPath);
        Elements imageElements = searchPhoto.getImageElements(page);

        searchPhoto.dowloadAllImages(imageElements, "src");

        urlField.setBorder(BaseUI.DEFAULT_BORDER);
        destinyFolderField.setBorder(BaseUI.DEFAULT_BORDER);
        LOGGER.info("Finished");
    }

    private void setDefault(JButton button) {
        final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        button.setText("Start Download");
        button.setForeground(BaseUI.BUTTON_FONT_COLOR);
        button.setBackground(BaseUI.BUTTON_BACK_DEFAULT_COLOR);
        button.setCursor(handCursor);
    }

    private void setError(JButton button, String message) {
        final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        button.setText(message);
        button.setForeground(BaseUI.BUTTON_FONT_ERROR_COLOR);
        button.setBackground(BaseUI.ERROR_COLOR);
        button.setCursor(handCursor);
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

}
