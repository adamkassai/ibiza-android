package com.kassaiweb.ibiza.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    public static Calendar convert(String atomTime) {
        Date date = null;
        try {
            date = dateFormat.parse(atomTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar result = Calendar.getInstance();
        result.setTime(date);
        return result;
    }

    public static String convert(Calendar date) {
        return dateFormat.format(date.getTime());
    }

    /**
     * @return a String with a format of '2018-09-03 18:09:07'
     */
    public static String format(Calendar calendar) {
        String month, dayOfMonth, hourOfDay, minute, second;
        int iMonth = calendar.get(Calendar.MONTH) + 1;
        month = iMonth > 9 ? String.valueOf(iMonth) : '0' + String.valueOf(iMonth);
        int iDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        dayOfMonth = iDayOfMonth > 9 ? String.valueOf(iDayOfMonth) : '0' + String.valueOf(iDayOfMonth);
        int iHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        hourOfDay = iHourOfDay > 9 ? String.valueOf(iHourOfDay) : '0' + String.valueOf(iHourOfDay);
        int iMinute = calendar.get(Calendar.MINUTE);
        minute = iMinute > 9 ? String.valueOf(iMinute) : '0' + String.valueOf(iMinute);
        int iSecond = calendar.get(Calendar.SECOND);
        second = iSecond > 9 ? String.valueOf(iSecond) : '0' + String.valueOf(iSecond);

        StringBuilder builder = new StringBuilder();
        builder.append(calendar.get(Calendar.YEAR))
                .append("-")
                .append(month)
                .append("-")
                .append(dayOfMonth)
                .append(' ')
                .append(hourOfDay)
                .append(":")
                .append(minute)
                .append(":")
                .append(second);
        return builder.toString();
    }
}
