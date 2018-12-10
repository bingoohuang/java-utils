package com.github.bingoohuang.utils.cron;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.joda.time.DateTimeConstants.DAYS_PER_WEEK;

// https://github.com/frode-carlsen/cron/blob/master/jodatime/src/main/java/fc/cron/CronExpression.java

/**
 * This provides cron support for java6 upwards and jodatime.
 * <pre>
 * Parser for unix-like cron expressions: Cron expressions allow specifying combinations of criteria for time
 * such as: Each Monday-Friday at 08:00 or Every last friday of the month at 01:30
 * </pre>
 * A cron expressions consists of 5 or 6 mandatory fields (seconds may be omitted) separated by space. <br>
 * These are:
 * <pre>
 * +--------------------------+-----------------------------------------------+--------------------+
 * | Field                    | Allowable values                              | Special Characters |
 * +--------------------------+-----------------------------------------------+--------------------+
 * | Seconds (may be omitted) | 0-59                                          | , - * /            |
 * +--------------------------+-----------------------------------------------+--------------------+
 * | Minutes                  | 0-59                                          | , - * /            |
 * +--------------------------+-----------------------------------------------+--------------------+
 * | Hours                    | 0-23                                          | , - * /            |
 * +--------------------------+-----------------------------------------------+--------------------+
 * | Day of month             | 1-31                                          | , - * ? / L W      |
 * +--------------------------+-----------------------------------------------+--------------------+
 * | Month                    | 1-12 or JAN-DEC (note: english abbreviations) | , - * /            |
 * +--------------------------+-----------------------------------------------+--------------------+
 * | Day of week              | 1-7 or MON-SUN (note: english abbreviations)  | , - * ? / L #      |
 * +--------------------------+-----------------------------------------------+--------------------+
 * </pre>
 * <pre>
 * '*' Can be used in all fields and means 'for all values'. E.g. * in minutes, means 'for all minutes'
 * '?' Can be used in Day-of-month and Day-of-week fields. Used to signify 'no special value'. It is used when one want
 * to specify something for one of those two fields, but not the other.
 * '-' Used to specify a time interval. E.g. 10-12 in Hours field means 'for hours 10, 11 and 12'
 * ',' Used to specify multiple values for a field. E.g. MON,WED,FRI in Day-of-week field means for
 * monday, wednesday and friday
 * '/' Used to specify increments. E.g. 0/15 in Seconds field means for seconds 0, 15, 30, ad
 * 45. And 5/15 in seconds field means for seconds 5, 20, 35, and 50. If '*' s specified
 * before '/' it is the same as saying it starts at 0. For every field there's a list of values that can be turned on or
 * off. For Seconds and Minutes these range from 0-59. For Hours from 0 to 23, For Day-of-month it's 1 to 31, For Months
 * 1 to 12. / character helsp turn some of these values back on. Thus 7/6 in Months field
 * specify just Month 7. It doesn't turn on every 6 month following, since cron fields never roll over
 * 'L' Can be used on Day-of-month and Day-of-week fields. It signifies last day of the set of allowed values. In
 * Day-of-month field it's the last day of the month (e.g.. 31 jan, 28 feb (29 in leap years), 31 march, etc.). In
 * Day-of-week field it's Sunday. If there's a prefix, this will be subtracted (5L in Day-of-month means 5 days before
 * last day of Month: 26 jan, 23 feb, etc.)
 * 'W' Can be specified in Day-of-Month field. It specifies closest weekday (monday-friday). Holidays are not accounted
 * for. 15W in Day-of-Month field means 'closest weekday to 15 i in given month'. If the 15th is a Saturday,
 * it gives Friday. If 15th is a Sunday, the it gives following Monday.
 * '#' Can be used in Day-of-Week field. For example: 5#3 means 'third friday in month' (day 5 = friday, #3
 * - the third). If the day does not exist (e.g. 5#5 - 5th friday of month) and there aren't 5 fridays in
 * the month, then it won't match until the next month with 5 fridays.
 *
 * Case-sensitive: No fields are case-sensitive
 * Dependencies between fields: Fields are always evaluated independently, but the expression doesn't match until
 * the constraints of each field are met. Overlap of intervals are not allowed. That is: for
 * Day-of-week field FRI-MON is invalid,but FRI-SUN,MON is valid
 * </pre>
 */
