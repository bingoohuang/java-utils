package com.github.bingoohuang.utils.cron;


import com.github.bingoohuang.utils.cron.CronExpression.CronFieldType;
import com.github.bingoohuang.utils.cron.CronExpression.DayOfMonthField;
import com.github.bingoohuang.utils.cron.CronExpression.DayOfWeekField;
import com.github.bingoohuang.utils.cron.CronExpression.SimpleField;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class CronExpressionTest {
    DateTimeZone original;

    @Before
    public void setUp() {
        original = DateTimeZone.getDefault();
        DateTimeZone.setDefault(DateTimeZone.forID("Europe/Oslo"));
    }

    @After
    public void tearDown() {
        DateTimeZone.setDefault(original);
    }

    @Test
    public void daily() {
        CronExpression cron = CronAlias.create("@daily");
        DateTime dt = DateTime.parse("2018-10-30T20:07:23");
        DateTime next = cron.nextTimeAfter(dt);
        Assertions.assertThat(next).isEqualTo(DateTime.parse("2018-10-31T00:00:00"));
    }

    @Test
    public void shall_parse_number() {
        SimpleField field = new SimpleField(CronFieldType.MINUTE, "5");
        assertPossibleValues(field, 5);
    }

    private void assertPossibleValues(SimpleField field, Integer... values) {
        Set<Integer> valid = values == null ? new HashSet<Integer>() : new HashSet<>(Arrays.asList(values));
        for (int i = field.fieldType.from; i <= field.fieldType.to; i++) {
            String errorText = i + ":" + valid;
            if (valid.contains(i)) {
                Assertions.assertThat(field.matches(i)).as(errorText).isTrue();
            } else {
                Assertions.assertThat(field.matches(i)).as(errorText).isFalse();
            }
        }
    }

    @Test
    public void shall_parse_number_with_increment() {
        SimpleField field = new SimpleField(CronFieldType.MINUTE, "0/15");
        assertPossibleValues(field, 0, 15, 30, 45);
    }

    @Test
    public void shall_parse_range() {
        SimpleField field = new SimpleField(CronFieldType.MINUTE, "5-10");
        assertPossibleValues(field, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void shall_parse_range_with_increment() {
        SimpleField field = new SimpleField(CronFieldType.MINUTE, "20-30/2");
        assertPossibleValues(field, 20, 22, 24, 26, 28, 30);
    }

    @Test
    public void shall_parse_asterix() {
        SimpleField field = new SimpleField(CronFieldType.DAY_OF_WEEK, "*");
        assertPossibleValues(field, 1, 2, 3, 4, 5, 6, 7);
    }

    @Test
    public void shall_parse_asterix_with_increment() {
        SimpleField field = new SimpleField(CronFieldType.DAY_OF_WEEK, "*/2");
        assertPossibleValues(field, 1, 3, 5, 7);
    }

    @Test
    public void shall_ignore_field_in_day_of_week() {
        DayOfWeekField field = new DayOfWeekField("?");
        Assertions.assertThat(field.matches(new LocalDate())).isTrue();
    }

    @Test
    public void shall_ignore_field_in_day_of_month() {
        DayOfMonthField field = new DayOfMonthField("?");
        Assertions.assertThat(field.matches(new LocalDate())).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shall_give_error_if_invalid_count_field() {
        new CronExpression("* 3 *");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shall_give_error_if_minute_field_ignored() {
        SimpleField field = new SimpleField(CronFieldType.MINUTE, "?");
        field.matches(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shall_give_error_if_hour_field_ignored() {
        SimpleField field = new SimpleField(CronFieldType.HOUR, "?");
        field.matches(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shall_give_error_if_month_field_ignored() {
        SimpleField field = new SimpleField(CronFieldType.MONTH, "?");
        field.matches(1);
    }

    @Test
    public void shall_give_last_day_of_month_in_leapyear() {
        DayOfMonthField field = new DayOfMonthField("L");
        Assertions.assertThat(field.matches(new LocalDate(2012, 02, 29))).isTrue();
    }

    @Test
    public void shall_give_last_day_of_month() {
        DayOfMonthField field = new DayOfMonthField("L");
        Assertions.assertThat(field.matches(new LocalDate().withDayOfMonth(new LocalDate().dayOfMonth().getMaximumValue()))).isTrue();
    }

    @Test
    public void check_all() {
        Assertions.assertThat(new CronExpression("* * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 01))).isEqualTo(new DateTime(2012, 4, 10, 13, 00, 02));
        Assertions.assertThat(new CronExpression("* * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 02))).isEqualTo(new DateTime(2012, 4, 10, 13, 02, 01));
        Assertions.assertThat(new CronExpression("* * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 59, 59))).isEqualTo(new DateTime(2012, 4, 10, 14, 00));
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_invalid_input() {
        new CronExpression(null);
    }

    @Test
    public void check_second_number() {
        Assertions.assertThat(new CronExpression("3 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 01))).isEqualTo(new DateTime(2012, 4, 10, 13, 01, 03));
        Assertions.assertThat(new CronExpression("3 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 01, 03))).isEqualTo(new DateTime(2012, 4, 10, 13, 02, 03));
        Assertions.assertThat(new CronExpression("3 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 59, 03))).isEqualTo(new DateTime(2012, 4, 10, 14, 00, 03));
        Assertions.assertThat(new CronExpression("3 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 23, 59, 03))).isEqualTo(new DateTime(2012, 4, 11, 00, 00, 03));
        Assertions.assertThat(new CronExpression("3 * * * * *").nextTimeAfter(new DateTime(2012, 4, 30, 23, 59, 03))).isEqualTo(new DateTime(2012, 5, 01, 00, 00, 03));
    }

    @Test
    public void check_second_increment() {
        Assertions.assertThat(new CronExpression("5/15 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 4, 10, 13, 00, 05));
        Assertions.assertThat(new CronExpression("5/15 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 05))).isEqualTo(new DateTime(2012, 4, 10, 13, 00, 20));
        Assertions.assertThat(new CronExpression("5/15 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 20))).isEqualTo(new DateTime(2012, 4, 10, 13, 00, 35));
        Assertions.assertThat(new CronExpression("5/15 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 35))).isEqualTo(new DateTime(2012, 4, 10, 13, 00, 50));
        Assertions.assertThat(new CronExpression("5/15 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 50))).isEqualTo(new DateTime(2012, 4, 10, 13, 01, 05));

        // if rolling over minute then reset second (cron rules - increment affects only values in own field)
        Assertions.assertThat(new CronExpression("10/100 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 50)))
                .isEqualTo(new DateTime(2012, 4, 10, 13, 01, 10));
        Assertions.assertThat(new CronExpression("10/100 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 01, 10)))
                .isEqualTo(new DateTime(2012, 4, 10, 13, 02, 10));
    }

    @Test
    public void check_second_list() {
        Assertions.assertThat(new CronExpression("7,19 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 4, 10, 13, 00, 07));
        Assertions.assertThat(new CronExpression("7,19 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 07))).isEqualTo(new DateTime(2012, 4, 10, 13, 00, 19));
        Assertions.assertThat(new CronExpression("7,19 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 19))).isEqualTo(new DateTime(2012, 4, 10, 13, 01, 07));
    }

    @Test
    public void check_second_range() {
        Assertions.assertThat(new CronExpression("42-45 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 4, 10, 13, 00, 42));
        Assertions.assertThat(new CronExpression("42-45 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 42))).isEqualTo(new DateTime(2012, 4, 10, 13, 00, 43));
        Assertions.assertThat(new CronExpression("42-45 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 43))).isEqualTo(new DateTime(2012, 4, 10, 13, 00, 44));
        Assertions.assertThat(new CronExpression("42-45 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 44))).isEqualTo(new DateTime(2012, 4, 10, 13, 00, 45));
        Assertions.assertThat(new CronExpression("42-45 * * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00, 45))).isEqualTo(new DateTime(2012, 4, 10, 13, 01, 42));
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_second_invalid_range() {
        new CronExpression("42-63 * * * * *");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_second_invalid_increment_modifier() {
        new CronExpression("42#3 * * * * *");
    }

    @Test
    public void check_minute_number() {
        Assertions.assertThat(new CronExpression("0 3 * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 01))).isEqualTo(new DateTime(2012, 4, 10, 13, 03));
        Assertions.assertThat(new CronExpression("0 3 * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 03))).isEqualTo(new DateTime(2012, 4, 10, 14, 03));
    }

    @Test
    public void check_minute_increment() {
        Assertions.assertThat(new CronExpression("0 0/15 * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 4, 10, 13, 15));
        Assertions.assertThat(new CronExpression("0 0/15 * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 15))).isEqualTo(new DateTime(2012, 4, 10, 13, 30));
        Assertions.assertThat(new CronExpression("0 0/15 * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 30))).isEqualTo(new DateTime(2012, 4, 10, 13, 45));
        Assertions.assertThat(new CronExpression("0 0/15 * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 45))).isEqualTo(new DateTime(2012, 4, 10, 14, 00));
    }

    @Test
    public void check_minute_list() {
        Assertions.assertThat(new CronExpression("0 7,19 * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 4, 10, 13, 07));
        Assertions.assertThat(new CronExpression("0 7,19 * * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 07))).isEqualTo(new DateTime(2012, 4, 10, 13, 19));
    }

    @Test
    public void check_hour_number() {
        Assertions.assertThat(new CronExpression("0 * 3 * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 01))).isEqualTo(new DateTime(2012, 4, 11, 03, 00));
        Assertions.assertThat(new CronExpression("0 * 3 * * *").nextTimeAfter(new DateTime(2012, 4, 11, 03, 00))).isEqualTo(new DateTime(2012, 4, 11, 03, 01));
        Assertions.assertThat(new CronExpression("0 * 3 * * *").nextTimeAfter(new DateTime(2012, 4, 11, 03, 59))).isEqualTo(new DateTime(2012, 4, 12, 03, 00));
    }

    @Test
    public void check_hour_increment() {
        Assertions.assertThat(new CronExpression("0 * 0/15 * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 4, 10, 15, 00));
        Assertions.assertThat(new CronExpression("0 * 0/15 * * *").nextTimeAfter(new DateTime(2012, 4, 10, 15, 00))).isEqualTo(new DateTime(2012, 4, 10, 15, 01));
        Assertions.assertThat(new CronExpression("0 * 0/15 * * *").nextTimeAfter(new DateTime(2012, 4, 10, 15, 59))).isEqualTo(new DateTime(2012, 4, 11, 00, 00));
        Assertions.assertThat(new CronExpression("0 * 0/15 * * *").nextTimeAfter(new DateTime(2012, 4, 11, 00, 00))).isEqualTo(new DateTime(2012, 4, 11, 00, 01));
        Assertions.assertThat(new CronExpression("0 * 0/15 * * *").nextTimeAfter(new DateTime(2012, 4, 11, 15, 00))).isEqualTo(new DateTime(2012, 4, 11, 15, 01));
    }

    @Test
    public void check_hour_list() {
        Assertions.assertThat(new CronExpression("0 * 7,19 * * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 4, 10, 19, 00));
        Assertions.assertThat(new CronExpression("0 * 7,19 * * *").nextTimeAfter(new DateTime(2012, 4, 10, 19, 00))).isEqualTo(new DateTime(2012, 4, 10, 19, 01));
        Assertions.assertThat(new CronExpression("0 * 7,19 * * *").nextTimeAfter(new DateTime(2012, 4, 10, 19, 59))).isEqualTo(new DateTime(2012, 4, 11, 07, 00));
    }

    @Test
    public void check_hour_shall_run_25_times_in_DST_change_to_wintertime() {
        CronExpression cron = new CronExpression("0 1 * * * *");
        DateTime start = new DateTime(2011, 10, 30, 0, 0, 0, 0);
        DateTime slutt = start.toLocalDate().plusDays(1).toDateTimeAtStartOfDay();
        DateTime tid = start;
        Assertions.assertThat(Hours.hoursBetween(start, slutt).getHours()).isEqualTo(25);
        int count = 0;
        DateTime lastTime = tid;
        while (tid.isBefore(slutt)) {
            DateTime nextTime = cron.nextTimeAfter(tid);
            Assertions.assertThat(nextTime.isAfter(lastTime)).isTrue();
            lastTime = nextTime;
            tid = tid.plusHours(1);
            count++;
        }
        Assertions.assertThat(count).isEqualTo(25);
    }

    @Test
    public void check_hour_shall_run_23_times_in_DST_change_to_summertime() {
        CronExpression cron = new CronExpression("0 0 * * * *");
        DateTime start = new DateTime(2011, 03, 27, 0, 0, 0, 0);
        DateTime slutt = start.toLocalDate().plusDays(1).toDateTimeAtStartOfDay();
        DateTime tid = start;
        Assertions.assertThat(Hours.hoursBetween(start, slutt).getHours()).isEqualTo(23);
        int count = 0;
        DateTime lastTime = tid;
        while (tid.isBefore(slutt)) {
            DateTime nextTime = cron.nextTimeAfter(tid);
            Assertions.assertThat(nextTime.isAfter(lastTime)).isTrue();
            lastTime = nextTime;
            tid = tid.plusHours(1);
            count++;
        }
        Assertions.assertThat(count).isEqualTo(23);
    }

    @Test
    public void check_dayOfMonth_number() {
        Assertions.assertThat(new CronExpression("0 * * 3 * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 5, 03, 00, 00));
        Assertions.assertThat(new CronExpression("0 * * 3 * *").nextTimeAfter(new DateTime(2012, 5, 03, 00, 00))).isEqualTo(new DateTime(2012, 5, 03, 00, 01));
        Assertions.assertThat(new CronExpression("0 * * 3 * *").nextTimeAfter(new DateTime(2012, 5, 03, 00, 59))).isEqualTo(new DateTime(2012, 5, 03, 01, 00));
        Assertions.assertThat(new CronExpression("0 * * 3 * *").nextTimeAfter(new DateTime(2012, 5, 03, 23, 59))).isEqualTo(new DateTime(2012, 6, 03, 00, 00));
    }

    @Test
    public void check_dayOfMonth_increment() {
        Assertions.assertThat(new CronExpression("0 0 0 1/15 * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 4, 16, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 1/15 * *").nextTimeAfter(new DateTime(2012, 4, 16, 00, 00))).isEqualTo(new DateTime(2012, 5, 01, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 1/15 * *").nextTimeAfter(new DateTime(2012, 4, 30, 00, 00))).isEqualTo(new DateTime(2012, 5, 01, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 1/15 * *").nextTimeAfter(new DateTime(2012, 5, 01, 00, 00))).isEqualTo(new DateTime(2012, 5, 16, 00, 00));
    }

    @Test
    public void check_dayOfMonth_list() {
        Assertions.assertThat(new CronExpression("0 0 0 7,19 * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 4, 19, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 7,19 * *").nextTimeAfter(new DateTime(2012, 4, 19, 00, 00))).isEqualTo(new DateTime(2012, 5, 07, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 7,19 * *").nextTimeAfter(new DateTime(2012, 5, 07, 00, 00))).isEqualTo(new DateTime(2012, 5, 19, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 7,19 * *").nextTimeAfter(new DateTime(2012, 5, 30, 00, 00))).isEqualTo(new DateTime(2012, 6, 07, 00, 00));
    }

    @Test
    public void check_dayOfMonth_last() {
        Assertions.assertThat(new CronExpression("0 0 0 L * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 4, 30, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 L * *").nextTimeAfter(new DateTime(2012, 2, 12, 00, 00))).isEqualTo(new DateTime(2012, 2, 29, 00, 00));
    }

    @Test
    public void check_dayOfMonth_number_last_L() {
        Assertions.assertThat(new CronExpression("0 0 0 3L * *").nextTimeAfter(new DateTime(2012, 4, 10, 13, 00))).isEqualTo(new DateTime(2012, 4, 30 - 3, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 3L * *").nextTimeAfter(new DateTime(2012, 2, 12, 00, 00))).isEqualTo(new DateTime(2012, 2, 29 - 3, 00, 00));
    }

    @Test
    public void check_dayOfMonth_closest_weekday_W() {
        // 9 - is weekday in may
        Assertions.assertThat(new CronExpression("0 0 0 9W * *").nextTimeAfter(new DateTime(2012, 5, 2, 00, 00))).isEqualTo(new DateTime(2012, 5, 9, 00, 00));

        // 9 - is weekday in may
        Assertions.assertThat(new CronExpression("0 0 0 9W * *").nextTimeAfter(new DateTime(2012, 5, 8, 00, 00))).isEqualTo(new DateTime(2012, 5, 9, 00, 00));

        // 9 - saturday, friday closest weekday in june
        Assertions.assertThat(new CronExpression("0 0 0 9W * *").nextTimeAfter(new DateTime(2012, 5, 9, 00, 00))).isEqualTo(new DateTime(2012, 6, 8, 00, 00));

        // 9 - sunday, monday closest weekday in september
        Assertions.assertThat(new CronExpression("0 0 0 9W * *").nextTimeAfter(new DateTime(2012, 9, 1, 00, 00))).isEqualTo(new DateTime(2012, 9, 10, 00, 00));
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_dayOfMonth_invalid_modifier() {
        new CronExpression("0 0 0 9X * *");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_dayOfMonth_invalid_increment_modifier() {
        new CronExpression("0 0 0 9#2 * *");
    }

    @Test
    public void check_month_number() {
        Assertions.assertThat(new CronExpression("0 0 0 1 5 *").nextTimeAfter(new DateTime(2012, 2, 12, 00, 00))).isEqualTo(new DateTime(2012, 5, 1, 00, 00));
    }

    @Test
    public void check_month_increment() {
        Assertions.assertThat(new CronExpression("0 0 0 1 5/2 *").nextTimeAfter(new DateTime(2012, 2, 12, 00, 00))).isEqualTo(new DateTime(2012, 5, 1, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 1 5/2 *").nextTimeAfter(new DateTime(2012, 5, 1, 00, 00))).isEqualTo(new DateTime(2012, 7, 1, 00, 00));

        // if rolling over year then reset month field (cron rules - increments only affect own field)
        Assertions.assertThat(new CronExpression("0 0 0 1 5/10 *").nextTimeAfter(new DateTime(2012, 5, 1, 00, 00))).isEqualTo(new DateTime(2013, 5, 1, 00, 00));
    }

    @Test
    public void check_month_list() {
        Assertions.assertThat(new CronExpression("0 0 0 1 3,7,12 *").nextTimeAfter(new DateTime(2012, 2, 12, 00, 00))).isEqualTo(new DateTime(2012, 3, 1, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 1 3,7,12 *").nextTimeAfter(new DateTime(2012, 3, 1, 00, 00))).isEqualTo(new DateTime(2012, 7, 1, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 1 3,7,12 *").nextTimeAfter(new DateTime(2012, 7, 1, 00, 00))).isEqualTo(new DateTime(2012, 12, 1, 00, 00));
    }

    @Test
    public void check_month_list_by_name() {
        Assertions.assertThat(new CronExpression("0 0 0 1 MAR,JUL,DEC *").nextTimeAfter(new DateTime(2012, 2, 12, 00, 00))).isEqualTo(new DateTime(2012, 3, 1, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 1 MAR,JUL,DEC *").nextTimeAfter(new DateTime(2012, 3, 1, 00, 00))).isEqualTo(new DateTime(2012, 7, 1, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 1 MAR,JUL,DEC *").nextTimeAfter(new DateTime(2012, 7, 1, 00, 00))).isEqualTo(new DateTime(2012, 12, 1, 00, 00));
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_month_invalid_modifier() {
        new CronExpression("0 0 0 1 ? *");
    }

    @Test
    public void check_dayOfWeek_number() {
        Assertions.assertThat(new CronExpression("0 0 0 * * 3").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 4, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 3").nextTimeAfter(new DateTime(2012, 4, 4, 00, 00))).isEqualTo(new DateTime(2012, 4, 11, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 3").nextTimeAfter(new DateTime(2012, 4, 12, 00, 00))).isEqualTo(new DateTime(2012, 4, 18, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 3").nextTimeAfter(new DateTime(2012, 4, 18, 00, 00))).isEqualTo(new DateTime(2012, 4, 25, 00, 00));
    }

    @Test
    public void check_dayOfWeek_increment() {
        Assertions.assertThat(new CronExpression("0 0 0 * * 3/2").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 4, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 3/2").nextTimeAfter(new DateTime(2012, 4, 4, 00, 00))).isEqualTo(new DateTime(2012, 4, 6, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 3/2").nextTimeAfter(new DateTime(2012, 4, 6, 00, 00))).isEqualTo(new DateTime(2012, 4, 8, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 3/2").nextTimeAfter(new DateTime(2012, 4, 8, 00, 00))).isEqualTo(new DateTime(2012, 4, 11, 00, 00));
    }

    @Test
    public void check_dayOfWeek_list() {
        Assertions.assertThat(new CronExpression("0 0 0 * * 1,5,7").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 2, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 1,5,7").nextTimeAfter(new DateTime(2012, 4, 2, 00, 00))).isEqualTo(new DateTime(2012, 4, 6, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 1,5,7").nextTimeAfter(new DateTime(2012, 4, 6, 00, 00))).isEqualTo(new DateTime(2012, 4, 8, 00, 00));
    }

    @Test
    public void check_dayOfWeek_list_by_name() {
        Assertions.assertThat(new CronExpression("0 0 0 * * MON,FRI,SUN").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 2, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * MON,FRI,SUN").nextTimeAfter(new DateTime(2012, 4, 2, 00, 00))).isEqualTo(new DateTime(2012, 4, 6, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * MON,FRI,SUN").nextTimeAfter(new DateTime(2012, 4, 6, 00, 00))).isEqualTo(new DateTime(2012, 4, 8, 00, 00));
    }

    @Test
    public void check_dayOfWeek_last_friday_in_month() {
        Assertions.assertThat(new CronExpression("0 0 0 * * 5L").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 27, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 5L").nextTimeAfter(new DateTime(2012, 4, 27, 00, 00))).isEqualTo(new DateTime(2012, 5, 25, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 5L").nextTimeAfter(new DateTime(2012, 2, 6, 00, 00))).isEqualTo(new DateTime(2012, 2, 24, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * FRIL").nextTimeAfter(new DateTime(2012, 2, 6, 00, 00))).isEqualTo(new DateTime(2012, 2, 24, 00, 00));
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_dayOfWeek_invalid_modifier() {
        new CronExpression("0 0 0 * * 5W");
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_dayOfWeek_invalid_increment_modifier() {
        new CronExpression("0 0 0 * * 5?3");
    }

    @Test
    public void check_dayOfWeek_shall_interpret_0_as_sunday() {
        Assertions.assertThat(new CronExpression("0 0 0 * * 0").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 8, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 0L").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 29, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 0#2").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 8, 00, 00));
    }

    @Test
    public void check_dayOfWeek_shall_interpret_7_as_sunday() {
        Assertions.assertThat(new CronExpression("0 0 0 * * 7").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 8, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 7L").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 29, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 7#2").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 8, 00, 00));
    }

    @Test
    public void check_dayOfWeek_nth_friday_in_month() {
        Assertions.assertThat(new CronExpression("0 0 0 * * 5#3").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 4, 20, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 5#3").nextTimeAfter(new DateTime(2012, 4, 20, 00, 00))).isEqualTo(new DateTime(2012, 5, 18, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 7#1").nextTimeAfter(new DateTime(2012, 3, 30, 00, 00))).isEqualTo(new DateTime(2012, 4, 1, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 7#1").nextTimeAfter(new DateTime(2012, 4, 1, 00, 00))).isEqualTo(new DateTime(2012, 5, 6, 00, 00));
        Assertions.assertThat(new CronExpression("0 0 0 * * 3#5").nextTimeAfter(new DateTime(2012, 2, 6, 00, 00))).isEqualTo(new DateTime(2012, 2, 29, 00, 00)); // leapday
        Assertions.assertThat(new CronExpression("0 0 0 * * WED#5").nextTimeAfter(new DateTime(2012, 2, 6, 00, 00))).isEqualTo(new DateTime(2012, 2, 29, 00, 00)); // leapday
    }

    @Test(expected = IllegalArgumentException.class)
    public void shall_not_not_support_rolling_period() {
        new CronExpression("* * 5-1 * * *");
    }

    @Test(expected = IllegalArgumentException.class)
    public void non_existing_date_throws_exception() {
        // Will check for the next 4 years - no 30th of February is found so a IAE is thrown.
        new CronExpression("* * * 30 2 *").nextTimeAfter(DateTime.now());
    }

    @Test
    public void test_default_barrier() {
        // the default barrier is 4 years - so leap years are considered.
        Assertions.assertThat(new CronExpression("* * * 29 2 *").nextTimeAfter(new DateTime(2012, 3, 1, 00, 00))).isEqualTo(new DateTime(2016, 2, 29, 00, 00));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_one_year_barrier() {
        // The next leap year is 2016, so an IllegalArgumentException is expected.
        new CronExpression("* * * 29 2 *").nextTimeAfter(new DateTime(2012, 3, 1, 00, 00), new DateTime(2013, 3, 1, 00, 00));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_two_year_barrier() {
        // The next leap year is 2016, so an IllegalArgumentException is expected.
        new CronExpression("* * * 29 2 *").nextTimeAfter(new DateTime(2012, 3, 1, 00, 00), 1000 * 60 * 60 * 24 * 356 * 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_seconds_specified_but_should_be_omitted() {
        CronExpression.createWithoutSeconds("* * * 29 2 *");
    }

    @Test
    public void test_without_seconds() {
        Assertions.assertThat(CronExpression.createWithoutSeconds("* * 29 2 *").nextTimeAfter(new DateTime(2012, 3, 1, 00, 00))).isEqualTo(new DateTime(2016, 2, 29, 00, 00));
    }
}