package com.ge.current.em.automation.util;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by 212554696 on 4/12/17.
 */
public class DateAndTimeUtil {

    private static final Log LOGGER = LogFactory.getLog(DateAndTimeUtil.class);

    public enum TimeUnit {
        MINUTES, HOURS, DAYS, MONTHS, YEARS
    }

    public static final String TIMESTAMP_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATE_FORMAT_WITH_TIME = "yyyyMMddHHmm";
    public static final String DATE_FORMAT_WO_TIME = "yyyyMMdd";
    public static final String TIME_FORMAT = "HHmm";

    public static String convertDateToString(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /**
     * Calculates the time difference of the given timeZone from UTC
     * @param timeZone
     * @return
     */
    public static int getHourDifferenceFromUTC(String timeZone) {
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        Calendar cal = Calendar.getInstance(tz);
        int offset = cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET);
        offset = offset / (1000 * 60 * 60);
        LOGGER.info("Time offset: " + offset);
        return offset;
    }

    /**
     * Add appropriate value to the given date and time based on the given unit.
     *
     * @param yyyyMMdd
     * @param hhmm
     * @param value value to be added
     * @param unit TimeUnit to be used
     * @return
     * @throws ParseException
     */
    public static Date getDateWithAddedValue(String yyyyMMdd, String hhmm, int value, TimeUnit unit)
            throws ParseException {
        SimpleDateFormat yyyyMMddHHmmFormat = new SimpleDateFormat(DATE_FORMAT_WITH_TIME);
        Date effectiveDate = yyyyMMddHHmmFormat.parse(yyyyMMdd + hhmm);
        return getDateWithAddedValue(effectiveDate, value, unit);
    }

    public static Date getDateWithAddedValue(String yyyyMMddHHmm, int value, TimeUnit unit) throws ParseException {
        SimpleDateFormat yyyyMMddHHmmFormat = new SimpleDateFormat(DATE_FORMAT_WITH_TIME);
        Date effectiveDate = yyyyMMddHHmmFormat.parse(yyyyMMddHHmm);
        return getDateWithAddedValue(effectiveDate, value, unit);
    }

    public static Date getDateWithAddedValue(Date date, int value, TimeUnit unit) {
        Date effectiveDate = new Date();
        switch (unit) {
        case MINUTES:
            effectiveDate = DateUtils.addMinutes(date, value);
            break;
        case HOURS:
            effectiveDate = DateUtils.addHours(date, value);
            break;
        case MONTHS:
            effectiveDate = DateUtils.addMonths(date, value);
            break;
        case YEARS:
            effectiveDate = DateUtils.addYears(date, value);
        default:
            break;
        }
        return  effectiveDate;
    }

    /**
     * Separate date into yyyyMMdd and HHmm
     * @param date
     * @return array[0]=yyyyMMdd, array[1]=HHmm
     */
    public static String[] separateDateAndTime(Date date) {
        String[] result = new String[2];

        SimpleDateFormat formatWithoutTime = new SimpleDateFormat(DateAndTimeUtil.DATE_FORMAT_WO_TIME);
        SimpleDateFormat formatJustTime = new SimpleDateFormat(DateAndTimeUtil.TIME_FORMAT);

        result[0] = formatWithoutTime.format(date);
        result[1] = formatJustTime.format(date);

        return result;
    }
}
