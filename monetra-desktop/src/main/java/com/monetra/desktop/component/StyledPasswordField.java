package com.monetra.desktop.component;

import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class StyledPasswordField extends JPasswordField {

    public StyledPasswordField(int columns) {
        super(columns);
        init();
    }

    public StyledPasswordField() {
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
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MonetraTheme.ACCENT_GOLD, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MonetraTheme.BORDER_COLOR, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                repaint();
            }
        });
    }
}

