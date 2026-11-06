package com.monetra.desktop.view;

import com.monetra.core.dto.CategoryCreateDto;
import com.monetra.core.dto.CategoryDto;
import com.monetra.core.model.TransactionType;
import com.monetra.desktop.component.ModernTable;
import com.monetra.desktop.component.PrimaryButton;
import com.monetra.desktop.component.SecondaryButton;
import com.monetra.desktop.component.StyledTextField;
import com.monetra.desktop.service.DesktopServiceManager;
import com.monetra.desktop.service.SessionManager;
import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoriesView extends JPanel {

    private final ModernTable table;
    private final DefaultTableModel tableModel;

    public CategoriesView() {
        setBackground(MonetraTheme.BG_MAIN);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Header Toolbar
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Category Management");
        title.setFont(MonetraTheme.FONT_TITLE_LARGE);
        title.setForeground(MonetraTheme.TEXT_PRIMARY);

        PrimaryButton addBtn = new PrimaryButton("+ New Custom Category");
        addBtn.addActionListener(e -> showAddCategoryDialog());
        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Category Name", "Type", "Scope", "Color Code"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new ModernTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(MonetraTheme.BORDER_COLOR, 1));
        add(scroll, BorderLayout.CENTER);
    }

    public void loadData() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) return;

        List<CategoryDto> list = DesktopServiceManager.getInstance().getCategoryService().getCategoriesForUser(userId);
        tableModel.setRowCount(0);
        for (CategoryDto c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getType().name(),
                    c.isDefault() ? "System Default" : "Custom User Category",
                    c.getColor()
            });
        }
    }

    private void showAddCategoryDialog() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create Custom Category", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 14));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        StyledTextField nameField = new StyledTextField();
        JComboBox<TransactionType> typeBox = new JComboBox<>(TransactionType.values());
        JComboBox<String> colorBox = new JComboBox<>(new String[]{"#E5A93C", "#10B981", "#EF4444", "#3B82F6", "#8B5CF6", "#EC4899", "#06B6D4"});

        form.add(new JLabel("Category Name:")); form.add(nameField);
        form.add(new JLabel("Type:")); form.add(typeBox);
        form.add(new JLabel("Accent Color:")); form.add(colorBox);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        PrimaryButton save = new PrimaryButton("Save");
        SecondaryButton cancel = new SecondaryButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        save.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                TransactionType type = (TransactionType) typeBox.getSelectedItem();
                String color = (String) colorBox.getSelectedItem();

                DesktopServiceManager.getInstance().getCategoryService().createCustomCategory(userId,
                        new CategoryCreateDto(name, type, "tag", color));
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(cancel);
        btnPanel.add(save);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
