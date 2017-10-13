/*
 * Copyright (c) 2016 GE. All Rights Reserved.
 * GE Confidential: Restricted Internal Distribution
 */
package com.ge.current.em.utils;

import java.util.Arrays;
import java.util.List;

public enum TimePeriod {
    YEAR_TO_DATE("YTD"),
    MONTH_TO_DATE("MTD"),
    TODAY("TODAY");

    private final String abbr;

    TimePeriod(String abbr) {
        this.abbr = abbr;
    }

    public String getAbbr() {
        return abbr;
    }

    public static List<TimePeriod> timePeriodsAsList() {
        return Arrays.asList(TimePeriod.values());
    }
}