public class CronExpression {
    @RequiredArgsConstructor
    enum CronFieldType {
        SECOND(0, 59, null),
        MINUTE(0, 59, null),
        HOUR(0, 23, null),
        DAY_OF_MONTH(1, 31, null),
        MONTH(1, 12, Arrays.asList("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")),
        DAY_OF_WEEK(1, 7, Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"));

        final int from, to;
        final List<String> names;
    }

    private final String expr;
    private final SimpleField secondField;
    private final SimpleField minuteField;
    private final SimpleField hourField;
    private final DayOfWeekField dayOfWeekField;
    private final SimpleField monthField;
    private final DayOfMonthField dayOfMonthField;

    public CronExpression(final String expr) {
        this(expr, true);
    }

    public CronExpression(String expr, final boolean withSeconds) {
        if (StringUtils.isEmpty(expr)) {
            throw new IllegalArgumentException("expr is empty"); //$NON-NLS-1$
        }


        this.expr = expr;

        final int expectedParts = withSeconds ? 6 : 5;
        final String[] parts = expr.split("\\s+"); //$NON-NLS-1$
        if (parts.length != expectedParts) {
            throw new IllegalArgumentException(String.format("Invalid cron expression [%s], expected %s felt, got %s"
                    , expr, expectedParts, parts.length));
        }

        int ix = withSeconds ? 1 : 0;
        this.secondField = new SimpleField(CronFieldType.SECOND, withSeconds ? parts[0] : "0");
        this.minuteField = new SimpleField(CronFieldType.MINUTE, parts[ix++]);
        this.hourField = new SimpleField(CronFieldType.HOUR, parts[ix++]);
        this.dayOfMonthField = new DayOfMonthField(parts[ix++]);
        this.monthField = new SimpleField(CronFieldType.MONTH, parts[ix++]);
        this.dayOfWeekField = new DayOfWeekField(parts[ix++]);
    }

    public static CronExpression create(final String expr) {
        return new CronExpression(expr, true);
    }

    public static CronExpression createWithoutSeconds(final String expr) {
        return new CronExpression(expr, false);
    }

    public DateTime nextTimeAfter(DateTime afterTime) {
        // will search for the next time within the next 4 years. If there is no
        // time matching, an InvalidArgumentException will be thrown (it is very
        // likely that the cron expression is invalid, like the February 30th).
        return nextTimeAfter(afterTime, afterTime.plusYears(4));
    }

    public DateTime nextTimeAfter(DateTime afterTime, long durationInMillis) {
        // will search for the next time within the next durationInMillis
        // millisecond. Be aware that the duration is specified in millis,
        // but in fact the limit is checked on a day-to-day basis.
        return nextTimeAfter(afterTime, afterTime.plus(durationInMillis));
    }

    public DateTime nextTimeAfter(DateTime afterTime, DateTime dateTimeBarrier) {
        MutableDateTime nextTime = new MutableDateTime(afterTime);
        nextTime.setMillisOfSecond(0);
        nextTime.secondOfDay().add(1);

        while (true) { // day of week
            while (true) { // month
                while (true) { // day of month
                    while (true) { // hour
                        while (true) { // minute
                            while (true) { // second
                                if (secondField.matches(nextTime.getSecondOfMinute())) {
                                    break;
                                }
                                nextTime.secondOfDay().add(1);
                            }
                            if (minuteField.matches(nextTime.getMinuteOfHour())) {
                                break;
                            }
                            nextTime.minuteOfDay().add(1);
                            nextTime.secondOfMinute().set(0);
                        }
                        if (hourField.matches(nextTime.getHourOfDay())) {
                            break;
                        }
                        nextTime.hourOfDay().add(1);
                        nextTime.minuteOfHour().set(0);
                        nextTime.secondOfMinute().set(0);
                    }
                    if (dayOfMonthField.matches(new LocalDate(nextTime))) {
                        break;
                    }
                    nextTime.addDays(1);
                    nextTime.setTime(0, 0, 0, 0);
                    checkIfDateTimeBarrierIsReached(nextTime, dateTimeBarrier);
                }
                if (monthField.matches(nextTime.getMonthOfYear())) {
                    break;
                }
                nextTime.addMonths(1);
                nextTime.setDayOfMonth(1);
                nextTime.setTime(0, 0, 0, 0);
                checkIfDateTimeBarrierIsReached(nextTime, dateTimeBarrier);
            }
            if (dayOfWeekField.matches(new LocalDate(nextTime))) {
                break;
            }
            nextTime.addDays(1);
            nextTime.setTime(0, 0, 0, 0);
            checkIfDateTimeBarrierIsReached(nextTime, dateTimeBarrier);
        }

        return nextTime.toDateTime();
    }

