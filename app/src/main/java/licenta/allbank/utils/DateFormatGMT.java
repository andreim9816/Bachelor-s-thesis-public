package licenta.allbank.utils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateFormatGMT {
    private static SimpleDateFormat simpleDateFormat;
    private static final DateTimeFormatter dateFormatTransactionUI = DateTimeFormat.forPattern("dd/MM/yyyy");
    private static final DateTimeFormatter dateFormatTransactionBarChartLabelUI = DateTimeFormat.forPattern("dd/MM");
    private static final DateTimeFormatter dateFormatTransactionDB = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dateFormatFileName = DateTimeFormat.forPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter dateFormatTransactionDetails = DateTimeFormat.forPattern("dd MMM YYYY");

    public static String getCurrentTime() {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        return simpleDateFormat.format(new Date()) + " GMT";
    }

    public static String convertDateToString(DateTime date) {
        return date.toString(dateFormatTransactionUI);
    }

    public static String convertDateToStringBarChartLabel(DateTime date) {
        return date.toString(dateFormatTransactionBarChartLabelUI);
    }

    public static String convertDateToStringFileName(DateTime date) {
        return date.toString(dateFormatFileName);
    }

    public static String convertDateToStringTransactionDetails(DateTime date) {
        return date.toString(dateFormatTransactionDetails);
    }

    public static DateTime convertStringToDate(String stringDate) {
        DateTime dateTime = dateFormatTransactionDB.parseDateTime(stringDate);
        dateTime = dateTime.hourOfDay().setCopy(0);
        dateTime = dateTime.minuteOfDay().setCopy(0);
        dateTime = dateTime.secondOfDay().setCopy(0);
        dateTime = dateTime.millisOfDay().setCopy(0);
        return dateTime;
    }

    public static DateTime convertDataToDate(int day, int month, int year) {
        String dateString = year + "-" + month + "-" + day;
        return convertStringToDate(dateString);
    }

    public static DateTime getTodayDate() {
        DateTime now = DateTime.now();
        now = now.secondOfDay().setCopy(0);
        now = now.minuteOfDay().setCopy(0);
        now = now.hourOfDay().setCopy(0);
        now = now.millisOfDay().setCopy(0);
        return now;
    }

    public static DateTime getNextDay(DateTime dateTime) {
        return dateTime.plusDays(1);
    }

    public static DateTime getDatePastDays(int pastDays) {
        DateTime now = DateTime.now();
        now = now.secondOfDay().setCopy(0);
        now = now.minuteOfDay().setCopy(0);
        now = now.hourOfDay().setCopy(0);
        now = now.millisOfDay().setCopy(0);
        now = now.minusDays(pastDays);
        return now;
    }

    public static int daysDifference(DateTime dateTime1, DateTime dateTime2) {
        return Days.daysBetween(dateTime1, dateTime2).getDays() + 1;
    }

    public static int weeksDifference(DateTime dateTime1, DateTime dateTime2) {
        return Weeks.weeksBetween(dateTime1, dateTime2).getWeeks();
    }

    public static int monthsDifference(DateTime dateTime1, DateTime dateTime2) {
        return Months.monthsBetween(dateTime1, dateTime2).getMonths();
    }

    public static DateTime maxDate(DateTime dateTime1, DateTime dateTime2) {
        if (dateTime1.isBefore(dateTime2)) {
            return dateTime2;
        }
        return dateTime1;
    }

    public static DateTime minDate(DateTime dateTime1, DateTime dateTime2) {
        if (dateTime1.isBefore(dateTime2)) {
            return dateTime1;
        }
        return dateTime2;
    }
}

