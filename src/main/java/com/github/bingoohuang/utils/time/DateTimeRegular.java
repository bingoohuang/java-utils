package com.github.bingoohuang.utils.time;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

// 规整时间日期表示字符串（例如：2018-10-23 21:47，2018/10/23 21:48:15，2018-10-23T21:48:15.235）
@RequiredArgsConstructor @Slf4j
public class DateTimeRegular {
    private final String dateTimeStr;

    private int year = 1970;
    private int monthOfYear = 1;
    private int dayOfMonth = 1;
    private int hourOfDay = 0;
    private int minuteOfHour = 0;
    private int secondOfMinute = 0;
    private int millisOfSecond = 0;

    public DateTime regular() {
        if (StringUtils.isEmpty(dateTimeStr)) return null;

        String dts = dateTimeStr.trim();
        dts = dts.replaceAll("\\s+", "T");
        dts = dts.replace('日', 'T');
        val posT = dts.indexOf("T");

        val splitter = Splitter.onPattern("[^\\d]+");

        String ts;
        if (posT > 0) {
            String ds = dts.substring(0, posT);
            val parts = Iterables.toArray(splitter.split(ds), String.class);
            parseDate(parts);

            ts = dts.substring(posT + 1);
        } else {
            ts = dts;
        }

        val parts = Iterables.toArray(splitter.split(ts), String.class);
        if (ts.contains(":")) {
            parseTime(parts);
        } else {
            parseDate(parts);
        }

        if (year < 100) year += 2000;

        return new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, millisOfSecond);
    }

    private void parseTime(String[] parts) {
        int partsLen = parts.length;
        if (parts[partsLen - 1].length() == 3) {
            millisOfSecond = Integer.parseInt(parts[partsLen - 1]);
            partsLen--;
        }

        if (partsLen >= 2) {
            hourOfDay = Integer.parseInt(parts[0]);
            minuteOfHour = Integer.parseInt(parts[1]);
        }

        if (partsLen >= 3) {
            secondOfMinute = Integer.parseInt(parts[2]);
        }
    }

    private void parseDate(String[] parts) {
        if (parts.length == 3 || parts.length == 2) {
            if (parts[0].length() == 4) {
                year = Integer.parseInt(parts[0]);
                monthOfYear = Integer.parseInt(parts[1]);
                if (parts.length == 3) dayOfMonth = Integer.parseInt(parts[2]);
            } else if (parts.length == 3) {
                year = Integer.parseInt(parts[0]);
                monthOfYear = Integer.parseInt(parts[1]);
                dayOfMonth = Integer.parseInt(parts[2]);
            } else {
                monthOfYear = Integer.parseInt(parts[0]);
                dayOfMonth = Integer.parseInt(parts[1]);
            }
        } else {
            log.warn("unknown format for datetime {}", dateTimeStr);
        }
    }
}