    private static void checkIfDateTimeBarrierIsReached(MutableDateTime nextTime, DateTime dateTimeBarrier) {
        if (nextTime.isAfter(dateTimeBarrier)) {
            throw new IllegalArgumentException("No next execution time could be determined that is before the limit of " + dateTimeBarrier);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + expr + ">";
    }

    static class FieldPart {
        private Integer from, to, increment;
        private String modifier, incrementModifier;
    }

    abstract static class BasicField {
        private static final Pattern CRON_FIELD_REGEXP = Pattern
                .compile("(?:                                             # start of group 1\n"
                                + "   (?:(\\*)|(\\?)|(L))  # global flag (L, ?, *)\n"
                                + " | ([0-9]{1,2}|[a-z]{3,3})              # or start number or symbol\n"
                                + "      (?:                                        # start of group 2\n"
                                + "         (L|W)                             # modifier (L,W)\n"
                                + "       | -([0-9]{1,2}|[a-z]{3,3})        # or end number or symbol (in range)\n"
                                + "      )?                                         # end of group 2\n"
                                + ")                                              # end of group 1\n"
                                + "(?:(/|\\#)([0-9]{1,7}))?        # increment and increment modifier (/ or \\#)\n"
                        , Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);

        final CronFieldType fieldType;
        final List<FieldPart> parts = new ArrayList<>();

        private BasicField(CronFieldType fieldType, String fieldExpr) {
            this.fieldType = fieldType;
            parse(fieldExpr);
        }

        private void parse(String fieldExpr) { // NOSONAR
            String[] rangeParts = fieldExpr.split(",");
            for (String rangePart : rangeParts) {
                Matcher m = CRON_FIELD_REGEXP.matcher(rangePart);
                if (!m.matches()) {
                    throw new IllegalArgumentException("Invalid cron field '" + rangePart + "' for field [" + fieldType + "]");
                }
                String startNumber = m.group(4);
                String modifier = m.group(5);
                String endNumber = m.group(6);
                String incrementModifier = m.group(7);
                String increment = m.group(8);

                FieldPart part = new FieldPart();
                part.increment = 999;
                if (startNumber != null) {
                    part.from = mapValue(startNumber);
                    part.modifier = modifier;
                    if (endNumber != null) {
                        part.to = mapValue(endNumber);
                        part.increment = 1;
                    } else if (increment != null) {
                        part.to = fieldType.to;
                    } else {
                        part.to = part.from;
                    }
                } else if (m.group(1) != null) {
                    part.from = fieldType.from;
                    part.to = fieldType.to;
                    part.increment = 1;
                } else if (m.group(2) != null) {
                    part.modifier = m.group(2);
                } else if (m.group(3) != null) {
                    part.modifier = m.group(3);
                } else {
                    throw new IllegalArgumentException("Invalid cron part: " + rangePart);
                }

                if (increment != null) {
                    part.incrementModifier = incrementModifier;
                    part.increment = Integer.valueOf(increment);
                }

                validateRange(part);
                validatePart(part);
                parts.add(part);

            }
        }

        protected void validatePart(FieldPart part) {
            if (part.modifier != null) {
                throw new IllegalArgumentException(String.format("Invalid modifier [%s]", part.modifier));
            } else if (part.incrementModifier != null && !"/".equals(part.incrementModifier)) {
                throw new IllegalArgumentException(String.format("Invalid increment modifier [%s]", part.incrementModifier));
            }
        }

        private void validateRange(FieldPart part) {
            if ((part.from != null && part.from < fieldType.from) || (part.to != null && part.to > fieldType.to)) {
                throw new IllegalArgumentException(String.format("Invalid interval [%s-%s], must be %s<=_<=%s", part.from, part.to, fieldType.from,
                        fieldType.to));
            } else if (part.from != null && part.to != null && part.from > part.to) {
                throw new IllegalArgumentException(
                        String.format(
                                "Invalid interval [%s-%s].  Rolling periods are not supported (ex. 5-1, only 1-5) since this won't give a deterministic result. Must be %s<=_<=%s",
                                part.from, part.to, fieldType.from, fieldType.to));
            }
        }

        protected Integer mapValue(String value) {
            Integer idx;
            if (fieldType.names != null && (idx = fieldType.names.indexOf(value.toUpperCase(Locale.getDefault()))) >= 0) {
                return idx + 1;
            }
            return Integer.valueOf(value);
        }

        protected boolean matches(int val, FieldPart part) {
            return val >= part.from && val <= part.to && (val - part.from) % part.increment == 0;
        }
    }

    static class SimpleField extends BasicField {
        SimpleField(CronFieldType fieldType, String fieldExpr) {
            super(fieldType, fieldExpr);
        }

        public boolean matches(int val) {
            if (val >= fieldType.from && val <= fieldType.to) {
                for (FieldPart part : parts) {
                    if (matches(val, part)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    static class DayOfWeekField extends BasicField {

        DayOfWeekField(String fieldExpr) {
            super(CronFieldType.DAY_OF_WEEK, fieldExpr);
        }

        boolean matches(LocalDate dato) {
            for (FieldPart part : parts) {
                if ("L".equals(part.modifier)) {
                    return dato.getDayOfWeek() == part.from && dato.getDayOfMonth() > (dato.dayOfMonth().getMaximumValue() - DAYS_PER_WEEK);
                } else if ("#".equals(part.incrementModifier)) {
                    if (dato.getDayOfWeek() == part.from) {
                        int num = dato.getDayOfMonth() / 7;
                        return part.increment == (dato.getDayOfMonth() % 7 == 0 ? num : num + 1);
                    }
                    return false;
                } else if (matches(dato.getDayOfWeek(), part)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected Integer mapValue(String value) {
            // Use 1-7 for weedays, but 0 will also represent sunday (linux practice)
            return "0".equals(value) ? Integer.valueOf(7) : super.mapValue(value);
        }

        @Override
        protected boolean matches(int val, FieldPart part) {
            return "?".equals(part.modifier) || super.matches(val, part);
        }

        @Override
        protected void validatePart(FieldPart part) {
            if (part.modifier != null && Arrays.asList("L", "?").indexOf(part.modifier) == -1) {
                throw new IllegalArgumentException(String.format("Invalid modifier [%s]", part.modifier));
            } else if (part.incrementModifier != null && Arrays.asList("/", "#").indexOf(part.incrementModifier) == -1) {
                throw new IllegalArgumentException(String.format("Invalid increment modifier [%s]", part.incrementModifier));
            }
        }
    }

    static class DayOfMonthField extends BasicField {
        DayOfMonthField(String fieldExpr) {
            super(CronFieldType.DAY_OF_MONTH, fieldExpr);
        }

        boolean matches(LocalDate dato) {
            for (FieldPart part : parts) {
                if ("L".equals(part.modifier)) {
                    return dato.getDayOfMonth() == (dato.dayOfMonth().getMaximumValue() - (part.from == null ? 0 : part.from));
                } else if ("W".equals(part.modifier)) {
                    if (dato.getDayOfWeek() <= 5) {
                        if (dato.getDayOfMonth() == part.from) {
                            return true;
                        } else if (dato.getDayOfWeek() == 5) {
                            return dato.plusDays(1).getDayOfMonth() == part.from;
                        } else if (dato.getDayOfWeek() == 1) {
                            return dato.minusDays(1).getDayOfMonth() == part.from;
                        }
                    }
                } else if (matches(dato.getDayOfMonth(), part)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void validatePart(FieldPart part) {
            if (part.modifier != null && Arrays.asList("L", "W", "?").indexOf(part.modifier) == -1) {
                throw new IllegalArgumentException(String.format("Invalid modifier [%s]", part.modifier));
            } else if (part.incrementModifier != null && !"/".equals(part.incrementModifier)) {
                throw new IllegalArgumentException(String.format("Invalid increment modifier [%s]", part.incrementModifier));
            }
        }

        @Override
        protected boolean matches(int val, FieldPart part) {
            return "?".equals(part.modifier) || super.matches(val, part);
        }
    }
}