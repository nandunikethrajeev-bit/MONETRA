package com.monetra.core.dto;

public class FinancialInsightDto {
    public enum InsightType {
        POSITIVE,
        WARNING,
        DANGER,
        INFO
    }

    private String title;
    private String description;
    private InsightType type;
    private String metricHighlight;

    public FinancialInsightDto() {
    }

    public FinancialInsightDto(String title, String description, InsightType type, String metricHighlight) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.metricHighlight = metricHighlight;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InsightType getType() {
        return type;
    }

    public void setType(InsightType type) {
        this.type = type;
    }

    public String getMetricHighlight() {
        return metricHighlight;
    }

    public void setMetricHighlight(String metricHighlight) {
        this.metricHighlight = metricHighlight;
    }
}
