package com.github.gaboso.photo.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class BaseUI {

    // FONTS
    public static final Font FONT_LABEL = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    public static final Font FONT_FIELD = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    // COLORS
    public static final Color BUTTON_FONT_COLOR = Color.decode("#D6E4FF");
    public static final Color BUTTON_FONT_ERROR_COLOR = Color.decode("#FFCBA9");
    public static final Color BUTTON_FONT_LOADING_COLOR = Color.decode("#ADC8FF");

    public static final Color BUTTON_BACK_DEFAULT_COLOR = Color.decode("#254EDB");

    public static final Color LOADING_COLOR = Color.decode("#102693");
    public static final Color ERROR_COLOR = Color.decode("#B71614");
    public static final Color SUCCESS_COLOR = Color.decode("#27A120");

    public static final Color BORDER_DEFAULT_COLOR = Color.decode("#ADC8FF");
    public static final Color PANEL_COLOR = Color.decode("#F5F5F5");
    public static final Color FONT_COLOR_DEFAULT = Color.decode("#222B45");


    // BORDERS
    public static final LineBorder ERROR_BORDER = new LineBorder(ERROR_COLOR, 1);
    public static final LineBorder SUCCESS_BORDER = new LineBorder(SUCCESS_COLOR, 1);
    public static final LineBorder DEFAULT_BORDER = new LineBorder(BORDER_DEFAULT_COLOR, 1);

    private BaseUI() {
    }

    public static JPanel newPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_COLOR);

        return panel;
    }

    public static JLabel newLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(FONT_COLOR_DEFAULT);
        label.setVisible(true);

        return label;
    }

    public static JTextField newField() {
        JTextField field = new JTextField();
        field.setBorder(DEFAULT_BORDER);
        field.setFont(FONT_FIELD);
        field.setColumns(10);

        return field;
    }

    public static JButton newButton() {
        final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        JButton button = new JButton("Start Download");
        button.setFont(FONT_LABEL);
        button.setForeground(BUTTON_FONT_COLOR);
        button.setBackground(BUTTON_BACK_DEFAULT_COLOR);
        button.setCursor(handCursor);
        button.setBorder(new EmptyBorder(0, 0, 0, 0));

        return button;
    }


}
