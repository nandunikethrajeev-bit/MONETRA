package com.monetra.desktop;

import com.monetra.desktop.theme.MonetraTheme;
import com.monetra.desktop.view.MainFrame;

import javax.swing.*;

public class MonetraDesktopApp {

    public static void main(String[] args) {
        // Apply custom modern dark charcoal & gold theme
        MonetraTheme.applyGlobalTheme();

        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to initialize MONETRA Desktop Application:\n" + e.getMessage(),
                        "Initialization Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

