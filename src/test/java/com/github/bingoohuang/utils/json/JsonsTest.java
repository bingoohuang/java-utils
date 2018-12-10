package com.github.bingoohuang.utils.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.github.bingoohuang.utils.joda.JodaDateTimeDeserializer;
import com.github.bingoohuang.utils.joda.JodaDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class JsonsTest {
    @Data
    public static class Other {
        private String name;
    }

    @Data
    public static class Bean {
        private Other key;
    }

    @Test
    public void test1() {
        String json = "{\"key\":\"\"}";

        Bean bean = JSON.parseObject(json, Bean.class);
        assertThat(bean).isEqualTo(new Bean());
    }


    @Test
    public void test2() {
        Bean bean = new Bean();

        ValueFilter filter = (o, s, v) -> v == null && o.getClass().getPackage().getName().startsWith(JsonsTest.class.getPackage().getName()) ? "" : v;

        String s = JSON.toJSONString(bean, filter);
        assertThat(s).isEqualTo("{\"key\":\"\"}");
    }

    @Data
    public static class TimeBean {
        private DateTime time;
    }

    @Test
    public void testDateTime() {
        ParserConfig parserConfig = new ParserConfig();
        parserConfig.putDeserializer(DateTime.class, new JodaDateTimeDeserializer("yyyy-MM-dd HH:mm:ss.SSS", "yyyy.MM.dd HH:mm:ss.SSS"));

        TimeBean o = JSON.parseObject("{\"time\":\"2018.05.25\"}", TimeBean.class, parserConfig, JSON.DEFAULT_PARSER_FEATURE);

        assertThat(o.getTime()).isNotNull();
    }

    @Test
    public void testSerialize1() {
        val config = new SerializeConfig();
        config.put(DateTime.class, new JodaDateTimeSerializer("yyyy-MM-dd HH:mm:ss.SSS", false));

        String time = "2018-07-26 11:38:57.123";
        val dt = DateTime.parse(time, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        String json = JSON.toJSONString(dt, config);
        assertThat(json).isEqualTo('"' + time + '"');
    }

    @Test
    public void testSerialize2() {
        val config = new SerializeConfig();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        config.put(DateTime.class, new JodaDateTimeSerializer(pattern, false));

        String time = "2018-07-26 11:38:57";
        val dt = DateTime.parse(time, DateTimeFormat.forPattern(pattern));

        String json = JSON.toJSONString(dt, config);
        assertThat(json).isEqualTo('"' + time + '"');
    }

    @Test
    public void testSerialize3() {
        val config = new SerializeConfig();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        config.put(DateTime.class, new JodaDateTimeSerializer(pattern, true, true));

        String time = "2018-07-26 11:38:57";
        val dt = DateTime.parse(time, DateTimeFormat.forPattern(pattern));

        String json = JSON.toJSONString(dt, config);
        assertThat(json).isEqualTo(dt.getMillis() + "");
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class DateTimeBean {
        private DateTime dateTime;
    }

    @Test
    public void testSerializeNull1() {
        val config = new SerializeConfig();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        config.put(DateTime.class, new JodaDateTimeSerializer(pattern, true));

        String json = JSON.toJSONString(new DateTimeBean(null), config);
        assertThat(json).isEqualTo("{}");
    }


    @Test
    public void testDeserialize1() {
        val config = new ParserConfig();
        config.putDeserializer(DateTime.class, new JodaDateTimeDeserializer("yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"));
        String time = "2018-07-26 11:38:57";
        String pattern = "yyyy-MM-dd HH:mm:ss";
        val dt1 = DateTime.parse(time, DateTimeFormat.forPattern(pattern));

        DateTime dt2 = JSON.parseObject(dt1.getMillis() + "", DateTime.class, config);
        assertThat(dt2).isEqualTo(dt1);

        DateTime dt3 = JSON.parseObject('"' + time + '"', DateTime.class, config);
        assertThat(dt3).isEqualTo(dt1);
    }
}