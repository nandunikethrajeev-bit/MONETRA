package com.monetra.desktop.component;

import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SecondaryButton extends JButton {

    private boolean isHovered = false;

    public SecondaryButton(String text) {
        super(text);
        setFont(MonetraTheme.FONT_BODY);
        setForeground(MonetraTheme.TEXT_PRIMARY);
        setBackground(MonetraTheme.BG_CARD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(isHovered ? MonetraTheme.BG_CARD_HOVER : MonetraTheme.BG_CARD);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

        g2.setColor(MonetraTheme.BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        g2.dispose();
        super.paintComponent(g);
    }
}

