package com.monetra.desktop.component;

import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class StatCard extends CardPanel {

    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel subtitleLabel;

    public StatCard(String title, String initialValue, String subtitle, Color valueColor) {
        super(new BorderLayout(8, 8));

        titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(MonetraTheme.FONT_SMALL);
        titleLabel.setForeground(MonetraTheme.TEXT_MUTED);

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(MonetraTheme.FONT_METRIC);
        valueLabel.setForeground(valueColor != null ? valueColor : MonetraTheme.TEXT_PRIMARY);

        subtitleLabel = new JLabel(subtitle != null ? subtitle : "");
        subtitleLabel.setFont(MonetraTheme.FONT_SMALL);
        subtitleLabel.setForeground(MonetraTheme.TEXT_SECONDARY);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(valueLabel);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(subtitleLabel);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setValue(BigDecimal amount) {
        valueLabel.setText(MonetraTheme.formatCurrency(amount));
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    public void setValueColor(Color color) {
        valueLabel.setForeground(color);
    }
}

