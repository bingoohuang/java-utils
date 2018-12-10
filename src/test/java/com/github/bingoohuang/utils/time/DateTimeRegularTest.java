package com.github.bingoohuang.utils.time;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class DateTimeRegularTest {
    public static DateTime regular(String dateTimeStr) {
        if (StringUtils.isEmpty(dateTimeStr)) return null;

        return new DateTimeRegular(dateTimeStr).regular();
    }


    @Test
    public void regularDateTime() {
        val fmt = "yyyy-MM-dd HH:mm:ss.SSS";
        assertThat(regular("2018-10-23").toString(fmt)).isEqualTo("2018-10-23 00:00:00.000");
        assertThat(regular("2018.10.23").toString(fmt)).isEqualTo("2018-10-23 00:00:00.000");
        assertThat(regular("10-23").toString(fmt)).isEqualTo("1970-10-23 00:00:00.000");
        assertThat(regular("2018-10-23  21:47").toString(fmt)).isEqualTo("2018-10-23 21:47:00.000");
        assertThat(regular("2018.10.23 21:47:01").toString(fmt)).isEqualTo("2018-10-23 21:47:01.000");
        assertThat(regular("21:47").toString(fmt)).isEqualTo("1970-01-01 21:47:00.000");
        assertThat(regular("21:47:40").toString(fmt)).isEqualTo("1970-01-01 21:47:40.000");
        assertThat(regular("2018-10-23 1:47").toString(fmt)).isEqualTo("2018-10-23 01:47:00.000");
        assertThat(regular("2018年9月23日21:47").toString(fmt)).isEqualTo("2018-09-23 21:47:00.000");
        assertThat(regular("18年9月23日21:47").toString(fmt)).isEqualTo("2018-09-23 21:47:00.000");
        assertThat(regular("2018/10/23 21:48:15").toString(fmt)).isEqualTo("2018-10-23 21:48:15.000");
        assertThat(regular("2018-10-23T21:48:15.235").toString(fmt)).isEqualTo("2018-10-23 21:48:15.235");
    }
}