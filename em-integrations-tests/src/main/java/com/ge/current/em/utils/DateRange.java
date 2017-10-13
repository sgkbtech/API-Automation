/*
 * Copyright (c) 2016 GE. All Rights Reserved.
 * GE Confidential: Restricted Internal Distribution
 */
package com.ge.current.em.utils;

import java.util.Date;

//Did not add this

public class DateRange {
    private Date start;
    private Date end;

    public Date getStart() {
        return start;
    }

    public DateRange setStart(Date start) {
        this.start = start;
        return this;
    }

    public Date getEnd() {
        return end;
    }

    public DateRange setEnd(Date end) {
        this.end = end;
        return this;
    }

    public static final class DateRangeBuilder {
        private Date start;
        private Date end;

        private DateRangeBuilder() {
        }

        public static DateRangeBuilder aDateRange() {
            return new DateRangeBuilder();
        }

        public DateRangeBuilder withStart(Date start) {
            this.start = start;
            return this;
        }

        public DateRangeBuilder withEnd(Date end) {
            this.end = end;
            return this;
        }

        public DateRange build() {
            DateRange dateRange = new DateRange();
            dateRange.setStart(start);
            dateRange.setEnd(end);
            return dateRange;
        }
    }
}
