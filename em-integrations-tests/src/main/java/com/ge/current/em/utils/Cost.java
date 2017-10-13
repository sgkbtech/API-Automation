/*
 * Copyright (c) 2016 GE. All Rights Reserved.
 * GE Confidential: Restricted Internal Distribution
 */
package com.ge.current.em.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public enum Cost {
    HOURLY("HOURLY"),
    DAILY("DAILY"),
    MONTHLY("MONTHLY");

    private String abbr;

    Cost(String abbr) {
        this.abbr = abbr;
    }

    public String getAbbr() {
        return abbr;
    }

    public static List<Cost> costsAsList() {
        return Arrays.asList(Cost.values());
    }


}
