package com.monetra.desktop.component;

import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PrimaryButton extends JButton {

    private boolean isHovered = false;

    public PrimaryButton(String text) {
        super(text);
        setFont(MonetraTheme.FONT_BODY_BOLD);
        setForeground(new Color(0x12, 0x12, 0x14)); // Dark text on gold button
        setBackground(MonetraTheme.ACCENT_GOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

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

        if (isEnabled()) {
            g2.setColor(isHovered ? MonetraTheme.ACCENT_GOLD_HOVER : MonetraTheme.ACCENT_GOLD);
        } else {
            g2.setColor(MonetraTheme.BORDER_COLOR);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2.dispose();
        super.paintComponent(g);
    }
}

