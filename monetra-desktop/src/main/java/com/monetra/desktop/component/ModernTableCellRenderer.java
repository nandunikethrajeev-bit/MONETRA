package com.monetra.desktop.component;

import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;

public class ModernTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (isSelected) {
            c.setBackground(MonetraTheme.ACCENT_GOLD_MUTED);
            c.setForeground(MonetraTheme.TEXT_PRIMARY);
        } else {
            c.setBackground(row % 2 == 0 ? MonetraTheme.BG_CARD : new Color(0x1E, 0x1E, 0x22));
            c.setForeground(MonetraTheme.TEXT_PRIMARY);
        }

        setFont(MonetraTheme.FONT_BODY);
        setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        // Format BigDecimals and Numbers
        if (value instanceof BigDecimal bd) {
            setText(MonetraTheme.formatCurrency(bd));
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else if (value instanceof Number n) {
            setText(n.toString());
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
            if (value != null) {
                String str = value.toString();
                if ("INCOME".equalsIgnoreCase(str)) {
                    setForeground(MonetraTheme.COLOR_INCOME);
                } else if ("EXPENSE".equalsIgnoreCase(str)) {
                    setForeground(MonetraTheme.COLOR_EXPENSE);
                } else if ("COMPLETED".equalsIgnoreCase(str) || "OK".equalsIgnoreCase(str)) {
                    setForeground(MonetraTheme.COLOR_INCOME);
                } else if ("WARNING".equalsIgnoreCase(str)) {
                    setForeground(MonetraTheme.COLOR_WARNING);
                } else if ("EXCEEDED".equalsIgnoreCase(str)) {
                    setForeground(MonetraTheme.COLOR_EXPENSE);
                }
            }
        }

        return c;
    }
}

