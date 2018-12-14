package com.github.bingoohuang.utils.time;

import org.joda.time.DateTime;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class DateTimesTest {

    @Test
    public void parseBirthday() {
        DateTime dt = DateTimes.regular("2018-11-04");
        assertThat(DateTimes.parseBirthday("2018年11月4日")).isEqualTo(dt);
        assertThat(DateTimes.parseBirthday("2018-11-04")).isEqualTo(dt);
        assertThat(DateTimes.parseBirthday("2018/11/4")).isEqualTo(dt);
        assertThat(DateTimes.parseBirthday("79-11-4")).isEqualTo(DateTimes.regular("1979-11-04"));
        assertThat(DateTimes.parseBirthday("18-11-4")).isEqualTo(DateTimes.regular("2018-11-04"));
        assertThat(DateTimes.parseBirthday("18-13-4")).isNull();
    }
}