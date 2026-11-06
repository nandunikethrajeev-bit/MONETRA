package com.monetra.desktop.theme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class MonetraTheme {

    // Palette: Dark Charcoal with Warm Gold Accent
    public static final Color BG_MAIN = new Color(0x12, 0x12, 0x14);
    public static final Color BG_SIDEBAR = new Color(0x18, 0x18, 0x1B);
    public static final Color BG_CARD = new Color(0x22, 0x22, 0x26);
    public static final Color BG_CARD_HOVER = new Color(0x2A, 0x2A, 0x30);
    public static final Color BG_INPUT = new Color(0x1C, 0x1C, 0x20);

    public static final Color ACCENT_GOLD = new Color(0xE5, 0xA9, 0x3C);
    public static final Color ACCENT_GOLD_HOVER = new Color(0xF5, 0xBE, 0x58);
    public static final Color ACCENT_GOLD_MUTED = new Color(0x4A, 0x3A, 0x18);

    public static final Color TEXT_PRIMARY = new Color(0xF4, 0xF4, 0xF5);
    public static final Color TEXT_SECONDARY = new Color(0xA1, 0xA1, 0xAA);
    public static final Color TEXT_MUTED = new Color(0x71, 0x71, 0x7A);

    public static final Color BORDER_COLOR = new Color(0x2E, 0x2E, 0x34);
    public static final Color BORDER_FOCUS = new Color(0xE5, 0xA9, 0x3C);

    public static final Color COLOR_INCOME = new Color(0x10, 0xB9, 0x81); // Emerald
    public static final Color COLOR_EXPENSE = new Color(0xEF, 0x44, 0x44); // Coral Red
    public static final Color COLOR_WARNING = new Color(0xF5, 0x9E, 0x0B); // Amber
    public static final Color COLOR_INFO = new Color(0x3B, 0x82, 0xF6); // Blue

    // Fonts
    public static final Font FONT_TITLE_LARGE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_METRIC = new Font("Segoe UI", Font.BOLD, 24);

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$#,##0.00");

    private MonetraTheme() {
    }

    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) return "$0.00";
        return CURRENCY_FORMAT.format(amount);
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );
    }

    public static Border createInputPadding() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );
    }

    public static void applyGlobalTheme() {
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
            UIManager.put("Panel.background", BG_MAIN);
            UIManager.put("Viewport.background", BG_MAIN);
            UIManager.put("ScrollPane.background", BG_MAIN);
            UIManager.put("Table.background", BG_CARD);
            UIManager.put("Table.foreground", TEXT_PRIMARY);
            UIManager.put("Table.selectionBackground", ACCENT_GOLD_MUTED);
            UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
            UIManager.put("TableHeader.background", BG_SIDEBAR);
            UIManager.put("TableHeader.foreground", TEXT_SECONDARY);
            UIManager.put("TableHeader.font", FONT_BODY_BOLD);
        } catch (Exception ignored) {
        }
    }
}
