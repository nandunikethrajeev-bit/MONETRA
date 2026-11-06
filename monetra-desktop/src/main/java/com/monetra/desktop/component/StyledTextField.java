package com.monetra.desktop.component;

import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class StyledTextField extends JTextField {

    private boolean isFocused = false;

    public StyledTextField(int columns) {
        super(columns);
        init();
    }

    public StyledTextField(String text) {
        super(text);
        init();
    }

    public StyledTextField() {
        init();
    }

    private void init() {
        setFont(MonetraTheme.FONT_BODY);
        setForeground(MonetraTheme.TEXT_PRIMARY);
        setBackground(MonetraTheme.BG_INPUT);
        setCaretColor(MonetraTheme.ACCENT_GOLD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MonetraTheme.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MonetraTheme.ACCENT_GOLD, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MonetraTheme.BORDER_COLOR, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                repaint();
            }
        });
    }
}

