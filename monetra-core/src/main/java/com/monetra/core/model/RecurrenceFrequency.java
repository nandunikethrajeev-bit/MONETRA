package com.monetra.core.model;

import java.time.LocalDate;

public enum RecurrenceFrequency {
    WEEKLY("Weekly") {
        @Override
        public LocalDate calculateNextDate(LocalDate currentDate) {
            return currentDate.plusWeeks(1);
        }
    },
    MONTHLY("Monthly") {
        @Override
        public LocalDate calculateNextDate(LocalDate currentDate) {
            return currentDate.plusMonths(1);
        }
    },
    YEARLY("Yearly") {
        @Override
        public LocalDate calculateNextDate(LocalDate currentDate) {
            return currentDate.plusYears(1);
        }
    };

    private final String displayName;

    RecurrenceFrequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public abstract LocalDate calculateNextDate(LocalDate currentDate);
}

