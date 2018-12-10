package com.github.bingoohuang.utils.time;

import lombok.Value;
import org.joda.time.DateTime;
import org.joda.time.Interval;

@Value
public class DateTimeSpan {
    private final DateTime start;
    private final DateTime end;

    private final Interval interval;

    public DateTimeSpan(DateTime start, DateTime end) {
        this.start = start;
        this.end = end;
        this.interval = new Interval(start, end);
    }

    public static DateTimeSpan of(DateTime start, DateTime end) {
        return new DateTimeSpan(start, end);
    }

    public static DateTimeSpan untilTomorrowZero(DateTime start) {
        return new DateTimeSpan(start, DateTimes.tomorrowZero());
    }

    public static DateTimeSpan pastDays(int days) {
        DateTime start = DateTime.now().withTimeAtStartOfDay();
        return new DateTimeSpan(start.minusDays(days), start);
    }

    public static DateTimeSpan today() {
        DateTime start = DateTime.now().withTimeAtStartOfDay();
        return new DateTimeSpan(start, start.plusDays(1));
    }


    @Override public String toString() {
        return "[" + start + ", " + end + ")";
    }
}
