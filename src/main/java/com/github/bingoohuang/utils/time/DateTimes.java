package com.github.bingoohuang.utils.time;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimes {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 将timestamp转换为yyyy-MM-dd格式的字符串。
     *
     * @param dateTime DateTime
     * @return yyyy-MM-dd
     */
    public static String toDayString(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        return dateTime.toString(YYYY_MM_DD);
    }

    /**
     * 将timestamp转换为yyyy-MM-dd HH:mm:ss格式的字符串。
     *
     * @param dateTime DateTime
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String toDateTimeString(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        return dateTime.toString(YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 解析一个yyyy-MM-dd格式的字符串到DateTime
     *
     * @param dateString yyyy-MM-dd格式的字符串
     * @return DateTime
     */
    public static DateTime parseDateString(String dateString) {
        return DateTime.parse(dateString, DateTimeFormat.forPattern(YYYY_MM_DD));
    }

    public static DateTimeParser[] parsers = {
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser(), // 2018-08-08 14:30:00
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").getParser(),  // 2018-08-08 14:30
            DateTimeFormat.forPattern("yyyy.MM.dd HH:mm").getParser(),
            DateTimeFormat.forPattern("HH:mm:ss").getParser(),
            DateTimeFormat.forPattern("HH:mm").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd").getParser(),
            DateTimeFormat.forPattern("yyyy.MM.dd").getParser(),
            DateTimeFormat.forPattern("MM-dd").getParser(),
            DateTimeFormat.forPattern("yyyy-MM").getParser(),

    };
    static DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();


    public static DateTime parse(String day) {
        if (StringUtils.isEmpty(day)) return null;

        return formatter.parseDateTime(day);
    }

    public static DateTime parse(String day, String pattern) {
        try {
            return DateTime.parse(day, DateTimeFormat.forPattern(pattern));
        } catch (IllegalFieldValueException e) {
            // try to fix invalid 2017.02.29，将会解析到3月1日
            val sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(true);
            try {
                Date parsedDate = sdf.parse(day);
                return new DateTime(parsedDate);
            } catch (ParseException e1) {
                throw e;
            }
        }
    }

    public static DateTimeSpan parseDateSpan(DateSpanAware aw) {
        return parseDateSpan(aw.getStartDate(), aw.getEndDate());
    }

    public static DateTimeSpan parseDateSpan(String start, String end) {
        return new DateTimeSpan(parseDate(start), parseEndDay(end));
    }

    public static DateTimeSpan parseDateSpan(DateTime start, DateTime end) {
        return new DateTimeSpan(start, end);
    }

    public static DateTime parseDate(String day) {
        if (StringUtils.isEmpty(day)) return null;

        return DateTime.parse(day, DateTimeFormat.forPattern("yyyy-MM-dd"));
    }

    public static DateTime parseDateTime(String day) {
        if (StringUtils.isEmpty(day)) return null;

        return DateTime.parse(day, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static DateTime parseEndDay(String day) {
        if (StringUtils.isEmpty(day)) return null;

        return DateTime.parse(day, DateTimeFormat.forPattern("yyyy-MM-dd")).plusDays(1);
    }

    public static DateTime parseEndDay(DateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.plusDays(1);
    }

    public static String formatDate(DateTime day) {
        return day == null ? null : day.toString("yyyy-MM-dd");
    }

    /**
     * 将格式化的小时时间值转化为毫秒值
     *
     * @param hourStr 格式 HH:mm:ss(.SSS)
     * @return 毫秒值
     */
    public static int time2Mills(String hourStr) {
        String[] a = hourStr.split(":");
        return 1000 * 60 * 60 * Integer.valueOf(a[0]) + 1000 * 60 * Integer.valueOf(a[1]) + (a.length > 2 ? (int) (1000 * Float.valueOf(a[2])) : 0);
    }


    /**
     * 将timestamp转换为yyyy.MM.dd格式的字符串。
     *
     * @param day DateTime
     * @return yyyy.MM.dd
     */
    public static String formatDateDot(DateTime day) {
        return day == null ? null : day.toString("yyyy.MM.dd");
    }

    public static String displayEndDate(DateTime date, String pattern) {
        return date == null ? null : date.minusSeconds(1).toString(pattern);
    }

    public static String displayEndDate(DateTime date) {
        return displayEndDate(date, "yyyy-MM-dd");
    }

    public static String displayDateDot(DateTime date) {
        return date == null ? "" : date.toString("yyyy.MM.dd");
    }

    public static String displayEndDateDot(DateTime date) {
        return date == null ? "" : date.minusSeconds(1).toString("yyyy.MM.dd");
    }


    public static String formatDateTimeDot(DateTime time) {
        return time != null ? time.toString("yyyy.MM.dd HH:mm:ss") : null;
    }

    public static String formatDateHHmmDot(DateTime time) {
        return time != null ? time.toString("yyyy.MM.dd HH:mm") : null;
    }

    public static String formatDateTime(DateTime createTime) {
        return createTime != null ? createTime.toString("yyyy-MM-dd HH:mm:ss") : null;
    }

    public static String formatTimeHHmm(DateTime startTime) {
        return startTime != null ? startTime.toString("HH:mm") : null;
    }

    public static String formatTimeHHmmRange(DateTime startTime, DateTime endTime) {
        return formatTimeHHmm(startTime) + "-" + formatTimeHHmm(endTime);
    }

    public static DateTime parseTimeHHmm(String hhMM) {
        return StringUtils.isEmpty(hhMM) ? null : DateTime.parse(hhMM, DateTimeFormat.forPattern("HH:mm"));
    }

    public static boolean isCurrentMonth(DateTime createTime) {
        return DateTime.now().getMonthOfYear() == createTime.getMonthOfYear();
    }

    public static boolean isLastMonth(DateTime createTime) {
        return DateTime.now().getMonthOfYear() - 1 == createTime.getMonthOfYear();
    }

    public static boolean isPastYear(DateTime createTime) {
        return createTime.getYear() < DateTime.now().getYear();
    }

    public static boolean isCurrentYear(LocalDate toLocalDate) {
        return toLocalDate.getYear() == DateTime.now().getYear();
    }

    public static String formatToday() {
        return DateTime.now().toString("yyyy-MM-dd");
    }

    public static DateTime todayZero() {
        return DateTime.now().withTimeAtStartOfDay();
    }

    public static DateTime tomorrowZero() {
        return DateTime.now().withTimeAtStartOfDay().plusDays(1);
    }

    /**
     * 判断一个dateTime是否是明天
     *
     * @param dateTime 日期值
     * @return 是否是明天
     */
    public static boolean isTomorrow(DateTime dateTime) {
        return isDiffToToday(dateTime, 1);
    }

    /**
     * 判断一个dateTime是否是昨天
     *
     * @param dateTime 日期值
     * @return 是否是昨天
     */
    public static boolean isYesterday(DateTime dateTime) {
        return isDiffToToday(dateTime, -1);
    }

    public static boolean isToday(DateTime d) {
        return isDiffToToday(d, 0);
    }

    public static boolean isDiffToToday(DateTime dateTime, int diffDays) {
        return LocalDate.now().plusDays(diffDays).compareTo(new LocalDate(dateTime)) == 0;
    }


    public static Interval parseTimeHHmmInterval(String startTime, String endTime) {
        return new Interval(parseTimeHHmm(startTime), parseTimeHHmm(endTime));
    }


    public static String dayOfWeek(DateTime startTime) {
        return "周" + "零一二三四五六日".charAt(startTime.getDayOfWeek());
    }

    public static int daysBeforeTodayZero(DateTime startTime) {
        return Days.daysBetween(startTime, DateTime.now().withTimeAtStartOfDay()).getDays();
    }

    public static int daysAfterToday(DateTime endTime) {
        return Days.daysBetween(DateTime.now().withTimeAtStartOfDay(), endTime).getDays();
    }

    public static DateTime roundFloor(final DateTime dateTime, final int minutes) {
        if (minutes < 1 || 60 % minutes != 0) {
            throw new IllegalArgumentException("minutes must be a factor of 60");
        }

        val hour = dateTime.hourOfDay().roundFloorCopy();
        val millisSinceHour = new Duration(hour, dateTime).getMillis();
        val roundedMinutes = ((int) Math.round(millisSinceHour / 60000.0 / minutes)) * minutes;
        return hour.plusMinutes(roundedMinutes);
    }

    public static DateTime getFirstDayOfMonth(DateTime time) {
        return time.withDayOfMonth(1).withTimeAtStartOfDay();
    }

    public static DateTime getCurrentMonthFirstDay() {
        return getFirstDayOfMonth(DateTime.now());
    }

    public static DateTime getFirstDayOfWeek(DateTime time) {
        return time.withDayOfWeek(1).withTimeAtStartOfDay();
    }

    public static DateTime getCurrentWeekFirstDay() {
        return getFirstDayOfWeek(DateTime.now());
    }

    public static DateTime parseTimeHHmmDateTime(DateTime day, String timeStr) {
        String[] split = timeStr.split(":");
        return day.withTime(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0, 0);
    }

    public static DateTimeSpan thisMonthSpan() {
        return DateTimeSpan.of(getCurrentMonthFirstDay(), DateTimes.tomorrowZero());
    }

    public static DateTime max(DateTime a, DateTime b) {
        return a.isAfter(b) ? a : b;
    }

    public static boolean isBeforeToday(DateTime dateTime) {
        return dateTime != null && dateTime.isBefore(DateTimes.todayZero());
    }

    public static String getWeekChineseName(DateTime dt) {
        return "周" + "零一二三四五六日".charAt(dt.getDayOfWeek());
    }

    /**
     * 获取中国月份名字。
     *
     * @param monthIndex 月份索引（1-12）
     * @return 中国月份名字
     */
    public static String getMonthChineseName(int monthIndex) {
        return new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"}[monthIndex - 1] + "月";
    }

    // 判断两个DateTime是否是在同一天。
    public static boolean isSameDay(DateTime a, DateTime b) {
        return a != null && b != null && a.withTimeAtStartOfDay().isEqual(b.withTimeAtStartOfDay());
    }

    // 判断两个DateTime是否是不在同一天。
    public static boolean isDifferentDay(DateTime a, DateTime b) {
        return !isSameDay(a, b);
    }
}
