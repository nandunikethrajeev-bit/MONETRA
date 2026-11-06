package com.monetra.desktop.component;

import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ModernTable extends JTable {

    public ModernTable(DefaultTableModel model) {
        super(model);
        init();
    }

    public ModernTable() {
        super();
        init();
    }

    private void init() {
        setRowHeight(38);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setDefaultRenderer(Object.class, new ModernTableCellRenderer());

        JTableHeader header = getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setBackground(MonetraTheme.BG_SIDEBAR);
        header.setForeground(MonetraTheme.TEXT_SECONDARY);
        header.setFont(MonetraTheme.FONT_BODY_BOLD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MonetraTheme.BORDER_COLOR));
        header.setReorderingAllowed(false);
    }
}

