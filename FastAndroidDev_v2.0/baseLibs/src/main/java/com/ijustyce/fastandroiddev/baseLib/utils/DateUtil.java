package com.ijustyce.fastandroiddev.baseLib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final String[] days = {"前天", "昨天", "今天", "明天", "后天"};
    private static final String MONTH = "月";
    private static final String DAY = "日";
    private static final String HOUR = "时";
    private static final String MINUTE = "分";

    public static final String FORMATTER = "yyyy/MM/dd HH:mm:ss";
    private static final String DAY_FORMATTER = "yyyy/MM/dd";

    /**
     * get day count between date and now
     *
     * @param date date count
     */
    public static String parseDate(Date date) {

        long old = dateToTimesTamp(date);
        long now = dateToTimesTamp(getDate(DAY_FORMATTER));
        long time = (old - now) / (24 * 60 * 60);
        String day = "";

        switch ((int) time) {

            case -2:
                day = days[0];
                break;
            case -1:
                day = days[1];
                break;
            case 0:
                day = days[2];
                break;
            case 1:
                day = days[3];
                break;
            case 2:
                day = days[4];
                break;
            default:
                day = dateToString(date, "MM" + MONTH + "dd" + DAY);
                break;
        }

        return day + dateToString(date, " HH" + HOUR + "mm" + MINUTE);
    }

    /**
     * get current date like yyyy/MM/dd HH:mm:ss
     *
     * @param formatter like yyyy/MM/dd HH:mm:ss
     * @return
     */
    public static String getDateString(String formatter) {
        SimpleDateFormat ft = new SimpleDateFormat(formatter, Locale.CHINA);
        Date dd = new Date();
        return ft.format(dd);
    }

    /**
     * get current date like yyyy/MM/dd HH:mm:ss
     *
     * @param formatter like yyyy/MM/dd HH:mm:ss
     * @return
     */
    public static Date getDate(String formatter) {
        SimpleDateFormat ft = new SimpleDateFormat(formatter, Locale.CHINA);
        Date dd = new Date();
        return stringToDate(ft.format(dd), formatter);
    }

    /**
     * get current date
     *
     * @return current date formatted like yyyy/MM/dd HH:mm:ss
     */
    public static Date getDate() {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
        Date dd = new Date();
        return stringToDate(ft.format(dd), "yyyy/MM/dd HH:mm:ss");
    }

    /**
     * convert date to string
     *
     * @param date
     * @param formatter
     * @return String
     */
    public static String dateToString(Date date, String formatter) {
        SimpleDateFormat ft = new SimpleDateFormat(formatter, Locale.CHINA);
        return ft.format(date);
    }

    /**
     * get current timesTamp , date formatted like yyyy/MM/dd HH:mm:ss
     *
     * @return
     */
    public static Long getTimesTamp() {

        return dateToTimesTamp(getDate("yyyy/MM/dd HH:mm:ss"));
    }

    public static Long subTimeTamp(Date first, Date second) {

        return dateToTimesTamp(first) - dateToTimesTamp(second);
    }

    /**
     * get current timesTamp
     *
     * @param formatter like yyyy/MM/dd HH:mm:ss
     * @return timesTamp
     */
    public static Long getTimesTamp(String formatter) {

        return dateToTimesTamp(getDate(formatter));
    }

    /**
     * convert a date string to date
     *
     * @param value  value of date , like 2014/04/27 11:42:00
     * @param format like yyyy/MM/dd HH:mm:ss
     * @return date
     */

    public static Date stringToDate(String value, String format) {

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * convert a certain date to Unix timestamp
     *
     * @param date , like 2014-04-27 11:42:00
     * @return Unix timestamp
     */
    public static long dateToTimesTamp(Date date) {

        if (date == null) {
            return 0;
        }
        long l = date.getTime();
        String str = String.valueOf(l);
        return Long.parseLong(str.substring(0, 10));
    }

    /**
     * convert Unix timestamp to a certain date
     *
     * @param timesTamp Unix timestamp
     * @return date , like 2014-04-27 11:42:00
     */
    public static String timesTampToDate(String timesTamp, String format) {

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long lcc_time = Long.valueOf(timesTamp);
        return sdf.format(new Date(lcc_time * 1000L));
    }

    public static long stringToTimesTamp(String date, String format) {

        Date tmp = stringToDate(date, format);
        return dateToTimesTamp(tmp);
    }

    /**
     * system.out.println(text)
     *
     * @param text
     */
    public static void Jout(String text) {

        System.out.println(text);
    }
}
