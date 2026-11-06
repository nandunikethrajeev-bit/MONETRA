package com.monetra.desktop.chart;

import com.monetra.core.dto.BudgetStatusDto;
import com.monetra.core.dto.CategorySpendingDto;
import com.monetra.core.dto.MonthlyTrendDto;
import com.monetra.desktop.theme.MonetraTheme;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public final class ChartFactoryHelper {

    private static final Color[] PALETTE = {
            new Color(0xF5, 0x9E, 0x0B), // Amber
            new Color(0x3B, 0x82, 0xF6), // Blue
            new Color(0x10, 0xB9, 0x81), // Emerald
            new Color(0xEC, 0x48, 0x99), // Pink
            new Color(0x8B, 0x5C, 0xF6), // Purple
            new Color(0xF4, 0x3F, 0x5E), // Rose
            new Color(0x06, 0xB6, 0xD4), // Cyan
            new Color(0x63, 0x66, 0xF1), // Indigo
            new Color(0x84, 0xCC, 0x16), // Lime
            new Color(0x64, 0x74, 0x8B)  // Slate
    };

    private ChartFactoryHelper() {
    }

    public static JFreeChart createIncomeExpenseBarChart(List<MonthlyTrendDto> trends) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (trends != null) {
            for (MonthlyTrendDto t : trends) {
                dataset.addValue(t.getIncome(), "Income", t.getMonthLabel());
                dataset.addValue(t.getExpense(), "Expense", t.getMonthLabel());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Income vs. Expenses",
                "Month",
                "Amount ($)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        applyDarkTheme(chart);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(MonetraTheme.BG_CARD);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinePaint(MonetraTheme.BORDER_COLOR);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, MonetraTheme.COLOR_INCOME);
        renderer.setSeriesPaint(1, MonetraTheme.COLOR_EXPENSE);
        renderer.setShadowVisible(false);

        return chart;
    }

    public static JFreeChart createCategoryPieChart(List<CategorySpendingDto> categories) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        if (categories != null) {
            for (CategorySpendingDto c : categories) {
                dataset.setValue(c.getCategoryName(), c.getAmount());
            }
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Category Spending Breakdown",
                dataset,
                true,
                true,
                false
        );

        applyDarkTheme(chart);

        @SuppressWarnings("unchecked")
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(MonetraTheme.BG_CARD);
        plot.setOutlinePaint(null);
        plot.setLabelBackgroundPaint(MonetraTheme.BG_SIDEBAR);
        plot.setLabelPaint(MonetraTheme.TEXT_PRIMARY);
        plot.setLabelFont(MonetraTheme.FONT_SMALL);
        plot.setShadowPaint(null);

        if (categories != null) {
            for (int i = 0; i < categories.size(); i++) {
                Color color = PALETTE[i % PALETTE.length];
                try {
                    String hex = categories.get(i).getCategoryColor();
                    if (hex != null && hex.startsWith("#")) {
                        color = Color.decode(hex);
                    }
                } catch (Exception ignored) {}
                plot.setSectionPaint(categories.get(i).getCategoryName(), color);
            }
        }

        return chart;
    }

    public static JFreeChart createSavingsTrendLineChart(List<MonthlyTrendDto> trends) {
        XYSeries series = new XYSeries("Net Savings");
        if (trends != null) {
            for (int i = 0; i < trends.size(); i++) {
                series.add(i + 1, trends.get(i).getNetSavings());
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Net Savings Trend",
                "Month Period",
                "Savings ($)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        applyDarkTheme(chart);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(MonetraTheme.BG_CARD);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinePaint(MonetraTheme.BORDER_COLOR);
        plot.setDomainGridlinePaint(MonetraTheme.BORDER_COLOR);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, MonetraTheme.ACCENT_GOLD);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);

        return chart;
    }

    public static JFreeChart createBudgetUtilizationBarChart(List<BudgetStatusDto> budgets) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (budgets != null) {
            for (BudgetStatusDto b : budgets) {
                dataset.addValue(b.getBudgetAmount(), "Budget Limit", b.getName());
                dataset.addValue(b.getSpentAmount(), "Actual Spent", b.getName());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Budget vs. Actual Spending",
                "Budget Target",
                "Amount ($)",
                dataset,
                PlotOrientation.HORIZONTAL,
                true,
                true,
                false
        );

        applyDarkTheme(chart);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(MonetraTheme.BG_CARD);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinePaint(MonetraTheme.BORDER_COLOR);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(0x63, 0x66, 0xF1)); // Limit in Indigo
        renderer.setSeriesPaint(1, MonetraTheme.ACCENT_GOLD);      // Spent in Gold
        renderer.setShadowVisible(false);

        return chart;
    }

    private static void applyDarkTheme(JFreeChart chart) {
        chart.setBackgroundPaint(MonetraTheme.BG_CARD);

        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setPaint(MonetraTheme.TEXT_PRIMARY);
            title.setFont(MonetraTheme.FONT_TITLE);
        }

        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setBackgroundPaint(MonetraTheme.BG_CARD);
            legend.setItemPaint(MonetraTheme.TEXT_SECONDARY);
            legend.setItemFont(MonetraTheme.FONT_SMALL);
            legend.setFrame(BlockBorder.NONE);
        }

        if (chart.getPlot() instanceof CategoryPlot cp) {
            CategoryAxis domainAxis = cp.getDomainAxis();
            domainAxis.setTickLabelPaint(MonetraTheme.TEXT_SECONDARY);
            domainAxis.setTickLabelFont(MonetraTheme.FONT_SMALL);
            domainAxis.setLabelPaint(MonetraTheme.TEXT_MUTED);
            domainAxis.setLabelFont(MonetraTheme.FONT_BODY);

            NumberAxis rangeAxis = (NumberAxis) cp.getRangeAxis();
            rangeAxis.setTickLabelPaint(MonetraTheme.TEXT_SECONDARY);
            rangeAxis.setTickLabelFont(MonetraTheme.FONT_SMALL);
            rangeAxis.setLabelPaint(MonetraTheme.TEXT_MUTED);
            rangeAxis.setLabelFont(MonetraTheme.FONT_BODY);
            rangeAxis.setNumberFormatOverride(new DecimalFormat("$#,##0"));
        } else if (chart.getPlot() instanceof XYPlot xyp) {
            xyp.getDomainAxis().setTickLabelPaint(MonetraTheme.TEXT_SECONDARY);
            xyp.getDomainAxis().setLabelPaint(MonetraTheme.TEXT_MUTED);
            NumberAxis rangeAxis = (NumberAxis) xyp.getRangeAxis();
            rangeAxis.setTickLabelPaint(MonetraTheme.TEXT_SECONDARY);
            rangeAxis.setLabelPaint(MonetraTheme.TEXT_MUTED);
            rangeAxis.setNumberFormatOverride(new DecimalFormat("$#,##0"));
        }
    }
}

